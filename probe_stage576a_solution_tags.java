import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage576a_solution_tags {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "576a_stage576_active_time_feedback_closure_refined_results.mph");
      for (String tag : model.sol().tags()) {
        System.out.println("SOL_TAG=" + tag);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
