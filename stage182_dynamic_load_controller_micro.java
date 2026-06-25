import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage182_dynamic_load_controller_micro {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "341_lid8mm_stage181_exact_static_results_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("tau_load182", "0.005[s]");
      String slideFraction =
          "min(1,0.5+t/(T_structure_slide-T_speed_ramp))";
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", slideFraction);
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "theta_slide_total*slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_lid_structure");
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr177");

      String study = "std_controller182";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label("Stage 182 dynamic indentation load controller");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set(
          "tlist", "range(0,0.00005,0.001)");
      model.study(study).feature("time").set("geometricNonlinearity", "on");
      model.study(study).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", "sol45");
      model.study(study).feature("time").set("initsoluse", "sol45");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      String step = study + "/time";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set("StudyStep", step);
      }
      model.component(comp).physics(globalEq).feature("ge1").set("StudyStep", step);
      model.component(comp).physics("solid").feature("dcnt1").set("useCutback", "1");

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature timeSolver = model.sol(solution).feature("t1");
      timeSolver.set("consistent", "off");
      timeSolver.set("initialstepbdfactive", "on");
      timeSolver.set("initialstepbdf", "1e-7");
      timeSolver.set("maxstepconstraintbdf", "const");
      timeSolver.set("maxstepbdf", "5e-5");
      try {
        timeSolver.feature().remove("se1");
      } catch (Exception ignored) {
      }
      try {
        timeSolver.feature().remove("fc1");
      } catch (Exception ignored) {
      }
      timeSolver.create("fc1", "FullyCoupled");
      timeSolver.feature("fc1").set("linsolver", "dDef");
      timeSolver.feature("fc1").set("maxiter", 120);
      model.save("345_lid8mm_stage182_dynamic_load_controller_setup_Model.mph");
      System.out.println("RUN_STAGE182 " + solution);
      model.sol(solution).runAll();
      model.save("346_lid8mm_stage182_dynamic_load_controller_results_Model.mph");

      try {
        model.result().dataset().remove("dset182");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset182", "Solution");
      model.result().dataset("dset182").set("solution", solution);
      try {
        model.result().numerical().remove("eval182");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval182", "EvalGlobal");
      model.result().numerical("eval182").set("data", "dset182");
      model.result().numerical("eval182").set(
          "expr",
          new String[] {
            "t", "phi_lid_structure", "Fn_contact119", "Wfilm177",
            "Ftotal177", "dr_indent119", "q_force_total111"
          });
      double[][] values = model.result().numerical("eval182").getReal();
      for (int index = 0; index < values[0].length; index++) {
        System.out.printf(
            Locale.US,
            "t=%.8g phi=%.8g Fc=%.8g Wf=%.8g Ft=%.8g d=%.8g q=%.8g%n",
            values[0][index],
            values[1][index],
            values[2][index],
            values[3][index],
            values[4][index],
            values[5][index],
            values[6][index]);
      }
      model.save("347_lid8mm_stage182_dynamic_load_controller_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
