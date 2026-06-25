import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_arc_sweep_source_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";

  private static void printParam(Model model, String name) {
    try {
      System.out.println("PARAM " + name + "=" + model.param().get(name)
          + " eval=" + model.param().evaluate(name));
    } catch (Exception ex) {
      System.out.println("PARAM " + name + " missing");
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    for (String p : new String[]{
        "theta_lid", "delta_indent", "Rcor", "R_lid_in", "R_lid_out",
        "L_lid_arc", "L_lid_chord", "W_lid_arc", "W_lid_chord",
        "lid_arc_length", "lid_cut_length", "lid_arc_width", "lid_cut_width",
        "t_lid", "tlid"}) {
      printParam(model, p);
    }
    System.out.println("GEOMETRY");
    for (String tag : model.component("comp1").geom("geom1").feature().tags()) {
      ModelEntity f = model.component("comp1").geom("geom1").feature(tag);
      System.out.println(tag + " : " + f.label());
      for (String key : new String[]{"size", "pos", "rot", "axis"}) {
        try {
          System.out.println("  " + key + "="
              + Arrays.toString(model.component("comp1").geom("geom1").feature(tag).getStringArray(key)));
        } catch (Exception ignored) {}
      }
    }
    System.out.println("SELECTIONS");
    for (String tag : new String[]{
        "sel_lid_box_rot", "sel_lid_contact_source_robust", "sel_lid_outer_support",
        "sel_cornea_anterior_surface"}) {
      try {
        System.out.println(tag + "="
            + Arrays.toString(model.component("comp1").selection(tag).entities(2)));
      } catch (Exception ex) {
        System.out.println(tag + " missing");
      }
    }
  }
}
