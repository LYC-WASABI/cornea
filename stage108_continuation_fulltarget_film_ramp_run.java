import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage108_continuation_fulltarget_film_ramp_run {
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
    t1.feature("fc1").label("Fully coupled solid plus full-target load equation");
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
          new String[]{"solid", "on", "tff", "off", "ge_force_total108", "on"});
      try { m.study("std_shear_feedback73").feature("time").set("useinitsol", "off"); } catch (Exception ignore) {}
      String[] before = m.sol().tags();
      m.study("std_shear_feedback73").createAutoSequences("sol");
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      setupFullyCoupled(m, solTag);
      System.out.println("RUN_STAGE108_" + name + " solver=" + solTag
          + " filmScale=" + filmScale + " penaltyScale=" + penaltyScale
          + " tlist=" + tlist);
      m.sol(solTag).runAll();
      try { m.result().dataset().remove("dset108_" + name); } catch (Exception ignore) {}
      m.result().dataset().create("dset108_" + name, "Solution");
      m.result().dataset("dset108_" + name).label("Stage 108 film-ramp continuation " + name);
      m.result().dataset("dset108_" + name).set("solution", solTag);
      return true;
    } catch (Exception e) {
      System.out.println("STAGE108_" + name + "_FAILED: " + e.getMessage());
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
      m.param().set("q_force_total108_init", "0.02", "Initial dimensionless radial indentation for full-target film-ramp continuation");
      m.component(c).variable(v).set("dr_force_total108", "q_force_total108*1[mm]");
      m.component(c).variable(v).set("Fn_contact108", fnContact);
      m.component(c).variable(v).set("Fn_film_applied108", "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total108", "Fn_contact108+Fn_film_applied108");
      m.component(c).variable(v).set("Fn_total_error108", "(Fn_total108-F_total_target)/F_total_target");

      try { m.component(c).physics().remove("ge_force_total108"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total108", "GlobalEquations");
      m.component(c).physics("ge_force_total108").label("Stage 108 full-target film-ramp total load control");
      m.component(c).physics("ge_force_total108").feature("ge1").set("name", 1, 1, "q_force_total108");
      m.component(c).physics("ge_force_total108").feature("ge1").set("equation", 1, 1,
          "(Fn_total108-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total108").feature("ge1").set("initialValueU", 1, 1, "q_force_total108_init");
      m.component(c).physics("ge_force_total108").feature("ge1").set("initialValueUt", 1, 1, "0");

      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total108*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total108*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });

      boolean ok1 = runStage(m, "a_short_film010", 0.10, 0.20, "range(0,0.001,0.01)");
      boolean ok2 = ok1 && runStage(m, "b_short_film030", 0.30, 0.35, "range(0,0.001,0.01)");
      boolean ok3 = ok2 && runStage(m, "c_short_film060", 0.60, 0.50, "range(0,0.001,0.01)");
      boolean ok4 = ok3 && runStage(m, "d_short_film100", 1.00, 0.70, "range(0,0.001,0.01)");
      String finalDset = ok4 ? "dset108_d_short_film100" : ok3 ? "dset108_c_short_film060" : ok2 ? "dset108_b_short_film030" : ok1 ? "dset108_a_short_film010" : "dset_shear_feedback76";

      addGlobalPlot(m, "pg108_continuation_load", "Stage 108 full-target film-ramp load control", finalDset,
          new String[]{"Fn_contact108", "Fn_film_applied108", "Fn_total108", "F_total_target", "Fn_total_error108", "dr_force_total108"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      addEval(m, "eval108_continuation_load", "Stage 108 continuation load control values", finalDset,
          new String[]{"Fn_contact108", "Fn_film_applied108", "Fn_total108", "F_total_target", "Fn_total_error108", "dr_force_total108"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      try { printRange(m, "eval108_continuation_load"); } catch (Exception e) { System.out.println("PRINT_RANGE_FAILED " + e.getMessage()); }

      m.save("186_lid8mm_stage108_fulltarget_film_ramp_continuation_Model.mph");
      System.out.println("Saved local: 186_lid8mm_stage108_fulltarget_film_ramp_continuation_Model.mph");
      System.out.println("STAGE108_STATUS ok1=" + ok1 + " ok2=" + ok2 + " ok3=" + ok3 + " ok4=" + ok4);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
