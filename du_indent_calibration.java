import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_indent_calibration {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_rounded_lid_displacement_calibration_results.mph";
  private static final String SETUP = DIR + "23_rounded_lid_geometric_indentation_calibration_setup.mph";
  private static final String OUT = DIR + "du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";

  private static boolean hasStudy(Model model, String tag) {
    for (String t : model.study().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasStudyFeature(Model model, String study, String tag) {
    for (String t : model.study(study).feature().tags()) if (t.equals(tag)) return true;
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

  private static void removeRigidConnectorAndLoad(Model model) {
    try { model.component("comp1").physics("solid").feature().remove("rc_lid_disp"); } catch (Exception ignore) {}
    try { model.component("comp1").physics("solid").feature().remove("load_lid"); } catch (Exception ignore) {}
  }

  private static void configureIndentation(Model model) {
    model.param().set("theta_lid", "0[deg]", "Indentation calibration at apex");
    model.param().set("delta_indent", "0.02[mm]", "Geometric indentation of lid lower surface into cornea");
    model.param().set("Rlid_in", "Rcor-delta_indent", "Indented lid lower surface radius");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid outer radius");
    model.param().set("lid_edge_round", "0.08[mm]", "Rounded corner radius of lid wiper contact footprint");
    model.param().set("lid_arc_length", "8[mm]", "Spherical rounded rectangular contact arc length");
    model.param().set("lid_arc_width", "1[mm]", "Spherical rounded rectangular contact width");
    model.param().set("lid_cut_length", "2*Rcor*sin(lid_arc_length/(2*Rcor))", "Cutter chord based on corneal radius");
    model.param().set("lid_cut_width", "2*Rcor*sin(lid_arc_width/(2*Rcor))", "Cutter chord based on corneal radius");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Lid center y coordinate");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Lid center z coordinate");

    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");
    try { model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid"); } catch (Exception ignore) {}
  }

  private static void updateContactSelections(Model model) {
    String[] old = {
      "sel_lid_box_rot", "sel_lid_inner_ball_rot", "sel_lid_outer_ball_rot",
      "sel_lid_inner_candidates_rot", "sel_lid_outer_candidates_rot",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn"
    };
    for (String s : old) {
      try { model.component("comp1").selection().remove(s); } catch (Exception ignore) {}
    }

    model.component("comp1").selection().create("sel_lid_box_rot", "Box");
    model.component("comp1").selection("sel_lid_box_rot").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_rot").set("xmin", "-lid_cut_length/2-0.10[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("xmax", "lid_cut_length/2+0.10[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymin", "ylid-lid_cut_width/2-0.15[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("ymax", "ylid+lid_cut_width/2+0.15[mm]");
    model.component("comp1").selection("sel_lid_box_rot").set("zmin", "zlid-delta_indent-0.15[mm]");
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
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Indented rounded lid lower contact surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add", new String[]{"sel_lid_inner_candidates_rot"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract", new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});
  }

  private static void setupStudy(Model model) {
    model.study().create("std_indent_cal");
    model.study("std_indent_cal").label("Geometric indentation calibration to 0.03 N");
    model.study("std_indent_cal").feature().create("param", "Parametric");
    model.study("std_indent_cal").feature("param").set("pname", new String[]{"delta_indent"});
    model.study("std_indent_cal").feature("param").set("plistarr", new String[]{"0.001 0.002 0.005 0.01 0.02 0.04 0.06 0.08 0.10"});
    model.study("std_indent_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_indent_cal").feature().create("stat", "Stationary");
    model.study("std_indent_cal").feature("stat").activate("solid", true);
    model.study("std_indent_cal").createAutoSequences("sol");
    model.sol("sol1").feature("s1").set("stol", 0.03);
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 300);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");
  }

  private static void addResults(Model model) {
    model.result().table().create("tbl_indent_cal_force", "Table");
    model.result().table("tbl_indent_cal_force").label("Indentation calibration force from anterior cornea contact pressure");
    model.result().numerical().create("int_indent_contact_force", "IntSurface");
    model.result().numerical("int_indent_contact_force").label("Contact pressure integral for indentation calibration");
    model.result().numerical("int_indent_contact_force").set("data", "dset1");
    model.result().numerical("int_indent_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_indent_contact_force").set("expr", "solid.Tn");
    model.result().numerical("int_indent_contact_force").set("unit", "N");
    model.result().numerical("int_indent_contact_force").set("table", "tbl_indent_cal_force");
    try { model.result().numerical("int_indent_contact_force").setResult(); } catch (Exception e) {
      System.out.println("setResult failed: " + e.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    cleanStudiesSolutionsResults(model);
    removeRigidConnectorAndLoad(model);
    configureIndentation(model);
    model.component("comp1").geom("geom1").run();
    updateContactSelections(model);
    model.component("comp1").mesh("mesh1").run();
    System.out.println("indent source=" + Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    setupStudy(model);
    model.save(SETUP);
    model.study("std_indent_cal").run();
    addResults(model);
    model.save(OUT);
  }
}
