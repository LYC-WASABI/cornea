import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_tff_properties_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration7_local_gain050_results.mph";

  private static void dump(Model model, String tag) {
    System.out.println("FEATURE " + tag + " TYPE="
        + model.component("comp1").physics("tff").feature(tag).getType());
    for (String p : model.component("comp1").physics("tff").feature(tag).properties()) {
      try { System.out.println("  " + p + "="
          + model.component("comp1").physics("tff").feature(tag).getString(p)); }
      catch (Exception ignored) {}
      try {
        String[] allowed = model.component("comp1").physics("tff").feature(tag)
            .getAllowedPropertyValues(p);
        if (allowed != null && allowed.length > 0) {
          System.out.println("  ALLOWED " + p + "=" + Arrays.toString(allowed));
        }
      } catch (Exception ignored) {}
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe", IN);
    model.component("comp1").physics().create("tff", "ThinFilmFlowShell", "geom1");
    for (String tag : model.component("comp1").physics("tff").feature().tags()) {
      dump(model, tag);
    }
  }
}
