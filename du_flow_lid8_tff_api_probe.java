import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_tff_api_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration7_local_gain050_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    String[] candidates = new String[]{
        "ThinFilmFlowShell", "ThinFilmFlow", "ReynoldsEquation", "ThinFilmFlowDomain"
    };
    for (String type : candidates) {
      Model model = ModelUtil.load("M" + type, IN);
      try {
        model.component("comp1").physics().create("tff", type, "geom1");
        System.out.println("SUCCESS type=" + type);
        System.out.println("DEFAULT_FEATURES=" + Arrays.toString(
            model.component("comp1").physics("tff").feature().tags()));
        for (String feature : model.component("comp1").physics("tff").feature().tags()) {
          System.out.println("FEATURE " + feature + " TYPE="
              + model.component("comp1").physics("tff").feature(feature).getType());
          System.out.println("  PROPS=" + Arrays.toString(
              model.component("comp1").physics("tff").feature(feature).properties()));
        }
      } catch (Exception ex) {
        System.out.println("FAILED type=" + type + " message=" + ex.getMessage());
      }
      ModelUtil.remove("M" + type);
    }
  }
}
