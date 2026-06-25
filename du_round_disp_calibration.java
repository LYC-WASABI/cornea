import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_round_disp_calibration {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_rotating_lid_wiper_position_scan_results.mph";
  private static final String SETUP = DIR + "22_rounded_lid_displacement_calibration_setup.mph";
  private static final String OUT = DIR + "du_cornea_lid_rounded_lid_displacement_calibration_results.mph";

  private static boolean hasSelection(Model model, String tag) {
    for (String t : model.component("comp1").selection().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void cleanStudiesSolutionsResults(Model model) {
    for (String s : model.sol().tags()) {
      try { model.sol().remove(s); } catch (Exception ignore) {}
    }
    for (String s : model.study().tags()) {
      try { model.study().remove(s); } catch (Exception ignore) {}
    }
    for (String r : model.result().tags()) {
      try { model.result().remove(r); } catch (Exception ignore) {}
    }
    for (String n : model.result().numerical().tags()) {
      try { model.result().numerical().remove(n); } catch (Exception ignore) {}
    }
    for (String t : model.result().table().tags()) {
      try { model.result().table().remove(t); } catch (Exception ignore) {}
    }
  }

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void buildRoundedRotatingLid(Model model) {
    String[] old = {
      "rot_lid", "int_lid", "blk_lid_window",
      "uni_round_cutter", "blk_round_core_x", "blk_round_core_y",
      "cyl_round_pxp", "cyl_round_pxn", "cyl_round_nxp", "cyl_round_nxn"
    };
    for (String tag : old) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignore) {}
    }

    model.param().set("theta_lid", "0[deg]", "Calibration angle");
    model.param().set("Rlid_in", "Rcor", "Lid lower surface radius equals corneal anterior radius");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid outer radius");
    model.param().set("lid_arc_length", "8[mm]", "Spherical rounded rectangular contact arc length");
    model.param().set("lid_arc_width", "1[mm]", "Spherical rounded rectangular contact width");
    model.param().set("lid_edge_round", "0.08[mm]", "Rounded corner radius of lid wiper contact footprint");
    model.param().set("lid_cut_length", "2*Rlid_in*sin(lid_arc_length/(2*Rlid_in))", "Block chord for 8 mm spherical arc length");
    model.param().set("lid_cut_width", "2*Rlid_in*sin(lid_arc_width/(2*Rlid_in))", "Block chord for 1 mm spherical arc width");
    model.param().set("d_lid_press", "0.02[mm]", "Prescribed lid displacement toward globe center");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Center y coordinate after whole-lid rotation");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Center z coordinate after whole-lid rotation");

    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");

    String z0 = "Rcor-0.1[mm]";
    String h = "tlid+0.35[mm]";
    model.component("comp1").geom("geom1").create("blk_round_core_x", "Block");
    model.component("comp1").geom("geom1").feature("blk_round_core_x").label("Rounded cutter long core");
    model.component("comp1").geom("geom1").feature("blk_round_core_x").set("size",
        new String[]{"lid_cut_length-2*lid_edge_round", "lid_cut_width", h});
    model.component("comp1").geom("geom1").feature("blk_round_core_x").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_round_core_x").set("pos",
        new String[]{"0", "0", "Rcor+tlid/2"});

    model.component("comp1").geom("geom1").create("blk_round_core_y", "Block");
    model.component("comp1").geom("geom1").feature("blk_round_core_y").label("Rounded cutter wide core");
    model.component("comp1").geom("geom1").feature("blk_round_core_y").set("size",
        new String[]{"lid_cut_length", "lid_cut_width-2*lid_edge_round", h});
    model.component("comp1").geom("geom1").feature("blk_round_core_y").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_round_core_y").set("pos",
        new String[]{"0", "0", "Rcor+tlid/2"});

    String[] names = {"cyl_round_pxp", "cyl_round_pxn", "cyl_round_nxp", "cyl_round_nxn"};
    String[][] pos = {
      {"lid_cut_length/2-lid_edge_round", "lid_cut_width/2-lid_edge_round", z0},
      {"lid_cut_length/2-lid_edge_round", "-lid_cut_width/2+lid_edge_round", z0},
      {"-lid_cut_length/2+lid_edge_round", "lid_cut_width/2-lid_edge_round", z0},
      {"-lid_cut_length/2+lid_edge_round", "-lid_cut_width/2+lid_edge_round", z0}
    };
    for (int i = 0; i < names.length; i++) {
      model.component("comp1").geom("geom1").create(names[i], "Cylinder");
      model.component("comp1").geom("geom1").feature(names[i]).label("Rounded cutter corner " + (i + 1));
      model.component("comp1").geom("geom1").feature(names[i]).set("r", "lid_edge_round");
      model.component("comp1").geom("geom1").feature(names[i]).set("h", h);
      model.component("comp1").geom("geom1").feature(names[i]).set("pos", pos[i]);
    }

    model.component("comp1").geom("geom1").create("uni_round_cutter", "Union");
    model.component("comp1").geom("geom1").feature("uni_round_cutter").label("Rounded rectangular cutter");
    model.component("comp1").geom("geom1").feature("uni_round_cutter").selection("input").set(new String[]{
      "blk_round_core_x", "blk_round_core_y", "cyl_round_pxp", "cyl_round_pxn", "cyl_round_nxp", "cyl_round_nxn"
    });
    try { model.component("comp1").geom("geom1").feature("uni_round_cutter").set("intbnd", "off"); } catch (Exception ignore) {}

    model.component("comp1").geom("geom1").create("int_lid", "Intersection");
    model.component("comp1").geom("geom1").feature("int_lid").label("Rounded-corner spherical rectangular lid wiper");
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"dif_lid_shell", "uni_round_cutter"});

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

  private static void rebuildSelections(Model model) {
    String[] tags = {
      "sel_lid_box_rot", "sel_lid_inner_ball_rot", "sel_lid_outer_ball_rot",
      "sel_lid_inner_candidates_rot", "sel_lid_outer_candidates_rot",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn",
      "sel_lid_dom", "sel_contact_zone"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_box_rot", "Box");
    model.component("comp1").selection("sel_lid_box_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_rot").set("xmin", "-lid_cut_length/2-0.10[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("xmax", "lid_cut_length/2+0.10[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymin", "ylid-lid_cut_width/2-0.15[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymax", "ylid+lid_cut_width/2+0.15[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("zmin", "zlid-0.15[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("zmax", "zlid+tlid+0.35[mm]");

    model.component("comp1").selection().create("sel_lid_inner_ball_rot", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("r", "Rlid_in+0.02[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_rot").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_inner_candidates_rot", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_candidates_rot").set("input", new String[]{"sel_lid_inner_ball_rot", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Rounded lid lower contact surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add", new String[]{"sel_lid_inner_candidates_rot"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract", new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_outer_ball_rot", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("r", "Rlid_out+0.02[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_rot").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_candidates_rot", "Intersection");
    model.component("comp1").selection("sel_lid_outer_candidates_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_candidates_rot").set("input", new String[]{"sel_lid_outer_ball_rot", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Rounded lid outer displacement control surface");
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("add", new String[]{"sel_lid_outer_candidates_rot"});
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("subtract", new String[]{"sel_lid_wiper_inner_surface_dyn", "sel_cornea_anterior_surface"});
  }

  private static void updatePhysics(Model model) {
    try { model.component("comp1").physics("solid").feature().remove("load_lid"); } catch (Exception ignore) {}
    try { model.component("comp1").physics("solid").feature().remove("rc_lid_disp"); } catch (Exception ignore) {}
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    model.component("comp1").physics("solid").feature().create("rc_lid_disp", "RigidConnector", 2);
    model.component("comp1").physics("solid").feature("rc_lid_disp").label("Displacement-controlled lid press toward globe center");
    model.component("comp1").physics("solid").feature("rc_lid_disp").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("rc_lid_disp").set("U0",
        new String[]{"0", "-d_lid_press*sin(theta_lid)", "-d_lid_press*cos(theta_lid)"});
    model.component("comp1").physics("solid").feature("rc_lid_disp").set("freeRot", new String[]{"1", "1", "1"});
  }

  private static void setupCalibrationStudy(Model model) {
    model.study().create("std_disp_cal");
    model.study("std_disp_cal").label("Displacement calibration to 0.03 N");
    model.study("std_disp_cal").feature().create("param", "Parametric");
    model.study("std_disp_cal").feature("param").set("pname", new String[]{"d_lid_press"});
    model.study("std_disp_cal").feature("param").set("plistarr", new String[]{"0.005 0.01 0.02 0.04 0.06 0.08 0.10"});
    model.study("std_disp_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_disp_cal").feature().create("stat", "Stationary");
    model.study("std_disp_cal").feature("stat").activate("solid", true);
    model.study("std_disp_cal").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.03);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");
  }

  private static void addCalibrationResults(Model model) {
    model.result().table().create("tbl_disp_cal_force", "Table");
    model.result().table("tbl_disp_cal_force").label("Calibration force from anterior cornea contact pressure");
    model.result().numerical().create("int_cal_contact_force", "IntSurface");
    model.result().numerical("int_cal_contact_force").label("Contact pressure integral for displacement calibration");
    model.result().numerical("int_cal_contact_force").set("data", "dset1");
    model.result().numerical("int_cal_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_cal_contact_force").set("expr", "solid.Tn");
    model.result().numerical("int_cal_contact_force").set("unit", "N");
    model.result().numerical("int_cal_contact_force").set("table", "tbl_disp_cal_force");
    try { model.result().numerical("int_cal_contact_force").setResult(); } catch (Exception e) {
      System.out.println("setResult failed: " + e.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    cleanStudiesSolutionsResults(model);
    buildRoundedRotatingLid(model);
    model.component("comp1").geom("geom1").run();
    rebuildSelections(model);
    updatePhysics(model);
    model.component("comp1").mesh("mesh1").run();
    System.out.println("rounded source=" + Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("disp surface=" + Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));
    setupCalibrationStudy(model);
    model.save(SETUP);
    model.study("std_disp_cal").run();
    addCalibrationResults(model);
    model.save(OUT);
  }
}
