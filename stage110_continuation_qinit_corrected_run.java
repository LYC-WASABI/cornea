import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage110_continuation_qinit_corrected_run {
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
    t1.feature("fc1").label("Fully coupled solid plus corrected-q total-load equation");
    t1.feature("fc1").set("linsolver", "dDef");
  }
  private static boolean runStage(Model m, String name, double filmScale, double penaltyScale, String tlist) {
    try {
      m.param().set("scale_partitioned_pfilm", Double.toString(filmScale));
      try {
        m.component("comp1").physics("solid").feature("dcnt1").set("fp_penalty", Double.toString(penaltyScale));
        m.component("comp1").physics("solid").feature("dcnt1").set("fp_init_penalty", Double.toString(Math.min(0.1, penaltyScale)));
        m.component("comp1").physics("solid").feature("dcnt1").set("useRelaxation", "Always");
        m.component("comp1").physics("solid").feature("dcnt1").set("irlx", "1e-2");
        m.component("comp1").physics("solid").feature("dcnt1").set("nRelax", "6");
      } catch (Exception e) {
        System.out.println("WARN contact tuning failed: " + e.getMessage());
      }
      m.study("std_shear_feedback73").feature("time").set("tlist", tlist);
      m.study("std_shear_feedback73").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", "ge_force_total110", "on"});
      try { m.study("std_shear_feedback73").feature("time").set("useinitsol", "off"); } catch (Exception ignore) {}
      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      setupFullyCoupled(m, solTag);
      System.out.println("RUN_STAGE110_" + name + " solver=" + solTag
          + " filmScale=" + filmScale + " penaltyScale=" + penaltyScale
          + " tlist=" + tlist);
      m.sol(solTag).runAll();
      try { m.result().dataset().remove("dset110_" + name); } catch (Exception ignore) {}
      m.result().dataset().create("dset110_" + name, "Solution");
      m.result().dataset("dset110_" + name).label("Stage 110 corrected-q continuation " + name);
      m.result().dataset("dset110_" + name).set("solution", solTag);
      return true;
    } catch (Exception e) {
      System.out.println("STAGE110_" + name + "_FAILED: " + e.getMessage());
      return false;
    }
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
  private static void addSurfacePlot(Model m, String tag, String label, String dset, String sel, String expr, String unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).selection().named(sel);
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
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
        double val = vals[i][j];
        if (!Double.isNaN(val)) {
          if (val < min) { min = val; imin = j; }
          if (val > max) { max = val; imax = j; }
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
      m.param().set("q_force_total110_init", "2e-4", "Corrected initial dimensionless indentation from Stage 109 probe");
      m.component(c).variable(v).set("dr_force_total110", "q_force_total110*1[mm]");
      m.component(c).variable(v).set("Fn_contact110", fnContact);
      m.component(c).variable(v).set("Fn_film_applied110", "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total110", "Fn_contact110+Fn_film_applied110");
      m.component(c).variable(v).set("Fn_total_error110", "(Fn_total110-F_total_target)/F_total_target");
      try { m.component(c).physics().remove("ge_force_total110"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total110", "GlobalEquations");
      m.component(c).physics("ge_force_total110").label("Stage 110 corrected-q total normal load control");
      m.component(c).physics("ge_force_total110").feature("ge1").set("name", 1, 1, "q_force_total110");
      m.component(c).physics("ge_force_total110").feature("ge1").set("equation", 1, 1,
          "(Fn_total110-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total110").feature("ge1").set("initialValueU", 1, 1, "q_force_total110_init");
      m.component(c).physics("ge_force_total110").feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total110*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total110*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      boolean ok1 = runStage(m, "a_short_film010", 0.10, 0.20, "range(0,0.001,0.01)");
      boolean ok2 = ok1 && runStage(m, "b_short_film030", 0.30, 0.35, "range(0,0.001,0.01)");
      boolean ok3 = ok2 && runStage(m, "c_short_film060", 0.60, 0.50, "range(0,0.001,0.01)");
      boolean ok4 = ok3 && runStage(m, "d_short_film100", 1.00, 0.70, "range(0,0.001,0.01)");
      String finalDset = ok4 ? "dset110_d_short_film100" : ok3 ? "dset110_c_short_film060" : ok2 ? "dset110_b_short_film030" : ok1 ? "dset110_a_short_film010" : "dset_shear_feedback76";
      addGlobalPlot(m, "pg110_continuation_load", "Stage 110 corrected-q film-ramp load control", finalDset,
          new String[]{"Fn_contact110", "Fn_film_applied110", "Fn_total110", "F_total_target", "Fn_total_error110", "dr_force_total110"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      addSurfacePlot(m, "pg110_cornea_mises", "Stage 110 cornea anterior von Mises stress", finalDset,
          "sel_cornea_anterior_surface", "solid.mises", "Pa");
      addSurfacePlot(m, "pg110_lid_mises", "Stage 110 lid contact surface von Mises stress", finalDset,
          "sel_lid_contact_source_robust", "solid.mises", "Pa");
      addEval(m, "eval110_continuation_load", "Stage 110 corrected-q load control values", finalDset,
          new String[]{"Fn_contact110", "Fn_film_applied110", "Fn_total110", "F_total_target", "Fn_total_error110", "dr_force_total110"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      try { printRange(m, "eval110_continuation_load"); } catch (Exception e) { System.out.println("PRINT_RANGE_FAILED " + e.getMessage()); }
      m.save("188_lid8mm_stage110_corrected_q_film_ramp_continuation_Model.mph");
      System.out.println("Saved local: 188_lid8mm_stage110_corrected_q_film_ramp_continuation_Model.mph");
      System.out.println("STAGE110_STATUS ok1=" + ok1 + " ok2=" + ok2 + " ok3=" + ok3 + " ok4=" + ok4);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
