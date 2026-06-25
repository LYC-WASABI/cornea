import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_static_calibration_coarse {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";
  private static final String SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\19_lid8mm_static_preload_calibration_setup.mph";
  private static final String THETA_LIST =
      "0 -2.5 -5 -7.5 -10 -12.5 -15 -17.5 -20 -22.5 -25 -27.5 -30 -32.5 -35";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    double[] deltas = new double[]{0.045, 0.055, 0.065, 0.075, 0.085, 0.095};
    Model setup = configuredModel("Setup", 0.070);
    setup.save(SETUP);
    ModelUtil.remove("Setup");
    System.out.println("SAVED_SETUP=" + SETUP);
    for (double delta : deltas) {
      String tag = "M" + Math.round(delta * 1000);
      try {
        Model model = configuredModel(tag, delta);
        double force = runAndEvaluate(model);
        System.out.printf("CALIBRATION delta_indent=%.6f[mm] force=%.12f[N]%n", delta, force);
        ModelUtil.remove(tag);
      } catch (Exception ex) {
        System.out.printf("CALIBRATION_FAILED delta_indent=%.6f[mm] message=%s%n",
            delta, ex.getMessage());
        try { ModelUtil.remove(tag); } catch (Exception ignored) {}
      }
    }
  }

  private static Model configuredModel(String tag, double deltaMm) throws Exception {
    Model model = ModelUtil.load(tag, IN);
    clean(model);
    model.param().set("s_lid", "8[mm]");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))");
    model.param().set("delta_indent", String.format(java.util.Locale.US, "%.6f[mm]", deltaMm));
    model.param().set("theta_lid", "0[deg]");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    fixMovingContactSource(model);
    model.component("comp1").mesh("mesh1").run();
    createStudy(model);
    return model;
  }

  private static void createStudy(Model model) {
    model.study().create("std_preload");
    model.study("std_preload").label("8 mm static preload calibration at scratch start");
    model.study("std_preload").create("param", "Parametric");
    model.study("std_preload").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_preload").feature("param").set("plistarr", new String[]{THETA_LIST});
    model.study("std_preload").feature("param").set("punit", new String[]{"deg"});
    model.study("std_preload").create("stat", "Stationary");
    model.study("std_preload").feature("stat").set("geometricNonlinearity", true);
  }

  private static double runAndEvaluate(Model model) {
    model.study("std_preload").run();
    model.result().dataset().create("dset_preload", "Solution");
    model.result().dataset("dset_preload").set("solution", "sol1");
    model.result().numerical().create("int_front_force", "IntSurface");
    model.result().numerical("int_front_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_front_force").set("data", "dset_preload");
    model.result().numerical("int_front_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_front_force").set("unit", new String[]{"N"});
    model.result().numerical("int_front_force").setResult();
    double[][] values = model.result().numerical("int_front_force").getReal();
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
