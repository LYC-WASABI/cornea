import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage112_probe_current_contact_props {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      String[] tags = m.component("comp1").physics("solid").feature().tags();
      for (String tag : tags) {
        try {
          String label = m.component("comp1").physics("solid").feature(tag).label();
          if (tag.toLowerCase().contains("cnt") || label.toLowerCase().contains("contact")) {
            System.out.println("FEATURE " + tag + " label=" + label);
            for (String p : m.component("comp1").physics("solid").feature(tag).properties()) {
              System.out.println("  prop " + p);
            }
            for (String child : m.component("comp1").physics("solid").feature(tag).feature().tags()) {
              System.out.println("  CHILD " + child + " label="
                  + m.component("comp1").physics("solid").feature(tag).feature(child).label());
              for (String p : m.component("comp1").physics("solid").feature(tag).feature(child).properties()) {
                System.out.println("    prop " + p);
              }
            }
          }
        } catch (Exception ignore) {}
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
