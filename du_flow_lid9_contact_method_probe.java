import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid9_contact_method_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    System.out.println("ContactMethodCtrl=" + model.component("comp1").physics("solid")
        .feature("dcnt1").getString("ContactMethodCtrl"));
    System.out.println("Allowed=" + Arrays.toString(
        model.component("comp1").physics("solid").feature("dcnt1")
            .getAllowedPropertyValues("ContactMethodCtrl")));
    for (String p : model.component("comp1").physics("solid").feature("dcnt1")
        .feature("fric1").properties()) {
      try { System.out.println("FRICTION " + p + "=" + model.component("comp1")
          .physics("solid").feature("dcnt1").feature("fric1").getString(p)); }
      catch (Exception ignored) {}
    }
  }
}
