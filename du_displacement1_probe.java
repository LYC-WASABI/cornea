import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_displacement1_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    for (String type : new String[]{"Displacement0", "Displacement1", "Displacement2", "Displacement3"}) {
      for (int dim : new int[]{0, 1, 2, 3}) {
      String tag = "probe_" + type + "_" + dim;
      try {
        model.component("comp1").physics("solid").create(tag, type, dim);
        System.out.println("OK type=" + type + " dim=" + dim);
        for (String p : model.component("comp1").physics("solid").feature(tag).properties()) {
          try { System.out.println("  " + p + "=" + Arrays.toString(
              model.component("comp1").physics("solid").feature(tag).getStringArray(p))); }
          catch (Exception ex) { System.out.println("  " + p); }
        }
      } catch (Exception ex) {
        System.out.println("NO type=" + type + " dim=" + dim + " : " + ex.getMessage());
      }
      }
    }
  }
}
