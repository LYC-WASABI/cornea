import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_apex_spherical_rect_lid {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_rectangular_curved_lid_wiper_geometry_fixed.mph";
  private static final String OUT = DIR + "du_cornea_lid_apex_spherical_rect_lid_wiper.mph";

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

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void replaceWithApicalSphericalLid(Model model) {
    String[] old = {
      "int_lid", "blk_lid_window", "dif_lid_shell",
      "cyl_lid_outer", "cyl_lid_inner",
      "sph_lid_outer", "sph_lid_inner"
    };
    for (String tag : old) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignore) {}
    }

    model.param().set("theta_lid", "0[deg]", "Lid wiper fixed at corneal apex");
    model.param().set("delta_lid_indent", "0[mm]", "No geometric indentation; lower surface conforms to anterior cornea");
    model.param().set("Rlid_in", "Rcor", "Lid wiper lower surface radius equals anterior corneal radius");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid wiper outer radius");
    model.param().set("lid_arc_length", "8[mm]", "Spherical rectangular contact arc length");
    model.param().set("lid_arc_width", "1[mm]", "Spherical rectangular contact width");
    model.param().set("lid_cut_length", "2*Rlid_in*sin(lid_arc_length/(2*Rlid_in))", "Block chord for 8 mm spherical arc length");
    model.param().set("lid_cut_width", "2*Rlid_in*sin(lid_arc_width/(2*Rlid_in))", "Block chord for 1 mm spherical arc width");
    model.param().set("ylid", "0", "Apex lid y coordinate");
    model.param().set("zlid", "Rcor", "Apex lid z coordinate");

    model.component("comp1").geom("geom1").create("sph_lid_outer", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").label("Apex lid outer sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("sph_lid_inner", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").label("Apex lid inner sphere, same curvature as cornea");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("dif_lid_shell", "Difference");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").label("0.8 mm thick spherical lid shell");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input").set(new String[]{"sph_lid_outer"});
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input2").set(new String[]{"sph_lid_inner"});

    model.component("comp1").geom("geom1").create("blk_lid_window", "Block");
    model.component("comp1").geom("geom1").feature("blk_lid_window").label("Rectangular cutter: 8 mm arc by 1 mm width");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size",
        new String[]{"lid_cut_length", "lid_cut_width", "tlid+0.35[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos",
        new String[]{"0", "0", "Rcor+tlid/2"});

    model.component("comp1").geom("geom1").create("int_lid", "Intersection");
    model.component("comp1").geom("geom1").feature("int_lid").label("Apical spherical rectangular lid wiper");
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"dif_lid_shell", "blk_lid_window"});
    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("createpairs", "on");
  }

  private static void rebuildSelections(Model model) {
    String[] tags = {
      "sel_lid_inner_ball_apex", "sel_lid_outer_ball_apex", "sel_lid_box_apex",
      "sel_lid_inner_candidates_apex", "sel_lid_outer_candidates_apex",
      "sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn",
      "sel_lid_dom", "sel_contact_zone"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_box_apex", "Box");
    model.component("comp1").selection("sel_lid_box_apex").label("Apex lid bounding box");
    model.component("comp1").selection("sel_lid_box_apex").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_apex").set("xmin", "-lid_cut_length/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("xmax", "lid_cut_length/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("ymin", "-lid_cut_width/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("ymax", "lid_cut_width/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("zmin", "Rcor-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_apex").set("zmax", "Rcor+tlid+0.2[mm]");

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

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Explicit");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Apex lid lower surface: rectangular spherical contact patch");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set(new int[]{16, 17, 18});

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

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Explicit");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Apex lid outer surface for 0.03 N center-directed load");
    model.component("comp1").selection("sel_lid_load_surface_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set(new int[]{11, 13, 14, 15, 19});

    model.component("comp1").selection().create("sel_lid_dom", "Box");
    model.component("comp1").selection("sel_lid_dom").set("entitydim", 3);
    model.component("comp1").selection("sel_lid_dom").set("xmin", "-lid_cut_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("xmax", "lid_cut_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymin", "-lid_cut_width/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymax", "lid_cut_width/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmin", "Rcor-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmax", "Rcor+tlid+0.2[mm]");

    model.component("comp1").selection().create("sel_contact_zone", "Box");
    model.component("comp1").selection("sel_contact_zone").set("entitydim", 3);
    model.component("comp1").selection("sel_contact_zone").set("xmin", "-lid_cut_length/2-0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("xmax", "lid_cut_length/2+0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymin", "-lid_cut_width/2-0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymax", "lid_cut_width/2+0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmin", "Rcor-0.4[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmax", "Rcor+tlid+0.3[mm]");
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

    model.component("comp1").physics("solid").feature("load_lid").label("0.03 N lid load directed to globe center");
    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("forceReferenceArea",
        new String[]{"p_lid_center*e_lid_cx", "p_lid_center*e_lid_cy", "p_lid_center*e_lid_cz"});
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    replaceWithApicalSphericalLid(model);
    model.component("comp1").geom("geom1").run();
    rebuildSelections(model);
    updatePhysics(model);
    model.component("comp1").mesh("mesh1").run();
    System.out.println("apex inner contact source=" +
        Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("apex outer load surface=" +
        Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));
    model.save(OUT);
  }
}
