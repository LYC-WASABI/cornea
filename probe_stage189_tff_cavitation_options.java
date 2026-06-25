import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage189_tff_cavitation_options {
  static void dump(Model model, String tag) {
    System.out.println("FEATURE " + tag + " TYPE="
        + model.component("comp1").physics("tff").feature(tag).getType());
    for (String property :
        model.component("comp1").physics("tff").feature(tag).properties()) {
      String lower = property.toLowerCase(Locale.ROOT);
      if (!lower.contains("cav") && !lower.contains("press")
          && !lower.contains("film") && !lower.contains("limit")) continue;
      try {
        System.out.println("  " + property + "="
            + Arrays.toString(model.component("comp1").physics("tff")
                .feature(tag).getStringArray(property)));
      } catch (Exception ignored) {
      }
      try {
        String[] allowed = model.component("comp1").physics("tff")
            .feature(tag).getAllowedPropertyValues(property);
        if (allowed != null && allowed.length > 0) {
          System.out.println("  ALLOWED " + property + "="
              + Arrays.toString(allowed));
        }
      } catch (Exception ignored) {
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "355_lid8mm_stage189_physical_h3um_reference_checked_Model.mph");
    for (String tag : model.component("comp1").physics("tff").feature().tags()) {
      dump(model, tag);
    }
    ModelUtil.disconnect();
  }
}
