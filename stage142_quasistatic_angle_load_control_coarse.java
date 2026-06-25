import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage142_quasistatic_angle_load_control_coarse {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "237_lid8mm_stage136_slow_motion_ramp_short_bridge_results_Model.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111", study = "std_qs142";
      String q = "q_force_total111";

      m.param().set("phi_qs142", "0[deg]",
          "Additional lid rotation for quasi-static scratch continuation");
      m.param().set("film_share_max120", "0.30");
      m.param().set("scale_partitioned_pfilm", "1");
      m.param().set("film_share_target147", "0.10",
          "Target normal-load fraction carried by moving tear-film footprint");
      m.param().set("A_mask_eps147", "1e-12[m^2]");
      m.component(c).variable(v).set("phi_lid_structure", "phi_qs142");
      m.component(c).variable("var_mixed_lub").set(
          "phi_lid_film_replay", "phi_qs142");
      m.component(c).variable(v).set("slide_fraction_structure",
          "min(1,max(0,phi_qs142/theta_slide_total))");
      m.component(c).variable(v).set("t_film_replay",
          "T_structure_pre+0.50[s]*slide_fraction_structure");
      m.component(c).variable(v).set("t_film_replay_lo",
          "min(0.53[s],max(0[s],0.01[s]*floor(t_film_replay/0.01[s])))");
      m.component(c).variable(v).set("t_film_replay_hi",
          "min(0.53[s],max(0[s],0.01[s]*ceil(t_film_replay/0.01[s])))");
      m.component(c).variable(v).set("alpha_film_replay",
          "(t_film_replay-t_film_replay_lo)"
              + "/max(0.01[s],t_film_replay_hi-t_film_replay_lo)");
      m.component(c).variable(v).set("A_lid_mask147",
          "intop_film(lid_mask)");
      m.component(c).variable(v).set("F_film_target147",
          "film_share_target147*F_total_target");
      m.component(c).variable(v).set("pfilm_replay53",
          "F_film_target147*lid_mask/max(A_lid_mask147,A_mask_eps147)");
      m.component(c).variable(v).set("W_film_replay53",
          "intop_film(pfilm_replay53)");
      m.component(c).variable(v).set("pfilm_ramp120", "1");
      m.component(c).variable(v).set("pfilm_cap120", "1");
      m.component(c).variable(v).set("scale_pfilm_effective120",
          "scale_partitioned_pfilm*pfilm_cap120");
      m.component(c).variable(v).set("Fn_contact119",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film119",
          "scale_pfilm_effective120*W_film_replay53");
      m.component(c).variable(v).set("Fn_total119", "Fn_contact119+Fn_film119");
      m.component(c).variable(v).set("Fn_error119",
          "(Fn_total119-F_total_target)/F_total_target");
      m.component(c).variable(v).set("dr_indent119", q + "*1[mm]");

      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1, "Fn_error119");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0",
          new String[]{
              "0",
              "Y*(cos(phi_qs142)-1)-Z*sin(phi_qs142)"
                  + "-dr_indent119*(Y*cos(phi_qs142)-Z*sin(phi_qs142))/sqrt(Y^2+Z^2)",
              "Y*sin(phi_qs142)+Z*(cos(phi_qs142)-1)"
                  + "-dr_indent119*(Y*sin(phi_qs142)+Z*cos(phi_qs142))/sqrt(Y^2+Z^2)"
          });
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea", new String[]{
              "-scale_pfilm_effective120*pfilm_replay53*nx",
              "-scale_pfilm_effective120*pfilm_replay53*ny",
              "-scale_pfilm_effective120*pfilm_replay53*nz"
          });
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "0");
      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "0.3");
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").active(false);
      } catch (Exception ignore) {}

      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 150 clean moving-film normal-load path");
      m.study(study).create("param", "Parametric");
      m.study(study).feature("param").set("pname", new String[]{"phi_qs142"});
      m.study(study).feature("param").set("plistarr",
          new String[]{"0 -5 -10 -15 -20 -25 -30 -35 -40 -45 -50 -55 -65 -69 -69.2 -69.4 -69.6 -69.8"});
      m.study(study).feature("param").set("punit", new String[]{"deg"});
      m.study(study).create("stat", "Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity", "on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      String step = study + "/stat";
      m.component(c).physics("solid").feature("dcnt1").set("StudyStep", step);
      m.component(c).physics("solid").feature("disp_lid_time").set("StudyStep", step);
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set("StudyStep", step);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newest(m, before);
      SolverFeature s1 = m.sol(sol).feature("s1");
      try { s1.feature().remove("se1"); } catch (Exception ignore) {}
      try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
      s1.create("fc1", "FullyCoupled");
      s1.feature("fc1").set("linsolver", "dDef");
      s1.feature("fc1").set("maxiter", 150);
      m.save("266_lid8mm_stage150_clean_moving_film_normal_path_setup_Model.mph");
      System.out.println("RUN_STAGE150 solver=" + sol);
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset142"); } catch (Exception ignore) {}
      m.result().dataset().create("dset142", "Solution");
      m.result().dataset("dset142").set("solution", sol);
      try { m.result().numerical().remove("eval142"); } catch (Exception ignore) {}
      m.result().numerical().create("eval142", "EvalGlobal");
      m.result().numerical("eval142").set("data", "dset142");
      m.result().numerical("eval142").set("expr", new String[]{
          "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
          "Fn_total119", "Fn_error119", "dr_indent119"});
      double[][] a = m.result().numerical("eval142").getReal();
      System.out.println("rows=" + a.length + " cols=" + a[0].length);
      for (int i = 0; i < a.length; i++) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (double x : a[i]) if (!Double.isNaN(x)) {
          min = Math.min(min, x); max = Math.max(max, x);
        }
        System.out.println("expr" + i + " min=" + min + " max=" + max);
      }
      m.save("267_lid8mm_stage150_clean_moving_film_normal_path_results_Model.mph");
      System.out.println("SAVED_STAGE150");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
