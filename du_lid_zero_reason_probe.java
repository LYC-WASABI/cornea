import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_lid_zero_reason_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results-2.mph");
    model.component("comp1").geom("geom1").run();
    System.out.println("Selections:");
    for (String s : new String[]{
      "sel_lid_contact_source_robust",
      "sel_lid_contact_source_robust2",
      "sel_lid_hold_robust",
      "sel_lid_hold_robust2",
      "sel_lid_box_rot",
      "sel_lid_wiper_inner_surface_dyn",
      "sel_cornea_anterior_surface"
    }) {
      try {
        System.out.println(s + " entities2=" + Arrays.toString(model.component("comp1").selection(s).entities(2)));
      } catch (Exception ex) {
        System.out.println(s + " missing/error=" + ex.getMessage());
      }
    }
    System.out.println("Solid features:");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      try {
        System.out.println(f + " label=" + model.component("comp1").physics("solid").feature(f).label()
            + " type=" + model.component("comp1").physics("solid").feature(f).getType()
            + " sel=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities()));
      } catch (Exception ex) {
        System.out.println(f + " error=" + ex.getMessage());
      }
    }
    System.out.println("Materials:");
    for (String m : model.component("comp1").material().tags()) {
      try {
        System.out.println(m + " label=" + model.component("comp1").material(m).label()
            + " sel=" + Arrays.toString(model.component("comp1").material(m).selection().entities()));
      } catch (Exception ex) {
        System.out.println(m + " error=" + ex.getMessage());
      }
    }
  }
}
