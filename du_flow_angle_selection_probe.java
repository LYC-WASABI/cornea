import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_angle_selection_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    double[] angles = {0, -10, -20, -25, -27.5, -30, -32.5, -35};
    for (double angle : angles) {
      model.param().set("theta_lid", angle + "[deg]");
      model.component("comp1").geom("geom1").run();
      System.out.println("ANGLE=" + angle);
      for (String tag : new String[]{
          "sel_lid_contact_source_robust", "sel_lid_outer_support",
          "sel_cornea_anterior_surface", "sel_lid_box_rot"}) {
        System.out.println("  " + tag + "="
            + Arrays.toString(model.component("comp1").selection(tag).entities(2)));
      }
    }
  }
}
