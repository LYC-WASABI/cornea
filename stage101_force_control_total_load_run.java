import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage101_force_control_total_load_run {
  private static String lastSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static void removeResult(Model m, String tag) {
    try { m.result().remove(tag); } catch (Exception ignore) {}
  }

  private static void removeNumerical(Model m, String tag) {
    try { m.result().numerical().remove(tag); } catch (Exception ignore) {}
  }

  private static void addGlobalPlot(Model m, String tag, String label, String dset,
                                    String[] expr, String[] unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  private static void addSurfacePlot(Model m, String tag, String label, String dset,
                                     String sel, String expr, String unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", dset);
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").selection().named(sel);
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void addEval(Model m, String tag, String label, String dset,
                              String[] expr, String[] unit) {
    removeNumerical(m, tag);
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).label(label);
    m.result().numerical(tag).set("data", dset);
    m.result().numerical(tag).set("expr", expr);
    m.result().numerical(tag).set("unit", unit);
  }

  private static void printRange(Model m, String tag) {
    try {
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
    } catch (Exception e) {
      System.out.println(tag + " ERROR " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph";
      String localOut = "179_lid8mm_stage101_total_load_force_control_results_Model.mph";
      Model m = ModelUtil.load("Model", inPath);

      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String fnContact = "intop_contact(" + pContact + ")";
      String fnTotal = fnContact + "+W_film_replay53";

      m.param().set("q_force_total101_init", "0.02", "Initial dimensionless radial indentation guess for total-load control");
      m.component(c).variable(v).set("dr_force_total101", "q_force_total101*1[mm]");
      m.component(c).variable(v).descr("dr_force_total101", "Closed-loop radial indentation from total normal load equation");
      m.component(c).variable(v).set("Fn_contact101", fnContact);
      m.component(c).variable(v).set("Fn_total101", fnTotal);
      m.component(c).variable(v).set("Fn_total_error101", "(" + fnTotal + "-F_total_target)/F_total_target");

      try { m.component(c).physics().remove("ge_force_total101"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total101", "GlobalEquations");
      m.component(c).physics("ge_force_total101").label("Total normal load closed-loop indentation");
      m.component(c).physics("ge_force_total101").feature("ge1").set("name", 1, 1, "q_force_total101");
      m.component(c).physics("ge_force_total101").feature("ge1").set("equation", 1, 1,
          "(Fn_total101-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total101").feature("ge1").set("initialValueU", 1, 1,
          "q_force_total101_init");
      m.component(c).physics("ge_force_total101").feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("ge_force_total101").feature("ge1").set("description", 1, 1,
          "Unknown radial indentation adjusted so contact plus replayed tear-film normal load equals 0.03 N");

      m.component(c).physics("solid").feature("disp_lid_time").set("U0",
          new String[]{
            "0",
            "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
                + "-dr_force_total101*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
                + "-dr_force_total101*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
          });

      try { m.study("std_shear_feedback73").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", "ge_force_total101", "on"}); }
      catch (Exception e) {
        System.out.println("WARN activate failed: " + e.getMessage());
      }
      try { m.study("std_shear_feedback73").feature("time").set("geometricNonlinearity", "on"); } catch (Exception ignore) {}
      try { m.study("std_shear_feedback73").feature("time").set("useinitsol", "off"); } catch (Exception ignore) {}

      String[] before = m.sol().tags();
      System.out.println("RUN_STAGE101=std_shear_feedback73 with total normal load Global Equation");
      m.study("std_shear_feedback73").run();
      String solTag = lastSolution(m, before);
      System.out.println("STAGE101_SOLUTION=" + solTag);

      try { m.result().dataset().remove("dset_force_total101"); } catch (Exception ignore) {}
      m.result().dataset().create("dset_force_total101", "Solution");
      m.result().dataset("dset_force_total101").label("Stage 101 total-load force-controlled structure");
      m.result().dataset("dset_force_total101").set("solution", solTag);

      String dset = "dset_force_total101";
      addGlobalPlot(m, "pg101_total_normal_load_time",
          "Stage 101 total normal load control over time",
          dset,
          new String[]{"Fn_contact101", "W_film_replay53", "Fn_total101", "F_total_target"},
          new String[]{"N", "N", "N", "N"});
      addGlobalPlot(m, "pg101_total_normal_load_error",
          "Stage 101 total normal load error",
          dset,
          new String[]{"Fn_total_error101", "q_force_total101", "dr_force_total101"},
          new String[]{"1", "1", "mm"});
      addSurfacePlot(m, "pg101_cornea_mises",
          "Stage 101 cornea anterior von Mises stress", dset,
          "sel_cornea_anterior_surface", "solid.mises", "Pa");
      addSurfacePlot(m, "pg101_lid_mises",
          "Stage 101 lid contact surface von Mises stress", dset,
          "sel_lid_contact_source_robust", "solid.mises", "Pa");
      addSurfacePlot(m, "pg101_cornea_displacement",
          "Stage 101 cornea anterior displacement", dset,
          "sel_cornea_anterior_surface", "solid.disp", "mm");
      addSurfacePlot(m, "pg101_lid_displacement",
          "Stage 101 lid contact surface displacement", dset,
          "sel_lid_contact_source_robust", "solid.disp", "mm");

      addEval(m, "eval101_total_normal_load",
          "Stage 101 total normal load control values", dset,
          new String[]{"Fn_contact101", "W_film_replay53", "Fn_total101", "F_total_target", "Fn_total_error101", "q_force_total101", "dr_force_total101"},
          new String[]{"N", "N", "N", "N", "1", "1", "mm"});
      printRange(m, "eval101_total_normal_load");

      m.save(localOut);
      System.out.println("Saved local: " + localOut);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
