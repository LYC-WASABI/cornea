import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_tff_mass_source {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "399_lid8mm_stage205_partitioned_first_step_film_results_Model.mph");
    String tag = "msprobe";
    try { model.component("comp1").physics("tff").feature().remove(tag); }
    catch (Exception ignored) {}
    model.component("comp1").physics("tff").create(tag, "MassSource", 2);
    model.component("comp1").physics("tff").feature(tag).selection().all();
    System.out.println("TYPE=" + model.component("comp1").physics("tff")
        .feature(tag).getType());
    for (String property :
        model.component("comp1").physics("tff").feature(tag).properties()) {
      try {
        System.out.println(property + "=" + Arrays.toString(
            model.component("comp1").physics("tff").feature(tag)
                .getStringArray(property)));
      } catch (Exception ignored) {}
      try {
        String[] allowed = model.component("comp1").physics("tff")
            .feature(tag).getAllowedPropertyValues(property);
        if (allowed != null && allowed.length > 0) {
          System.out.println("ALLOWED " + property + "="
              + Arrays.toString(allowed));
        }
      } catch (Exception ignored) {}
    }
    ModelUtil.disconnect();
  }
}
