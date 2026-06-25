import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_indent_robust_contact_calibration {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";
  private static final String SETUP = "D:\\COMSOL_Outputs\\models\\du\\25_rounded_lid_geometric_indentation_robust_contact_setup.mph";
  private static final String OUT = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results.mph";

  private static void removeIfExists(Model model, String group, String tag) {
    try {
      if ("study".equals(group)) model.study().remove(tag);
      if ("sol".equals(group)) model.sol().remove(tag);
      if ("plot".equals(group)) model.result().remove(tag);
      if ("num".equals(group)) model.result().numerical().remove(tag);
      if ("tbl".equals(group)) model.result().table().remove(tag);
      if ("dset".equals(group)) model.result().dataset().remove(tag);
    } catch (Exception ignored) {}
  }

  private static void rebuildRobustSelections(Model model) {
    String[] old = {
      "sel_lid_contact_near_inner_robust",
      "sel_lid_contact_candidates_robust",
      "sel_lid_contact_source_robust",
      "sel_lid_hold_robust"
    };
    for (String s : old) {
      try { model.component("comp1").selection().remove(s); } catch (Exception ignored) {}
    }

    model.component("comp1").selection().create("sel_lid_contact_near_inner_robust", "Ball");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").label("Robust lid source: surfaces intersecting inner-radius neighborhood");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("posx", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("posy", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("posz", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("r", "Rlid_in+0.05[mm]");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust").set("condition", "intersects");

    model.component("comp1").selection().create("sel_lid_contact_candidates_robust", "Intersection");
    model.component("comp1").selection("sel_lid_contact_candidates_robust").label("Robust lid source candidates inside rectangular footprint");
    model.component("comp1").selection("sel_lid_contact_candidates_robust").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_candidates_robust").set("input",
        new String[]{"sel_lid_contact_near_inner_robust", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_contact_source_robust", "Difference");
    model.component("comp1").selection("sel_lid_contact_source_robust").label("Robust source: lid wiper near-inner contact surface");
    model.component("comp1").selection("sel_lid_contact_source_robust").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_source_robust").set("add",
        new String[]{"sel_lid_contact_candidates_robust"});
    model.component("comp1").selection("sel_lid_contact_source_robust").set("subtract",
        new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_hold_robust", "Difference");
    model.component("comp1").selection("sel_lid_hold_robust").label("Robust fixed lid wiper boundaries");
    model.component("comp1").selection("sel_lid_hold_robust").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_hold_robust").set("add", new String[]{"sel_lid_box_rot"});
    model.component("comp1").selection("sel_lid_hold_robust").set("subtract", new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_contact_source_robust");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_rounded_lid_geometric_indentation_robust_contact.mph");

    for (String tag : model.study().tags()) removeIfExists(model, "study", tag);
    for (String tag : model.sol().tags()) removeIfExists(model, "sol", tag);
    for (String tag : model.result().numerical().tags()) removeIfExists(model, "num", tag);
    for (String tag : model.result().table().tags()) removeIfExists(model, "tbl", tag);
    for (String tag : model.result().tags()) removeIfExists(model, "plot", tag);

    model.param().set("theta_lid", "0[deg]", "Apex calibration position");
    model.param().set("delta_indent", "0.13[mm]", "Geometric indentation for robust contact calibration");
    model.param().set("Rlid_in", "Rcor-delta_indent", "Indented inner radius for calibration");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Outer radius of indented lid wiper");

    rebuildRobustSelections(model);

    try { model.component("comp1").physics("solid").feature().remove("rc_lid_disp"); } catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("load_lid"); } catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("fix_lid_hold"); } catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("fix_lid_hold", "Fixed", 2);
    model.component("comp1").physics("solid").feature("fix_lid_hold").label("Hold lid wiper with robust dynamic boundary selection");
    model.component("comp1").physics("solid").feature("fix_lid_hold").selection().named("sel_lid_hold_robust");

    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std_indent_robust_cal");
    model.study("std_indent_robust_cal").label("Geometric indentation calibration with robust contact pair");
    model.study("std_indent_robust_cal").create("param", "Parametric");
    model.study("std_indent_robust_cal").feature("param").set("pname", new String[]{"delta_indent"});
    model.study("std_indent_robust_cal").feature("param").set("plistarr",
        new String[]{"0.10[mm] 0.12[mm] 0.13[mm] 0.14[mm] 0.145[mm] 0.15[mm]"});
    model.study("std_indent_robust_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_indent_robust_cal").create("stat", "Stationary");
    model.study("std_indent_robust_cal").feature("stat").set("geometricNonlinearity", "on");

    model.save(SETUP);
    model.study("std_indent_robust_cal").run();

    model.result().dataset().create("dset_robust_scan", "Solution");
    model.result().dataset("dset_robust_scan").label("Robust contact indentation calibration solution");
    model.result().dataset("dset_robust_scan").set("solution", "sol2");

    model.result().table().create("tbl_robust_indent_force", "Table");
    model.result().table("tbl_robust_indent_force").label("Robust contact indentation calibration contact force");
    model.result().numerical().create("int_robust_indent_contact_force", "IntSurface");
    model.result().numerical("int_robust_indent_contact_force").label("Contact pressure integral, robust source selection");
    model.result().numerical("int_robust_indent_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_robust_indent_contact_force").set("data", "dset_robust_scan");
    model.result().numerical("int_robust_indent_contact_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_robust_indent_contact_force").set("unit", new String[]{"N"});
    model.result().numerical("int_robust_indent_contact_force").set("descr", new String[]{"Integral of contact pressure on anterior cornea"});
    model.result().numerical("int_robust_indent_contact_force").set("table", "tbl_robust_indent_force");
    model.result().numerical("int_robust_indent_contact_force").setResult();

    model.save(OUT);
    System.out.println("Saved robust contact calibration result: " + OUT);
  }
}
