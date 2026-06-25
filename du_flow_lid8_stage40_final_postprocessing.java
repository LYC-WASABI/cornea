import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage40_final_postprocessing {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\110_lid8mm_stage39_reaction_iteration6_full_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void surface(Model model, String tag, String label, String selection, String expr) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", "dset5");
    model.result(tag).selection().named(selection);
    model.result(tag).create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
  }

  private static void global(Model model, String tag, String label, String[] expr) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
    model.result().create(tag, "PlotGroup1D");
    model.result(tag).label(label);
    model.result(tag).set("data", "dset5");
    model.result(tag).create("glob1", "Global");
    model.result(tag).feature("glob1").set("expr", expr);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph");

    if (!hasCpl(model, "intop_lid_support_final")) {
      model.component("comp1").cpl().create("intop_lid_support_final", "Integration");
    }
    model.component("comp1").cpl("intop_lid_support_final").selection().named("sel_lid_outer_support");
    model.component("comp1").cpl("intop_lid_support_final").label("Final total radial support reaction");
    model.component("comp1").variable().create("var_stage40_final");
    model.component("comp1").variable("var_stage40_final").label("Final calibrated dynamic lubrication outputs");
    model.component("comp1").variable("var_stage40_final").set("W_total_support_final",
        "intop_lid_support_final(-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2))");
    model.component("comp1").variable("var_stage40_final").set("W_contact_budget_final",
        "max(W_total_support_final-W_film_replay,0)");
    model.component("comp1").variable("var_stage40_final").set("F_friction_final",
        "F_film_shear_replay+0.02*W_contact_budget_final");
    model.component("comp1").variable("var_stage40_final").set("mu_app_final",
        "F_friction_final/F_total_target");

    surface(model, "pg_final_cornea_disp", "Final calibrated cornea anterior displacement",
        "sel_cornea_anterior_surface", "solid.disp");
    surface(model, "pg_final_cornea_mises", "Final calibrated cornea anterior von Mises stress",
        "sel_cornea_anterior_surface", "solid.mises");
    surface(model, "pg_final_lid_disp", "Final calibrated lid wiper contact-surface displacement",
        "sel_lid_wiper_inner_surface", "solid.disp");
    surface(model, "pg_final_lid_mises", "Final calibrated lid wiper contact-surface von Mises stress",
        "sel_lid_wiper_inner_surface", "solid.mises");
    surface(model, "pg_final_film_base_disp", "Final tear-film base displacement on anterior cornea",
        "sel_cornea_anterior_surface", "solid.disp");
    surface(model, "pg_final_pfilm_replay", "Final replayed local tear-film pressure",
        "sel_cornea_anterior_surface", "pfilm_replay");
    surface(model, "pg_final_contact_pressure", "Final contact pressure",
        "sel_cornea_anterior_surface", "if(isdefined(solid.Tn),solid.Tn,0)");
    surface(model, "pg_final_gap", "Final geometric gap distance",
        "sel_cornea_anterior_surface", "geomgap_dst_cp_lid_cornea");

    global(model, "pg_final_load_budget", "Final normal-load budget",
        new String[]{"W_total_support_final", "W_film_replay", "W_contact_budget_final"});
    global(model, "pg_final_friction_force", "Final friction force",
        new String[]{"F_friction_final", "F_film_shear_replay"});
    global(model, "pg_final_mu", "Final apparent friction coefficient",
        new String[]{"mu_app_final"});

    model.save(OUT);
    System.out.println("SAVED_STAGE40_FINAL_POSTPROCESSING=" + OUT);
  }
}
