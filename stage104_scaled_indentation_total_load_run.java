import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage104_scaled_indentation_total_load_run {
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
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      m.param().set("scale_indent104", "0.5", "Stable first-pass scaling of smooth indentation schedule for total-load reduction");
      m.component(c).variable(v).set("dr_force_total104", "scale_indent104*dr_force_mixed54");
      m.component(c).variable(v).set("Fn_contact104", "intop_contact(" + pContact + ")");
      m.component(c).variable(v).set("Fn_total104", "Fn_contact104+W_film_replay53");
      m.component(c).variable(v).set("Fn_total_error104", "(Fn_total104-F_total_target)/F_total_target");
      try { m.component(c).physics().remove("ge_force_total101"); } catch (Exception ignore) {}
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total104*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total104*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate", new String[]{"solid", "on", "tff", "off"});
      String[] before = m.sol().tags();
      System.out.println("RUN_STAGE104_SCALED_INDENTATION_TOTAL_LOAD scale_indent104=0.5");
      m.study("std_shear_feedback73").run();
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      try { m.result().dataset().remove("dset_force_total104"); } catch (Exception ignore) {}
      m.result().dataset().create("dset_force_total104", "Solution");
      m.result().dataset("dset_force_total104").label("Stage 104 scaled-indentation total-load structure");
      m.result().dataset("dset_force_total104").set("solution", solTag);
      String dset = "dset_force_total104";
      addGlobalPlot(m, "pg104_load_sharing_time", "Stage 104 load sharing after smooth indentation scaling", dset,
          new String[]{"Fn_contact104", "W_film_replay53", "Fn_total104", "F_total_target", "Fn_total_error104"},
          new String[]{"N", "N", "N", "N", "1"});
      addSurfacePlot(m, "pg104_cornea_mises", "Stage 104 cornea anterior von Mises stress", dset,
          "sel_cornea_anterior_surface", "solid.mises", "Pa");
      addSurfacePlot(m, "pg104_lid_mises", "Stage 104 lid contact surface von Mises stress", dset,
          "sel_lid_contact_source_robust", "solid.mises", "Pa");
      addSurfacePlot(m, "pg104_cornea_displacement", "Stage 104 cornea anterior displacement", dset,
          "sel_cornea_anterior_surface", "solid.disp", "mm");
      addSurfacePlot(m, "pg104_lid_displacement", "Stage 104 lid contact surface displacement", dset,
          "sel_lid_contact_source_robust", "solid.disp", "mm");
      addEval(m, "eval104_load_control", "Stage 104 load control values", dset,
          new String[]{"Fn_contact104", "W_film_replay53", "Fn_total104", "F_total_target", "Fn_total_error104", "dr_force_total104"},
          new String[]{"N", "N", "N", "N", "1", "mm"});
      printRange(m, "eval104_load_control");
      m.save("182_lid8mm_stage104_scaled_indentation_total_load_results_Model.mph");
      System.out.println("Saved local: 182_lid8mm_stage104_scaled_indentation_total_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
