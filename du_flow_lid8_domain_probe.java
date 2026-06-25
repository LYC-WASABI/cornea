import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_domain_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph");
    for (String tag : model.component("comp1").material().tags()) {
      System.out.println(tag + " label=" + model.component("comp1").material(tag).label());
      try { System.out.println("  domains=" + Arrays.toString(
          model.component("comp1").material(tag).selection().entities())); }
      catch (Exception ignored) {}
    }
  }
}
