import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_arc_length_iterative_coarse {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph";
  private static final String SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\29_rect_lid_arc_length_iterative_calibration_setup.mph";

  private static void clean(Model model) {
    for (String tag : model.result().numerical().tags()) {
      try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().table().tags()) {
      try { model.result().table().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().tags()) {
      try { model.result().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().dataset().tags()) {
      try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.study().tags()) {
      try { model.study().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.sol().tags()) {
      try { model.sol().remove(tag); } catch (Exception ignored) {}
    }
  }

  private static void configureStraightRectangularGeometry(Model model) {
    String[] rounded = {
      "uni_round_cutter", "uni_round_cutter2",
      "blk_round_core_x", "blk_round_core_y", "blk_round_core_x2", "blk_round_core_y2",
      "cyl_round_pxp", "cyl_round_pxn", "cyl_round_nxp", "cyl_round_nxn",
      "cyl_round_pxp2", "cyl_round_pxn2", "cyl_round_nxp2", "cyl_round_nxn2"
    };
    for (String tag : rounded) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignored) {}
    }
    model.param().set("theta_lid", "0[deg]", "Apex calibration position");
    model.param().set("s_lid", "1[mm]", "Lid wiper contact-surface arc length");
    model.param().set("lid_arc_length", "s_lid", "Alias for lid wiper contact-surface arc length");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))",
        "Rectangular cutter chord corresponding to spherical contact arc length");
    model.param().set("delta_indent", "0.10[mm]", "Geometric indentation calibration parameter");
    model.component("comp1").geom("geom1").feature("blk1")
        .label("Straight rectangular cutter: variable arc length x 1 mm width");
    model.component("comp1").geom("geom1").feature("int_lid")
        .label("Straight-edged spherical rectangular lid wiper");
    model.component("comp1").geom("geom1").feature("blk1").set("size",
        new String[]{"L_lid_chord", "W_lid_chord", "t_lid + 1[mm]"});
  }

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static double solveForce(Model model, double arcMm, double indentMm) {
    clean(model);
    model.param().set("s_lid", arcMm + "[mm]");
    model.param().set("delta_indent", indentMm + "[mm]");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    model.study().create("std_work");
    model.study("std_work").create("stat", "Stationary");
    model.study("std_work").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_work").run();
    model.result().dataset().create("dset_work", "Solution");
    model.result().dataset("dset_work").set("solution", lastSolution(model));
    model.result().table().create("tbl_work", "Table");
    model.result().numerical().create("int_work", "IntSurface");
    model.result().numerical("int_work").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_work").set("data", "dset_work");
    model.result().numerical("int_work").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_work").set("unit", new String[]{"N"});
    model.result().numerical("int_work").set("table", "tbl_work");
    model.result().numerical("int_work").setResult();
    String[][] rows = model.result().table("tbl_work").getTableData(false);
    return Double.parseDouble(rows[rows.length - 1][rows[0].length - 1]);
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("29_rect_lid_arc_length_iterative_calibration_setup.mph");
    clean(model);
    configureStraightRectangularGeometry(model);
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    model.save(SETUP);

    double[] arcs = {1.0, 1.5, 2.0};
    double[] indents = {0.04, 0.07, 0.10, 0.13, 0.16, 0.19, 0.22, 0.25, 0.28, 0.31};
    System.out.println("ARC_MM | INDENT_MM | CORNEA_INT_SOLID_TN_N");
    for (double arc : arcs) {
      for (double indent : indents) {
        double force = solveForce(model, arc, indent);
        System.out.println(arc + " | " + indent + " | " + force);
      }
    }
  }
}
