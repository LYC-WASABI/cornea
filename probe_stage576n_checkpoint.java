import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage576n_checkpoint {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "576n_stage576_full_dynamic_recursive_results.mph");
      for (String tag : model.sol().tags()) {
        System.out.println("SOL tag=" + tag + " label=" + model.sol(tag).label());
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
