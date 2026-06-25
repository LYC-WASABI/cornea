import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_material_frame_disconnect_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\14_lid9mm_quasistatic_dynamic_sliding_material_frame_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\15_lid9mm_material_frame_pair_disconnect_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("15_lid9mm_material_frame_pair_disconnect_setup.mph");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("pairDisconnect=" + model.component("comp1").physics("solid")
        .feature("dcnt1").getString("pairDisconnect"));
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
