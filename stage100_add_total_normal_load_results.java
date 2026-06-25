import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage100_add_total_normal_load_results {
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

  private static void addEval(Model model, String tag, String label, String dataset,
                              String[] expr, String[] unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
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

  private static void printRange(Model model, String tag) {
    try {
      double[][] vals = model.result().numerical(tag).getReal();
      System.out.println(tag + " rows=" + vals.length + " cols=" + vals[0].length);
      for (int i = 0; i < vals.length; i++) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int imin = -1, imax = -1;
        for (int j = 0; j < vals[i].length; j++) {
          double v = vals[i][j];
          if (!Double.isNaN(v)) {
            if (v < min) { min = v; imin = j; }
            if (v > max) { max = v; imax = j; }
          }
        }
        System.out.println("  expr" + i + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
      }
    } catch (Exception e) {
      System.out.println(tag + " ERROR " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\177_lid8mm_stage99_lid_pressure_time_results.mph";
      String localOut = "178_lid8mm_stage100_total_normal_load_results_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      String film = "dset_rq0p5_film92";
      String solid = "dset_shear_feedback76";
      String cornea = "sel_cornea_anterior_surface";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String fnContact = "intop_film(max(" + pContact + ",0))";
      String fnFilm = "W_film";
      String fnTotal = "(" + fnContact + ")+" + fnFilm;
      String target = "F_total_target";

      addGlobalPlot(model, "pg_normal_load_sharing_time",
        "Normal load sharing: contact plus tear film",
        solid,
        new String[]{fnContact, fnFilm, fnTotal, target},
        new String[]{"N", "N", "N", "N"});

      addGlobalPlot(model, "pg_normal_load_error_time",
        "Normal load error relative to 0.03 N",
        solid,
        new String[]{fnTotal, fnTotal + "-" + target, "(" + fnTotal + ")/" + target},
        new String[]{"N", "N", "1"});

      addEval(model, "eval_normal_load_sharing_time",
        "Normal load sharing over time",
        solid,
        new String[]{fnContact, fnFilm, fnTotal, target, fnTotal + "-" + target, "(" + fnTotal + ")/" + target},
        new String[]{"N", "N", "N", "N", "N", "1"});

      addSurfaceIntegral(model, "int_contact_pressure_target_for_loadshare",
        "Contact normal force on cornea target for load sharing",
        solid, cornea, "max(" + pContact + ",0)", "N");

      printRange(model, "eval_normal_load_sharing_time");
      printRange(model, "int_contact_pressure_target_for_loadshare");

      model.save(localOut);
      System.out.println("Saved local: " + localOut);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
