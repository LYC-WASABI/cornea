import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_tff_boundary_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\59_lid8mm_stage17_strong_coupling_continuation_controls_setup.mph");
    for (String tag : model.component("comp1").physics("tff").feature().tags()) {
      System.out.println(tag + " type=" + model.component("comp1").physics("tff").feature(tag).getType()
          + " label=" + model.component("comp1").physics("tff").feature(tag).label());
      try { System.out.println("  entities=" + Arrays.toString(
          model.component("comp1").physics("tff").feature(tag).selection().entities())); }
      catch (Exception ignored) {}
    }
  }
}
