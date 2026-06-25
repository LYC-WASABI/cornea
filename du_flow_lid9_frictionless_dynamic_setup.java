import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_frictionless_dynamic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\12_lid9mm_quasistatic_dynamic_sliding_frictionless_diagnostic_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("12_lid9mm_quasistatic_dynamic_sliding_frictionless_diagnostic_setup.mph");
    model.param().set("mu_friction", "0", "Diagnostic frictionless dynamic scratching");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("ContactMethodCtrl", "Penalty");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("useCutback", "1");
    model.save(OUT);
    System.out.println("mu_friction=" + model.param().get("mu_friction"));
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
