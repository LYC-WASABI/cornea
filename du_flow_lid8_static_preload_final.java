import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_static_preload_final {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_static_preload_minus35_calibrated_0p03N_results.mph";
  private static final String THETA_LIST =
      "0 -2.5 -5 -7.5 -10 -12.5 -15 -17.5 -20 -22.5 -25 -27.5 -30 -32.5 -35";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    clean(model);
    model.param().set("s_lid", "8[mm]");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))");
    model.param().set("delta_indent", "0.0530[mm]");
    model.param().set("theta_lid", "0[deg]");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    fixMovingContactSource(model);
    model.component("comp1").mesh("mesh1").run();
    createStudy(model);
    model.study("std_preload").run();
    createResults(model);
    double force = lastIntegral(model, "int_front_contact_force");
    System.out.printf("FINAL_STATIC delta_indent=0.053000[mm] force=%.12f[N]%n", force);
    model.save(OUT);
    System.out.println("SAVED_RESULT=" + OUT);
  }

  private static void createStudy(Model model) {
    model.study().create("std_preload");
    model.study("std_preload").label("Step 1: static center-directed preload at -35 deg");
    model.study("std_preload").create("param", "Parametric");
    model.study("std_preload").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_preload").feature("param").set("plistarr", new String[]{THETA_LIST});
    model.study("std_preload").feature("param").set("punit", new String[]{"deg"});
    model.study("std_preload").create("stat", "Stationary");
    model.study("std_preload").feature("stat").set("geometricNonlinearity", true);
  }

  private static void createResults(Model model) {
    model.result().dataset().create("dset_preload", "Solution");
    model.result().dataset("dset_preload").label("Step 1 static preload solution");
    model.result().dataset("dset_preload").set("solution", "sol1");
    integral(model, "int_front_contact_force", "Front cornea integral of solid.Tn",
        "sel_cornea_anterior_surface", "solid.Tn", "N");
    maximum(model, "max_front_disp", "Max front cornea displacement",
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    maximum(model, "max_front_mises", "Max front cornea von Mises stress",
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    maximum(model, "max_lid_disp", "Max lid inner surface displacement",
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    maximum(model, "max_lid_mises", "Max lid inner surface von Mises stress",
        "sel_lid_contact_source_robust", "solid.mises", "Pa");
    surfacePlot(model, "pg_preload_front_disp", "Front cornea displacement",
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    surfacePlot(model, "pg_preload_front_mises", "Front cornea von Mises stress",
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    surfacePlot(model, "pg_preload_lid_disp", "Lid wiper inner surface displacement",
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    surfacePlot(model, "pg_preload_lid_mises", "Lid wiper inner surface von Mises stress",
        "sel_lid_contact_source_robust", "solid.mises", "Pa");
    surfacePlot(model, "pg_preload_contact_pressure", "Contact pressure on lid wiper",
        "sel_lid_contact_source_robust", "solid.Tn", "Pa");
  }

  private static void integral(Model model, String tag, String label, String selection,
      String expr, String unit) {
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", "dset_preload");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void maximum(Model model, String tag, String label, String selection,
      String expr, String unit) {
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", "dset_preload");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void surfacePlot(Model model, String tag, String label, String selection,
      String expr, String unit) {
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", "dset_preload");
    model.result(tag).selection().named(selection);
    model.result(tag).create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  private static double lastIntegral(Model model, String tag) {
    double[][] values = model.result().numerical(tag).getReal();
    return values[0][values[0].length - 1];
  }

  private static void fixMovingContactSource(Model model) {
    try { model.component("comp1").selection().remove("sel_cornea_all_boundaries_dyn"); }
    catch (Exception ignored) {}
    model.component("comp1").selection().create("sel_cornea_all_boundaries_dyn", "Union");
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn")
        .label("All cornea boundaries excluded from moving lid contact source");
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn")
        .set("input", new String[]{"sel_cornea_anterior_surface", "sel_posterior", "sel_limbus"});
    model.component("comp1").selection("sel_lid_contact_source_robust")
        .set("subtract", new String[]{"sel_cornea_all_boundaries_dyn"});
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_contact_source_robust");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
  }

  private static void clean(Model model) {
    for (String tag : model.study().tags()) model.study().remove(tag);
    for (String tag : model.sol().tags()) model.sol().remove(tag);
    for (String tag : model.result().tags()) model.result().remove(tag);
    for (String tag : model.result().dataset().tags()) model.result().dataset().remove(tag);
    for (String tag : model.result().numerical().tags()) model.result().numerical().remove(tag);
    for (String tag : model.result().table().tags()) model.result().table().remove(tag);
  }
}
