import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_rectangular_lid_wiper_scan {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_center_directed_lid_load_scan_results.mph";
  private static final String SETUP = DIR + "19_rectangular_curved_lid_wiper_setup.mph";
  private static final String OUT = DIR + "du_cornea_lid_rectangular_curved_lid_wiper_scan_results.mph";

  private static boolean hasSelection(Model model, String tag) {
    for (String t : model.component("comp1").selection().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasVariable(Model model, String tag) {
    for (String t : model.component("comp1").variable().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasStudy(Model model, String tag) {
    for (String t : model.study().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasStudyFeature(Model model, String study, String tag) {
    for (String t : model.study(study).feature().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void replaceLidGeometry(Model model) {
    String[] old = {"int_lid", "blk_lid_window", "dif_lid_shell", "sph_lid_outer", "sph_lid_inner"};
    for (String tag : old) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignore) {}
    }

    model.param().set("delta_lid_indent", "0.005[mm]", "Initial lid wiper indentation into corneal anterior cylinder reference");
    model.param().set("Rlid_in", "Rcor-delta_lid_indent", "Inner radius of cylindrical rectangular lid wiper");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Outer radius of cylindrical rectangular lid wiper");
    model.param().set("lid_arc_half", "asin(lid_width/(2*Rlid_in))", "Half angular width of 1 mm lid wiper band");
    model.param().set("ylid", "Rlid_in*sin(theta_lid)", "Lid wiper center y on cylindrical inner surface");
    model.param().set("zlid", "Rlid_in*cos(theta_lid)", "Lid wiper center z on cylindrical inner surface");

    model.component("comp1").geom("geom1").create("cyl_lid_outer", "Cylinder");
    model.component("comp1").geom("geom1").feature("cyl_lid_outer").label("Lid outer cylinder, x axis");
    model.component("comp1").geom("geom1").feature("cyl_lid_outer").set("r", "Rlid_out");
    model.component("comp1").geom("geom1").feature("cyl_lid_outer").set("h", "lid_length");
    model.component("comp1").geom("geom1").feature("cyl_lid_outer").set("pos", new String[]{"-lid_length/2", "0", "0"});
    model.component("comp1").geom("geom1").feature("cyl_lid_outer").set("axis", new String[]{"1", "0", "0"});

    model.component("comp1").geom("geom1").create("cyl_lid_inner", "Cylinder");
    model.component("comp1").geom("geom1").feature("cyl_lid_inner").label("Lid inner cylinder, x axis");
    model.component("comp1").geom("geom1").feature("cyl_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("cyl_lid_inner").set("h", "lid_length+0.2[mm]");
    model.component("comp1").geom("geom1").feature("cyl_lid_inner").set("pos", new String[]{"-lid_length/2-0.1[mm]", "0", "0"});
    model.component("comp1").geom("geom1").feature("cyl_lid_inner").set("axis", new String[]{"1", "0", "0"});

    model.component("comp1").geom("geom1").create("dif_lid_shell", "Difference");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").label("0.8 mm thick cylindrical lid shell");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input").set(new String[]{"cyl_lid_outer"});
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input2").set(new String[]{"cyl_lid_inner"});

    model.component("comp1").geom("geom1").create("blk_lid_window", "Block");
    model.component("comp1").geom("geom1").feature("blk_lid_window").label("1 mm wide rectangular lid window");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size",
        new String[]{"lid_length+0.02[mm]", "lid_width+0.02[mm]", "tlid+0.35[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos",
        new String[]{"0", "ylid", "zlid+tlid/2"});

    model.component("comp1").geom("geom1").create("int_lid", "Intersection");
    model.component("comp1").geom("geom1").feature("int_lid").label("Rectangular curved lid wiper, 8 x 1 x 0.8 mm");
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"dif_lid_shell", "blk_lid_window"});
    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("createpairs", "on");
  }

  private static void rebuildSelections(Model model) {
    String[] tags = {
      "sel_lid_inner_cyl_hi_dyn", "sel_lid_inner_cyl_lo_dyn", "sel_lid_inner_window_dyn",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_outer_cyl_hi_dyn", "sel_lid_outer_cyl_lo_dyn",
      "sel_lid_load_surface_dyn"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_inner_window_dyn", "Box");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("xmin", "-lid_length/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("xmax", "lid_length/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("ymin", "ylid-lid_width/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("ymax", "ylid+lid_width/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("zmin", "zlid-0.08[mm]");
    model.component("comp1").selection("sel_lid_inner_window_dyn").set("zmax", "zlid+0.16[mm]");

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Explicit");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("One rectangular curved lid wiper inner surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set(new int[]{12, 16});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Explicit");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Rectangular lid outer load surface");
    model.component("comp1").selection("sel_lid_load_surface_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set(new int[]{11, 13, 14});
  }

  private static void updatePhysics(Model model) {
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    if (!hasCpl(model, "intop_lid_load_area")) model.component("comp1").cpl().create("intop_lid_load_area", "Integration");
    model.component("comp1").cpl("intop_lid_load_area").selection().named("sel_lid_load_surface_dyn");

    if (!hasVariable(model, "var_lid_center_load")) model.component("comp1").variable().create("var_lid_center_load");
    model.component("comp1").variable("var_lid_center_load").set("r_center_lid", "sqrt(x^2+y^2+z^2)");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cx", "-x/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cy", "-y/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cz", "-z/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("A_lid_load", "intop_lid_load_area(1)");
    model.component("comp1").variable("var_lid_center_load").set("p_lid_center", "F_lid/A_lid_load");

    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("forceReferenceArea",
        new String[]{"p_lid_center*e_lid_cx", "p_lid_center*e_lid_cy", "p_lid_center*e_lid_cz"});
  }

  private static void setupStudy(Model model) {
    if (!hasStudy(model, "std_rect_lid")) model.study().create("std_rect_lid");
    model.study("std_rect_lid").label("Quasi-static scan with rectangular curved lid wiper");
    if (!hasStudyFeature(model, "std_rect_lid", "param")) model.study("std_rect_lid").feature().create("param", "Parametric");
    model.study("std_rect_lid").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_rect_lid").feature("param").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.study("std_rect_lid").feature("param").set("punit", new String[]{"deg"});
    if (!hasStudyFeature(model, "std_rect_lid", "stat")) model.study("std_rect_lid").feature().create("stat", "Stationary");
    model.study("std_rect_lid").feature("stat").activate("solid", true);
    model.study("std_rect_lid").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.1);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");
  }

  private static void addResults(Model model) {
    try { model.result().remove("pg_rect_contact_pressure"); } catch (Exception ignore) {}
    try { model.result().remove("pg_rect_gap_distance"); } catch (Exception ignore) {}
    try { model.result().numerical().remove("int_rect_contact_pressure_cornea"); } catch (Exception ignore) {}
    try { model.result().table().remove("tbl_rect_contact_pressure_int"); } catch (Exception ignore) {}

    model.result().create("pg_rect_contact_pressure", "PlotGroup3D");
    model.result("pg_rect_contact_pressure").label("Rectangular lid contact pressure");
    model.result("pg_rect_contact_pressure").set("data", "dset2");
    model.result("pg_rect_contact_pressure").feature().create("surf1", "Surface");
    model.result("pg_rect_contact_pressure").feature("surf1").set("expr", "solid.Tn");
    model.result("pg_rect_contact_pressure").feature("surf1").set("unit", "Pa");

    model.result().create("pg_rect_gap_distance", "PlotGroup3D");
    model.result("pg_rect_gap_distance").label("Rectangular lid gap distance");
    model.result("pg_rect_gap_distance").set("data", "dset2");
    model.result("pg_rect_gap_distance").feature().create("surf1", "Surface");
    model.result("pg_rect_gap_distance").feature("surf1").set("expr", "solid.gap");
    model.result("pg_rect_gap_distance").feature("surf1").set("unit", "m");

    model.result().table().create("tbl_rect_contact_pressure_int", "Table");
    model.result().numerical().create("int_rect_contact_pressure_cornea", "IntSurface");
    model.result().numerical("int_rect_contact_pressure_cornea").set("data", "dset2");
    model.result().numerical("int_rect_contact_pressure_cornea").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_rect_contact_pressure_cornea").set("expr", "solid.Tn");
    model.result().numerical("int_rect_contact_pressure_cornea").set("unit", "N");
    model.result().numerical("int_rect_contact_pressure_cornea").set("table", "tbl_rect_contact_pressure_int");
    model.result().numerical("int_rect_contact_pressure_cornea").setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    model.param().set("theta_lid", "30[deg]");
    replaceLidGeometry(model);
    model.component("comp1").geom("geom1").run();
    rebuildSelections(model);
    updatePhysics(model);
    setupStudy(model);
    System.out.println("inner source at theta_lid=30: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    model.save(SETUP);
    model.study("std_rect_lid").run();
    addResults(model);
    model.save(OUT);
  }
}
