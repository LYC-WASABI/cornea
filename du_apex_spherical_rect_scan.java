import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_apex_spherical_rect_scan {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_apex_spherical_rect_lid_wiper.mph";
  private static final String SETUP = DIR + "20_apex_spherical_rect_lid_position_scan_setup.mph";
  private static final String OUT = DIR + "du_cornea_lid_apex_spherical_rect_lid_position_scan_results.mph";

  private static boolean hasStudy(Model model, String tag) {
    for (String t : model.study().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasStudyFeature(Model model, String study, String tag) {
    for (String t : model.study(study).feature().tags()) if (t.equals(tag)) return true;
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

  private static void removeIfPresent(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignore) {}
  }

  private static void updateGeometryForScan(Model model) {
    model.param().set("theta_lid", "30[deg]", "Lid wiper position; 30..-30 deg correspond to paper 10..70 deg");
    model.param().set("Rlid_in", "Rcor", "Lid wiper lower surface radius equals anterior corneal radius");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid wiper outer radius");
    model.param().set("lid_arc_length", "8[mm]", "Spherical rectangular contact arc length");
    model.param().set("lid_arc_width", "1[mm]", "Spherical rectangular contact width");
    model.param().set("lid_cut_length", "2*Rlid_in*sin(lid_arc_length/(2*Rlid_in))", "Block chord for 8 mm spherical arc length");
    model.param().set("lid_cut_width", "2*Rlid_in*sin(lid_arc_width/(2*Rlid_in))", "Block chord for 1 mm spherical arc width");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Lid center y coordinate on anterior corneal sphere");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Lid center z coordinate on anterior corneal sphere");

    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size",
        new String[]{"lid_cut_length", "lid_cut_width", "tlid+0.35[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos",
        new String[]{"0", "ylid", "zlid+tlid/2"});
  }

  private static void updateMovingSelections(Model model) {
    String[] remove = {
      "sel_lid_box_apex", "sel_lid_inner_ball_apex", "sel_lid_outer_ball_apex",
      "sel_lid_inner_candidates_apex", "sel_lid_outer_candidates_apex",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn",
      "sel_lid_dom", "sel_contact_zone"
    };
    for (String tag : remove) {
      try { model.component("comp1").selection().remove(tag); } catch (Exception ignore) {}
    }

    model.component("comp1").selection().create("sel_lid_box_apex", "Box");
    model.component("comp1").selection("sel_lid_box_apex").label("Moving spherical rectangular lid bounding box");
    model.component("comp1").selection("sel_lid_box_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_apex").set("xmin", "-lid_cut_length/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("xmax", "lid_cut_length/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("ymin", "ylid-lid_cut_width/2-0.08[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("ymax", "ylid+lid_cut_width/2+0.08[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("zmin", "zlid-0.08[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("sel_lid_inner_ball_apex", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("r", "Rlid_in+0.02[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_apex").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_inner_candidates_apex", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_candidates_apex").set("input",
        new String[]{"sel_lid_inner_ball_apex", "sel_lid_box_apex"});

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Moving lower surface: spherical rectangular contact patch");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add",
        new String[]{"sel_lid_inner_candidates_apex"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract",
        new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_outer_ball_apex", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("r", "Rlid_out+0.02[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_apex").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_candidates_apex", "Intersection");
    model.component("comp1").selection("sel_lid_outer_candidates_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_candidates_apex").set("input",
        new String[]{"sel_lid_outer_ball_apex", "sel_lid_box_apex"});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Moving outer surface for 0.03 N center-directed load");
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("add",
        new String[]{"sel_lid_outer_candidates_apex"});
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("subtract",
        new String[]{"sel_lid_wiper_inner_surface_dyn", "sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_dom", "Box");
    model.component("comp1").selection("sel_lid_dom").set("entitydim", 3);
    model.component("comp1").selection("sel_lid_dom").set("xmin", "-lid_cut_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("xmax", "lid_cut_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymin", "ylid-lid_cut_width/2-0.12[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymax", "ylid+lid_cut_width/2+0.12[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmin", "zlid-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmax", "zlid+tlid+0.25[mm]");

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
    if (!hasStudy(model, "std_apex_rect_scan")) model.study().create("std_apex_rect_scan");
    model.study("std_apex_rect_scan").label("Quasi-static position scan: spherical rectangular lid wiper");
    if (!hasStudyFeature(model, "std_apex_rect_scan", "param")) {
      model.study("std_apex_rect_scan").feature().create("param", "Parametric");
    }
    model.study("std_apex_rect_scan").feature("param").label("theta_lid scan: paper 10-70 deg");
    model.study("std_apex_rect_scan").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_apex_rect_scan").feature("param").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.study("std_apex_rect_scan").feature("param").set("punit", new String[]{"deg"});
    if (!hasStudyFeature(model, "std_apex_rect_scan", "stat")) {
      model.study("std_apex_rect_scan").feature().create("stat", "Stationary");
    }
    model.study("std_apex_rect_scan").feature("stat").activate("solid", true);
    model.study("std_apex_rect_scan").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.03);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");
  }

  private static void addResults(Model model) {
    removeIfPresent(model, "pg_apex_rect_contact_pressure");
    removeIfPresent(model, "pg_apex_rect_gap");
    try { if (hasNumerical(model, "int_apex_rect_contact_pressure")) model.result().numerical().remove("int_apex_rect_contact_pressure"); } catch (Exception ignore) {}
    try { if (hasTable(model, "tbl_apex_rect_contact_pressure")) model.result().table().remove("tbl_apex_rect_contact_pressure"); } catch (Exception ignore) {}

    model.result().create("pg_apex_rect_contact_pressure", "PlotGroup3D");
    model.result("pg_apex_rect_contact_pressure").label("Contact pressure: spherical rectangular lid wiper");
    model.result("pg_apex_rect_contact_pressure").set("data", "dset2");
    model.result("pg_apex_rect_contact_pressure").feature().create("surf1", "Surface");
    model.result("pg_apex_rect_contact_pressure").feature("surf1").set("expr", "solid.Tn");
    model.result("pg_apex_rect_contact_pressure").feature("surf1").set("unit", "Pa");

    model.result().create("pg_apex_rect_gap", "PlotGroup3D");
    model.result("pg_apex_rect_gap").label("Gap distance: spherical rectangular lid wiper");
    model.result("pg_apex_rect_gap").set("data", "dset2");
    model.result("pg_apex_rect_gap").feature().create("surf1", "Surface");
    model.result("pg_apex_rect_gap").feature("surf1").set("expr", "solid.gap");
    model.result("pg_apex_rect_gap").feature("surf1").set("unit", "m");

    model.result().table().create("tbl_apex_rect_contact_pressure", "Table");
    model.result().numerical().create("int_apex_rect_contact_pressure", "IntSurface");
    model.result().numerical("int_apex_rect_contact_pressure").set("data", "dset2");
    model.result().numerical("int_apex_rect_contact_pressure").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_apex_rect_contact_pressure").set("expr", "solid.Tn");
    model.result().numerical("int_apex_rect_contact_pressure").set("unit", "N");
    model.result().numerical("int_apex_rect_contact_pressure").set("table", "tbl_apex_rect_contact_pressure");
    try { model.result().numerical("int_apex_rect_contact_pressure").setResult(); } catch (Exception e) {
      System.out.println("Contact integral table creation skipped: " + e.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    updateGeometryForScan(model);
    model.component("comp1").geom("geom1").run();
    updateMovingSelections(model);
    updatePhysics(model);
    model.component("comp1").mesh("mesh1").run();
    System.out.println("scan source at theta=30: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("scan load at theta=30: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));
    setupStudy(model);
    model.save(SETUP);
    model.study("std_apex_rect_scan").run();
    addResults(model);
    model.save(OUT);
  }
}
