import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_short_entry_diagnostic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\13_lid9mm_short_entry_minus35_to_minus30_frictionless_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("13_lid9mm_short_entry_minus35_to_minus30_frictionless_setup.mph");
    model.param().set("mu_friction", "0", "Frictionless diagnostic");
    model.param().set("theta_slide_total", "-5[deg]", "Diagnostic additional rotation from -35 deg to -30 deg");
    model.component("comp1").variable("var_dynamic_lid_motion").set("theta_lid_physical",
        "-35[deg]+5[deg]*slide_fraction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("ContactMethodCtrl", "Penalty");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
