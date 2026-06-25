import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage102_offline_total_load_calibrated_run {
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

  private static double[] eval(Model m, String tag, String dset, String expr, String unit) {
    removeNumerical(m, tag);
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", dset);
    m.result().numerical(tag).set("expr", expr);
    m.result().numerical(tag).set("unit", unit);
    return m.result().numerical(tag).getReal()[0];
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

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String dsetOld = "dset_shear_feedback76";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      double[] time;
      try { time = eval(m, "tmp102_time", dsetOld, "t", "s"); }
      catch (Exception e) {
        double[] idx = eval(m, "tmp102_dr_probe", dsetOld, "dr_force_mixed54", "mm");
        time = new double[idx.length];
        for (int i = 0; i < idx.length; i++) time[i] = i;
      }
      double[] drOld = eval(m, "tmp102_dr_old", dsetOld, "dr_force_mixed54", "mm");
      double[] fnContact = eval(m, "tmp102_fn_contact_old", dsetOld, "intop_contact(" + pContact + ")", "N");
      double[] wFilm = eval(m, "tmp102_wfilm_old", dsetOld, "W_film_replay53", "N");
      double k = 0.8051323301;
      double gain = 0.5;
      double target = 0.03;
      int n = Math.min(Math.min(time.length, drOld.length), Math.min(fnContact.length, wFilm.length));
      ArrayList<String[]> rows = new ArrayList<String[]>();
      double lastT = Double.NEGATIVE_INFINITY;
      double minDr = Double.POSITIVE_INFINITY, maxDr = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < n; i++) {
        double desiredContact = Math.max(0.0, target - wFilm[i]);
        double drNew = Math.max(0.0, drOld[i] + gain * (desiredContact - fnContact[i]) / k);
        if (time[i] > lastT + 1e-12 || i == n - 1) {
          rows.add(new String[]{Double.toString(time[i]), Double.toString(drNew)});
          lastT = time[i];
        }
        minDr = Math.min(minDr, drNew);
        maxDr = Math.max(maxDr, drNew);
      }
      try { m.func().remove("dr102"); } catch (Exception ignore) {}
      m.func().create("dr102", "Interpolation");
      m.func("dr102").label("Stage 102 total-load corrected radial indentation schedule");
      m.func("dr102").set("funcname", "dr_force_sched102");
      m.func("dr102").set("table", rows.toArray(new String[0][0]));
      m.func("dr102").set("argunit", new String[]{"s"});
      m.func("dr102").set("fununit", "mm");
      m.func("dr102").set("interp", "piecewisecubic");
      m.func("dr102").set("extrap", "const");

      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      m.component(c).variable(v).set("dr_force_total102", "dr_force_sched102(t)");
      m.component(c).variable(v).set("Fn_contact102", "intop_contact(" + pContact + ")");
      m.component(c).variable(v).set("Fn_contact_target102", "max(0[N],F_total_target-W_film_replay53)");
      m.component(c).variable(v).set("Fn_total102", "Fn_contact102+W_film_replay53");
      m.component(c).variable(v).set("Fn_total_error102", "(Fn_total102-F_total_target)/F_total_target");
      m.component(c).variable(v).set("Fn_contact_error102", "(Fn_contact102-Fn_contact_target102)/F_total_target");

      try { m.component(c).physics().remove("ge_force_total101"); } catch (Exception ignore) {}
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total102*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total102*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate", new String[]{"solid", "on", "tff", "off"});

      String[] before = m.sol().tags();
      System.out.println("STAGE102_ROWS=" + rows.size() + " drNew[min,max]=[" + minDr + "," + maxDr + "] mm");
      System.out.println("RUN_STAGE102_OFFLINE_TOTAL_LOAD_CALIBRATED");
      m.study("std_shear_feedback73").run();
      String solTag = lastSolution(m, before);
      if (solTag == null || solTag.length() == 0) solTag = "sol22";
      try { m.result().dataset().remove("dset_force_total102"); } catch (Exception ignore) {}
      m.result().dataset().create("dset_force_total102", "Solution");
      m.result().dataset("dset_force_total102").label("Stage 102 offline total-load calibrated structure");
      m.result().dataset("dset_force_total102").set("solution", solTag);
      String dset = "dset_force_total102";

      addGlobalPlot(m, "pg102_load_sharing_time", "Stage 102 load sharing after offline indentation calibration", dset,
          new String[]{"Fn_contact102", "Fn_contact_target102", "W_film_replay53", "Fn_total102", "F_total_target"},
          new String[]{"N", "N", "N", "N", "N"});
      addGlobalPlot(m, "pg102_load_error_indent", "Stage 102 load error and corrected indentation", dset,
          new String[]{"Fn_contact_error102", "Fn_total_error102", "dr_force_total102"},
          new String[]{"1", "1", "mm"});
      addSurfacePlot(m, "pg102_cornea_mises", "Stage 102 cornea anterior von Mises stress", dset,
          "sel_cornea_anterior_surface", "solid.mises", "Pa");
      addSurfacePlot(m, "pg102_lid_mises", "Stage 102 lid contact surface von Mises stress", dset,
          "sel_lid_contact_source_robust", "solid.mises", "Pa");
      addSurfacePlot(m, "pg102_cornea_displacement", "Stage 102 cornea anterior displacement", dset,
          "sel_cornea_anterior_surface", "solid.disp", "mm");
      addSurfacePlot(m, "pg102_lid_displacement", "Stage 102 lid contact surface displacement", dset,
          "sel_lid_contact_source_robust", "solid.disp", "mm");

      addEval(m, "eval102_load_control", "Stage 102 load control values", dset,
          new String[]{"Fn_contact102", "Fn_contact_target102", "W_film_replay53", "Fn_total102", "F_total_target", "Fn_contact_error102", "Fn_total_error102", "dr_force_total102"},
          new String[]{"N", "N", "N", "N", "N", "1", "1", "mm"});
      printRange(m, "eval102_load_control");

      m.save("180_lid8mm_stage102_offline_total_load_calibrated_results_Model.mph");
      System.out.println("Saved local: 180_lid8mm_stage102_offline_total_load_calibrated_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
