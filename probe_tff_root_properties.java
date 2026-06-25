import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_tff_root_properties {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "368_lid8mm_stage194_cavitation_limited_joint_load_results_Model.mph");
    for (String group :
        model.component("comp1").physics("tff").prop().tags()) {
      System.out.println("GROUP " + group);
      for (String property :
          model.component("comp1").physics("tff").prop(group).properties()) {
        try {
          System.out.println("  " + property + "=" + Arrays.toString(
              model.component("comp1").physics("tff").prop(group)
                  .getStringArray(property)));
        } catch (Exception ignored) {
        }
        try {
          String[] allowed = model.component("comp1").physics("tff")
              .prop(group).getAllowedPropertyValues(property);
          if (allowed != null && allowed.length > 0) {
            System.out.println("  ALLOWED " + property + "="
                + Arrays.toString(allowed));
          }
        } catch (Exception ignored) {
        }
      }
    }
    ModelUtil.disconnect();
  }
}
