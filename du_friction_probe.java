import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_friction_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    String[] parents = new String[]{"dcnt1", "dgcnt1", "gcnt1"};
    String[] types = new String[]{"FrictionSlipVelocity", "Friction", "ContactFriction", "ContactDamping", "Wear"};
    for (String parent : parents) {
      for (String type : types) {
        String tag = "p_" + parent + "_" + type;
        try {
          model.component("comp1").physics("solid").feature(parent).feature().create(tag, type);
          System.out.println("CREATED parent=" + parent + " type=" + type);
          for (String p : model.component("comp1").physics("solid").feature(parent).feature(tag).properties()) {
            System.out.println("  prop " + p);
          }
        } catch (Exception e) {
          System.out.println("FAILED parent=" + parent + " type=" + type + " :: " + e.getMessage());
        }
      }
    }
  }
}
