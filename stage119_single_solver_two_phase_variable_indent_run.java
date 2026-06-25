import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage119_single_solver_two_phase_variable_indent_run {
  private static void printRange(Model m, String tag) {
    double[][] vals = m.result().numerical(tag).getReal();
    System.out.println(tag + " rows=" + vals.length + " cols=" + vals[0].length);
    for (int i = 0; i < vals.length; i++) {
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double x : vals[i]) {
        if (!Double.isNaN(x)) {
          min = Math.min(min, x);
          max = Math.max(max, x);
        }
      }
      System.out.println("  expr" + i + " min=" + min + " max=" + max);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "237_lid8mm_stage136_slow_motion_ramp_short_bridge_results_Model.mph");
      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111";
      String q = "q_force_total111";
      String study = "std_shear_feedback73";
      String sol = "sol22";

      m.param().set("scale_partitioned_pfilm", "1.0");
      m.param().set("T_pre", "0.03[s]",
          "Synchronized preload hold for motion, film replay, and load control");
      m.param().set("T_structure_pre", "0.03[s]",
          "Extended stationary preload and load-control stabilization period");
      m.param().set("T_structure_slide", "5.0[s]",
          "Numerical pseudo-time duration for full -35 to +35 degree continuation");
      m.param().set("T_speed_ramp", "0.50[s]",
          "Smooth acceleration and deceleration duration");
      m.param().set("q_force_total111_init", "0.02",
          "Initial indentation estimate for algebraic total-load control");
      m.param().set("tau_pre119", "2e-3[s]",
          "Regularized preload controller time constant");
      m.param().set("tau_dyn119", "2e-3[s]",
          "Dynamic controller time constant");
      m.param().set("T_force_transition119", "0.01[s]",
          "Smooth transition of load-controller response before scratching");
      m.param().set("q_scale119", "0.01");
      m.param().set("T_pfilm_start120", "0.01[s]",
          "Start time for smooth local film-pressure continuation");
      m.param().set("T_pfilm_full120", "0.025[s]",
          "Time at which local film-pressure continuation reaches full level");
      m.param().set("film_share_max120", "0.30",
          "Maximum fraction of the 0.03 N target carried by replayed film pressure");
      m.param().set("W_eps120", "1e-6[N]",
          "Regularization load for film-pressure load-share cap");
      m.param().set("T_replay_release133", "6.0[s]",
          "Hold film-pressure footprint fixed through full structural continuation");
      m.param().set("T_replay_ramp134", "0.02[s]",
          "Smooth acceleration duration for film-pressure replay");

      m.component(c).variable(v).set("force_transition119",
          "if(t<=T_pre,0,if(t>=T_pre+T_force_transition119,1,"
              + "0.5-0.5*cos(pi*(t-T_pre)/T_force_transition119)))");
      m.component(c).variable(v).set("t_film_replay",
          "min(0.53[s],if(t<=T_replay_release133,T_pfilm_full120,"
              + "if(t<T_replay_release133+T_replay_ramp134,"
              + "T_pfilm_full120+0.5*(t-T_replay_release133)"
              + "-T_replay_ramp134/(2*pi)*sin(pi*(t-T_replay_release133)"
              + "/T_replay_ramp134),"
              + "T_pfilm_full120+t-T_replay_release133-0.5*T_replay_ramp134)))");
      m.component(c).variable(v).set("alpha_pfilm_smooth134",
          "min(1,max(0,(t_film_replay-0.02[s])/0.02[s]))");
      m.component(c).variable(v).set("pfilm_replay53",
          "(1-alpha_pfilm_smooth134)*withsol('sol21',max(pfilm,0),"
              + "setval(t_replay,0.02[s]))"
              + "+alpha_pfilm_smooth134*withsol('sol21',max(pfilm,0),"
              + "setval(t_replay,0.04[s]))");
      m.component(c).variable(v).set("W_film_replay53",
          "(1-alpha_pfilm_smooth134)*W_film_sched54(0.02[s])"
              + "+alpha_pfilm_smooth134*W_film_sched54(0.04[s])");
      m.component(c).variable(v).set("tau_effective119",
          "tau_pre119+(tau_dyn119-tau_pre119)*force_transition119");
      m.component(c).variable(v).set("Fn_contact119",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("pfilm_ramp120",
          "if(t<=T_pfilm_start120,0,if(t>=T_pfilm_full120,1,"
              + "0.5-0.5*cos(pi*(t-T_pfilm_start120)"
              + "/(T_pfilm_full120-T_pfilm_start120))))");
      m.component(c).variable(v).set("pfilm_cap120",
          "min(1,film_share_max120*F_total_target/max(W_film_replay53,W_eps120))");
      m.component(c).variable(v).set("scale_pfilm_effective120",
          "scale_partitioned_pfilm*pfilm_ramp120*pfilm_cap120");
      m.component(c).variable(v).set("Fn_film119",
          "scale_pfilm_effective120*W_film_replay53");
      m.component(c).variable(v).set("Fn_total119", "Fn_contact119+Fn_film119");
      m.component(c).variable(v).set("Fn_error119",
          "(Fn_total119-F_total_target)/F_total_target");
      m.component(c).variable(v).set("dr_indent119", q + "*1[mm]");

      m.component(c).physics(ge).label(
          "Algebraic solved indentation for total 0.03 N normal load");
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1,
          "Fn_error119");
      m.component(c).physics(ge).feature("ge1").set(
          "StudyStep", "std_shear_feedback73/time");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent119*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent119*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "0");
      m.component(c).physics("solid").feature("dcnt1").set("useCutback", "1");
      m.component(c).physics("solid").feature("dcnt1").set(
          "ContactMethodCtrl", "Penalty");
      m.component(c).physics("solid").feature("dcnt1").set(
          "penaltyFunction", "ramp");
      m.component(c).physics("solid").feature("dcnt1").set(
          "viscousTunedFor", "Speed");
      m.component(c).physics("solid").feature("dcnt1").set(
          "SolutionMethod", "segregated");
      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "0.3");
      m.component(c).physics("solid").prop("StructuralTransientBehavior").set(
          "StructuralTransientBehavior", "Quasistatic");
      m.component(c).physics("solid").feature("dcnt1").set(
          "StudyStep", "std_shear_feedback73/time");
      m.component(c).physics("solid").feature("disp_lid_time").set(
          "StudyStep", "std_shear_feedback73/time");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "StudyStep", "std_shear_feedback73/time");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea", new String[]{
              "-scale_pfilm_effective120*pfilm_replay53*nx",
              "-scale_pfilm_effective120*pfilm_replay53*ny",
              "-scale_pfilm_effective120*pfilm_replay53*nz"
          });
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").active(false);
      } catch (Exception ignore) {}
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);

      m.study(study).feature("time").set("tlist", "range(0,0.01,5.05)");
      m.study(study).feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.sol(sol).feature("t1").set("tlist", "range(0,0.01,5.05)");
      m.sol(sol).feature("t1").set("consistent", "on");
      try { m.sol(sol).feature("t1").feature().remove("se1"); } catch (Exception ignore) {}
      try { m.sol(sol).feature("t1").feature().remove("fc1"); } catch (Exception ignore) {}
      m.sol(sol).feature("t1").create("fc1", "FullyCoupled");
      m.sol(sol).feature("t1").feature("fc1").set("linsolver", "dDef");

      m.save("246_lid8mm_stage140_full_angle_pseudotime_frozen_film_setup_Model.mph");
      System.out.println("Saved setup: 246_lid8mm_stage140_full_angle_pseudotime_frozen_film_setup_Model.mph");
      System.out.println("RUN_STAGE140 solver=sol22");
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset119_dynamic"); } catch (Exception ignore) {}
      m.result().dataset().create("dset119_dynamic", "Solution");
      m.result().dataset("dset119_dynamic").set("solution", sol);
      m.result().dataset("dset119_dynamic").label(
          "Stage 119 two-phase variable-indentation total-load result");
      String[] expr = {"Fn_contact119", "Fn_film119", "Fn_total119",
          "F_total_target", "Fn_error119", "dr_indent119", q + "t",
          "tau_effective119", "force_transition119", "pfilm_ramp120",
          "pfilm_cap120", "scale_pfilm_effective120"};
      String[] unit = {"N", "N", "N", "N", "1", "mm", "1/s", "s", "1",
          "1", "1", "1"};
      try { m.result().remove("pg119_total_load_indent"); } catch (Exception ignore) {}
      m.result().create("pg119_total_load_indent", "PlotGroup1D");
      m.result("pg119_total_load_indent").set("data", "dset119_dynamic");
      m.result("pg119_total_load_indent").label(
          "Total load, load sharing, and solved indentation");
      m.result("pg119_total_load_indent").feature().create("glob1", "Global");
      m.result("pg119_total_load_indent").feature("glob1").set("expr", expr);
      m.result("pg119_total_load_indent").feature("glob1").set("unit", unit);
      try { m.result().numerical().remove("eval119_total_load_indent"); } catch (Exception ignore) {}
      m.result().numerical().create("eval119_total_load_indent", "EvalGlobal");
      m.result().numerical("eval119_total_load_indent").set("data", "dset119_dynamic");
      m.result().numerical("eval119_total_load_indent").set("expr", expr);
      m.result().numerical("eval119_total_load_indent").set("unit", unit);
      printRange(m, "eval119_total_load_indent");

      m.save("247_lid8mm_stage140_full_angle_pseudotime_frozen_film_results_Model.mph");
      System.out.println("Saved result: 247_lid8mm_stage140_full_angle_pseudotime_frozen_film_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
