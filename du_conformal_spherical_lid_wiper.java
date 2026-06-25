import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_conformal_spherical_lid_wiper {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_rectangular_curved_lid_wiper_geometry_fixed.mph";
  private static final String OUT = DIR + "du_cornea_lid_conformal_spherical_rectangular_lid_wiper.mph";

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

  private static void replaceWithConformalSphericalPatch(Model model) {
    String[] oldGeom = {
      "int_lid", "blk_lid_window", "dif_lid_shell",
      "sph_lid_outer", "sph_lid_inner",
      "cyl_lid_outer", "cyl_lid_inner"
    };
    for (String tag : oldGeom) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignore) {}
    }

    model.param().set("lid_arc_length", "8[mm]", "Spherical arc length of lid wiper lower surface");
    model.param().set("lid_arc_chord", "2*Rcor*sin(lid_arc_length/(2*Rcor))",
        "Chord window giving 8 mm spherical arc length");
    model.param().set("lid_width_chord", "2*Rcor*sin(lid_width/(2*Rcor))",
        "Chord window giving 1 mm spherical width");
    model.param().set("Rlid_in", "Rcor", "Lid wiper lower surface fully conforms to anterior cornea");
    model.param().set("Rlid_out", "Rcor+tlid", "Lid wiper outer radius with unchanged 0.8 mm thickness");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Lid wiper center y on corneal anterior sphere");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Lid wiper center z on corneal anterior sphere");

    model.component("comp1").geom("geom1").create("sph_lid_outer", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").label("Conformal lid outer sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("sph_lid_inner", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").label("Conformal lid inner sphere, same as cornea");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("dif_lid_shell", "Difference");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").label("0.8 mm conformal spherical shell");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input").set(new String[]{"sph_lid_outer"});
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input2").set(new String[]{"sph_lid_inner"});

    model.component("comp1").geom("geom1").create("blk_lid_window", "Block");
    model.component("comp1").geom("geom1").feature("blk_lid_window").label("Rectangular window: 8 mm arc x 1 mm width");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size",
        new String[]{"lid_arc_chord+0.02[mm]", "lid_width_chord+0.02[mm]", "tlid+0.4[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos",
        new String[]{"0", "ylid", "zlid+tlid/2"});

    model.component("comp1").geom("geom1").create("int_lid", "Intersection");
    model.component("comp1").geom("geom1").feature("int_lid").label("Conformal spherical rectangular lid wiper");
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"dif_lid_shell", "blk_lid_window"});
    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("createpairs", "on");
  }

  private static void rebuildConformalSelections(Model model) {
    String[] tags = {
      "sel_lid_inner_ball_conformal",
      "sel_lid_box_conformal",
      "sel_lid_inner_candidates_conformal",
      "sel_lid_wiper_inner_surface_dyn",
      "sel_lid_outer_ball_conformal_hi",
      "sel_lid_outer_ball_conformal_lo",
      "sel_lid_outer_candidates_conformal",
      "sel_lid_load_surface_dyn"
    };
    for (String tag : tags) removeSelectionIfPresent(model, tag);

    model.component("comp1").selection().create("sel_lid_box_conformal", "Box");
    model.component("comp1").selection("sel_lid_box_conformal").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_conformal").set("xmin", "-lid_arc_chord/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_conformal").set("xmax", "lid_arc_chord/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_box_conformal").set("ymin", "ylid-lid_width_chord/2-0.05[mm]");
    model.component("comp1").selection("sel_lid_box_conformal").set("ymax", "ylid+lid_width_chord/2+0.05[mm]");
    model.component("comp1").selection("sel_lid_box_conformal").set("zmin", "zlid-0.08[mm]");
    model.component("comp1").selection("sel_lid_box_conformal").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("sel_lid_inner_ball_conformal", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("r", "Rcor+0.02[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_conformal").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_inner_candidates_conformal", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_conformal").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_candidates_conformal").set("input",
        new String[]{"sel_lid_inner_ball_conformal", "sel_lid_box_conformal"});

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").label("Conformal lid lower rectangular spherical surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("add",
        new String[]{"sel_lid_inner_candidates_conformal"});
    model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").set("subtract",
        new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_outer_ball_conformal_hi", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("r", "Rlid_out+0.04[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_hi").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_ball_conformal_lo", "Ball");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("r", "Rlid_out-0.04[mm]");
    model.component("comp1").selection("sel_lid_outer_ball_conformal_lo").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_candidates_conformal", "Intersection");
    model.component("comp1").selection("sel_lid_outer_candidates_conformal").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_candidates_conformal").set("input",
        new String[]{"sel_lid_outer_ball_conformal_hi", "sel_lid_box_conformal"});

    model.component("comp1").selection().create("sel_lid_load_surface_dyn", "Difference");
    model.component("comp1").selection("sel_lid_load_surface_dyn").label("Conformal lid upper load surface");
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("add",
        new String[]{"sel_lid_outer_candidates_conformal"});
    model.component("comp1").selection("sel_lid_load_surface_dyn").set("subtract",
        new String[]{"sel_lid_outer_ball_conformal_lo", "sel_cornea_anterior_surface"});
  }

  private static void updateLoadAndPair(Model model) {
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    if (!hasCpl(model, "intop_lid_load_area")) model.component("comp1").cpl().create("intop_lid_load_area", "Integration");
    model.component("comp1").cpl("intop_lid_load_area").label("Conformal lid load surface area integral");
    model.component("comp1").cpl("intop_lid_load_area").selection().named("sel_lid_load_surface_dyn");

    if (!hasVariable(model, "var_lid_center_load")) model.component("comp1").variable().create("var_lid_center_load");
    model.component("comp1").variable("var_lid_center_load").set("r_center_lid", "sqrt(x^2+y^2+z^2)");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cx", "-x/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cy", "-y/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("e_lid_cz", "-z/r_center_lid");
    model.component("comp1").variable("var_lid_center_load").set("A_lid_load", "intop_lid_load_area(1)");
    model.component("comp1").variable("var_lid_center_load").set("p_lid_center", "F_lid/A_lid_load");

    model.component("comp1").physics("solid").feature("load_lid").label("0.03 N conformal lid load directed to globe center");
    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_load_surface_dyn");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("forceReferenceArea",
        new String[]{"p_lid_center*e_lid_cx", "p_lid_center*e_lid_cy", "p_lid_center*e_lid_cz"});
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    model.param().set("theta_lid", "30[deg]",
        "Lid wiper position; 30..-30 deg correspond to paper 10..70 deg");

    replaceWithConformalSphericalPatch(model);
    model.component("comp1").geom("geom1").run();
    rebuildConformalSelections(model);
    updateLoadAndPair(model);
    model.component("comp1").mesh("mesh1").run();

    System.out.println("Conformal lid lower surface boundaries: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("Conformal lid upper load boundaries: " +
        Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));

    model.save(OUT);
  }
}
