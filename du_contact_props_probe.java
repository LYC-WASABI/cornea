import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_contact_props_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    String[] features = new String[]{"dcnt1", "dgcnt1", "gcnt1"};
    for (String f : features) {
      try {
        System.out.println("FEATURE " + f + " label=" + model.component("comp1").physics("solid").feature(f).label());
        for (String p : model.component("comp1").physics("solid").feature(f).properties()) {
          System.out.println("  prop " + p);
        }
        for (String child : model.component("comp1").physics("solid").feature(f).feature().tags()) {
          System.out.println("  child " + child + " label=" + model.component("comp1").physics("solid").feature(f).feature(child).label());
          for (String p : model.component("comp1").physics("solid").feature(f).feature(child).properties()) {
            System.out.println("    prop " + p);
          }
        }
      } catch (Exception e) {
        System.out.println("FEATURE " + f + " ERROR " + e.getMessage());
      }
    }
  }
}
