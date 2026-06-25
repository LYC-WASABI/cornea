import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage111_short_strict_q02_run {
  private static String lastSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }
  private static void removeResult(Model m, String tag) { try { m.result().remove(tag); } catch (Exception ignore) {} }
  private static void removeNumerical(Model m, String tag) { try { m.result().numerical().remove(tag); } catch (Exception ignore) {} }
  private static void setupFullyCoupled(Model m, String solTag) {
    SolverFeature t1 = m.sol(solTag).feature("t1");
    try { t1.feature().remove("se1"); } catch (Exception ignore) {}
    try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
    t1.create("fc1", "FullyCoupled");
    t1.feature("fc1").label("Fully coupled strict short total-load equation");
    t1.feature("fc1").set("linsolver", "dDef");
  }
  private static void addGlobalPlot(Model m, String tag, String label, String dset, String[] expr, String[] unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }
  private static void addEval(Model m, String tag, String label, String dset, String[] expr, String[] unit) {
    removeNumerical(m, tag);
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).label(label);
    m.result().numerical(tag).set("data", dset);
    m.result().numerical(tag).set("expr", expr);
    m.result().numerical(tag).set("unit", unit);
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
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String fnContact = "intop_contact(" + pContact + ")";
      m.param().set("scale_partitioned_pfilm", "1.0");
      m.param().set("q_force_total111_init", "0.02", "Robust overclosed initial q for strict short total-load control");
      m.component(c).variable(v).set("dr_force_total111", "q_force_total111*1[mm]");
      m.component(c).variable(v).set("Fn_contact111", fnContact);
      m.component(c).variable(v).set("Fn_film_applied111", "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total111", "Fn_contact111+Fn_film_applied111");
      m.component(c).variable(v).set("Fn_total_error111", "(Fn_total111-F_total_target)/F_total_target");
      try { m.component(c).physics().remove("ge_force_total111"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total111", "GlobalEquations");
      m.component(c).physics("ge_force_total111").label("Stage 111 strict short total normal load control");
      m.component(c).physics("ge_force_total111").feature("ge1").set("name", 1, 1, "q_force_total111");
      m.component(c).physics("ge_force_total111").feature("ge1").set("equation", 1, 1,
          "(Fn_total111-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total111").feature("ge1").set("initialValueU", 1, 1, "q_force_total111_init");
      m.component(c).physics("ge_force_total111").feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total111*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total111*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", "ge_force_total111", "on"});
      m.study("std_shear_feedback73").feature("time").set("tlist", "range(0,0.001,0.01)");
      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      setupFullyCoupled(m, solTag);
      System.out.println("RUN_STAGE111_SHORT_STRICT_Q02 solver=" + solTag);
      m.sol(solTag).runAll();
      try { m.result().dataset().remove("dset111_short_strict"); } catch (Exception ignore) {}
      m.result().dataset().create("dset111_short_strict", "Solution");
      m.result().dataset("dset111_short_strict").label("Stage 111 short strict total-load force control");
      m.result().dataset("dset111_short_strict").set("solution", solTag);
      addGlobalPlot(m, "pg111_short_total_load", "Stage 111 short strict total-load control", "dset111_short_strict",
          new String[]{"Fn_contact111", "Fn_film_applied111", "Fn_total111", "F_total_target", "Fn_total_error111", "dr_force_total111"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      addEval(m, "eval111_short_total_load", "Stage 111 short strict total-load values", "dset111_short_strict",
          new String[]{"Fn_contact111", "Fn_film_applied111", "Fn_total111", "F_total_target", "Fn_total_error111", "dr_force_total111"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      printRange(m, "eval111_short_total_load");
      m.save("189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      System.out.println("Saved local: 189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
