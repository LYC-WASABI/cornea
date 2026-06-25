import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage13_add_results {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\49_lid8mm_mixed_lubrication_stage11_weakcoupled_film_h12um_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph";

  private static void surface(Model model, String pg, String label, String data,
                              String expr, String unit) {
    model.result().create(pg, "PlotGroup3D");
    model.result(pg).label(label);
    model.result(pg).set("data", data);
    model.result(pg).create("surf1", "Surface");
    model.result(pg).feature("surf1").set("expr", expr);
    model.result(pg).feature("surf1").set("unit", unit);
  }

  private static void global(Model model, String pg, String label, String data,
                             String[] expr, String[] unit, String[] descr) {
    model.result().create(pg, "PlotGroup1D");
    model.result(pg).label(label);
    model.result(pg).set("data", data);
    model.result(pg).create("glob1", "Global");
    model.result(pg).feature("glob1").set("expr", expr);
    model.result(pg).feature("glob1").set("unit", unit);
    model.result(pg).feature("glob1").set("descr", descr);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph");

    surface(model, "pg_solid_disp", "Cornea and lid wiper displacement", "dset_dynamic_slide",
        "solid.disp", "mm");
    surface(model, "pg_solid_mises", "Cornea and lid wiper von Mises stress", "dset_dynamic_slide",
        "solid.mises", "Pa");
    surface(model, "pg_film_base_disp", "Tear-film base displacement: anterior cornea", "dset_dynamic_slide",
        "solid.disp", "mm");
    surface(model, "pg_film_pressure", "Tear-film pressure on anterior cornea", "dset_tff_oneway",
        "pfilm", "Pa");
    surface(model, "pg_film_shear", "Tear-film wall shear stress", "dset_tff_oneway",
        "tau_film_wall", "Pa");

    global(model, "pg_friction_force", "Friction force from tear-film shear", "dset_tff_oneway",
        new String[]{"F_film_shear"}, new String[]{"N"},
        new String[]{"Integrated tear-film wall shear force"});
    global(model, "pg_friction_coefficient", "Apparent friction coefficient from tear-film shear", "dset_tff_oneway",
        new String[]{"mu_app_film_only"}, new String[]{"1"},
        new String[]{"Tear-film shear force divided by 0.03 N total lid load"});
    global(model, "pg_film_load", "Tear-film load sharing", "dset_tff_oneway",
        new String[]{"W_film", "F_total_target-W_film", "F_total_target"},
        new String[]{"N", "N", "N"},
        new String[]{"Tear-film load", "Cornea contact-load budget", "Total lid load"});

    model.save(OUT);
    System.out.println("SAVED_STAGE13_RESULTS=" + OUT);
  }
}
