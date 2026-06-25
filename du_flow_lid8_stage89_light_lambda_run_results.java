import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage89_light_lambda_run_results {
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
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n",
          label, trapz(values, .01, 1, 51) / .50, unit);
    }
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_light_lambda89");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surfaceInt(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "IntSurface");
    m.result().numerical(tag).set("data", "dset_light_lambda89");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surfaceMax(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "MaxSurface");
    m.result().numerical(tag).set("data", "dset_light_lambda89");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_light_lambda89");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void plot1(Model m, String tag, String label, String[] expr, String[] unit) {
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_light_lambda89");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\166_lid8mm_stage88_light_lambda_film_shear_setup.mph");
    m.label("167_lid8mm_stage89_light_lambda_film_shear_results.mph");
    String[] before = m.sol().tags();
    System.out.println("RUN_STAGE89=std_shear_feedback73 light local-lambda film shear feedback");
    m.study("std_shear_feedback73").run();
    String sol = lastSolution(m, before);
    System.out.println("STAGE89_SOLUTION=" + sol);

    if (Arrays.asList(m.result().dataset().tags()).contains("dset_light_lambda89")) {
      m.result().dataset().remove("dset_light_lambda89");
    }
    m.result().dataset().create("dset_light_lambda89", "Solution");
    m.result().dataset("dset_light_lambda89").set("solution", sol);

    global(m, "eval89_Ffilm", "F_film_feedback88", "N");
    global(m, "eval89_Fasp", "F_asp_feedback88", "N");
    global(m, "eval89_Ftotal", "F_total_feedback88", "N");
    global(m, "eval89_mu", "mu_feedback88", "1");
    global(m, "eval89_tau_nominal", "tau_nominal_film88", "Pa");
    surfaceInt(m, "int89_cornea_applied_shear", "sel_cornea_anterior_surface",
        "sqrt((tau_nominal_film88*pfilm_window_shear73*ty_shear73)^2+(tau_nominal_film88*pfilm_window_shear73*tz_shear73)^2)", "N");
    surfaceInt(m, "int89_lid_applied_shear", "sel_lid_contact_source_robust",
        "sqrt((tau_nominal_film88*ty_shear73)^2+(tau_nominal_film88*tz_shear73)^2)", "N");
    surfaceMax(m, "max89_cornea_disp", "sel_cornea_anterior_surface", "solid.disp", "mm");
    surfaceMax(m, "max89_cornea_mises", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    surfaceMax(m, "max89_lid_disp", "sel_lid_contact_source_robust", "solid.disp", "mm");
    surfaceMax(m, "max89_lid_mises", "sel_lid_contact_source_robust", "solid.mises", "Pa");

    plot3(m, "pg89_disp_all", "Stage 89 light local-lambda film shear displacement", "solid.disp", "mm");
    plot3(m, "pg89_mises_all", "Stage 89 light local-lambda film shear von Mises stress", "solid.mises", "Pa");
    plot3(m, "pg89_lambda", "Stage 89 local lambda field", "lambda_local88", "1");
    plot3(m, "pg89_fasp", "Stage 89 local asperity weight", "f_asp_local88", "1");
    plot3(m, "pg89_tau", "Stage 89 applied nominal film shear traction", "tau_nominal_film88*pfilm_window_shear73", "Pa");
    plot1(m, "pg89_force_mu", "Stage 89 local-lambda film-only shear force and friction coefficient",
        new String[] {"F_film_feedback88", "F_asp_feedback88", "F_total_feedback88", "mu_feedback88"},
        new String[] {"N", "N", "N", "1"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\167_lid8mm_stage89_light_lambda_film_shear_results.mph");
    System.out.println("SAVED_STAGE89=167_lid8mm_stage89_light_lambda_film_shear_results.mph");
  }
}
