import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage576n8_stopped_checkpoint {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576n11_stage576_local_halfstep_resume180_results.mph");
      System.out.println("SOLUTION_COUNT=" + model.sol().tags().length);
      for (String tag : model.sol().tags()) {
        int number = -1;
        try { number = Integer.parseInt(tag.replace("sol", "")); }
        catch (Exception ignored) {}
        if (number < 3550) continue;
        String label;
        try {
          label = model.sol(tag).label();
        } catch (Exception error) {
          label = "<unreadable>";
        }
        System.out.println("SOL tag=" + tag + " label=" + label);
      }
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
