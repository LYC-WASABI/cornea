import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid9_quasistatic_dynamic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_9mm_static_preload_minus35_calibrated_0p03N_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph";

  private static boolean hasVariable(Model model, String tag) {
    for (String t : model.component("comp1").variable().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    model.param().set("theta_lid", "-35[deg]", "Initial lid position for dynamic scratching");
    model.param().set("theta_slide_total", "-70[deg]", "Additional whole-lid rotation from -35 deg to +35 deg");
    model.param().set("T_pre", "0.01[s]", "Initial hold after stationary preload");
    model.param().set("T_slide", "0.50[s]", "Smooth scratching duration");
    model.param().set("T_hold", "0.02[s]", "Final hold");
    model.param().set("dt_out", "0.01[s]", "Output step");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();

    if (!hasVariable(model, "var_dynamic_lid_motion")) {
      model.component("comp1").variable().create("var_dynamic_lid_motion");
    }
    model.component("comp1").variable("var_dynamic_lid_motion").label("Whole-lid scratching motion");
    model.component("comp1").variable("var_dynamic_lid_motion").set("slide_fraction",
        "if(t<T_pre,0,if(t<T_pre+T_slide,0.5-0.5*cos(pi*(t-T_pre)/T_slide),1))");
    model.component("comp1").variable("var_dynamic_lid_motion").set("phi_lid_dyn",
        "theta_slide_total*slide_fraction");
    model.component("comp1").variable("var_dynamic_lid_motion").set("theta_lid_physical",
        "-35[deg]+70[deg]*slide_fraction");

    try { model.component("comp1").physics("solid").feature().remove("fix_lid_outer_support"); }
    catch (Exception ignored) {}
    try { model.component("comp1").physics("solid").feature().remove("disp_lid_time"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("disp_lid_time", "Displacement2", 2);
    model.component("comp1").physics("solid").feature("disp_lid_time")
        .label("Prescribed whole-lid rotation around corneal center");
    model.component("comp1").physics("solid").feature("disp_lid_time")
        .selection().named("sel_lid_outer_support");
    model.component("comp1").physics("solid").feature("disp_lid_time")
        .set("Direction", new String[]{"prescribed", "prescribed", "prescribed"});
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{"0", "y*(cos(phi_lid_dyn)-1)-z*sin(phi_lid_dyn)",
            "y*sin(phi_lid_dyn)+z*(cos(phi_lid_dyn)-1)"});
    model.component("comp1").physics("solid").prop("StructuralTransientBehavior")
        .set("StructuralTransientBehavior", "Quasistatic");
    try {
      model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    } catch (Exception ignored) {}

    try { model.study().remove("std_dynamic_slide"); } catch (Exception ignored) {}
    model.study().create("std_dynamic_slide");
    model.study("std_dynamic_slide").label("Step 2: quasi-static dynamic scratching -35 deg to +35 deg");
    model.study("std_dynamic_slide").create("time", "Transient");
    model.study("std_dynamic_slide").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.study("std_dynamic_slide").feature("time").set("geometricNonlinearity", "on");
    model.study("std_dynamic_slide").feature("time").set("useinitsol", "on");
    model.study("std_dynamic_slide").feature("time").set("initmethod", "sol");
    model.study("std_dynamic_slide").feature("time").set("initstudy", "std_preload");
    model.study("std_dynamic_slide").feature("time").set("initstudystep", "stat");
    model.study("std_dynamic_slide").feature("time").set("initsol", "sol1");
    model.study("std_dynamic_slide").feature("time").set("initsoluse", "sol1");
    model.study("std_dynamic_slide").feature("time").set("initsolusesolnum", 15);

    System.out.println("contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    model.save(OUT);
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
