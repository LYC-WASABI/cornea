import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage181_exact_static_then_micro_transient {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  static void bind(Model model, String step) {
    for (String feature :
        new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
      model.component("comp1").physics("solid").feature(feature).set("StudyStep", step);
    }
    model.component("comp1").physics("ge_force_total111").feature("ge1").set(
        "StudyStep", step);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "330_lid8mm_stage178_loadshare_bridge_results_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("film_share177", "0.99");
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", "0.5");
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "-35[deg]");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "0.5");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "-35[deg]");

      String staticStudy = "std_exact181";
      try {
        model.study().remove(staticStudy);
      } catch (Exception ignored) {
      }
      model.study().create(staticStudy);
      model.study(staticStudy).label("Stage 181 exact 99 percent static branch");
      model.study(staticStudy).create("stat", "Stationary");
      model.study(staticStudy).feature("stat").set("geometricNonlinearity", "on");
      model.study(staticStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(staticStudy).feature("stat").set("useinitsol", "on");
      model.study(staticStudy).feature("stat").set("initmethod", "sol");
      model.study(staticStudy).feature("stat").set("initsol", "sol44");
      model.study(staticStudy).feature("stat").set("initsoluse", "sol44");
      model.study(staticStudy).feature("stat").set("initsolusesolnum", 3);
      bind(model, staticStudy + "/stat");
      String[] before = model.sol().tags();
      model.study(staticStudy).createAutoSequences("sol");
      String staticSolution = newest(model, before);
      SolverFeature stationary = model.sol(staticSolution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      model.save("340_lid8mm_stage181_exact_static_setup_Model.mph");
      System.out.println("RUN_STATIC181 " + staticSolution);
      model.sol(staticSolution).runAll();
      model.save("341_lid8mm_stage181_exact_static_results_Model.mph");

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
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", slideFraction);
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "theta_slide_total*slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_lid_structure");

      String transientStudy = "std_micro181";
      try {
        model.study().remove(transientStudy);
      } catch (Exception ignored) {
      }
      model.study().create(transientStudy);
      model.study(transientStudy).label("Stage 181 micro transient from exact static branch");
      model.study(transientStudy).create("time", "Transient");
      model.study(transientStudy).feature("time").set(
          "tlist", "range(0.28,0.00001,0.2801)");
      model.study(transientStudy).feature("time").set("geometricNonlinearity", "on");
      model.study(transientStudy).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(transientStudy).feature("time").set("useinitsol", "on");
      model.study(transientStudy).feature("time").set("initmethod", "sol");
      model.study(transientStudy).feature("time").set("initsol", staticSolution);
      model.study(transientStudy).feature("time").set("initsoluse", staticSolution);
      model.study(transientStudy).feature("time").set("initsolusesolnum", "last");
      bind(model, transientStudy + "/time");
      model.component(comp).physics("solid").feature("dcnt1").set("useCutback", "1");

      before = model.sol().tags();
      model.study(transientStudy).createAutoSequences("sol");
      String transientSolution = newest(model, before);
      SolverFeature timeSolver = model.sol(transientSolution).feature("t1");
      timeSolver.set("consistent", "off");
      timeSolver.set("initialstepbdfactive", "on");
      timeSolver.set("initialstepbdf", "1e-7");
      timeSolver.set("maxstepconstraintbdf", "const");
      timeSolver.set("maxstepbdf", "1e-5");
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
      model.save("342_lid8mm_stage181_micro_transient_setup_Model.mph");
      System.out.println("RUN_TRANSIENT181 " + transientSolution);
      model.sol(transientSolution).runAll();
      model.save("343_lid8mm_stage181_micro_transient_results_Model.mph");

      try {
        model.result().dataset().remove("dset181");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset181", "Solution");
      model.result().dataset("dset181").set("solution", transientSolution);
      try {
        model.result().numerical().remove("eval181");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval181", "EvalGlobal");
      model.result().numerical("eval181").set("data", "dset181");
      model.result().numerical("eval181").set(
          "expr",
          new String[] {
            "t", "phi_lid_structure", "Fn_contact119", "Wfilm177",
            "Ftotal177", "dr_indent119"
          });
      double[][] values = model.result().numerical("eval181").getReal();
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
      model.save("344_lid8mm_stage181_micro_transient_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
