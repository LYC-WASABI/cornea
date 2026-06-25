import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_solid_root_props_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\03_dynamic_sliding_from_preload_setup.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    for (String group : model.component("comp1").physics("solid").prop().tags()) {
      System.out.println("GROUP " + group);
      for (String p : model.component("comp1").physics("solid").prop(group).properties()) {
        try {
          System.out.println("  " + p + "=" + Arrays.toString(
              model.component("comp1").physics("solid").prop(group).getStringArray(p)));
        } catch (Exception ex) {
          try { System.out.println("  " + p + "="
              + model.component("comp1").physics("solid").prop(group).getString(p)); }
          catch (Exception ignored) { System.out.println("  " + p); }
        }
      }
    }
  }
}
