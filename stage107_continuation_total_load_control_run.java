import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage107_continuation_total_load_control_run {
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
    t1.feature("fc1").label("Fully coupled solid plus total-load equation");
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

  private static void addSurfacePlot(Model m, String tag, String label, String dset, String sel, String expr, String unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").selection().named(sel);
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
        double v = vals[i][j];
        if (!Double.isNaN(v)) {
          if (v < min) { min = v; imin = j; }
          if (v > max) { max = v; imax = j; }
        }
      }
      System.out.println("  expr" + i + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
    }
  }

  private static boolean runStage(Model m, String name, double loadScale, double filmScale,
                                  double penaltyScale, String tlist, String initSolTag) {
    try {
      m.param().set("load_scale107", Double.toString(loadScale));
      m.param().set("scale_partitioned_pfilm", Double.toString(filmScale));
      m.param().set("contact_penalty_scale107", Double.toString(penaltyScale));
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
          new String[]{"solid", "on", "tff", "off", "ge_force_total107", "on"});
      try {
        if (initSolTag != null && initSolTag.length() > 0) {
          m.study("std_shear_feedback73").feature("time").set("useinitsol", "on");
          m.study("std_shear_feedback73").feature("time").set("initmethod", "sol");
          m.study("std_shear_feedback73").feature("time").set("initsol", initSolTag);
          m.study("std_shear_feedback73").feature("time").set("initsoluse", initSolTag);
        } else {
          m.study("std_shear_feedback73").feature("time").set("useinitsol", "off");
        }
      } catch (Exception e) {
        System.out.println("WARN init solution setup failed: " + e.getMessage());
      }

      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      setupFullyCoupled(m, solTag);
      System.out.println("RUN_STAGE107_" + name + " solver=" + solTag
          + " loadScale=" + loadScale + " filmScale=" + filmScale
          + " penaltyScale=" + penaltyScale + " tlist=" + tlist);
      m.sol(solTag).runAll();
      try { m.result().dataset().remove("dset107_" + name); } catch (Exception ignore) {}
      m.result().dataset().create("dset107_" + name, "Solution");
      m.result().dataset("dset107_" + name).label("Stage 107 continuation " + name);
      m.result().dataset("dset107_" + name).set("solution", solTag);
      return true;
    } catch (Exception e) {
      System.out.println("STAGE107_" + name + "_FAILED: " + e.getMessage());
      return false;
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String fnContact = "intop_contact(" + pContact + ")";

      m.param().set("load_scale107", "0.1", "Continuation multiplier for target total normal load");
      m.param().set("contact_penalty_scale107", "0.2", "Continuation multiplier for contact penalty factor");
      m.param().set("q_force_total107_init", "0.01", "Initial dimensionless radial indentation for continuation total-load control");
      m.component(c).variable(v).set("dr_force_total107", "q_force_total107*1[mm]");
      m.component(c).variable(v).set("Fn_contact107", fnContact);
      m.component(c).variable(v).set("Fn_film_applied107", "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_target107", "load_scale107*F_total_target");
      m.component(c).variable(v).set("Fn_total107", "Fn_contact107+Fn_film_applied107");
      m.component(c).variable(v).set("Fn_total_error107", "(Fn_total107-Fn_target107)/F_total_target");

      try { m.component(c).physics().remove("ge_force_total107"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total107", "GlobalEquations");
      m.component(c).physics("ge_force_total107").label("Stage 107 continuation total normal load control");
      m.component(c).physics("ge_force_total107").feature("ge1").set("name", 1, 1, "q_force_total107");
      m.component(c).physics("ge_force_total107").feature("ge1").set("equation", 1, 1,
          "(Fn_total107-Fn_target107)/F_total_target");
      m.component(c).physics("ge_force_total107").feature("ge1").set("initialValueU", 1, 1, "q_force_total107_init");
      m.component(c).physics("ge_force_total107").feature("ge1").set("initialValueUt", 1, 1, "0");

      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total107*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total107*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });

      boolean ok1 = runStage(m, "a_short_low", 0.10, 0.10, 0.20, "range(0,0.001,0.01)", "");
      boolean ok2 = false;
      boolean ok3 = false;
      boolean ok4 = false;
      if (ok1) ok2 = runStage(m, "b_short_mid", 0.30, 0.30, 0.35, "range(0,0.001,0.01)", "");
      if (ok2) ok3 = runStage(m, "c_short_full_target_lowfilm", 1.00, 0.30, 0.50, "range(0,0.001,0.01)", "");
      if (ok3) ok4 = runStage(m, "d_short_full", 1.00, 1.00, 0.70, "range(0,0.001,0.01)", "");

      String finalDset = ok4 ? "dset107_d_short_full" : ok3 ? "dset107_c_short_full_target_lowfilm" : ok2 ? "dset107_b_short_mid" : ok1 ? "dset107_a_short_low" : "dset_shear_feedback76";
      addGlobalPlot(m, "pg107_continuation_load", "Stage 107 continuation load control", finalDset,
          new String[]{"Fn_contact107", "Fn_film_applied107", "Fn_total107", "Fn_target107", "Fn_total_error107", "dr_force_total107"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      addSurfacePlot(m, "pg107_cornea_mises", "Stage 107 cornea anterior von Mises stress", finalDset,
          "sel_cornea_anterior_surface", "solid.mises", "Pa");
      addSurfacePlot(m, "pg107_lid_mises", "Stage 107 lid contact surface von Mises stress", finalDset,
          "sel_lid_contact_source_robust", "solid.mises", "Pa");
      addEval(m, "eval107_continuation_load", "Stage 107 continuation load control values", finalDset,
          new String[]{"Fn_contact107", "Fn_film_applied107", "Fn_total107", "Fn_target107", "Fn_total_error107", "dr_force_total107"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      try { printRange(m, "eval107_continuation_load"); } catch (Exception e) { System.out.println("PRINT_RANGE_FAILED " + e.getMessage()); }

      m.save("185_lid8mm_stage107_continuation_total_load_control_Model.mph");
      System.out.println("Saved local: 185_lid8mm_stage107_continuation_total_load_control_Model.mph");
      System.out.println("STAGE107_STATUS ok1=" + ok1 + " ok2=" + ok2 + " ok3=" + ok3 + " ok4=" + ok4);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
