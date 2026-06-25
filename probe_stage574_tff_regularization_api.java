import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_tff_regularization_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_cornea_dynamic_regions_checked.mph");
      ModelNode comp = model.component("comp1");
      try {
        System.out.println("EQUATION_TYPE="
            + Arrays.toString(comp.physics("tff")
                .prop("EquationType").getStringArray("EquationType")));
      } catch (Exception error) {
        System.out.println("EQUATION_TYPE_ERROR=" + error.getMessage());
      }
      for (String tag : comp.physics("tff").feature().tags()) {
        var feature = comp.physics("tff").feature(tag);
        System.out.println("FEATURE=" + tag + "|" + feature.getType()
            + "|" + feature.label());
      }
      for (String type : new String[] {
          "WeakContribution", "WeakConstraint",
          "DomainODE", "DistributedODE", "Reaction"
      }) {
        String tag = "probe_" + type.toLowerCase(Locale.ROOT);
        try {
          comp.physics("tff").create(tag, type, 2);
          var feature = comp.physics("tff").feature(tag);
          System.out.println("CREATED=" + type + "|"
              + Arrays.toString(feature.properties()));
        } catch (Exception error) {
          System.out.println("FAILED=" + type + "|" + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
