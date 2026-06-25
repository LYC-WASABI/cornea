import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_tff_ffp1_all_properties {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "369_lid8mm_stage195_official_jfo_h3um_setup_Model.mph");
    System.out.println("TYPE=" + model.component("comp1").physics("tff")
        .feature("ffp1").getType());
    for (String property :
        model.component("comp1").physics("tff").feature("ffp1").properties()) {
      try {
        System.out.println(property + "=" + Arrays.toString(
            model.component("comp1").physics("tff").feature("ffp1")
                .getStringArray(property)));
      } catch (Exception ignored) {
      }
      try {
        String[] allowed =
            model.component("comp1").physics("tff").feature("ffp1")
                .getAllowedPropertyValues(property);
        if (allowed != null && allowed.length > 0) {
          System.out.println("ALLOWED " + property + "="
              + Arrays.toString(allowed));
        }
      } catch (Exception ignored) {
      }
    }
    ModelUtil.disconnect();
  }
}
