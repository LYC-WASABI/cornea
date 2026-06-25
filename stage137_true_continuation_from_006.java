import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage137_true_continuation_from_006 {
  private static String newestSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String newest = "";
    for (String tag : m.sol().tags()) {
      newest = tag;
      if (!old.contains(tag)) return tag;
    }
    return newest;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "237_lid8mm_stage136_slow_motion_ramp_short_bridge_results_Model.mph");
      String c = "comp1";
      String study = "std_cont137";
      String step = study + "/time";
      String ge = "ge_force_total111";

      m.param().set("T_replay_release133", "0.13[s]",
          "Frozen film footprint through the 0.12 s continuation");
      m.param().set("T_speed_ramp", "0.10[s]");
      m.param().set("film_share_max120", "0.30");

      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 137 true continuation from 0.06 to 0.12 s");
      m.study(study).create("time", "Transient");
      m.study(study).feature("time").set("tlist", "range(0.0605,0.0005,0.12)");
      m.study(study).feature("time").set("geometricNonlinearity", "on");
      m.study(study).feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study(study).feature("time").set("useinitsol", "on");
      m.study(study).feature("time").set("initmethod", "sol");
      m.study(study).feature("time").set("initsol", "sol22");
      m.study(study).feature("time").set("initsoluse", "sol22");
      m.study(study).feature("time").set("initsolusesolnum", "last");

      m.component(c).physics("solid").feature("dcnt1").set("StudyStep", step);
      m.component(c).physics("solid").feature("disp_lid_time").set("StudyStep", step);
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set("StudyStep", step);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").active(false);
      } catch (Exception ignore) {}
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newestSolution(m, before);
      SolverFeature t1 = m.sol(sol).feature("t1");
      t1.set("consistent", "off");
      try { t1.feature().remove("se1"); } catch (Exception ignore) {}
      try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
      t1.create("fc1", "FullyCoupled");
      t1.feature("fc1").set("linsolver", "dDef");

      m.save("240_lid8mm_stage137_true_continuation_006_to_012_setup_Model.mph");
      System.out.println("RUN_STAGE137_CONT solver=" + sol);
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset137_cont"); } catch (Exception ignore) {}
      m.result().dataset().create("dset137_cont", "Solution");
      m.result().dataset("dset137_cont").set("solution", sol);
      m.result().dataset("dset137_cont").label("Stage 137 continuation 0.06 to 0.12 s");
      try { m.result().numerical().remove("eval137_cont"); } catch (Exception ignore) {}
      m.result().numerical().create("eval137_cont", "EvalGlobal");
      m.result().numerical("eval137_cont").set("data", "dset137_cont");
      m.result().numerical("eval137_cont").set("expr", new String[]{
          "Fn_contact119", "Fn_film119", "Fn_total119", "F_total_target",
          "Fn_error119", "dr_indent119", "slide_fraction_structure"});
      m.result().numerical("eval137_cont").set("unit",
          new String[]{"N", "N", "N", "N", "1", "mm", "1"});
      double[][] values = m.result().numerical("eval137_cont").getReal();
      System.out.println("rows=" + values.length + " cols=" + values[0].length);
      for (int i = 0; i < values.length; i++) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (double x : values[i]) if (!Double.isNaN(x)) {
          min = Math.min(min, x);
          max = Math.max(max, x);
        }
        System.out.println("expr" + i + " min=" + min + " max=" + max);
      }
      m.save("241_lid8mm_stage137_true_continuation_006_to_012_results_Model.mph");
      System.out.println("SAVED_STAGE137_CONT");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
