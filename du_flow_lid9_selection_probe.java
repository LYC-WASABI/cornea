import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid9_selection_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph");
    model.param().set("s_lid", "9[mm]");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))");
    model.param().set("theta_lid", "0[deg]");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    for (String tag : model.component("comp1").selection().tags()) {
      try {
        int[] entities = model.component("comp1").selection(tag).entities(2);
        System.out.println("SELECTION " + tag + "=" + Arrays.toString(entities));
      } catch (Exception ignored) {}
    }
  }
}
