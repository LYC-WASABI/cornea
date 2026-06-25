import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_model_motion_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results-2.mph");
    System.out.println("PARAM theta_lid=" + model.param().evaluate("theta_lid"));
    System.out.println("PARAM delta_indent=" + model.param().evaluate("delta_indent"));
    System.out.println("Geometry features:");
    for (String tag : model.component("comp1").geom("geom1").feature().tags()) {
      try {
        System.out.println(tag + " : " + model.component("comp1").geom("geom1").feature(tag).label()
            + " type=" + model.component("comp1").geom("geom1").feature(tag).getType());
        for (String key : new String[]{"rot", "axis", "pos", "selresult", "input", "intbnd"}) {
          try { System.out.println("  " + key + "=" + Arrays.toString(model.component("comp1").geom("geom1").feature(tag).getStringArray(key))); } catch (Exception ignored) {}
          try { System.out.println("  " + key + "=" + model.component("comp1").geom("geom1").feature(tag).getString(key)); } catch (Exception ignored) {}
        }
      } catch (Exception ex) {
        System.out.println(tag + " : " + ex.getMessage());
      }
    }
    System.out.println("Studies=" + Arrays.toString(model.study().tags()));
    System.out.println("Solutions=" + Arrays.toString(model.sol().tags()));
    System.out.println("Result groups=" + Arrays.toString(model.result().tags()));
  }
}
