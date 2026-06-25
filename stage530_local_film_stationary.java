import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage530_local_film_stationary {
  static final String BASE =
      "523_stage520_local_tff_drainage_checked.mph";
  static final String INPUT =
      "530_stage530_local_film_input.mph";
  static final String SETUP =
      "531_stage530_local_film_stationary_setup.mph";
  static final String RESULTS =
      "532_stage530_local_film_stationary_results.mph";
  static final String CHECKED =
      "533_stage530_local_film_stationary_checked.mph";

  static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) if (!old.contains(tag)) return tag;
    throw new IllegalStateException("No new solution was created");
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
      Model model, String tag, String label, String expression, String unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", "dset530");
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
      if (Math.abs(model.param().evaluate("stage520_revision") - 520) > 0.1) {
        throw new IllegalStateException("Stage 520 dependency is missing");
      }
      model.save(INPUT);

      model.param().set(
          "stage530_revision", "530",
          "Local-film stationary validation stage");
      model.param().set(
          "t_snapshot530", "0.28[s]",
          "Mid-stroke snapshot in the constant-speed interval");
      model.param().set(
          "v_wall530", "v_blink_avg",
          "Local film wall speed at the validation snapshot");
      model.param().set("t_replay", "t_snapshot530");
      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_jfo197");
      model.component(comp).physics("tff").feature("ffp1")
          .set("TangentialWallVelocity", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("vw", new String[] {
            "0",
            "-lid_mask*v_wall530*Z/sqrt(Y^2+Z^2)",
            "lid_mask*v_wall530*Y/sqrt(Y^2+Z^2)"
          });
      model.component(comp).physics("tff").prop("EquationType").set(
          "EquationType", "ReynoldsEquation");
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm", "0[Pa]");

      String study = "std_localfilm530";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 530 local JFO film stationary midpoint snapshot");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String step = study + "/stat";
      for (String tag :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 250);

      model.label("Stage 530 local film stationary setup");
      model.save(SETUP);
      System.out.println("RUN_STAGE530 SOLUTION=" + solution);
      model.sol(solution).runAll();
      model.label("Stage 530 local film stationary results");
      model.save(RESULTS);

      removeDataset(model, "dset530");
      model.result().dataset().create("dset530", "Solution");
      model.result().dataset("dset530").set("solution", solution);
      model.result().dataset("dset530").label(
          "Stage 530 local film stationary solution");
      removeNumerical(model, "eval530");
      model.result().numerical().create("eval530", "EvalGlobal");
      model.result().numerical("eval530").set("data", "dset530");
      model.result().numerical("eval530").set(
          "expr", new String[] {
            "intop_film(1)",
            "intop_film(h_jfo197)/intop_film(1)",
            "intop_film(tff.p)",
            "intop_film(max(tff.p,0))",
            "intop_film(tau_film_wall)",
            "intop_film(tau_film_wall)/F_total_target"
          });
      model.result().numerical("eval530").set(
          "unit", new String[] {
            "mm^2", "um", "N", "N", "N", "1"
          });
      double[][] values = model.result().numerical("eval530").getReal();
      for (int i = 0; i < values.length; i++) {
        requireFinite(values[i][0], "eval530[" + i + "]");
        System.out.printf(Locale.US,
            "STAGE530[%d]=%.12g%n", i, values[i][0]);
      }
      surface(model, "pg530_hfilm",
          "Stage 530 local film thickness", "h_jfo197", "um");
      surface(model, "pg530_pfilm",
          "Stage 530 local JFO film pressure", "tff.p", "Pa");
      surface(model, "pg530_shear",
          "Stage 530 local film wall shear", "tau_film_wall", "Pa");

      if (!Arrays.equals(
          model.component(comp).physics("tff").selection().entities(),
          model.component(comp).selection("sel_film_track").entities(2))) {
        throw new IllegalStateException("TFF is no longer local");
      }
      System.out.println("STAGE530_SOLUTION=" + solution);
      System.out.println("STAGE530 CHECK=PASS");
      model.label("Stage 530 local film stationary checked");
      model.save(CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
