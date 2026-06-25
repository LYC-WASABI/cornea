import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_selection_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph");
    System.out.println("Component selections:");
    for (String tag : model.component("comp1").selection().tags()) {
      try {
        System.out.println(tag + " : " + model.component("comp1").selection(tag).label()
            + " geom=" + model.component("comp1").selection(tag).geom()
            + " entities=" + Arrays.toString(model.component("comp1").selection(tag).entities()));
      } catch (Exception ex) {
        System.out.println(tag + " : " + ex.getMessage());
      }
    }
    System.out.println("Solid features:");
    for (String tag : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println(tag + " : " + model.component("comp1").physics("solid").feature(tag).label()
          + " type=" + model.component("comp1").physics("solid").feature(tag).getType());
      try {
        System.out.println("  sel=" + Arrays.toString(model.component("comp1").physics("solid").feature(tag).selection().entities()));
      } catch (Exception ignored) {}
    }
  }
}
