import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_contact_friction_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\02_dynamic_preload_minus35_fixed_results.mph";

  private static void dump(Model model, String tag) {
    System.out.println("FEATURE " + tag + " type="
        + model.component("comp1").physics("solid").feature(tag).getType()
        + " label=" + model.component("comp1").physics("solid").feature(tag).label());
    for (String p : model.component("comp1").physics("solid").feature(tag).properties()) {
      try { System.out.println("  " + p + "=" + Arrays.toString(
          model.component("comp1").physics("solid").feature(tag).getStringArray(p))); }
      catch (Exception ex) { System.out.println("  " + p); }
    }
    for (String c : model.component("comp1").physics("solid").feature(tag).feature().tags()) {
      System.out.println("  CHILD " + c + " type="
          + model.component("comp1").physics("solid").feature(tag).feature(c).getType()
          + " label=" + model.component("comp1").physics("solid").feature(tag).feature(c).label());
      for (String p : model.component("comp1").physics("solid").feature(tag).feature(c).properties()) {
        try { System.out.println("    " + p + "=" + Arrays.toString(
            model.component("comp1").physics("solid").feature(tag).feature(c).getStringArray(p))); }
        catch (Exception ex) { System.out.println("    " + p); }
      }
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    dump(model, "dcnt1");
  }
}
