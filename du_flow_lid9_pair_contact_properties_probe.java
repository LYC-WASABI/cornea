import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_pair_contact_properties_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    System.out.println("PAIR_SOURCE=" + java.util.Arrays.toString(model.component("comp1")
        .selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("PAIR_DESTINATION=" + java.util.Arrays.toString(model.component("comp1")
        .selection("sel_cornea_anterior_surface").entities(2)));
    System.out.println("CONTACT_PROPERTIES");
    for (String p : model.component("comp1").physics("solid").feature("dcnt1").properties()) {
      try { System.out.println("  " + p + "=" + model.component("comp1")
          .physics("solid").feature("dcnt1").getString(p)); } catch (Exception ignored) {}
    }
  }
}
