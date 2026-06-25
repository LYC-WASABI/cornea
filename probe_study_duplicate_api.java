import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_study_duplicate_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      model.study().duplicate("std_dup_test", "std7");
      System.out.println("DUPLICATED="
          + model.study("std_dup_test").label());
      for (String tag : model.study("std_dup_test").feature().tags()) {
        System.out.println(tag + "="
            + model.study("std_dup_test").feature(tag).getType());
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
