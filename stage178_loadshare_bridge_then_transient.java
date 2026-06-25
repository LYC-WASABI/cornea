import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage178_loadshare_bridge_then_transient {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  static void bind(Model model, String studyStep) {
    for (String feature :
        new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
      model.component("comp1").physics("solid").feature(feature).set(
          "StudyStep", studyStep);
    }
    model.component("comp1").physics("ge_force_total111").feature("ge1").set(
        "StudyStep", studyStep);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "326_lid8mm_stage177_continuous_midpoint_bridge_setup_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      // First reconcile the load split at the fixed midpoint.
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", "0.5");
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "-35[deg]");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "0.5");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "-35[deg]");

      String bridgeStudy = "std_loadshare178";
      try {
        model.study().remove(bridgeStudy);
      } catch (Exception ignored) {
      }
      model.study().create(bridgeStudy);
      model.study(bridgeStudy).label("Stage 178 fixed-midpoint load-share continuation");
      model.study(bridgeStudy).create("param", "Parametric");
      model.study(bridgeStudy).feature("param").set(
          "pname", new String[] {"film_share177"});
      model.study(bridgeStudy).feature("param").set(
          "plistarr", new String[] {"0.999 0.995 0.99 0.98 0.95"});
      model.study(bridgeStudy).feature("param").set("punit", new String[] {"1"});
      model.study(bridgeStudy).create("stat", "Stationary");
      model.study(bridgeStudy).feature("stat").set("geometricNonlinearity", "on");
      model.study(bridgeStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(bridgeStudy).feature("stat").set("useinitsol", "on");
      model.study(bridgeStudy).feature("stat").set("initmethod", "sol");
      model.study(bridgeStudy).feature("stat").set("initsol", "sol42");
      model.study(bridgeStudy).feature("stat").set("initsoluse", "sol42");
      model.study(bridgeStudy).feature("stat").set("initsolusesolnum", "last");
      bind(model, bridgeStudy + "/stat");

      String[] before = model.sol().tags();
      model.study(bridgeStudy).createAutoSequences("sol");
      String bridgeSolution = newest(model, before);
      SolverFeature stationary = model.sol(bridgeSolution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      model.save("329_lid8mm_stage178_loadshare_bridge_setup_Model.mph");
      System.out.println("RUN_BRIDGE178 " + bridgeSolution);
      model.sol(bridgeSolution).runAll();
      model.save("330_lid8mm_stage178_loadshare_bridge_results_Model.mph");

      // Restore the smooth, nearly constant-speed motion law.
      String slideFraction =
          "if(t<T_structure_pre,0,"
              + "if(t<T_structure_pre+T_speed_ramp,"
              + "(0.5*(t-T_structure_pre)-T_speed_ramp/(2*pi)"
              + "*sin(pi*(t-T_structure_pre)/T_speed_ramp))"
              + "/(T_structure_slide-T_speed_ramp),"
              + "if(t<T_structure_pre+T_structure_slide-T_speed_ramp,"
              + "((t-T_structure_pre)-0.5*T_speed_ramp)"
              + "/(T_structure_slide-T_speed_ramp),"
              + "if(t<T_structure_pre+T_structure_slide,"
              + "1-(0.5*(T_structure_pre+T_structure_slide-t)"
              + "-T_speed_ramp/(2*pi)*sin(pi*(T_structure_pre"
              + "+T_structure_slide-t)/T_speed_ramp))"
              + "/(T_structure_slide-T_speed_ramp),1))))";
      model.param().set("film_share177", "0.95");
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", slideFraction);
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "theta_slide_total*slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_lid_structure");

      String transientStudy = "std_transient178";
      try {
        model.study().remove(transientStudy);
      } catch (Exception ignored) {
      }
      model.study().create(transientStudy);
      model.study(transientStudy).label("Stage 178 continuous midpoint transient");
      model.study(transientStudy).create("time", "Transient");
      model.study(transientStudy).feature("time").set(
          "tlist", "range(0.2805,0.0005,0.30)");
      model.study(transientStudy).feature("time").set("geometricNonlinearity", "on");
      model.study(transientStudy).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(transientStudy).feature("time").set("useinitsol", "on");
      model.study(transientStudy).feature("time").set("initmethod", "sol");
      model.study(transientStudy).feature("time").set("initsol", bridgeSolution);
      model.study(transientStudy).feature("time").set("initsoluse", bridgeSolution);
      model.study(transientStudy).feature("time").set("initsolusesolnum", "last");
      bind(model, transientStudy + "/time");

      before = model.sol().tags();
      model.study(transientStudy).createAutoSequences("sol");
      String transientSolution = newest(model, before);
      SolverFeature timeSolver = model.sol(transientSolution).feature("t1");
      timeSolver.set("consistent", "off");
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
      model.save("331_lid8mm_stage178_continuous_bridge_setup_Model.mph");
      System.out.println("RUN_TRANSIENT178 " + transientSolution);
      model.sol(transientSolution).runAll();
      model.save("332_lid8mm_stage178_continuous_bridge_results_Model.mph");

      try {
        model.result().dataset().remove("dset178");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset178", "Solution");
      model.result().dataset("dset178").set("solution", transientSolution);
      try {
        model.result().numerical().remove("eval178");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval178", "EvalGlobal");
      model.result().numerical("eval178").set("data", "dset178");
      model.result().numerical("eval178").set(
          "expr",
          new String[] {
            "t", "phi_lid_structure", "Fn_contact119", "Wfilm177",
            "Ftotal177", "dr_indent119"
          });
      double[][] values = model.result().numerical("eval178").getReal();
      for (int index = 0; index < values[0].length; index++) {
        System.out.printf(
            Locale.US,
            "t=%.8g phi=%.8g Fc=%.8g Wf=%.8g Ft=%.8g d=%.8g%n",
            values[0][index],
            values[1][index],
            values[2][index],
            values[3][index],
            values[4][index],
            values[5][index]);
      }
      model.save("333_lid8mm_stage178_continuous_bridge_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
