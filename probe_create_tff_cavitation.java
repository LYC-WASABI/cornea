import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_create_tff_cavitation {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "368_lid8mm_stage194_cavitation_limited_joint_load_results_Model.mph");
    String[] types = {"Cavitation", "ThinFilmCavitation"};
    for (String type : types) {
      try {
        model.component("comp1").physics("tff").create("cavprobe", type, 2);
        System.out.println("CREATED type=" + type);
        System.out.println("PROPERTIES=" + Arrays.toString(
            model.component("comp1").physics("tff")
                .feature("cavprobe").properties()));
        for (String property :
            model.component("comp1").physics("tff")
                .feature("cavprobe").properties()) {
          try {
            System.out.println(property + "=" + Arrays.toString(
                model.component("comp1").physics("tff")
                    .feature("cavprobe").getStringArray(property)));
          } catch (Exception ignored) {
          }
          try {
            String[] allowed = model.component("comp1").physics("tff")
                .feature("cavprobe").getAllowedPropertyValues(property);
            if (allowed != null && allowed.length > 0) {
              System.out.println("ALLOWED " + property + "="
                  + Arrays.toString(allowed));
            }
          } catch (Exception ignored) {
          }
        }
        model.component("comp1").physics("tff").feature().remove("cavprobe");
      } catch (Exception error) {
        System.out.println("FAILED type=" + type + " message=" + error.getMessage());
      }
    }
    ModelUtil.disconnect();
  }
}
