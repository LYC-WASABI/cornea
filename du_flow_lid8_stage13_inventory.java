import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage13_inventory {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\49_lid8mm_mixed_lubrication_stage11_weakcoupled_film_h12um_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("DATASETS");
    for (String tag : model.result().dataset().tags()) {
      System.out.printf("%s type=%s label=%s%n", tag,
          model.result().dataset(tag).getType(), model.result().dataset(tag).label());
    }
    System.out.println("SELECTIONS");
    for (String tag : model.component("comp1").selection().tags()) {
      try {
        System.out.printf("%s label=%s entities=%s%n", tag,
            model.component("comp1").selection(tag).label(),
            Arrays.toString(model.component("comp1").selection(tag).entities()));
      } catch (Exception ignored) {}
    }
    System.out.println("SOLUTIONS");
    for (String tag : model.sol().tags()) {
      System.out.printf("%s label=%s study=%s%n", tag, model.sol(tag).label(), model.sol(tag).study());
    }
  }
}
