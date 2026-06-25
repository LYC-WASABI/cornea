import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_indent_fixed_lid_calibration {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";
  private static final String SETUP = "D:\\COMSOL_Outputs\\models\\du\\24_rounded_lid_geometric_indentation_fixed_lid_calibration_setup.mph";
  private static final String OUT = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_fixed_lid_calibration_results.mph";

  private static void removeIfExists(Model model, String group, String tag) {
    try {
      if ("study".equals(group)) model.study().remove(tag);
      if ("sol".equals(group)) model.sol().remove(tag);
      if ("plot".equals(group)) model.result().remove(tag);
      if ("num".equals(group)) model.result().numerical().remove(tag);
      if ("tbl".equals(group)) model.result().table().remove(tag);
    } catch (Exception ignored) {}
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_rounded_lid_geometric_indentation_fixed_lid_calibration.mph");

    for (String tag : model.study().tags()) removeIfExists(model, "study", tag);
    for (String tag : model.sol().tags()) removeIfExists(model, "sol", tag);
    for (String tag : model.result().numerical().tags()) removeIfExists(model, "num", tag);
    for (String tag : model.result().table().tags()) removeIfExists(model, "tbl", tag);
    for (String tag : model.result().tags()) removeIfExists(model, "plot", tag);

    model.param().set("theta_lid", "0[deg]", "Apex calibration position");
    model.param().set("delta_indent", "0.02[mm]", "Geometric indentation of lid inner surface into anterior cornea");
    model.param().set("Rlid_in", "Rcor-delta_indent", "Indented inner radius for displacement-force calibration");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Outer radius of indented lid wiper");

    try { model.component("comp1").physics("solid").feature().remove("rc_lid_disp"); } catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("load_lid"); } catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("fix_lid_hold"); } catch (Exception ignored) {}

    model.component("comp1").physics("solid").create("fix_lid_hold", "Fixed", 2);
    model.component("comp1").physics("solid").feature("fix_lid_hold")
        .label("Hold lid wiper position for geometric indentation calibration");
    model.component("comp1").physics("solid").feature("fix_lid_hold").selection()
        .set(new int[]{10,11,12,13,14,15,16,17,21,23,24,25,26,27});

    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std_indent_fixed_cal");
    model.study("std_indent_fixed_cal").label("Geometric indentation calibration with fixed lid body");
    model.study("std_indent_fixed_cal").create("param", "Parametric");
    model.study("std_indent_fixed_cal").feature("param").set("pname", new String[]{"delta_indent"});
    model.study("std_indent_fixed_cal").feature("param").set("plistarr",
        new String[]{"0.15[mm] 0.16[mm] 0.17[mm]"});
    model.study("std_indent_fixed_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_indent_fixed_cal").create("stat", "Stationary");
    model.study("std_indent_fixed_cal").feature("stat").set("geometricNonlinearity", "on");

    model.save(SETUP);
    model.study("std_indent_fixed_cal").run();

    model.result().dataset().create("dset_fixed_scan", "Solution");
    model.result().dataset("dset_fixed_scan").label("Fixed lid indentation calibration solution");
    model.result().dataset("dset_fixed_scan").set("solution", "sol2");

    model.result().table().create("tbl_fixed_indent_force", "Table");
    model.result().table("tbl_fixed_indent_force").label("Fixed lid indentation calibration contact force");
    model.result().numerical().create("int_fixed_indent_contact_force", "IntSurface");
    model.result().numerical("int_fixed_indent_contact_force").label("Contact pressure integral, fixed lid indentation");
    model.result().numerical("int_fixed_indent_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_fixed_indent_contact_force").set("data", "dset_fixed_scan");
    model.result().numerical("int_fixed_indent_contact_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_fixed_indent_contact_force").set("unit", new String[]{"N"});
    model.result().numerical("int_fixed_indent_contact_force").set("descr", new String[]{"Integral of contact pressure on anterior cornea"});
    model.result().numerical("int_fixed_indent_contact_force").set("table", "tbl_fixed_indent_force");
    try { model.result().numerical("int_fixed_indent_contact_force").setResult(); } catch (Exception ex) {
      System.out.println("setResult warning: " + ex.getMessage());
    }

    model.save(OUT);
    System.out.println("Saved fixed-lid scheme B calibration result: " + OUT);
  }
}
