import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_contact_selection_delta_probe {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";

  private static void printSel(Model model, String tag) {
    try {
      System.out.println("  " + tag + " = " + Arrays.toString(model.component("comp1").selection(tag).entities(2)));
    } catch (Exception ex) {
      System.out.println("  " + tag + " error = " + ex.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    String[] vals = {"0.15[mm]", "0.17[mm]", "0.18[mm]", "0.19[mm]"};
    for (String v : vals) {
      System.out.println("delta_indent=" + v);
      model.param().set("delta_indent", v);
      model.param().set("theta_lid", "0[deg]");
      model.component("comp1").geom("geom1").run();
      printSel(model, "sel_lid_box_rot");
      printSel(model, "sel_lid_inner_ball_rot");
      printSel(model, "sel_lid_inner_candidates_rot");
      printSel(model, "sel_lid_wiper_inner_surface_dyn");
      printSel(model, "sel_cornea_anterior_surface");
    }
  }
}
