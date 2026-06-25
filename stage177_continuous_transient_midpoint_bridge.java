import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage177_continuous_transient_midpoint_bridge {
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
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      String comp = "comp1";
      String mixedVars = "var_mixed_lub";
      String pressureVars = "var_partitioned_local_pfilm";
      String globalEq = "ge_force_total111";

      model.param().set("film_share177", "0.95");
      model.param().set("T_speed_ramp", "0.05[s]");
      model.param().set(
          "omega_slide_const",
          "theta_slide_total/(T_structure_slide-T_speed_ramp)");

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
      model.component(comp).variable(pressureVars).set(
          "t_film_replay",
          "min(T_structure_pre+T_structure_slide+T_structure_hold,max(0[s],t))");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_lid_structure");

      model.component(comp).variable(pressureVars).set(
          "pfilm177",
          "film_share177*F_total_target*lid_mask/max(A_lid_mask147,A_mask_eps147)");
      model.component(comp).variable(pressureVars).set(
          "Wfilm177", "intop_film(pfilm177)");
      model.component(comp).variable(pressureVars).set(
          "Ftotal177", "Fn_contact119+Wfilm177");
      model.component(comp).variable(pressureVars).set(
          "Ferr177",
          "(Ftotal177-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).variable(pressureVars).set(
          "dr_indent119",
          "2*d_indent_bound154/pi*atan(pi*q_force_total111/(2*q_indent_scale154))");

      model.component(comp).physics("solid").feature("disp_lid_time").set(
          "U0",
          new String[] {
            "0",
            "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
                + "-dr_indent119*(Y*cos(phi_lid_structure)"
                + "-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
                + "-dr_indent119*(Y*sin(phi_lid_structure)"
                + "+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
          });
      model.component(comp).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",
          new String[] {"-pfilm177*nx", "-pfilm177*ny", "-pfilm177*nz"});
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr177");
      try {
        model.component(comp).physics("solid").feature("load_shear_cornea73")
            .active(false);
        model.component(comp).physics("solid").feature("load_shear_lid73")
            .active(false);
      } catch (Exception ignored) {
      }

      String study = "std_bridge177";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label("Stage 177 continuous transient midpoint bridge");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set("tlist", "range(0.28,0.001,0.30)");
      model.study(study).feature("time").set("geometricNonlinearity", "on");
      model.study(study).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", "sol42");
      model.study(study).feature("time").set("initsoluse", "sol42");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      String step = study + "/time";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set("StudyStep", step);
      }
      model.component(comp).physics(globalEq).feature("ge1").set("StudyStep", step);

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature timeSolver = model.sol(solution).feature("t1");
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
      model.save("326_lid8mm_stage177_continuous_midpoint_bridge_setup_Model.mph");
      System.out.println("RUN_STAGE177 " + solution);
      model.sol(solution).runAll();
      model.save("327_lid8mm_stage177_continuous_midpoint_bridge_results_Model.mph");

      try {
        model.result().dataset().remove("dset177");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset177", "Solution");
      model.result().dataset("dset177").set("solution", solution);
      try {
        model.result().numerical().remove("eval177");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval177", "EvalGlobal");
      model.result().numerical("eval177").set("data", "dset177");
      model.result().numerical("eval177").set(
          "expr",
          new String[] {
            "t",
            "phi_lid_structure",
            "Fn_contact119",
            "Wfilm177",
            "Ftotal177",
            "dr_indent119"
          });
      double[][] values = model.result().numerical("eval177").getReal();
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
      model.save("328_lid8mm_stage177_continuous_midpoint_bridge_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
