import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_material_frame_stable_range_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\14_lid9mm_quasistatic_dynamic_sliding_material_frame_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\17_lid9mm_material_frame_stable_range_minus35_to_plus32_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("17_lid9mm_material_frame_stable_range_minus35_to_plus32_setup.mph");
    model.param().set("theta_slide_total", "-67[deg]",
        "Stable-range whole-lid rotation from -35 deg to +32 deg");
    model.component("comp1").variable("var_dynamic_lid_motion").set("theta_lid_physical",
        "-35[deg]+67[deg]*slide_fraction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("ContactMethodCtrl", "Penalty");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "0");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
