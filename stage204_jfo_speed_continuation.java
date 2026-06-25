import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage204_jfo_speed_continuation {
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
          "Model", "391_lid8mm_stage202_jfo_rest_preload_checked_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("speed_scale204", "0");
      model.param().set("p_cav_transition195", "1[MPa]");
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", "0");
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "phi_start201");
      model.component(comp).variable(pressureVars).set("t_replay", "0[s]");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "0");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_start201");
      model.component(comp).variable(mixedVars).set(
          "omega_lid", "speed_scale204*omega_slide_const");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "withsol('sol48',pfilm)");
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr201");

      String study = "std_speed204";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 204 JFO fixed-position speed continuation");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"speed_scale204"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {"1"});
      model.study(study).feature("param").set(
          "punit", new String[] {"1"});
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
          "395_lid8mm_stage204_jfo_speed_continuation_setup_Model.mph");
      System.out.println("RUN_STAGE204 " + solution);
      model.sol(solution).runAll();
      model.save(
          "396_lid8mm_stage204_jfo_speed_continuation_results_Model.mph");

      try { model.result().dataset().remove("dset204"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset204", "Solution");
      model.result().dataset("dset204").set("solution", solution);
      try { model.result().numerical().remove("eval204"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval204", "EvalGlobal");
      model.result().numerical("eval204").set("data", "dset204");
      model.result().numerical("eval204").set(
          "expr",
          new String[] {
            "speed_scale204", "Fn_contact119", "Wfilm201",
            "Ftotal201", "dr_indent119",
            "intop_film(h_jfo201)/intop_film(1)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval204").set(
          "unit",
          new String[] {"1", "N", "N", "N", "mm", "um", "N"});
      double[][] x = model.result().numerical("eval204").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(
            Locale.US,
            "speed=%.8g Fc=%.10g Wf=%.10g Ft=%.10g"
                + " d=%.10g havg=%.10g Fshear=%.10g%n",
            x[0][j], x[1][j], x[2][j], x[3][j],
            x[4][j], x[5][j], x[6][j]);
      }
      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")
            && !tag.equals("sol51") && !tag.equals(solution)) {
          try { model.sol(tag).clearSolution(); } catch (Exception ignored) {}
        }
      }
      model.save(
          "397_lid8mm_stage204_jfo_speed_continuation_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
