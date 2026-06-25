import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_run_quasistatic_scan {
  private static final String DIR = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";
  private static final String IN = DIR + "du_cornea_lid_explicit_pair_final change.mph";
  private static final String SETUP = DIR + "15_quasistatic_stationary_scan_setup_from_change.mph";
  private static final String OUT = DIR + "du_cornea_lid_explicit_pair_quasistatic_scan_results.mph";

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

  private static void removeSelectionIfPresent(Model model, String tag) {
    if (hasSelection(model, tag)) model.component("comp1").selection().remove(tag);
  }

  private static void rebuildDynamicContactSelections(Model model) {
    removeSelectionIfPresent(model, "sel_lid_inner_ball_dyn");
    removeSelectionIfPresent(model, "sel_lid_box_dyn");
    removeSelectionIfPresent(model, "sel_lid_inner_candidates_dyn");
    removeSelectionIfPresent(model, "sel_lid_wiper_inner_surface_dyn");

    model.component("comp1").selection().create("sel_lid_inner_ball_dyn", "Ball");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").label("Dynamic helper: near lid wiper inner radius");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posx", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posy", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("posz", "0");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("r", "Rlid_in+0.04[mm]");
    model.component("comp1").selection("sel_lid_inner_ball_dyn").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_box_dyn", "Box");
    model.component("comp1").selection("sel_lid_box_dyn").label("Dynamic helper: lid wiper local box");
    model.component("comp1").selection("sel_lid_box_dyn").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_box_dyn").set("xmin", "-lid_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("xmax", "lid_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("ymin", "ylid-lid_width/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("ymax", "ylid+lid_width/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("zmin", "zlid-0.1[mm]");
    model.component("comp1").selection("sel_lid_box_dyn").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("sel_lid_inner_candidates_dyn", "Intersection");
    model.component("comp1").selection("sel_lid_inner_candidates_dyn").label("Dynamic helper: lid inner candidates");
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
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");

    model.param().set("theta_lid", "30[deg]",
        "Lid wiper position; scan values 30..-30 deg correspond to paper 10..70 deg");

    rebuildDynamicContactSelections(model);
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface_dyn");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    if (!hasStudy(model, "std_quasi")) model.study().create("std_quasi");
    model.study("std_quasi").label("Quasi-static lid position scan");

    if (!hasStudyFeature(model, "std_quasi", "param")) {
      model.study("std_quasi").feature().create("param", "Parametric");
    }
    model.study("std_quasi").feature("param").label("theta_lid scan: paper 10-70 deg");
    model.study("std_quasi").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_quasi").feature("param").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.study("std_quasi").feature("param").set("punit", new String[]{"deg"});

    if (!hasStudyFeature(model, "std_quasi", "stat")) {
      model.study("std_quasi").feature().create("stat", "Stationary");
    }
    model.study("std_quasi").feature("stat").label("Stationary");
    model.study("std_quasi").feature("stat").activate("solid", true);

    model.save(SETUP);

    model.study("std_quasi").createAutoSequences("sol");
    model.sol("sol1").feature("s1").feature("fc1").set("maxiter", 100);
    model.sol("sol1").feature("s1").feature("fc1").set("mindamp", 1.0E-4);
    model.sol("sol1").feature("s1").feature("d1").set("errorchk", "off");
    model.sol("sol1").feature("s1").feature("dDef").set("errorchk", "off");

    boolean hasSolverParam = false;
    for (String tag : model.sol("sol1").feature("s1").feature().tags()) {
      if (tag.equals("p1")) hasSolverParam = true;
    }
    if (!hasSolverParam) {
      model.sol("sol1").feature("s1").feature().create("p1", "Parametric");
    }
    model.sol("sol1").feature("s1").feature("p1").label("theta_lid solver sweep");
    model.sol("sol1").feature("s1").feature("p1").set("pname", new String[]{"theta_lid"});
    model.sol("sol1").feature("s1").feature("p1").set("plistarr", new String[]{"30 20 10 0 -10 -20 -30"});
    model.sol("sol1").feature("s1").feature("p1").set("punit", new String[]{"deg"});
    model.sol("sol1").feature("s1").feature("p1").set("pcontinuationmode", "no");
    model.save(DIR + "16_quasistatic_stationary_scan_solver_tuned.mph");
    model.sol("sol1").runAll();
    model.sol("sol1").label("Quasi-static theta_lid scan solution");

    model.save(OUT);
  }
}
