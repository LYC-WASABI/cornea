import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_constant_force_dynamic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\20_lid8mm_quasistatic_dynamic_sliding_material_frame_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\21_lid8mm_constant_force_dynamic_setup.mph";

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("21_lid8mm_constant_force_dynamic_setup.mph");
    model.param().set("F_target", "0.03[N]", "Target normal contact force throughout scratching");
    model.param().set("q_force_init", "-4.9e-4",
        "Initial dimensionless radial correction estimate; dr_force=q_force*1[mm]");

    if (!hasCpl(model, "intop_cornea_force")) {
      model.component("comp1").cpl().create("intop_cornea_force", "Integration");
    }
    model.component("comp1").cpl("intop_cornea_force")
        .label("Anterior cornea contact-force integration operator");
    model.component("comp1").cpl("intop_cornea_force")
        .selection().named("sel_cornea_anterior_surface");

    try { model.component("comp1").physics().remove("ge_force"); }
    catch (Exception ignored) {}
    model.component("comp1").physics().create("ge_force", "GlobalEquations");
    model.component("comp1").physics("ge_force")
        .label("Constant normal-force radial correction");
    model.component("comp1").physics("ge_force").feature("ge1")
        .set("name", 1, 1, "q_force");
    model.component("comp1").physics("ge_force").feature("ge1")
        .set("equation", 1, 1, "(intop_cornea_force(solid.Tn)-F_target)/F_target");
    model.component("comp1").physics("ge_force").feature("ge1")
        .set("initialValueU", 1, 1, "q_force_init");
    model.component("comp1").physics("ge_force").feature("ge1")
        .set("initialValueUt", 1, 1, "0");
    model.component("comp1").physics("ge_force").feature("ge1")
        .set("description", 1, 1, "Radial lid correction enforcing 0.03 N normal contact force");

    model.component("comp1").variable("var_dynamic_lid_motion")
        .set("dr_force", "q_force*1[mm]");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_dyn)-1)-Z*sin(phi_lid_dyn)"
              + "-dr_force*(Y*cos(phi_lid_dyn)-Z*sin(phi_lid_dyn))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_dyn)+Z*(cos(phi_lid_dyn)-1)"
              + "-dr_force*(Y*sin(phi_lid_dyn)+Z*cos(phi_lid_dyn))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "0");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("GLOBAL_EQUATION=(intop_cornea_force(solid.Tn)-F_target)/F_target");
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
