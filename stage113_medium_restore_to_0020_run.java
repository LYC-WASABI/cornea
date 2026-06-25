import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage113_medium_restore_to_0020_run {
  private static String lastSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }
  private static void removeOldGlobalEquations(Model m) {
    for (String tag : m.component("comp1").physics().tags()) {
      if (tag.startsWith("ge_force_total")) {
        try { m.component("comp1").physics().remove(tag); } catch (Exception ignore) {}
      }
    }
  }
  private static void setupFullyCoupled(Model m, String solTag) {
    SolverFeature t1 = m.sol(solTag).feature("t1");
    try { t1.feature().remove("se1"); } catch (Exception ignore) {}
    try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
    t1.create("fc1", "FullyCoupled");
    t1.feature("fc1").label("Stage 113 medium restore to 0.020 s fully coupled");
    t1.feature("fc1").set("linsolver", "dDef");
  }
  private static void printRange(Model m, String tag) {
    double[][] vals = m.result().numerical(tag).getReal();
    System.out.println(tag + " rows=" + vals.length + " cols=" + vals[0].length);
    for (int i = 0; i < vals.length; i++) {
      double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
      int imin = -1, imax = -1;
      for (int j = 0; j < vals[i].length; j++) {
        double v = vals[i][j];
        if (!Double.isNaN(v)) {
          if (v < min) { min = v; imin = j; }
          if (v > max) { max = v; imax = j; }
        }
      }
      System.out.println("  expr" + i + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
    }
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "190_lid8mm_stage112_stabilized_continuation_results_Model.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      removeOldGlobalEquations(m);
      m.param().set("scale_partitioned_pfilm", "0.5");
      m.param().set("q_force_total113_init", "0.02");
      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "0.3");
      try { m.component(c).physics("solid").feature("dcnt1").feature("fric_partitioned_stabilizer").set("ft_penalty", "0.3"); } catch (Exception ignore) {}
      m.component(c).variable(v).set("dr_force_total113", "q_force_total113*1[mm]");
      m.component(c).variable(v).set("Fn_contact113", "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film_applied113", "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total113", "Fn_contact113+Fn_film_applied113");
      m.component(c).variable(v).set("Fn_total_error113", "(Fn_total113-F_total_target)/F_total_target");
      m.component(c).physics().create("ge_force_total113", "GlobalEquations");
      m.component(c).physics("ge_force_total113").label("Stage 113 medium restored total-load control to 0.020 s");
      m.component(c).physics("ge_force_total113").feature("ge1").set("name", 1, 1, "q_force_total113");
      m.component(c).physics("ge_force_total113").feature("ge1").set("equation", 1, 1, "(Fn_total113-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total113").feature("ge1").set("initialValueU", 1, 1, "q_force_total113_init");
      m.component(c).physics("ge_force_total113").feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total113*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total113*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate", new String[]{"solid", "on", "tff", "off", "ge_force_total113", "on"});
      m.study("std_shear_feedback73").feature("time").set("tlist", "range(0,0.001,0.02)");
      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      setupFullyCoupled(m, solTag);
      System.out.println("RUN_STAGE113_MEDIUM_TO_0020 solver=" + solTag);
      m.sol(solTag).runAll();
      try { m.result().dataset().remove("dset113_medium_to_0020"); } catch (Exception ignore) {}
      m.result().dataset().create("dset113_medium_to_0020", "Solution");
      m.result().dataset("dset113_medium_to_0020").label("Stage 113 medium restore to 0.020 s");
      m.result().dataset("dset113_medium_to_0020").set("solution", solTag);
      String[] expr = {"Fn_contact113", "Fn_film_applied113", "Fn_total113", "F_total_target", "Fn_total_error113", "dr_force_total113"};
      String[] unit = {"N", "N", "N", "N", "1", "mm"};
      try { m.result().remove("pg113_medium_to_0020_total_load"); } catch (Exception ignore) {}
      m.result().create("pg113_medium_to_0020_total_load", "PlotGroup1D");
      m.result("pg113_medium_to_0020_total_load").label("Stage 113 medium restore total-load control");
      m.result("pg113_medium_to_0020_total_load").set("data", "dset113_medium_to_0020");
      m.result("pg113_medium_to_0020_total_load").feature().create("glob1", "Global");
      m.result("pg113_medium_to_0020_total_load").feature("glob1").set("expr", expr);
      m.result("pg113_medium_to_0020_total_load").feature("glob1").set("unit", unit);
      try { m.result().numerical().remove("eval113_medium_to_0020_total_load"); } catch (Exception ignore) {}
      m.result().numerical().create("eval113_medium_to_0020_total_load", "EvalGlobal");
      m.result().numerical("eval113_medium_to_0020_total_load").set("data", "dset113_medium_to_0020");
      m.result().numerical("eval113_medium_to_0020_total_load").set("expr", expr);
      m.result().numerical("eval113_medium_to_0020_total_load").set("unit", unit);
      printRange(m, "eval113_medium_to_0020_total_load");
      m.save("191_lid8mm_stage113_medium_restore_to_0020_results_Model.mph");
      System.out.println("Saved local: 191_lid8mm_stage113_medium_restore_to_0020_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
