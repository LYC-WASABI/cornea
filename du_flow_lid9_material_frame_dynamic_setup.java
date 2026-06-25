import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_material_frame_dynamic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\14_lid9mm_quasistatic_dynamic_sliding_material_frame_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("14_lid9mm_quasistatic_dynamic_sliding_material_frame_setup.mph");
    model.param().set("mu_friction", "0.1", "Coulomb friction coefficient");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("ContactMethodCtrl", "Penalty");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("useCutback", "1");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{"0", "Y*(cos(phi_lid_dyn)-1)-Z*sin(phi_lid_dyn)",
            "Y*sin(phi_lid_dyn)+Z*(cos(phi_lid_dyn)-1)"});
    model.save(OUT);
    System.out.println("U0=" + java.util.Arrays.toString(model.component("comp1")
        .physics("solid").feature("disp_lid_time").getStringArray("U0")));
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
