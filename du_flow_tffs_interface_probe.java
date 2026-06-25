import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_tffs_interface_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\31_lid8mm_force_calibrated_speed_0p5mms_setup.mph");
    model.component("comp1").physics().create("tffs", "ThinFilmFlowShell", "geom1");
    model.component("comp1").physics("tffs").selection().named("sel_cornea_anterior_surface");
    System.out.println("TFFS_FEATURES=" + Arrays.toString(model.component("comp1")
        .physics("tffs").feature().tags()));
    for (String tag : model.component("comp1").physics("tffs").feature().tags()) {
      System.out.println("FEATURE " + tag + " TYPE="
          + model.component("comp1").physics("tffs").feature(tag).getType());
      for (String p : model.component("comp1").physics("tffs").feature(tag).properties()) {
        try { System.out.println("  " + p + "=" + model.component("comp1")
            .physics("tffs").feature(tag).getString(p)); } catch (Exception ignored) {}
      }
    }
    System.out.println("ROOT_PROPERTIES");
    for (String p : model.component("comp1").physics("tffs").properties()) {
      try { System.out.println("  " + p + "=" + model.component("comp1")
          .physics("tffs").getString(p)); } catch (Exception ignored) {}
    }
  }
}
