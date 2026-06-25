import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558o_stage565_selections {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558o_stage565_pressure_relaxation_setup.mph");
      ModelNode comp = model.component("comp1");

      String[] loads = {
        "press_iop", "press_iop1", "load_partitioned_pfilm"
      };
      for (String tag : loads) {
        try {
          System.out.println("LOAD|" + tag + "|"
              + comp.physics("solid").feature(tag).label());
          System.out.println("NAMED|" + tag + "|"
              + comp.physics("solid").feature(tag).selection().named());
          System.out.println("ENTITIES|" + tag + "|"
              + Arrays.toString(
                  comp.physics("solid").feature(tag).selection().entities(2)));
          System.out.println("FPERAREA|" + tag + "|"
              + Arrays.toString(
                  comp.physics("solid").feature(tag)
                      .getStringArray("FperArea")));
        } catch (Exception error) {
          System.out.println("LOAD_ERROR|" + tag + "|" + error.getMessage());
        }
      }

      for (String tag : comp.variable().tags()) {
        if (tag.toLowerCase().contains("565")
            || comp.variable(tag).label().contains("565")) {
          System.out.println("VARIABLE|" + tag + "|"
              + comp.variable(tag).label());
          System.out.println("VAR_NAMED|" + tag + "|"
              + comp.variable(tag).selection().named());
          System.out.println("VAR_ENTITIES|" + tag + "|"
              + Arrays.toString(comp.variable(tag).selection().entities(2)));
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
