import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_contact_selection_radius_probe {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph";

  private static void printSource(Model model, String tol) {
    String ball = "tol_ball";
    String cand = "tol_cand";
    String src = "tol_src";
    for (String s : new String[]{ball, cand, src}) {
      try { model.component("comp1").selection().remove(s); } catch (Exception ignored) {}
    }
    model.component("comp1").selection().create(ball, "Ball");
    model.component("comp1").selection(ball).set("entitydim", 2);
    model.component("comp1").selection(ball).set("posx", "0");
    model.component("comp1").selection(ball).set("posy", "0");
    model.component("comp1").selection(ball).set("posz", "0");
    model.component("comp1").selection(ball).set("r", "Rlid_in+" + tol);
    model.component("comp1").selection(ball).set("condition", "intersects");
    model.component("comp1").selection().create(cand, "Intersection");
    model.component("comp1").selection(cand).set("entitydim", 2);
    model.component("comp1").selection(cand).set("input", new String[]{ball, "sel_lid_box_rot"});
    model.component("comp1").selection().create(src, "Difference");
    model.component("comp1").selection(src).set("entitydim", 2);
    model.component("comp1").selection(src).set("add", new String[]{cand});
    model.component("comp1").selection(src).set("subtract", new String[]{"sel_cornea_anterior_surface"});
    System.out.println("  tol=" + tol + " source=" + Arrays.toString(model.component("comp1").selection(src).entities(2)));
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    for (String v : new String[]{"0.17[mm]", "0.18[mm]", "0.19[mm]", "0.20[mm]"}) {
      System.out.println("delta_indent=" + v);
      model.param().set("delta_indent", v);
      model.param().set("theta_lid", "0[deg]");
      model.component("comp1").geom("geom1").run();
      for (String tol : new String[]{"0.05[mm]", "0.10[mm]", "0.15[mm]", "0.20[mm]"}) {
        printSource(model, tol);
      }
    }
  }
}
