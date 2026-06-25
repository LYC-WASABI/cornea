import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_solver_init {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "stage555_phase2_to_020_output_Model.mph");
      for (String sol : model.sol().tags()) {
        if (!"sol61".equals(sol) && !"sol62".equals(sol)) continue;
        System.out.println("SOLUTION " + sol);
        for (String tag : model.sol(sol).feature().tags()) {
          System.out.println("  TOP " + tag + " "
              + model.sol(sol).feature(tag).getType());
          SolverFeature top = model.sol(sol).feature(tag);
          for (String p : top.properties()) {
            if ("v1".equals(tag) || "st1".equals(tag)) {
              try {
                System.out.println("    PROP " + p + "="
                    + Arrays.toString(top.getStringArray(p)));
              } catch (Exception ignored) {}
            }
          }
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
