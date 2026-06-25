import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_contact_selection_strategy_probe {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";

  private static void rebuildSelections(Model model) {
    String[] old = {
      "probe_lid_inner_ball", "probe_lid_inner_candidates", "probe_lid_source"
    };
    for (String s : old) {
      try { model.component("comp1").selection().remove(s); } catch (Exception ignored) {}
    }
    model.component("comp1").selection().create("probe_lid_inner_ball", "Ball");
    model.component("comp1").selection("probe_lid_inner_ball").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_inner_ball").set("posx", "0");
    model.component("comp1").selection("probe_lid_inner_ball").set("posy", "0");
    model.component("comp1").selection("probe_lid_inner_ball").set("posz", "0");
    model.component("comp1").selection("probe_lid_inner_ball").set("r", "Rlid_in+0.20[mm]");
    model.component("comp1").selection("probe_lid_inner_ball").set("condition", "intersects");

    model.component("comp1").selection().create("probe_lid_inner_candidates", "Intersection");
    model.component("comp1").selection("probe_lid_inner_candidates").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_inner_candidates").set("input", new String[]{"probe_lid_inner_ball", "sel_lid_box_rot"});

    model.component("comp1").selection().create("probe_lid_source", "Difference");
    model.component("comp1").selection("probe_lid_source").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_source").set("add", new String[]{"probe_lid_inner_candidates"});
    model.component("comp1").selection("probe_lid_source").set("subtract", new String[]{"sel_cornea_anterior_surface"});
  }

  private static void printSel(Model model, String tag) {
    try {
      System.out.println("  " + tag + " = " + Arrays.toString(model.component("comp1").selection(tag).entities(2)));
    } catch (Exception ex) {
      System.out.println("  " + tag + " error = " + ex.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    rebuildSelections(model);
    String[] vals = {"0.15[mm]", "0.17[mm]", "0.18[mm]", "0.19[mm]", "0.20[mm]"};
    for (String v : vals) {
      System.out.println("delta_indent=" + v);
      model.param().set("delta_indent", v);
      model.param().set("theta_lid", "0[deg]");
      model.component("comp1").geom("geom1").run();
      printSel(model, "probe_lid_inner_ball");
      printSel(model, "probe_lid_inner_candidates");
      printSel(model, "probe_lid_source");
      printSel(model, "sel_cornea_anterior_surface");
    }
  }
}
