import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage141_reconnecting_contact_static_then_dynamic {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static void fullyCoupled(Model m, String sol, String node) {
    SolverFeature f = m.sol(sol).feature(node);
    try { f.feature().remove("se1"); } catch (Exception ignore) {}
    try { f.feature().remove("fc1"); } catch (Exception ignore) {}
    f.create("fc1", "FullyCoupled");
    f.feature("fc1").set("linsolver", "dDef");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "237_lid8mm_stage136_slow_motion_ramp_short_bridge_results_Model.mph");
      String c = "comp1", ge = "ge_force_total111";
      String stat = "std_reconnect_pre141";
      String dyn = "std_reconnect_dyn141";

      m.param().set("T_structure_pre", "0.03[s]");
      m.param().set("T_structure_slide", "0.50[s]");
      m.param().set("T_speed_ramp", "0.10[s]");
      m.param().set("T_replay_release133", "1.0[s]");
      m.param().set("film_share_max120", "0.30");
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "1");
      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "0.3");
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").active(false);
      } catch (Exception ignore) {}

      try { m.study().remove(stat); } catch (Exception ignore) {}
      m.study().create(stat);
      m.study(stat).label("Stage 141 reconnecting-contact static load equilibrium");
      m.study(stat).create("stat", "Stationary");
      m.study(stat).feature("stat").set("geometricNonlinearity", "on");
      m.study(stat).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      String statStep = stat + "/stat";
      m.component(c).physics("solid").feature("dcnt1").set("StudyStep", statStep);
      m.component(c).physics("solid").feature("disp_lid_time").set("StudyStep", statStep);
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set("StudyStep", statStep);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", statStep);

      String[] beforeStat = m.sol().tags();
      m.study(stat).createAutoSequences("sol");
      String solStat = newest(m, beforeStat);
      fullyCoupled(m, solStat, "s1");
      System.out.println("RUN_STAGE141_STATIC solver=" + solStat);
      m.sol(solStat).runAll();

      try { m.study().remove(dyn); } catch (Exception ignore) {}
      m.study().create(dyn);
      m.study(dyn).label("Stage 141 reconnecting-contact dynamic test");
      m.study(dyn).create("time", "Transient");
      m.study(dyn).feature("time").set("tlist", "range(0,0.0005,0.12)");
      m.study(dyn).feature("time").set("geometricNonlinearity", "on");
      m.study(dyn).feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study(dyn).feature("time").set("useinitsol", "on");
      m.study(dyn).feature("time").set("initmethod", "sol");
      m.study(dyn).feature("time").set("initsol", solStat);
      m.study(dyn).feature("time").set("initsoluse", solStat);
      m.study(dyn).feature("time").set("initsolusesolnum", "last");
      String dynStep = dyn + "/time";
      m.component(c).physics("solid").feature("dcnt1").set("StudyStep", dynStep);
      m.component(c).physics("solid").feature("disp_lid_time").set("StudyStep", dynStep);
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set("StudyStep", dynStep);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", dynStep);

      String[] beforeDyn = m.sol().tags();
      m.study(dyn).createAutoSequences("sol");
      String solDyn = newest(m, beforeDyn);
      m.sol(solDyn).feature("t1").set("consistent", "on");
      fullyCoupled(m, solDyn, "t1");

      m.save("248_lid8mm_stage141_reconnect_static_then_dynamic_setup_Model.mph");
      System.out.println("RUN_STAGE141_DYNAMIC solver=" + solDyn);
      m.sol(solDyn).runAll();

      try { m.result().dataset().remove("dset141"); } catch (Exception ignore) {}
      m.result().dataset().create("dset141", "Solution");
      m.result().dataset("dset141").set("solution", solDyn);
      try { m.result().numerical().remove("eval141"); } catch (Exception ignore) {}
      m.result().numerical().create("eval141", "EvalGlobal");
      m.result().numerical("eval141").set("data", "dset141");
      m.result().numerical("eval141").set("expr", new String[]{
          "Fn_contact119", "Fn_film119", "Fn_total119", "F_total_target",
          "Fn_error119", "dr_indent119", "slide_fraction_structure"});
      double[][] a = m.result().numerical("eval141").getReal();
      System.out.println("rows=" + a.length + " cols=" + a[0].length);
      m.save("249_lid8mm_stage141_reconnect_static_then_dynamic_results_Model.mph");
      System.out.println("SAVED_STAGE141");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
