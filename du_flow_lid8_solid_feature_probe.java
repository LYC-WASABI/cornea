import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_solid_feature_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\39_lid8mm_mixed_lubrication_stage6_partitioned_solid_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    for (String tag : model.component("comp1").physics("solid").feature().tags()) {
      System.out.printf("%s type=%s label=%s%n", tag,
          model.component("comp1").physics("solid").feature(tag).getType(),
          model.component("comp1").physics("solid").feature(tag).label());
      try {
        int[] entities = model.component("comp1").physics("solid").feature(tag).selection().entities();
        System.out.printf("  selection_count=%d%n", entities.length);
      } catch (Exception ignored) {}
      for (String child : model.component("comp1").physics("solid").feature(tag).feature().tags()) {
        System.out.printf("  %s type=%s label=%s%n", child,
            model.component("comp1").physics("solid").feature(tag).feature(child).getType(),
            model.component("comp1").physics("solid").feature(tag).feature(child).label());
      }
    }
  }
}
