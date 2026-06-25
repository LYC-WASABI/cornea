import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage112_113_114_continuation_stabilized_run {
  private static String lastSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }
  private static void rmResult(Model m, String tag) { try { m.result().remove(tag); } catch (Exception ignore) {} }
  private static void rmNum(Model m, String tag) { try { m.result().numerical().remove(tag); } catch (Exception ignore) {} }
  private static void setupFullyCoupled(Model m, String solTag, String label) {
    SolverFeature t1 = m.sol(solTag).feature("t1");
    try { t1.feature().remove("se1"); } catch (Exception ignore) {}
    try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
    t1.create("fc1", "FullyCoupled");
    t1.feature("fc1").label(label);
    t1.feature("fc1").set("linsolver", "dDef");
  }
  private static void eval(Model m, String tag, String label, String dset, String[] expr, String[] unit) {
    rmNum(m, tag);
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).label(label);
    m.result().numerical(tag).set("data", dset);
    m.result().numerical(tag).set("expr", expr);
    m.result().numerical(tag).set("unit", unit);
  }
  private static void plot(Model m, String tag, String label, String dset, String[] expr, String[] unit) {
    rmResult(m, tag);
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
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
  private static boolean runStage(Model m, String id, String label, String filmScale, String penaltyScale, String tEnd) {
    String c = "comp1", v = "var_partitioned_local_pfilm";
    String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
    String fnContact = "intop_contact(" + pContact + ")";
    String ge = "ge_force_total" + id;
    String q = "q_force_total" + id;
    String dr = "dr_force_total" + id;
    String fc = "Fn_contact" + id;
    String ff = "Fn_film_applied" + id;
    String ft = "Fn_total" + id;
    String er = "Fn_total_error" + id;
    try {
      System.out.println("RUN_STAGE" + id + " filmScale=" + filmScale + " penaltyScale=" + penaltyScale + " tEnd=" + tEnd);
      m.param().set("scale_partitioned_pfilm", filmScale);
      m.param().set("q_force_total" + id + "_init", "0.02");
      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", penaltyScale);
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", penaltyScale);
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", penaltyScale);
      try {
        m.component(c).physics("solid").feature("dcnt1").feature("fric_partitioned_stabilizer").set("ft_penalty", penaltyScale);
      } catch (Exception ignore) {}
      m.component(c).variable(v).set(dr, q + "*1[mm]");
      m.component(c).variable(v).set(fc, fnContact);
      m.component(c).variable(v).set(ff, "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set(ft, fc + "+" + ff);
      m.component(c).variable(v).set(er, "(" + ft + "-F_total_target)/F_total_target");
      try { m.component(c).physics().remove(ge); } catch (Exception ignore) {}
      m.component(c).physics().create(ge, "GlobalEquations");
      m.component(c).physics(ge).label(label);
      m.component(c).physics(ge).feature("ge1").set("name", 1, 1, q);
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1, "(" + ft + "-F_total_target)/F_total_target");
      m.component(c).physics(ge).feature("ge1").set("initialValueU", 1, 1, "q_force_total" + id + "_init");
      m.component(c).physics(ge).feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-" + dr + "*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-" + dr + "*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      String[] activate = new String[]{"solid", "on", "tff", "off", "ge_force_total111", "off", ge, "on"};
      m.study("std_shear_feedback73").feature("time").set("activate", activate);
      m.study("std_shear_feedback73").feature("time").set("tlist", "range(0,0.001," + tEnd + ")");
      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      setupFullyCoupled(m, solTag, label + " fully coupled");
      m.sol(solTag).runAll();
      String dset = "dset" + id + "_continuation";
      try { m.result().dataset().remove(dset); } catch (Exception ignore) {}
      m.result().dataset().create(dset, "Solution");
      m.result().dataset(dset).label("Stage " + id + " stabilized continuation");
      m.result().dataset(dset).set("solution", solTag);
      String[] expr = new String[]{fc, ff, ft, "F_total_target", er, dr};
      String[] unit = new String[]{"N", "N", "N", "N", "1", "mm"};
      plot(m, "pg" + id + "_total_load", "Stage " + id + " stabilized total-load control", dset, expr, unit);
      eval(m, "eval" + id + "_total_load", "Stage " + id + " stabilized total-load values", dset, expr, unit);
      printRange(m, "eval" + id + "_total_load");
      m.save(("19" + (Integer.parseInt(id) - 112)) + "_lid8mm_stage" + id + "_stabilized_continuation_results_Model.mph");
      return true;
    } catch (Exception e) {
      System.out.println("STAGE" + id + "_FAILED: " + e.getMessage());
      try { m.save(("19" + (Integer.parseInt(id) - 112)) + "_lid8mm_stage" + id + "_stabilized_continuation_failed_setup_Model.mph"); } catch (Exception ignore) {}
      e.printStackTrace();
      return false;
    }
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      boolean ok112 = runStage(m, "112", "Stage 112 weak contact low film-pressure total-load control", "0.1", "0.1", "0.02");
      if (!ok112) { ModelUtil.disconnect(); System.exit(2); }
      boolean ok113 = runStage(m, "113", "Stage 113 medium contact medium film-pressure total-load control", "0.5", "0.3", "0.025");
      if (!ok113) { ModelUtil.disconnect(); System.exit(3); }
      boolean ok114 = runStage(m, "114", "Stage 114 restored contact and full film-pressure total-load control", "1.0", "1.0", "0.03");
      if (!ok114) { ModelUtil.disconnect(); System.exit(4); }
      m.save("192_lid8mm_stage114_full_restored_stabilized_total_load_results_Model.mph");
      System.out.println("Saved final local: 192_lid8mm_stage114_full_restored_stabilized_total_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
