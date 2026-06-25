import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_model_inventory {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\38_lid8mm_mixed_lubrication_stage5_partitioned_film_h6um_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("STUDIES");
    for (String tag : model.study().tags()) {
      System.out.printf("%s label=%s%n", tag, model.study(tag).label());
      for (String step : model.study(tag).feature().tags()) {
        System.out.printf("  %s type=%s label=%s%n", step,
            model.study(tag).feature(step).getType(), model.study(tag).feature(step).label());
      }
    }
    System.out.println("SOLUTIONS");
    for (String tag : model.sol().tags()) {
      System.out.printf("%s label=%s study=%s%n", tag, model.sol(tag).label(), model.sol(tag).study());
    }
    System.out.println("DRY_DYNAMIC_INIT");
    StudyFeature dry = model.study("std_dynamic_slide").feature("time");
    for (String key : new String[]{"useinitsol", "initmethod", "initstudy", "initstudystep",
                                   "initsol", "initsoluse", "initsolusesolnum"}) {
      try { System.out.printf("%s=%s%n", key, dry.getString(key)); }
      catch (Exception e) { System.out.printf("%s=<non-string>%n", key); }
    }
    System.out.println("DATASETS");
    for (String tag : model.result().dataset().tags()) {
      System.out.printf("%s type=%s label=%s%n", tag,
          model.result().dataset(tag).getType(), model.result().dataset(tag).label());
    }
  }
}
