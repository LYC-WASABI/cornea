import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage540_jfo_joint_static_balance {
  static final String BASE =
      "533_stage530_local_film_stationary_checked.mph";
  static final String INPUT =
      "540_stage540_jfo_joint_input.mph";
  static final String SETUP =
      "541_stage540_jfo_joint_static_setup.mph";
  static final String RESULTS =
      "542_stage540_jfo_joint_static_results.mph";
  static final String CHECKED =
      "543_stage540_jfo_joint_static_checked.mph";

  static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) if (!old.contains(tag)) return tag;
    throw new IllegalStateException("No new solution created");
  }

  static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  static void surface(
      Model model, String tag, String label, String data,
      String expression, String unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", data);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expression);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  static void requireFinite(double value, String name) {
    if (!Double.isFinite(value)) {
      throw new IllegalStateException(name + " is not finite");
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String comp = "comp1";
      String globalEq = "ge_force_total111";
      if (Math.abs(model.param().evaluate("stage530_revision") - 530) > 0.1) {
        throw new IllegalStateException("Stage 530 dependency missing");
      }
      model.save(INPUT);

      model.param().set(
          "stage540_revision", "540",
          "Local JFO and film-contact joint static balance stage");
      model.param().set(
          "p_cav_transition540", "1[MPa]",
          "Initial JFO pressure transition width");
      model.param().set(
          "h_sep_uniform540", "21[um]",
          "Stage 540 uniform dynamic separation for joint load sharing");
      model.component(comp).variable("var_mixed_lub").set(
          "h_jfo197",
          "max(h_residual189,h0_tear+h_sep_uniform540)");
      model.component(comp).physics("tff").prop("EquationType").set(
          "EquationType", "ReynoldsEquationWithCavitation");
      model.component(comp).physics("tff").prop("EquationType").set(
          "sftransition", "p_cav_transition540");
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm", "0[Pa]");

      String filmStudy = "std_localjfo540";
      try { model.study().remove(filmStudy); } catch (Exception ignored) {}
      model.study().create(filmStudy);
      model.study(filmStudy).label(
          "Stage 540 local JFO stationary continuation");
      model.study(filmStudy).create("stat", "Stationary");
      model.study(filmStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      model.study(filmStudy).feature("stat").set("useinitsol", "on");
      model.study(filmStudy).feature("stat").set("initmethod", "sol");
      model.study(filmStudy).feature("stat").set("initsol", "sol50");
      model.study(filmStudy).feature("stat").set("initsoluse", "sol50");
      model.study(filmStudy).feature("stat").set(
          "initsolusesolnum", "last");
      String filmStep = filmStudy + "/stat";
      for (String tag :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", filmStep);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(filmStudy).createAutoSequences("sol");
      String filmSolution = newest(model, before);
      SolverFeature filmStationary =
          model.sol(filmSolution).feature("s1");
      if (!Arrays.asList(filmStationary.feature().tags()).contains("fc1")) {
        filmStationary.create("fc1", "FullyCoupled");
      }
      filmStationary.feature("fc1").set("linsolver", "dDef");
      filmStationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 540 local JFO and joint-balance setup");
      model.save(SETUP);
      System.out.println("RUN_STAGE540_JFO SOLUTION=" + filmSolution);
      model.sol(filmSolution).runAll();

      String vars = "var_partitioned_local_pfilm";
      model.component(comp).variable(vars).set(
          "pfilm540", "withsol('" + filmSolution + "',max(tff.p,0))");
      model.component(comp).variable(vars).set(
          "Wfilm540",
          "withsol('" + filmSolution
              + "',intop_film(max(tff.p,0)))");
      model.component(comp).variable(vars).set(
          "thetaAvg540",
          "withsol('" + filmSolution
              + "',intop_film(tff.theta)/intop_film(1))");
      model.component(comp).variable(vars).set(
          "FshearFilm540",
          "withsol('" + filmSolution
              + "',intop_film(tau_film_wall))");
      model.component(comp).variable(vars).set(
          "Ftotal540", "Fn_contact119+Wfilm540");
      model.component(comp).variable(vars).set(
          "Ferr540",
          "(Ftotal540-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");

      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").selection()
          .named("sel_film_track");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-pfilm540*nx", "-pfilm540*ny", "-pfilm540*nz"
              });
      model.component(comp).physics(globalEq).feature("ge1")
          .set("equation", 1, 1, "Ferr540");

      String structureStudy = "std_joint540";
      try { model.study().remove(structureStudy); }
      catch (Exception ignored) {}
      model.study().create(structureStudy);
      model.study(structureStudy).label(
          "Stage 540 local film-contact joint 0.03 N balance");
      model.study(structureStudy).create("stat", "Stationary");
      model.study(structureStudy).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(structureStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(structureStudy).feature("stat").set("useinitsol", "on");
      model.study(structureStudy).feature("stat").set("initmethod", "sol");
      model.study(structureStudy).feature("stat").set("initsol", "sol49");
      model.study(structureStudy).feature("stat").set("initsoluse", "sol49");
      model.study(structureStudy).feature("stat").set(
          "initsolusesolnum", "last");
      String structureStep = structureStudy + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        model.component(comp).physics("solid").feature(tag)
            .set("StudyStep", structureStep);
      }
      model.component(comp).physics(globalEq).feature("ge1")
          .set("StudyStep", structureStep);
      before = model.sol().tags();
      model.study(structureStudy).createAutoSequences("sol");
      String structureSolution = newest(model, before);
      SolverFeature structureStationary =
          model.sol(structureSolution).feature("s1");
      if (!Arrays.asList(structureStationary.feature().tags())
          .contains("fc1")) {
        structureStationary.create("fc1", "FullyCoupled");
      }
      structureStationary.feature("fc1").set("linsolver", "dDef");
      structureStationary.feature("fc1").set("maxiter", 350);
      System.out.println(
          "RUN_STAGE540_JOINT SOLUTION=" + structureSolution);
      model.sol(structureSolution).runAll();
      model.label("Stage 540 local JFO joint static results");
      model.save(RESULTS);

      removeDataset(model, "dset540f");
      model.result().dataset().create("dset540f", "Solution");
      model.result().dataset("dset540f").set("solution", filmSolution);
      removeDataset(model, "dset540s");
      model.result().dataset().create("dset540s", "Solution");
      model.result().dataset("dset540s").set(
          "solution", structureSolution);
      removeNumerical(model, "eval540");
      model.result().numerical().create("eval540", "EvalGlobal");
      model.result().numerical("eval540").set("data", "dset540s");
      model.result().numerical("eval540").set(
          "expr", new String[] {
            "Wfilm540", "Fn_contact119", "Ftotal540",
            "(Ftotal540-F_total_target)/F_total_target",
            "thetaAvg540", "FshearFilm540",
            "FshearFilm540/Ftotal540", "dr_indent119"
          });
      model.result().numerical("eval540").set(
          "unit", new String[] {
            "N", "N", "N", "1", "1", "N", "1", "mm"
          });
      double[][] x = model.result().numerical("eval540").getReal();
      for (int i = 0; i < x.length; i++) {
        requireFinite(x[i][0], "eval540[" + i + "]");
        System.out.printf(Locale.US,
            "STAGE540[%d]=%.12g%n", i, x[i][0]);
      }
      if (Math.abs(x[3][0]) > 0.01) {
        throw new IllegalStateException(
            "Stage 540 total-load error exceeds 1 percent");
      }
      surface(model, "pg540_pfilm",
          "Stage 540 local JFO pressure", "dset540f", "tff.p", "Pa");
      surface(model, "pg540_theta",
          "Stage 540 local JFO fractional content",
          "dset540f", "tff.theta", "1");
      surface(model, "pg540_contact",
          "Stage 540 cornea contact pressure",
          "dset540s", "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      surface(model, "pg540_disp",
          "Stage 540 cornea and lid displacement",
          "dset540s", "solid.disp", "mm");
      surface(model, "pg540_mises",
          "Stage 540 cornea and lid von Mises stress",
          "dset540s", "solid.mises", "Pa");

      System.out.println("STAGE540_FILM_SOLUTION=" + filmSolution);
      System.out.println(
          "STAGE540_STRUCTURE_SOLUTION=" + structureSolution);
      System.out.println("STAGE540 CHECK=PASS");
      model.label("Stage 540 local JFO joint static checked");
      model.save(CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
