import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_friction_properties_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Dry",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration7_local_gain050_results.mph");
    for (String p : model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").properties()) {
      try { System.out.println(p + "="
          + model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").getString(p)); }
      catch (Exception ignored) {}
      try {
        String[] values = model.component("comp1").physics("solid").feature("dcnt1")
            .feature("fric1").getAllowedPropertyValues(p);
        if (values != null && values.length > 0) System.out.println("  allowed=" + Arrays.toString(values));
      } catch (Exception ignored) {}
    }
  }
}
