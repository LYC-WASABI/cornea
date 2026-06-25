import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage13_add_separate_views {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\58_lid8mm_stage13_complete_postprocessing_separate_views_results.mph";

  private static void surface(Model model, String pg, String label, String expr, String unit) {
    model.result().create(pg, "PlotGroup3D");
    model.result(pg).label(label);
    model.result(pg).set("data", "dset_dynamic_slide");
    model.result(pg).create("surf1", "Surface");
    model.result(pg).feature("surf1").set("expr", expr);
    model.result(pg).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("58_lid8mm_stage13_complete_postprocessing_separate_views_results.mph");
    surface(model, "pg_cornea_disp_only", "Cornea displacement only", "if(dom==1,solid.disp,NaN)", "mm");
    surface(model, "pg_cornea_mises_only", "Cornea von Mises stress only", "if(dom==1,solid.mises,NaN)", "Pa");
    surface(model, "pg_lid_disp_only", "Lid wiper displacement only", "if(dom==2,solid.disp,NaN)", "mm");
    surface(model, "pg_lid_mises_only", "Lid wiper von Mises stress only", "if(dom==2,solid.mises,NaN)", "Pa");
    model.save(OUT);
    System.out.println("SAVED_STAGE13_SEPARATE_VIEWS=" + OUT);
  }
}
