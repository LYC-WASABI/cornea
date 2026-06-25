import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage152_fullpath_shear_feedback {
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
          "268_lid8mm_stage150_verified_normal_path_Model.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111", study = "std_full_shear152";

      m.param().set("mu_target152", "0.1");
      m.param().set("F_shear_target152", "mu_target152*F_total_target");
      m.param().set("shear_scale152", "1");
      m.component(c).variable(v).set("F_shear_feedback73",
          "shear_scale152*F_shear_target152");
      m.component(c).variable(v).set("tau_nominal_shear73",
          "F_shear_feedback73/A_contact_nominal73");
      m.component(c).variable(v).set("pfilm_weight_shear73",
          "max(pfilm_replay53,0)/max(W_film_replay53,W_eps_shear73)");
      m.component(c).variable(v).set("tau_pfilm_shear73",
          "F_shear_feedback73*pfilm_weight_shear73");
      m.component(c).variable(v).set("mu_shear_feedback73",
          "F_shear_feedback73/Fn_total119");

      m.component(c).physics("solid").feature("load_shear_cornea73").active(true);
      m.component(c).physics("solid").feature("load_shear_lid73").active(true);
      m.component(c).physics("solid").feature("load_shear_cornea73").set(
          "FperArea", new String[]{
              "0", "tau_pfilm_shear73*ty_shear73",
              "tau_pfilm_shear73*tz_shear73"
          });
      m.component(c).physics("solid").feature("load_shear_lid73").set(
          "FperArea", new String[]{
              "0", "-tau_nominal_shear73*ty_shear73",
              "-tau_nominal_shear73*tz_shear73"
          });

      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 152 full path with mixed shear feedback");
      m.study(study).create("param", "Parametric");
      m.study(study).feature("param").set("pname", new String[]{"phi_qs142"});
      m.study(study).feature("param").set("plistarr", new String[]{
          "0 -5 -10 -15 -20 -25 -30 -35 -40 -45 -50 -55 -65"
              + " -69 -69.2 -69.4 -69.6 -69.8 -70"
      });
      m.study(study).feature("param").set("punit", new String[]{"deg"});
      m.study(study).create("stat", "Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity", "on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      String step = study + "/stat";
      for (String tag : new String[]{
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm",
          "load_shear_cornea73", "load_shear_lid73"}) {
        m.component(c).physics("solid").feature(tag).set("StudyStep", step);
      }
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newest(m, before);
      SolverFeature s1 = m.sol(sol).feature("s1");
      try { s1.feature().remove("se1"); } catch (Exception ignore) {}
      try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
      s1.create("fc1", "FullyCoupled");
      s1.feature("fc1").set("linsolver", "dDef");
      s1.feature("fc1").set("maxiter", 200);
      m.save("271_lid8mm_stage152_fullpath_shear_feedback_setup_Model.mph");
      System.out.println("RUN_STAGE152 solver=" + sol);
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset152"); } catch (Exception ignore) {}
      m.result().dataset().create("dset152", "Solution");
      m.result().dataset("dset152").set("solution", sol);
      try { m.result().numerical().remove("eval152"); } catch (Exception ignore) {}
      m.result().numerical().create("eval152", "EvalGlobal");
      m.result().numerical("eval152").set("data", "dset152");
      m.result().numerical("eval152").set("expr", new String[]{
          "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
          "Fn_total119", "Fn_error119", "dr_indent119",
          "F_shear_feedback73", "mu_shear_feedback73"
      });
      double[][] a = m.result().numerical("eval152").getReal();
      String[] n = {"phi", "time", "Fcontact", "Ffilm", "Ftotal",
          "err", "indent", "Fshear", "mu"};
      for (int j = 0; j < a[0].length; j++) {
        StringBuilder b = new StringBuilder("row=" + j);
        for (int i = 0; i < a.length; i++)
          b.append(" ").append(n[i]).append("=")
              .append(String.format(Locale.US, "%.9g", a[i][j]));
        System.out.println(b);
      }
      m.save("272_lid8mm_stage152_fullpath_shear_feedback_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
