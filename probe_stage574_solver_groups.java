import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_solver_groups {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574a_stage574_midpoint_source_jfo_setup.mph");
      SolverFeature stationary = model.sol("sol95").feature("s1");
      System.out.println("S1_CHILDREN="
          + Arrays.toString(stationary.feature().tags()));
      for (String tag : stationary.feature().tags()) {
        SolverFeature feature = stationary.feature(tag);
        System.out.println("FEATURE|" + tag + "|" + feature.label()
            + "|TYPE=" + feature.getType());
        System.out.println("PROPS="
            + Arrays.toString(feature.properties()));
        for (String property : feature.properties()) {
          try {
            String[] values = feature.getStringArray(property);
            if (values.length > 0) {
              System.out.println("  " + property + "="
                  + Arrays.toString(values));
            }
          } catch (Exception ignored) {}
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
