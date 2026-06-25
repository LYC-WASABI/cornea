import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_rotating_lid_wiper_scan {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_apex_spherical_rect_lid_wiper.mph";
  private static final String SETUP = DIR + "21_rotating_lid_wiper_setup.mph";
  private static final String OUT = DIR + "du_cornea_lid_rotating_lid_wiper_position_scan_results.mph";

  private static boolean hasStudy(Model model, String tag) {
    for (String t : model.study().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasStudyFeature(Model model, String study, String tag) {
    for (String t : model.study(study).feature().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasSelection(Model model, String tag) {
    for (String t : model.component("comp1").selection().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasResult(Model model, String tag) {
    for (String t : model.result().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasTable(Model model, String tag) {
    for (String t : model.result().table().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasNumerical(Model model, String tag) {
    for (String t : model.result().numerical().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void configureApexLidThenRotate(Model model) {
    model.param().set("theta_lid", "30[deg]", "Whole lid wiper rotation about globe center; 30..-30 deg correspond to paper 10..70 deg");
    model.param().set("Rlid_in", "Rcor", "Lid lower surface radius equals corneal anterior radius");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid outer radius");
    model.param().set("lid_arc_length", "8[mm]", "Spherical rectangular contact arc length");
    model.param().set("lid_arc_width", "1[mm]", "Spherical rectangular contact width");
    model.param().set("lid_cut_length", "2*Rlid_in*sin(lid_arc_length/(2*Rlid_in))", "Block chord for 8 mm spherical arc length");
    model.param().set("lid_cut_width", "2*Rlid_in*sin(lid_arc_width/(2*Rlid_in))", "Block chord for 1 mm spherical arc width");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Center y coordinate after whole-lid rotation");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Center z coordinate after whole-lid rotation");

    try { model.component("comp1").geom("geom1").feature().remove("rot_lid"); } catch (Exception ignore) {}

    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size",
        new String[]{"lid_cut_length", "lid_cut_width", "tlid+0.35[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos",
        new String[]{"0", "0", "Rcor+tlid/2"});

    model.component("comp1").geom("geom1").create("rot_lid", "Rotate");
    model.component("comp1").geom("geom1").feature("rot_lid").label("Whole lid wiper rotation about globe center");
    model.component("comp1").geom("geom1").feature("rot_lid").selection("input").set(new String[]{"int_lid"});
    model.component("comp1").geom("geom1").feature("rot_lid").set("pos", new String[]{"0", "0", "0"});
    model.component("comp1").geom("geom1").feature("rot_lid").set("axis", new String[]{"1", "0", "0"});
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    try { model.component("comp1").geom("geom1").feature("rot_lid").set("keep", "off"); } catch (Exception ignore) {}

    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("createpairs", "on");
  }

  private static void rebuildMovingSelections(Model model) {
    String[] tags = {
      "sel_lid_box_rot", "sel_lid_inner_ball_rot", "sel_lid_outer_ball_rot",
      "sel_lid_inner_candidates_rot", "sel_lid_outer_candidates_rot",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn",
      "sel_lid_dom", "sel_contact_zone"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_box_rot", "Box");
    model.component("comp1").selection("sel_lid_box_rot").label("Moving box following whole rotated lid");
    model.component("comp1").selection("sel_lid_box_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_rot").set("xmin", "-lid_cut_length/2-0.08[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("xmax", "lid_cut_length/2+0.08[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymin", "ylid-lid_cut_width/2-0.12[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymax", "ylid+lid_cut_width/2+0.12[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("zmin", "zlid-0.12[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("zmax", "zlid+tlid+0.30[mm]");

    model.component("comp1").selection().create("sel_lid_inner_ball_rot", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("r", "Rlid_in+0.02[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_inner_candidates_rot", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_candidates_rot").set("input",
        new String[]{"sel_lid_inner_ball_rot", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Rotated lid lower rectangular spherical surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add",
        new String[]{"sel_lid_inner_candidates_rot"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract",
        new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_outer_ball_rot", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("r", "Rlid_out+0.02[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_candidates_rot", "Intersection");
    model.component("comp1").selection("sel_lid_outer_candidates_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_candidates_rot").set("input",
        new String[]{"sel_lid_outer_ball_rot", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Rotated lid outer surface for 0.03 N center load");
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("add",
        new String[]{"sel_lid_outer_candidates_rot"});
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("subtract",
        new String[]{"sel_lid_wiper_inner_surface_dyn", "sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_dom", "Box");
    model.component("comp1").selection("sel_lid_dom").set("entitydim", 3);
    model.component("comp1").selection("sel_lid_dom").set("xmin", "-lid_cut_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("xmax", "lid_cut_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymin", "ylid-lid_cut_width/2-0.15[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymax", "ylid+lid_cut_width/2+0.15[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmin", "zlid-0.15[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmax", "zlid+tlid+0.35[mm]");

    model.component("comp1").selection().create("sel_contact_zone", "Box");
    model.component("comp1").selection("sel_contact_zone").set("entitydim", 3);
    model.component("comp1").selection("sel_contact_zone").set("xmin", "-lid_cut_length/2-0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("xmax", "lid_cut_length/2+0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymin", "ylid-lid_cut_width/2-0.25[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymax", "ylid+lid_cut_width/2+0.25[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmin", "zlid-0.5[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmax", "zlid+tlid+0.35[mm]");
  }

  private static void updatePhysics(Model model) {
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    model.component("comp1").cpl("intop_lid_load_area").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("forceReferenceArea",
        new String[]{"p_lid_center*e_lid_cx", "p_lid_center*e_lid_cy", "p_lid_center*e_lid_cz"});
  }

  private static void setupStudy(Model model) {
    if (!hasStudy(model, "std_rot_lid")) model.study().create("std_rot_lid");
    model.study("std_rot_lid").label("Quasi-static scan: whole rotating lid wiper");
    if (!hasStudyFeature(model, "std_rot_lid", "param")) model.study("std_rot_lid").feature().create("param", "Parametric");
    model.study("std_rot_lid").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_rot_lid").feature("param").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.study("std_rot_lid").feature("param").set("punit", new String[]{"deg"});
    if (!hasStudyFeature(model, "std_rot_lid", "stat")) model.study("std_rot_lid").feature().create("stat", "Stationary");
    model.study("std_rot_lid").feature("stat").activate("solid", true);
    model.study("std_rot_lid").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.03);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");
  }

  private static void addResults(Model model) {
    try { if (hasResult(model, "pg_rot_contact_pressure")) model.result().remove("pg_rot_contact_pressure"); } catch (Exception ignore) {}
    try { if (hasResult(model, "pg_rot_gap")) model.result().remove("pg_rot_gap"); } catch (Exception ignore) {}
    try { if (hasNumerical(model, "int_rot_contact_pressure")) model.result().numerical().remove("int_rot_contact_pressure"); } catch (Exception ignore) {}
    try { if (hasTable(model, "tbl_rot_contact_pressure")) model.result().table().remove("tbl_rot_contact_pressure"); } catch (Exception ignore) {}

    model.result().create("pg_rot_contact_pressure", "PlotGroup3D");
    model.result("pg_rot_contact_pressure").label("Contact pressure: whole rotating lid wiper");
    model.result("pg_rot_contact_pressure").set("data", "dset2");
    model.result("pg_rot_contact_pressure").feature().create("surf1", "Surface");
    model.result("pg_rot_contact_pressure").feature("surf1").set("expr", "solid.Tn");
    model.result("pg_rot_contact_pressure").feature("surf1").set("unit", "Pa");

    model.result().create("pg_rot_gap", "PlotGroup3D");
    model.result("pg_rot_gap").label("Gap distance: whole rotating lid wiper");
    model.result("pg_rot_gap").set("data", "dset2");
    model.result("pg_rot_gap").feature().create("surf1", "Surface");
    model.result("pg_rot_gap").feature("surf1").set("expr", "solid.gap");
    model.result("pg_rot_gap").feature("surf1").set("unit", "m");

    model.result().table().create("tbl_rot_contact_pressure", "Table");
    model.result().numerical().create("int_rot_contact_pressure", "IntSurface");
    model.result().numerical("int_rot_contact_pressure").set("data", "dset2");
    model.result().numerical("int_rot_contact_pressure").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_rot_contact_pressure").set("expr", "solid.Tn");
    model.result().numerical("int_rot_contact_pressure").set("unit", "N");
    model.result().numerical("int_rot_contact_pressure").set("table", "tbl_rot_contact_pressure");
    try { model.result().numerical("int_rot_contact_pressure").setResult(); } catch (Exception e) {
      System.out.println("Contact integral table creation skipped: " + e.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    configureApexLidThenRotate(model);
    model.component("comp1").geom("geom1").run();
    rebuildMovingSelections(model);
    updatePhysics(model);
    model.component("comp1").mesh("mesh1").run();
    System.out.println("rotated source at theta=30: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("rotated load at theta=30: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));
    setupStudy(model);
    model.save(SETUP);
    model.study("std_rot_lid").run();
    addResults(model);
    model.save(OUT);
  }
}
