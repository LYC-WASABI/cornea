import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage99_add_lid_pressure_time_results {
  private static void removeResult(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignore) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
  }

  private static void addGlobalPlot(Model model, String pgTag, String label, String dataset,
                                    String[] expr, String[] unit) {
    removeResult(model, pgTag);
    model.result().create(pgTag, "PlotGroup1D");
    model.result(pgTag).label(label);
    model.result(pgTag).set("data", dataset);
    model.result(pgTag).feature().create("glob1", "Global");
    model.result(pgTag).feature("glob1").label(label);
    model.result(pgTag).feature("glob1").set("expr", expr);
    model.result(pgTag).feature("glob1").set("unit", unit);
  }

  private static void addSurfaceIntegral(Model model, String tag, String label, String dataset,
                                         String selection, String expr, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
  }

  private static void addSurfaceAverage(Model model, String tag, String label, String dataset,
                                        String selection, String expr, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "AvSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
  }

  private static void addSurfaceMaximum(Model model, String tag, String label, String dataset,
                                        String selection, String expr, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
  }

  private static void printRange(Model model, String tag) {
    try {
      double[][] vals = model.result().numerical(tag).getReal();
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      int imin = -1;
      int imax = -1;
      int n = 0;
      for (int r = 0; r < vals.length; r++) {
        for (int c = 0; c < vals[r].length; c++) {
          double v = vals[r][c];
          if (!Double.isNaN(v)) {
            n++;
            if (v < min) { min = v; imin = c; }
            if (v > max) { max = v; imax = c; }
          }
        }
      }
      System.out.println(tag + " n=" + n + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
    } catch (Exception e) {
      System.out.println(tag + " ERROR " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\176_lid8mm_stage98_gap_limited_hfilm_results.mph";
      String localOut = "177_lid8mm_stage99_lid_pressure_time_results_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      String solid = "dset_shear_feedback76";
      String cornea = "sel_cornea_anterior_surface";
      String lid = "sel_lid_contact_source_robust";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String pContactPos = "max(" + pContact + ",0)";

      addGlobalPlot(model, "pg_lid_pressure_time",
        "Lid wiper contact pressure over time",
        solid,
        new String[]{
          "intop_film(" + pContactPos + ")",
          "intop_film(" + pContactPos + ")/intop_film(1)",
          "maxop1(" + pContactPos + ")"
        },
        new String[]{"N", "Pa", "Pa"});

      addSurfaceIntegral(model, "int_lid_pressure_time_target",
        "Lid load from target contact pressure over time",
        solid, cornea, pContactPos, "N");
      addSurfaceAverage(model, "avg_lid_pressure_time_target",
        "Mean lid contact pressure on cornea target over time",
        solid, cornea, pContactPos, "Pa");
      addSurfaceMaximum(model, "max_lid_pressure_time_target",
        "Maximum lid contact pressure on cornea target over time",
        solid, cornea, pContactPos, "Pa");

      addSurfaceIntegral(model, "int_lid_pressure_time_source_check",
        "Source-side lid pressure check over time",
        solid, lid, pContactPos, "N");
      addSurfaceAverage(model, "avg_lid_pressure_time_source_check",
        "Source-side mean pressure check over time",
        solid, lid, pContactPos, "Pa");
      addSurfaceMaximum(model, "max_lid_pressure_time_source_check",
        "Source-side maximum pressure check over time",
        solid, lid, pContactPos, "Pa");

      printRange(model, "int_lid_pressure_time_target");
      printRange(model, "avg_lid_pressure_time_target");
      printRange(model, "max_lid_pressure_time_target");
      printRange(model, "int_lid_pressure_time_source_check");

      model.save(localOut);
      System.out.println("Saved local: " + localOut);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
