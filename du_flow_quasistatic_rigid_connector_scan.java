import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_quasistatic_rigid_connector_scan {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\02_dynamic_preload_minus35_fixed_results.mph";
  private static final String SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\06_quasistatic_whole_lid_displacement_scan_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_quasistatic_whole_lid_displacement_minus35_to_plus35_results.mph";

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

  private static void addValue(Model model, String tag, String table, String label,
      String type, String dataset, String selection, String expr, String unit) {
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).label(label);
    if (selection != null) model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_quasistatic_whole_lid_displacement_minus35_to_plus35_results.mph");
    model.param().set("theta_lid", "-35[deg]", "Initial geometry position for whole-lid sliding");
    model.param().set("phi_drive", "0[deg]", "Additional Rigid Connector rotation");
    model.param().set("theta_lid_physical_qs", "-35[deg]-phi_drive",
        "Physical lid position angle along cornea");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();

    try { model.component("comp1").physics("solid").feature().remove("fix_lid_outer_support"); }
    catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("disp_lid_qs"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("disp_lid_qs", "Displacement2", 2);
    model.component("comp1").physics("solid").feature("disp_lid_qs")
        .label("Quasi-static analytical whole-lid rotation from -35 deg to +35 deg");
    model.component("comp1").physics("solid").feature("disp_lid_qs")
        .selection().named("sel_lid_outer_support");
    model.component("comp1").physics("solid").feature("disp_lid_qs")
        .set("Direction", new String[]{"prescribed", "prescribed", "prescribed"});
    model.component("comp1").physics("solid").feature("disp_lid_qs").set("U0",
        new String[]{"0", "y*(cos(phi_drive)-1)-z*sin(phi_drive)",
            "y*sin(phi_drive)+z*(cos(phi_drive)-1)"});

    model.study().create("std_qs_slide");
    model.study("std_qs_slide").label("Quasi-static whole-lid analytical displacement scan: -35 deg to +35 deg");
    model.study("std_qs_slide").create("param", "Parametric");
    model.study("std_qs_slide").feature("param").set("pname", new String[]{"phi_drive"});
    model.study("std_qs_slide").feature("param").set("plistarr",
        new String[]{"range(0,-0.25,-70)"});
    model.study("std_qs_slide").feature("param").set("punit", new String[]{"deg"});
    model.study("std_qs_slide").create("stat", "Stationary");
    model.study("std_qs_slide").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_qs_slide").feature("stat").set("useinitsol", "on");
    model.study("std_qs_slide").feature("stat").set("initmethod", "sol");
    model.study("std_qs_slide").feature("stat").set("initstudy", "std_preload");
    model.study("std_qs_slide").feature("stat").set("initstudystep", "stat");
    model.study("std_qs_slide").feature("stat").set("initsol", "sol1");
    model.study("std_qs_slide").feature("stat").set("initsoluse", "sol1");
    model.study("std_qs_slide").feature("stat").set("initsolusesolnum", 15);
    model.study("std_qs_slide").createAutoSequences("sol");
    String qsSolution = lastSolution(model);
    model.sol(qsSolution).feature("s1").feature("fc1").set("maxiter", 80);
    model.save(SETUP);

    model.sol(qsSolution).runAll();
    model.result().dataset().create("dset_qs_slide", "Solution");
    model.result().dataset("dset_qs_slide").label("Quasi-static whole-lid sliding solution");
    model.result().dataset("dset_qs_slide").set("solution", qsSolution);

    addPlot(model, "pg_qs_cornea_disp", "Quasi-static sliding: cornea anterior displacement",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.disp", "mm");
    addPlot(model, "pg_qs_lid_disp", "Quasi-static sliding: lid contact displacement",
        "dset_qs_slide", "sel_lid_contact_source_robust", "solid.disp", "mm");
    addPlot(model, "pg_qs_cornea_mises", "Quasi-static sliding: cornea anterior von Mises stress",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addPlot(model, "pg_qs_lid_mises", "Quasi-static sliding: lid contact von Mises stress",
        "dset_qs_slide", "sel_lid_contact_source_robust", "solid.mises", "Pa");
    addPlot(model, "pg_qs_contact_pressure", "Quasi-static sliding: contact pressure",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.Tn", "Pa");

    addValue(model, "int_qs_contact_force", "tbl_qs_contact_force",
        "Quasi-static sliding anterior cornea intop(solid.Tn)", "IntSurface",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.Tn", "N");
    addValue(model, "eval_qs_theta", "tbl_qs_theta", "Quasi-static lid physical angle",
        "EvalGlobal", "dset_qs_slide", null, "theta_lid_physical_qs", "deg");
    addValue(model, "max_qs_cornea_disp", "tbl_max_qs_cornea_disp",
        "Quasi-static maximum cornea displacement", "MaxSurface",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.disp", "mm");
    addValue(model, "max_qs_lid_disp", "tbl_max_qs_lid_disp",
        "Quasi-static maximum lid contact displacement", "MaxSurface",
        "dset_qs_slide", "sel_lid_contact_source_robust", "solid.disp", "mm");
    addValue(model, "max_qs_cornea_mises", "tbl_max_qs_cornea_mises",
        "Quasi-static maximum cornea von Mises stress", "MaxSurface",
        "dset_qs_slide", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addValue(model, "max_qs_lid_mises", "tbl_max_qs_lid_mises",
        "Quasi-static maximum lid contact von Mises stress", "MaxSurface",
        "dset_qs_slide", "sel_lid_contact_source_robust", "solid.mises", "Pa");
    model.save(OUT);
    System.out.println("FINAL contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("FINAL outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    System.out.println("Saved: " + OUT);
  }
}
