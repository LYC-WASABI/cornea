import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage85_local_lambda_run_results {
  private static String lastSolution(Model m, String[] before) {
    List<String> old = Arrays.asList(before);
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi && i < values.length; i++) {
      sum += 0.5 * dt * (values[i - 1] + values[i]);
    }
    return sum;
  }

  private static void printRange(String label, double[] values, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", label, min, unit, max, unit);
    if (values.length > 51) {
      double avg = trapz(values, .01, 1, 51) / .50;
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n", label, avg, unit);
    }
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_local_lambda85");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surfaceInt(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "IntSurface");
    m.result().numerical(tag).set("data", "dset_local_lambda85");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surfaceMax(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "MaxSurface");
    m.result().numerical(tag).set("data", "dset_local_lambda85");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_local_lambda85");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void plot1(Model m, String tag, String label, String[] expr, String[] unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_local_lambda85");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\164_lid8mm_stage86_local_lambda_film_only_setup.mph");
    m.label("165_lid8mm_stage87_local_lambda_film_only_results.mph");
    String[] before = m.sol().tags();
    System.out.println("RUN_STAGE87=std_local_lambda_shear82 local-lambda film-only shear feedback");
    m.study("std_local_lambda_shear82").run();
    String sol = lastSolution(m, before);
    System.out.println("STAGE85_SOLUTION=" + sol);

    if (Arrays.asList(m.result().dataset().tags()).contains("dset_local_lambda85")) {
      m.result().dataset().remove("dset_local_lambda85");
    }
    m.result().dataset().create("dset_local_lambda85", "Solution");
    m.result().dataset("dset_local_lambda85").set("solution", sol);

    global(m, "eval85_Ffilm_replay", "shear_speed_window73*F_film_shear_replay81", "N");
    global(m, "eval85_Fasp_lambda", "shear_speed_window73*F_asp_lambda_replay81", "N");
    global(m, "eval85_Ftotal_lambda", "F_total_lambda_replay81", "N");
    global(m, "eval85_mu_lambda", "mu_lambda_replay81", "1");
    global(m, "eval85_tau_lid_nominal", "tau_lid_nominal_lambda81", "Pa");
    surfaceInt(m, "int85_cornea_lambda_shear", "sel_cornea_anterior_surface",
        "sqrt((tau_total_lambda81*ty_shear73)^2+(tau_total_lambda81*tz_shear73)^2)", "N");
    surfaceMax(m, "max85_cornea_disp", "sel_cornea_anterior_surface", "solid.disp", "mm");
    surfaceMax(m, "max85_cornea_mises", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    surfaceMax(m, "max85_lid_disp", "sel_lid_contact_source_robust", "solid.disp", "mm");
    surfaceMax(m, "max85_lid_mises", "sel_lid_contact_source_robust", "solid.mises", "Pa");

    plot3(m, "pg85_disp_all", "Stage 85 local lambda mixed lubrication displacement", "solid.disp", "mm");
    plot3(m, "pg85_mises_all", "Stage 85 local lambda mixed lubrication von Mises stress", "solid.mises", "Pa");
    plot3(m, "pg85_lambda", "Stage 85 local film-thickness ratio lambda", "lambda_local81", "1");
    plot3(m, "pg85_fasp", "Stage 85 local asperity-contact weight", "f_asp_lambda81", "1");
    plot3(m, "pg85_tau_total", "Stage 85 local mixed shear traction", "tau_total_lambda81", "Pa");
    plot1(m, "pg85_force_mu", "Stage 85 local lambda shear forces and friction coefficient",
        new String[] {"shear_speed_window73*F_film_shear_replay81", "shear_speed_window73*F_asp_lambda_replay81",
            "F_total_lambda_replay81", "mu_lambda_replay81"},
        new String[] {"N", "N", "N", "1"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\165_lid8mm_stage87_local_lambda_film_only_results.mph");
    System.out.println("SAVED_STAGE87=165_lid8mm_stage87_local_lambda_film_only_results.mph");
  }
}
