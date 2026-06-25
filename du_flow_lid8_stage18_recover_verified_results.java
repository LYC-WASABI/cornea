import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage18_recover_verified_results {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\74_lid8mm_stage22_disconnect_reconnect_transient_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\75_lid8mm_stage22_disconnect_reconnect_transient_complete_postprocessing_results.mph";

  private static void surface(Model model, String pg, String label, String expr) {
    try { model.result().remove(pg); } catch (Exception ignored) {}
    model.result().create(pg, "PlotGroup3D");
    model.result(pg).label(label);
    model.result(pg).set("data", "dset5");
    model.result(pg).create("surf1", "Surface");
    model.result(pg).feature("surf1").set("expr", expr);
  }

  private static void global(Model model, String pg, String label, String[] expr) {
    try { model.result().remove(pg); } catch (Exception ignored) {}
    model.result().create(pg, "PlotGroup1D");
    model.result(pg).label(label);
    model.result(pg).set("data", "dset5");
    model.result(pg).create("glob1", "Global");
    model.result(pg).feature("glob1").set("expr", expr);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("75_lid8mm_stage22_disconnect_reconnect_transient_complete_postprocessing_results.mph");

    surface(model, "pg_stage18_cornea_disp", "Stage 18 cornea displacement", "if(dom==1,solid.disp,NaN)");
    surface(model, "pg_stage18_cornea_mises", "Stage 18 cornea von Mises stress", "if(dom==1,solid.mises,NaN)");
    surface(model, "pg_stage18_lid_disp", "Stage 18 lid wiper displacement", "if(dom==2,solid.disp,NaN)");
    surface(model, "pg_stage18_lid_mises", "Stage 18 lid wiper von Mises stress", "if(dom==2,solid.mises,NaN)");

    try { model.result().remove("pg_stage18_pfilm_replay"); } catch (Exception ignored) {}
    model.result().create("pg_stage18_pfilm_replay", "PlotGroup3D");
    model.result("pg_stage18_pfilm_replay").label("Stage 18 replayed local film pressure on cornea");
    model.result("pg_stage18_pfilm_replay").set("data", "dset5");
    model.result("pg_stage18_pfilm_replay").selection().named("sel_cornea_anterior_surface");
    model.result("pg_stage18_pfilm_replay").create("surf1", "Surface");
    model.result("pg_stage18_pfilm_replay").feature("surf1").set("expr", "comp1.pfilm_replay");

    global(model, "pg_stage18_load_budget", "Stage 18 load budget",
        new String[]{"comp1.W_contact_partitioned", "comp1.W_film_replay", "comp1.W_total_partitioned_local"});
    global(model, "pg_stage18_friction_force", "Stage 18 friction force",
        new String[]{"comp1.F_film_shear_replay+0.02*max(comp1.W_contact_partitioned,0)"});
    global(model, "pg_stage18_friction_coefficient", "Stage 18 apparent friction coefficient",
        new String[]{"comp1.mu_app_partitioned_local"});

    model.save(OUT);
    System.out.println("RECOVERED_STAGE18_VERIFIED_RESULTS=" + OUT);
  }
}
