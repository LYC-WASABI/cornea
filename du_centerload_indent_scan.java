import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_centerload_indent_scan {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_explicit_pair_final change.mph";
  private static final String SETUP = DIR + "18_center_directed_lid_load_setup_from_change.mph";
  private static final String OUT = DIR + "du_cornea_lid_center_directed_lid_load_scan_results.mph";

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

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasVariable(Model model, String tag) {
    for (String t : model.component("comp1").variable().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasPhysicsFeature(Model model, String tag) {
    for (String t : model.component("comp1").physics("solid").feature().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void rebuildDynamicSelections(Model model) {
    String[] tags = {
      "sel_lid_inner_ball_dyn",
      "sel_lid_box_dyn",
      "sel_lid_inner_candidates_dyn",
      "sel_lid_wiper_inner_surface_dyn",
      "sel_lid_outer_ball_hi_dyn",
      "sel_lid_outer_ball_lo_dyn",
      "sel_lid_outer_candidates_dyn",
      "sel_lid_load_surface_dyn"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_box_dyn", "Box");
    model.component("comp1").selection("sel_lid_box_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_dyn").set("xmin", "-lid_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("xmax", "lid_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("ymin", "ylid-lid_width/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("ymax", "ylid+lid_width/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("zmin", "zlid-0.15[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("sel_lid_inner_ball_dyn", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("r", "Rlid_in+0.04[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_inner_candidates_dyn", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_candidates_dyn").set("input",
        new String[]{"sel_lid_inner_ball_dyn", "sel_lid_box_dyn"});

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Source: lid wiper inner surface, dynamic");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add",
        new String[]{"sel_lid_inner_candidates_dyn"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract",
        new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_outer_ball_hi_dyn", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("r", "Rlid_out+0.04[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_hi_dyn").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_ball_lo_dyn", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("r", "Rlid_out-0.04[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_lo_dyn").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_candidates_dyn", "Intersection");
    model.component("comp1").selection("sel_lid_outer_candidates_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_candidates_dyn").set("input",
        new String[]{"sel_lid_outer_ball_hi_dyn", "sel_lid_box_dyn"});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Lid wiper loaded outer surface, dynamic");
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("add",
        new String[]{"sel_lid_outer_candidates_dyn"});
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("subtract",
        new String[]{"sel_lid_outer_ball_lo_dyn", "sel_cornea_anterior_surface"});
  }

  private static void setCenterDirectedLoad(Model model) {
    if (!hasCpl(model, "intop_lid_load_area")) {
      model.component("comp1").cpl().create("intop_lid_load_area", "Integration");
    }
    model.component("comp1").cpl("intop_lid_load_area").label("Lid wiper load surface area integral");
    model.component("comp1").cpl("intop_lid_load_area").selection().named("sel_lid_load_surface_dyn");

    if (!hasVariable(model, "var_lid_center_load")) {
      model.component("comp1").variable().create("var_lid_center_load");
    }
    model.component("comp1").variable("var_lid_center_load").label("Lid wiper center-directed load variables");
    model.component("comp1").variable("var_lid_center_load").set("r_center_lid", "sqrt(x^2+y^2+z^2)");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cx", "-x/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cy", "-y/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cz", "-z/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("A_lid_load", "intop_lid_load_area(1)");
    model.component("comp1").variable("var_lid_center_load").set("p_lid_center", "F_lid/A_lid_load");

    if (!hasPhysicsFeature(model, "load_lid")) {
      model.component("comp1").physics("solid").feature().create("load_lid", "BoundaryLoad", 2);
    }
    model.component("comp1").physics("solid").feature("load_lid").label("0.03 N lid load directed to globe center");
    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("forceReferenceArea",
        new String[]{"p_lid_center*e_lid_cx", "p_lid_center*e_lid_cy", "p_lid_center*e_lid_cz"});
  }

  private static void createResults(Model model) {
    if (!hasStudy(model, "std_centerload")) return;
    if (!model.result().dataset().tags().toString().contains("dset2")) return;

    model.result().create("pg_contact_pressure", "PlotGroup3D");
    model.result("pg_contact_pressure").label("Contact pressure - lid wiper to cornea");
    model.result("pg_contact_pressure").set("data", "dset2");
    model.result("pg_contact_pressure").feature().create("surf1", "Surface");
    model.result("pg_contact_pressure").feature("surf1").selection().named("sel_cornea_anterior_surface");
    model.result("pg_contact_pressure").feature("surf1").set("expr", "solid.Tn");
    model.result("pg_contact_pressure").feature("surf1").set("unit", "Pa");

    model.result().create("pg_gap_distance", "PlotGroup3D");
    model.result("pg_gap_distance").label("Gap distance - lid wiper to cornea");
    model.result("pg_gap_distance").set("data", "dset2");
    model.result("pg_gap_distance").feature().create("surf1", "Surface");
    model.result("pg_gap_distance").feature("surf1").selection().named("sel_cornea_anterior_surface");
    model.result("pg_gap_distance").feature("surf1").set("expr", "solid.gap");
    model.result("pg_gap_distance").feature("surf1").set("unit", "m");

    model.result().table().create("tbl_contact_pressure_int", "Table");
    model.result("tbl_contact_pressure_int").label("Surface integration of corneal contact pressure");
    model.result().numerical().create("int_contact_pressure_cornea", "IntSurface");
    model.result().numerical("int_contact_pressure_cornea").label("Anterior cornea surface integral of contact pressure");
    model.result().numerical("int_contact_pressure_cornea").set("data", "dset2");
    model.result().numerical("int_contact_pressure_cornea").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_contact_pressure_cornea").set("expr", "solid.Tn");
    model.result().numerical("int_contact_pressure_cornea").set("unit", "N");
    model.result().numerical("int_contact_pressure_cornea").set("descr", "Surface integration of contact pressure on anterior cornea");
    model.result().numerical("int_contact_pressure_cornea").set("table", "tbl_contact_pressure_int");
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");

    model.param().set("delta_lid_indent", "0.005[mm]", "Initial lid wiper indentation into corneal anterior sphere");
    model.param().set("Rlid_in", "Rcor-delta_lid_indent", "Lid wiper inner radius with prescribed initial indentation");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid wiper outer radius");
    model.param().set("theta_lid", "30[deg]",
        "Lid wiper position; scan values 30..-30 deg correspond to paper 10..70 deg");

    rebuildDynamicSelections(model);
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    setCenterDirectedLoad(model);

    if (!hasStudy(model, "std_centerload")) model.study().create("std_centerload");
    model.study("std_centerload").label("Quasi-static scan with center-directed lid load");

    if (!hasStudyFeature(model, "std_centerload", "param")) {
      model.study("std_centerload").feature().create("param", "Parametric");
    }
    model.study("std_centerload").feature("param").label("theta_lid scan: paper 10-70 deg");
    model.study("std_centerload").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_centerload").feature("param").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.study("std_centerload").feature("param").set("punit", new String[]{"deg"});

    if (!hasStudyFeature(model, "std_centerload", "stat")) {
      model.study("std_centerload").feature().create("stat", "Stationary");
    }
    model.study("std_centerload").feature("stat").activate("solid", true);

    model.study("std_centerload").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.03);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");

    model.save(SETUP);
    model.study("std_centerload").run();
    createResults(model);
    try {
      model.result().numerical("int_contact_pressure_cornea").setResult();
    } catch (Exception e) {
      System.out.println("Contact pressure surface integration was created but not evaluated: " + e.getMessage());
    }
    model.save(OUT);
  }
}
