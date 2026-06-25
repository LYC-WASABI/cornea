import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_region_scope {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573a_stage573_cornea_dynamic_regions_setup.mph");
      ModelNode comp = model.component("comp1");

      System.out.println("SWEPT="
          + Arrays.toString(
              comp.selection("sel_film_swept571").entities(2)));
      System.out.println("TFF="
          + Arrays.toString(comp.physics("tff").selection().entities()));
      System.out.println("INTOP="
          + Arrays.toString(comp.cpl("intop_film").selection().entities()));

      for (String tag : comp.variable().tags()) {
        String label = comp.variable(tag).label();
        if (tag.contains("572") || tag.contains("573")
            || label.contains("572") || label.contains("573")) {
          System.out.println("VARIABLE=" + tag + "|" + label);
          try {
            System.out.println("  NAMED="
                + comp.variable(tag).selection().named());
          } catch (Exception ignored) {}
          try {
            System.out.println("  ENTITIES="
                + Arrays.toString(
                    comp.variable(tag).selection().entities(2)));
          } catch (Exception error) {
            System.out.println("  ENTITY_ERROR=" + error.getMessage());
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
