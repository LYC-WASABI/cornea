import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_ffp_all_allowed_values {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "368_lid8mm_stage194_cavitation_limited_joint_load_results_Model.mph");
    for (String property :
        model.component("comp1").physics("tff").feature("ffp1").properties()) {
      try {
        String[] allowed = model.component("comp1").physics("tff")
            .feature("ffp1").getAllowedPropertyValues(property);
        if (allowed != null && allowed.length > 0) {
          System.out.println(property + "="
              + Arrays.toString(model.component("comp1").physics("tff")
                  .feature("ffp1").getStringArray(property)));
          System.out.println("ALLOWED " + property + "="
              + Arrays.toString(allowed));
        }
      } catch (Exception ignored) {
      }
    }
    ModelUtil.disconnect();
  }
}
