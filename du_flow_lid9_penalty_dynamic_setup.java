import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_penalty_dynamic_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\11_lid9mm_quasistatic_dynamic_sliding_penaltydynamic_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("11_lid9mm_quasistatic_dynamic_sliding_penaltydynamic_setup.mph");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("ContactMethodCtrl", "PenaltyDynamic");
    model.component("comp1").physics("solid").feature("dcnt1")
        .set("useCutback", "1");
    model.save(OUT);
    System.out.println("CONTACT_METHOD=" + model.component("comp1").physics("solid")
        .feature("dcnt1").getString("ContactMethodCtrl"));
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
