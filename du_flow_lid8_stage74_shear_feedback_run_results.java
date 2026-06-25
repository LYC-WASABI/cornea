import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage74_shear_feedback_run_results {
  private static String lastSolution(Model m, String[] before) {
    List<String> old = Arrays.asList(before);
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static void printRange(String label, double[][] values, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", label, min, unit, max, unit);
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_shear_feedback74");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal(), unit);
  }

  private static void surfaceInt(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "IntSurface");
    m.result().numerical(tag).set("data", "dset_shear_feedback74");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal(), unit);
  }

  private static void surfaceMax(Model m, String tag, String sel, String expr, String unit) {
    m.result().numerical().create(tag, "MaxSurface");
    m.result().numerical(tag).set("data", "dset_shear_feedback74");
    m.result().numerical(tag).selection().named(sel);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal(), unit);
  }

  private static void surfacePlot(Model m, String tag, String label, String sel, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_shear_feedback74");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").selection().named(sel);
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void globalPlot(Model m, String tag, String label, String[] expr, String[] unit) {
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_shear_feedback74");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\155_lid8mm_stage73_shear_feedback_setup.mph");
    m.label("156_lid8mm_stage74_shear_feedback_structure_results.mph");
    String[] before = m.sol().tags();
    System.out.println("RUN_STAGE74=std_shear_feedback73 structural transient with tangential shear feedback");
    m.study("std_shear_feedback73").run();
    String solTag = lastSolution(m, before);
    System.out.println("STAGE74_SOLUTION=" + solTag);

    if (Arrays.asList(m.result().dataset().tags()).contains("dset_shear_feedback74")) {
      m.result().dataset().remove("dset_shear_feedback74");
    }
    m.result().dataset().create("dset_shear_feedback74", "Solution");
    m.result().dataset("dset_shear_feedback74").set("solution", solTag);

    global(m, "eval74_Fshear_target", "F_shear_feedback73", "N");
    global(m, "eval74_mu_target", "mu_shear_feedback73", "1");
    global(m, "eval74_tau_nominal", "tau_nominal_shear73", "Pa");
    surfaceInt(m, "int74_cornea_applied_shear",
        "sel_cornea_anterior_surface",
        "sqrt((tau_pfilm_shear73*ty_shear73)^2+(tau_pfilm_shear73*tz_shear73)^2)", "N");
    surfaceInt(m, "int74_lid_applied_shear",
        "sel_lid_contact_source_robust",
        "sqrt((tau_nominal_shear73*ty_shear73)^2+(tau_nominal_shear73*tz_shear73)^2)", "N");
    surfaceMax(m, "max74_cornea_disp",
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    surfaceMax(m, "max74_cornea_mises",
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    surfaceMax(m, "max74_lid_disp",
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    surfaceMax(m, "max74_lid_mises",
        "sel_lid_contact_source_robust", "solid.mises", "Pa");

    surfacePlot(m, "pg74_cornea_disp", "Stage 74 cornea anterior displacement with shear feedback",
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    surfacePlot(m, "pg74_cornea_mises", "Stage 74 cornea anterior von Mises stress with shear feedback",
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    surfacePlot(m, "pg74_lid_disp", "Stage 74 lid inner surface displacement with shear feedback",
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    surfacePlot(m, "pg74_lid_mises", "Stage 74 lid inner surface von Mises stress with shear feedback",
        "sel_lid_contact_source_robust", "solid.mises", "Pa");
    surfacePlot(m, "pg74_shear_cornea", "Stage 74 cornea applied tangential shear traction",
        "sel_cornea_anterior_surface", "sqrt((tau_pfilm_shear73*ty_shear73)^2+(tau_pfilm_shear73*tz_shear73)^2)", "Pa");

    globalPlot(m, "pg74_shear_mu", "Stage 74 shear feedback force and apparent friction coefficient",
        new String[] {"F_shear_feedback73", "mu_shear_feedback73", "tau_nominal_shear73"},
        new String[] {"N", "1", "Pa"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\156_lid8mm_stage74_shear_feedback_structure_results.mph");
    System.out.println("SAVED_STAGE74=156_lid8mm_stage74_shear_feedback_structure_results.mph");
  }
}
