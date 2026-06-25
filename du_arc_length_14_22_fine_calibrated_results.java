import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_arc_length_14_22_fine_calibrated_results {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rect_arc_length_14_22mm_sweep_calibrated_0p03N_results.mph";
  private static final double TARGET = 0.03;

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
    model.param().set("s_lid", "14[mm]", "Lid wiper contact-surface arc length");
    model.param().set("lid_arc_length", "s_lid", "Alias for lid wiper contact-surface arc length");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))",
        "Rectangular cutter chord corresponding to spherical contact arc length");
    model.param().set("delta_indent", "0.0725[mm]", "Calibrated geometric indentation parameter");
    model.component("comp1").geom("geom1").feature("blk1")
        .label("Straight rectangular cutter: variable 14 to 22 mm arc length x 1 mm width");
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

  private static double calibrate(Model model, double arcMm) {
    double low = 0.070;
    double high = 0.075;
    double bestIndent = low;
    double bestError = Double.POSITIVE_INFINITY;
    System.out.println("FINE ARC " + arcMm + " mm");
    for (int i = 0; i < 9; i++) {
      double mid = (low + high) / 2.0;
      double force = solveForce(model, arcMm, mid);
      double error = Math.abs(force - TARGET);
      System.out.println("  indent=" + mid + " mm force=" + force + " N");
      if (error < bestError) {
        bestError = error;
        bestIndent = mid;
      }
      if (force < TARGET) low = mid; else high = mid;
    }
    double force = solveForce(model, arcMm, bestIndent);
    System.out.println("CALIBRATED arc=" + arcMm + " mm indent=" + bestIndent
        + " mm force=" + force + " N");
    return bestIndent;
  }

  private static void addPlot(Model model, String tag, String label, String dataset,
      String selection, String expr, String unit) {
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", dataset);
    model.result(tag).selection().named(selection);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  private static void addValue(Model model, String tag, String table, String label,
      String type, String dataset, String selection, String expr, String unit) {
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
  }

  private static void addCaseResults(Model model, String suffix, String dataset) {
    String label = suffix + " mm";
    addValue(model, "int_force_" + suffix, "tbl_force_" + suffix,
        "Anterior cornea intop(solid.Tn), arc " + label, "IntSurface", dataset,
        "sel_cornea_anterior_surface", "solid.Tn", "N");
    addPlot(model, "pg_cornea_disp_" + suffix, "Cornea anterior displacement, arc " + label,
        dataset, "sel_cornea_anterior_surface", "solid.disp", "mm");
    addPlot(model, "pg_lid_disp_" + suffix, "Lid contact displacement, arc " + label,
        dataset, "sel_lid_contact_source_robust", "solid.disp", "mm");
    addPlot(model, "pg_cornea_mises_" + suffix, "Cornea anterior von Mises stress, arc " + label,
        dataset, "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addPlot(model, "pg_lid_mises_" + suffix, "Lid contact von Mises stress, arc " + label,
        dataset, "sel_lid_contact_source_robust", "solid.mises", "Pa");
    addValue(model, "max_cornea_disp_" + suffix, "tbl_max_cornea_disp_" + suffix,
        "Maximum cornea anterior displacement, arc " + label, "MaxSurface", dataset,
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    addValue(model, "max_lid_disp_" + suffix, "tbl_max_lid_disp_" + suffix,
        "Maximum lid contact displacement, arc " + label, "MaxSurface", dataset,
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    addValue(model, "max_cornea_mises_" + suffix, "tbl_max_cornea_mises_" + suffix,
        "Maximum cornea anterior von Mises stress, arc " + label, "MaxSurface", dataset,
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addValue(model, "max_lid_mises_" + suffix, "tbl_max_lid_mises_" + suffix,
        "Maximum lid contact von Mises stress, arc " + label, "MaxSurface", dataset,
        "sel_lid_contact_source_robust", "solid.mises", "Pa");
  }

  private static void solveFinalCase(Model model, double arcMm, double indentMm, String suffix)
      throws java.io.IOException {
    model.param().set("s_lid", arcMm + "[mm]");
    model.param().set("delta_indent", indentMm + "[mm]",
        "Calibrated indentation for arc length " + arcMm + " mm and force near 0.03 N");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    String study = "std_arc_" + suffix;
    model.study().create(study);
    model.study(study).label("Calibrated straight rectangular lid, arc " + arcMm + " mm");
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).run();
    String dataset = "dset_arc_" + suffix;
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).label("Calibrated solution: straight rectangular lid arc " + arcMm + " mm");
    model.result().dataset(dataset).set("solution", lastSolution(model));
    addCaseResults(model, suffix, dataset);
    model.save("D:\\COMSOL_Outputs\\models\\du\\arc_" + suffix
        + "mm_rect_lid_calibrated_checkpoint.mph");
  }

  public static void main(String[] args) throws java.io.IOException {
    double[] arcs = {14.0, 16.0, 18.0, 20.0, 22.0};
    String[] suffixes = {"14", "16", "18", "20", "22"};
    double[] calibrated = new double[arcs.length];

    Model calibration = ModelUtil.load("Calibration", IN);
    clean(calibration);
    configureStraightRectangularGeometry(calibration);
    for (int i = 0; i < arcs.length; i++) calibrated[i] = calibrate(calibration, arcs[i]);
    ModelUtil.remove("Calibration");

    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_rect_arc_length_14_22mm_sweep_calibrated_0p03N_results.mph");
    clean(model);
    configureStraightRectangularGeometry(model);
    for (int i = 0; i < arcs.length; i++) solveFinalCase(model, arcs[i], calibrated[i], suffixes[i]);
    System.out.println("FINAL contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("FINAL outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
