import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_dynamic_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    System.out.println("PARAMETERS");
    for (String p : model.param().varnames()) {
      try { System.out.println(p + "=" + model.param().get(p)); } catch (Exception ignored) {}
    }
    System.out.println("SOLID FEATURES");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println(f + " : " + model.component("comp1").physics("solid").feature(f).label()
          + " type=" + model.component("comp1").physics("solid").feature(f).getType());
      try {
        System.out.println("  sel="
            + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities()));
      } catch (Exception ignored) {}
    }
    System.out.println("SELECTIONS");
    for (String s : model.component("comp1").selection().tags()) {
      if (s.contains("lid") || s.contains("cornea") || s.contains("contact")) {
        try {
          System.out.println(s + "=" + Arrays.toString(model.component("comp1").selection(s).entities(2)));
        } catch (Exception ex) {
          System.out.println(s + " error=" + ex.getMessage());
        }
      }
    }
    System.out.println("PAIR");
    for (String p : model.component("comp1").pair().tags()) {
      System.out.println(p + " : " + model.component("comp1").pair(p).label());
    }
    try { model.component("comp1").physics("solid").feature().remove("probe_disp"); } catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("probe_disp", "PrescribedDisplacement", 2);
    System.out.println("PRESCRIBED PROPERTIES");
    for (String p : model.component("comp1").physics("solid").feature("probe_disp").properties()) {
      try {
        System.out.println(p + "=" + Arrays.toString(
            model.component("comp1").physics("solid").feature("probe_disp").getStringArray(p)));
      } catch (Exception ex) {
        try {
          System.out.println(p + "="
              + model.component("comp1").physics("solid").feature("probe_disp").getString(p));
        } catch (Exception ignored) {
          System.out.println(p);
        }
      }
    }
  }
}
