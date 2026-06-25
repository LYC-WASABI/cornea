import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage202_jfo_rest_preload_bridge {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!oldTags.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "386_lid8mm_stage201_jfo_strong_micro_setup_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", "0");
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "phi_start201");
      model.component(comp).variable(pressureVars).set(
          "t_replay", "0[s]");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "0");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_start201");
      model.component(comp).variable(mixedVars).set(
          "h_jfo201",
          "max(h_residual189,h0_tear+lid_mask*"
              + "(delta_h_jfo197+d_indent_ref201-dr_indent119))");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "0[Pa]");

      String study = "std_rest202";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 202 JFO zero-speed joint preload bridge");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", globalEq, "on"});
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol49");
      model.study(study).feature("stat").set("initsoluse", "sol49");
      model.study(study).feature("stat").set("initsolusesolnum", "last");
      String step = study + "/stat";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set(
            "StudyStep", step);
      }
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", step);
        } catch (Exception ignored) {}
      }
      model.component(comp).physics(globalEq).feature("ge1").set(
          "StudyStep", step);

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature stationary = model.sol(solution).feature("s1");
      try { stationary.feature().remove("se1"); }
      catch (Exception ignored) {}
      try { stationary.feature().remove("fc1"); }
      catch (Exception ignored) {}
      stationary.create("fc1", "FullyCoupled");
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      model.save(
          "389_lid8mm_stage202_jfo_rest_preload_setup_Model.mph");
      System.out.println("RUN_STAGE202 " + solution);
      model.sol(solution).runAll();
      model.save(
          "390_lid8mm_stage202_jfo_rest_preload_results_Model.mph");

      try { model.result().dataset().remove("dset202"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset202", "Solution");
      model.result().dataset("dset202").set("solution", solution);
      try { model.result().numerical().remove("eval202"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval202", "EvalGlobal");
      model.result().numerical("eval202").set("data", "dset202");
      model.result().numerical("eval202").set(
          "expr",
          new String[] {
            "phi_lid_structure", "Fn_contact119", "Wfilm201",
            "Ftotal201", "dr_indent119",
            "intop_film(h_jfo201)/intop_film(1)",
            "intop_film(tff.theta)/intop_film(1)"
          });
      model.result().numerical("eval202").set(
          "unit",
          new String[] {"deg", "N", "N", "N", "mm", "um", "1"});
      double[][] x = model.result().numerical("eval202").getReal();
      System.out.printf(
          Locale.US,
          "STAGE202 phi=%.12g Fc=%.12g Wf=%.12g Ft=%.12g"
              + " d=%.12g havg=%.12g theta=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0],
          x[4][0], x[5][0], x[6][0]);
      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")
            && !tag.equals(solution)) {
          try { model.sol(tag).clearSolution(); } catch (Exception ignored) {}
        }
      }
      model.save(
          "391_lid8mm_stage202_jfo_rest_preload_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
