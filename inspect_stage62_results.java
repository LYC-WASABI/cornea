import com.comsol.model.*;
import com.comsol.model.util.*;

public class inspect_stage62_results {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    System.out.println("RESULT_TAGS");
    for (String tag : m.result().tags()) {
      System.out.println(tag + " | " + m.result(tag).label());
    }
    System.out.println("DATASET_TAGS");
    for (String tag : m.result().dataset().tags()) {
      System.out.println(tag + " | " + m.result().dataset(tag).label());
    }
  }
}
