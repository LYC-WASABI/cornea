import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage199_jfo_joint_load_balance {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!oldTags.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "380_lid8mm_stage198_jfo_separation_fine_checked_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String globalEq = "ge_force_total111";

      model.param().set("delta_h_jfo197", "1.6593[um]",
          "Interpolated JFO separation for about 95 percent film load");

      String filmStudy = "std_jfofilm199";
      try { model.study().remove(filmStudy); } catch (Exception ignored) {}
      model.study().create(filmStudy);
      model.study(filmStudy).label(
          "Stage 199 exact JFO physical film pressure");
      model.study(filmStudy).create("stat", "Stationary");
      model.study(filmStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String filmStep = filmStudy + "/stat";
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", filmStep);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(filmStudy).createAutoSequences("sol");
      String filmSolution = newest(model, before);
      model.save("381_lid8mm_stage199_exact_jfo_film_setup_Model.mph");
      System.out.println("RUN_FILM199 " + filmSolution);
      model.sol(filmSolution).runAll();
      model.save("382_lid8mm_stage199_exact_jfo_film_results_Model.mph");

      model.component(comp).variable(pressureVars).set(
          "pfilm199", "withsol('" + filmSolution + "',tff.p)");
      model.component(comp).variable(pressureVars).set(
          "Wfilm199",
          "withsol('" + filmSolution + "',intop_film(tff.p))");
      model.component(comp).variable(pressureVars).set(
          "thetaAvg199",
          "withsol('" + filmSolution
              + "',intop_film(tff.theta)/intop_film(1))");
      model.component(comp).variable(pressureVars).set(
          "FshearFilm199",
          "withsol('" + filmSolution
              + "',intop_film(tau_film_wall))");
      model.component(comp).variable(pressureVars).set(
          "dr_indent119",
          "2*d_indent_bound154/pi*atan(pi*q_force_total111/"
              + "(2*q_indent_scale154))");
      model.component(comp).variable(pressureVars).set(
          "Ftotal199", "Fn_contact119+Wfilm199");
      model.component(comp).variable(pressureVars).set(
          "Ferr199",
          "(Ftotal199-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea",
              new String[] {
                "-pfilm199*nx", "-pfilm199*ny", "-pfilm199*nz"
              });
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr199");

      String structureStudy = "std_jfobalance199";
      try { model.study().remove(structureStudy); } catch (Exception ignored) {}
      model.study().create(structureStudy);
      model.study(structureStudy).label(
          "Stage 199 JFO film-contact joint 0.03 N balance");
      model.study(structureStudy).create("stat", "Stationary");
      model.study(structureStudy).feature("stat").set(
          "geometricNonlinearity", "on");
      model.study(structureStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(structureStudy).feature("stat").set("useinitsol", "on");
      model.study(structureStudy).feature("stat").set("initmethod", "sol");
      model.study(structureStudy).feature("stat").set("initsol", "sol42");
      model.study(structureStudy).feature("stat").set("initsoluse", "sol42");
      model.study(structureStudy).feature("stat").set(
          "initsolusesolnum", "last");
      String structureStep = structureStudy + "/stat";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set(
            "StudyStep", structureStep);
      }
      model.component(comp).physics(globalEq).feature("ge1").set(
          "StudyStep", structureStep);
      before = model.sol().tags();
      model.study(structureStudy).createAutoSequences("sol");
      String structureSolution = newest(model, before);
      SolverFeature stationary = model.sol(structureSolution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      model.save("383_lid8mm_stage199_jfo_joint_balance_setup_Model.mph");
      System.out.println("RUN_BALANCE199 " + structureSolution);
      model.sol(structureSolution).runAll();
      model.save("384_lid8mm_stage199_jfo_joint_balance_results_Model.mph");

      removeDataset(model, "dset199");
      model.result().dataset().create("dset199", "Solution");
      model.result().dataset("dset199").set("solution", structureSolution);
      removeNumerical(model, "eval199");
      model.result().numerical().create("eval199", "EvalGlobal");
      model.result().numerical("eval199").set("data", "dset199");
      model.result().numerical("eval199").set(
          "expr",
          new String[] {
            "delta_h_jfo197",
            "withsol('" + filmSolution
                + "',intop_film(h_jfo197)/intop_film(1))",
            "Fn_contact119",
            "Wfilm199",
            "Ftotal199",
            "dr_indent119",
            "FshearFilm199",
            "FshearFilm199/Ftotal199",
            "thetaAvg199"
          });
      model.result().numerical("eval199").set(
          "unit",
          new String[] {
            "um", "um", "N", "N", "N", "mm", "N", "1", "1"
          });
      double[][] x = model.result().numerical("eval199").getReal();
      System.out.printf(
          Locale.US,
          "STAGE199 separation=%.12g havg=%.12g Fc=%.12g"
              + " Wfilm=%.12g Ftotal=%.12g d=%.12g"
              + " Fshear=%.12g muFilm=%.12g thetaAvg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0], x[4][0],
          x[5][0], x[6][0], x[7][0], x[8][0]);
      model.save("385_lid8mm_stage199_jfo_joint_balance_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
