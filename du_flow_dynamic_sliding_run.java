import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_dynamic_sliding_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\07_quasistatic_time_displacement_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_quasistatic_time_displacement_minus35_to_plus35_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void addPlot(Model model, String tag, String label, String dataset,
      String selection, String expr, String unit) {
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", dataset);
    model.result(tag).selection().named(selection);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  private static void addSurfaceIntegral(Model model, String tag, String table, String label,
      String dataset, String selection, String expr, String unit) {
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
  }

  private static void addGlobal(Model model, String tag, String table, String label,
      String dataset, String expr, String unit) {
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_quasistatic_time_displacement_minus35_to_plus35_results.mph");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.study("std_dynamic_slide").createAutoSequences("sol");
    String dynamicSolution = lastSolution(model);
    model.sol(dynamicSolution).feature("t1").feature("fc1").set("maxiter", 50);
    model.sol(dynamicSolution).runAll();

    model.result().dataset().create("dset_dynamic_slide", "Solution");
    model.result().dataset("dset_dynamic_slide").label("Dynamic sliding solution: -35 deg to +35 deg");
    model.result().dataset("dset_dynamic_slide").set("solution", dynamicSolution);

    addPlot(model, "pg_dyn_cornea_disp", "Dynamic cornea anterior displacement",
        "dset_dynamic_slide", "sel_cornea_anterior_surface", "solid.disp", "mm");
    addPlot(model, "pg_dyn_lid_disp", "Dynamic lid contact displacement",
        "dset_dynamic_slide", "sel_lid_contact_source_robust", "solid.disp", "mm");
    addPlot(model, "pg_dyn_cornea_mises", "Dynamic cornea anterior von Mises stress",
        "dset_dynamic_slide", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addPlot(model, "pg_dyn_lid_mises", "Dynamic lid contact von Mises stress",
        "dset_dynamic_slide", "sel_lid_contact_source_robust", "solid.mises", "Pa");
    addPlot(model, "pg_dyn_contact_pressure", "Dynamic contact pressure on cornea anterior surface",
        "dset_dynamic_slide", "sel_cornea_anterior_surface", "solid.Tn", "Pa");

    addSurfaceIntegral(model, "int_dyn_contact_force", "tbl_dyn_contact_force",
        "Dynamic anterior cornea intop(solid.Tn)", "dset_dynamic_slide",
        "sel_cornea_anterior_surface", "solid.Tn", "N");
    addGlobal(model, "eval_dyn_theta", "tbl_dyn_theta", "Dynamic lid physical angle",
        "dset_dynamic_slide", "theta_lid_physical", "deg");
    addGlobal(model, "eval_dyn_slide_fraction", "tbl_dyn_slide_fraction", "Dynamic lid slide fraction",
        "dset_dynamic_slide", "slide_fraction", "1");

    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
