import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_geometric_indent_deformable_lid_fix {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results-2.mph";
  private static final String SETUP = "D:\\COMSOL_Outputs\\models\\du\\27_geometric_indent_deformable_lid_outer_support_setup.mph";
  private static final String OUT = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph";

  private static void clean(Model model) {
    for (String tag : model.study().tags()) { try { model.study().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.sol().tags()) { try { model.sol().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().numerical().tags()) { try { model.result().numerical().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().table().tags()) { try { model.result().table().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().tags()) { try { model.result().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().dataset().tags()) { try { model.result().dataset().remove(tag); } catch (Exception ignored) {} }
  }

  private static void rebuildSupportSelections(Model model) {
    String[] old = {
      "sel_lid_outer_hi_support", "sel_lid_outer_lo_support",
      "sel_lid_outer_shell_support", "sel_lid_outer_support_candidates",
      "sel_lid_outer_support"
    };
    for (String s : old) {
      try { model.component("comp1").selection().remove(s); } catch (Exception ignored) {}
    }

    model.component("comp1").selection().create("sel_lid_outer_hi_support", "Ball");
    model.component("comp1").selection("sel_lid_outer_hi_support").label("Outer support shell upper radius");
    model.component("comp1").selection("sel_lid_outer_hi_support").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_hi_support").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_hi_support").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_hi_support").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_hi_support").set("r", "R_lid_out+0.03[mm]");
    model.component("comp1").selection("sel_lid_outer_hi_support").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_lo_support", "Ball");
    model.component("comp1").selection("sel_lid_outer_lo_support").label("Outer support shell lower radius");
    model.component("comp1").selection("sel_lid_outer_lo_support").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_lo_support").set("posx", "0");
    model.component("comp1").selection("sel_lid_outer_lo_support").set("posy", "0");
    model.component("comp1").selection("sel_lid_outer_lo_support").set("posz", "0");
    model.component("comp1").selection("sel_lid_outer_lo_support").set("r", "R_lid_out-0.03[mm]");
    model.component("comp1").selection("sel_lid_outer_lo_support").set("condition", "inside");

    model.component("comp1").selection().create("sel_lid_outer_shell_support", "Difference");
    model.component("comp1").selection("sel_lid_outer_shell_support").label("Lid outer-radius shell surfaces");
    model.component("comp1").selection("sel_lid_outer_shell_support").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_shell_support").set("add", new String[]{"sel_lid_outer_hi_support"});
    model.component("comp1").selection("sel_lid_outer_shell_support").set("subtract", new String[]{"sel_lid_outer_lo_support"});

    model.component("comp1").selection().create("sel_lid_outer_support_candidates", "Intersection");
    model.component("comp1").selection("sel_lid_outer_support_candidates").label("Outer lid support candidates inside lid footprint");
    model.component("comp1").selection("sel_lid_outer_support_candidates").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_support_candidates").set("input",
        new String[]{"sel_lid_outer_shell_support", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_outer_support", "Difference");
    model.component("comp1").selection("sel_lid_outer_support").label("Outer lid support only, excluding contact source");
    model.component("comp1").selection("sel_lid_outer_support").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_support").set("add",
        new String[]{"sel_lid_outer_support_candidates"});
    model.component("comp1").selection("sel_lid_outer_support").set("subtract",
        new String[]{"sel_lid_contact_source_robust", "sel_cornea_anterior_surface"});
  }

  private static void rebuildPhysics(Model model) {
    try { model.component("comp1").physics("solid").feature().remove("fix_lid_hold"); } catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("fix_lid_outer_support"); } catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("fix_lid_outer_support", "Fixed", 2);
    model.component("comp1").physics("solid").feature("fix_lid_outer_support")
        .label("Geometric indentation support: lid outer surface only");
    model.component("comp1").physics("solid").feature("fix_lid_outer_support").selection()
        .named("sel_lid_outer_support");

    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_contact_source_robust");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});
  }

  private static void addStudy(Model model) {
    model.study().create("std_deform_lid_indent_cal");
    model.study("std_deform_lid_indent_cal").label("Geometric indentation calibration with deformable lid outer support");
    model.study("std_deform_lid_indent_cal").create("param", "Parametric");
    model.study("std_deform_lid_indent_cal").feature("param").set("pname", new String[]{"delta_indent"});
    model.study("std_deform_lid_indent_cal").feature("param").set("plistarr",
        new String[]{"0.070[mm] 0.071[mm] 0.072[mm] 0.073[mm]"});
    model.study("std_deform_lid_indent_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_deform_lid_indent_cal").create("stat", "Stationary");
    model.study("std_deform_lid_indent_cal").feature("stat").set("geometricNonlinearity", "on");
  }

  private static void addResults(Model model) {
    model.result().dataset().create("dset_deform_lid_indent", "Solution");
    model.result().dataset("dset_deform_lid_indent").label("Deformable lid geometric indentation calibration solution");
    model.result().dataset("dset_deform_lid_indent").set("solution", "sol2");

    model.result().table().create("tbl_cornea_Tn_integral", "Table");
    model.result().table("tbl_cornea_Tn_integral").label("Anterior cornea intop(solid.Tn) contact-force calibration");
    model.result().numerical().create("intop_cornea_Tn", "IntSurface");
    model.result().numerical("intop_cornea_Tn").label("intop(solid.Tn) on anterior cornea");
    model.result().numerical("intop_cornea_Tn").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("intop_cornea_Tn").set("data", "dset_deform_lid_indent");
    model.result().numerical("intop_cornea_Tn").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("intop_cornea_Tn").set("unit", new String[]{"N"});
    model.result().numerical("intop_cornea_Tn").set("descr", new String[]{"Surface integral of contact pressure on anterior cornea"});
    model.result().numerical("intop_cornea_Tn").set("table", "tbl_cornea_Tn_integral");
    model.result().numerical("intop_cornea_Tn").setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_geometric_indent_deformable_lid_outer_support.mph");
    clean(model);
    model.param().set("theta_lid", "0[deg]", "Apex calibration; whole lid remains rotated by rot_lid for later position sweep");
    model.param().set("delta_indent", "0.072[mm]", "Geometric indentation parameter calibrated near 0.03 N");
    rebuildSupportSelections(model);
    rebuildPhysics(model);
    model.component("comp1").geom("geom1").run();
    System.out.println("contact source=" + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("outer support=" + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    model.component("comp1").mesh("mesh1").run();
    addStudy(model);
    model.save(SETUP);
    model.study("std_deform_lid_indent_cal").run();
    addResults(model);
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
