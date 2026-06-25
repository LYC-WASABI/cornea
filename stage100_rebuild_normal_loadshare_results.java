import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage100_rebuild_normal_loadshare_results {
  private static void removeResult(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignore) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); } catch (Exception ignore) {}
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

  private static double[] evalGlobalRow(Model model, String tag, String label, String dataset,
                                        String expr, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
    double[][] vals = model.result().numerical(tag).getReal();
    return vals[0];
  }

  private static double[] evalSurfaceIntegral(Model model, String tag, String label, String dataset,
                                              String selection, String expr, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
    double[][] vals = model.result().numerical(tag).getReal();
    return vals[0];
  }

  private static double[] tryTime(Model model, int n) {
    try {
      return evalGlobalRow(model, "eval_loadshare_time_values",
        "Time values for normal load sharing table", "dset_rq0p5_film92", "t_replay", "s");
    } catch (Exception e1) {
      try {
        return evalGlobalRow(model, "eval_loadshare_time_values",
          "Time values for normal load sharing table", "dset_rq0p5_film92", "t", "s");
      } catch (Exception e2) {
        double[] idx = new double[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        return idx;
      }
    }
  }

  private static void printRange(String label, double[] vals) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    int imin = -1;
    int imax = -1;
    for (int i = 0; i < vals.length; i++) {
      double v = vals[i];
      if (!Double.isNaN(v)) {
        if (v < min) { min = v; imin = i; }
        if (v > max) { max = v; imax = i; }
      }
    }
    System.out.println(label + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\177_lid8mm_stage99_lid_pressure_time_results.mph";
      String localOut = "178_lid8mm_stage100_total_normal_loadshare_results_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      String film = "dset_rq0p5_film92";
      String solid = "dset_shear_feedback76";
      String cornea = "sel_cornea_anterior_surface";
      String pContactPos = "max(if(isdefined(solid.Tn),solid.Tn,0),0)";
      String fnContactGlobal = "intop_film(" + pContactPos + ")";

      removeResult(model, "pg_normal_load_sharing_time");
      removeResult(model, "pg_normal_load_error_time");
      removeNumerical(model, "eval_normal_load_sharing_time");
      removeNumerical(model, "eval_lid_pressure_plot_check");

      addGlobalPlot(model, "pg_contact_normal_force_time",
        "Contact normal force on cornea over time",
        solid,
        new String[]{fnContactGlobal, "F_total_target"},
        new String[]{"N", "N"});

      addGlobalPlot(model, "pg_film_normal_load_time",
        "Tear-film normal load over time",
        film,
        new String[]{"W_film", "F_total_target"},
        new String[]{"N", "N"});

      double[] fnContact = evalSurfaceIntegral(model, "eval_contact_normal_force_target_time",
        "Contact normal force on cornea target over time",
        solid, cornea, pContactPos, "N");
      double[] fnFilm = evalGlobalRow(model, "eval_film_normal_load_time",
        "Tear-film normal load over time", film, "W_film", "N");
      int n = Math.min(fnContact.length, fnFilm.length);
      double[] time = tryTime(model, n);

      double[][] table = new double[n][7];
      for (int i = 0; i < n; i++) {
        double t = i < time.length ? time[i] : i;
        double contact = fnContact[i];
        double filmLoad = fnFilm[i];
        double total = contact + filmLoad;
        double target = 0.03;
        table[i][0] = t;
        table[i][1] = contact;
        table[i][2] = filmLoad;
        table[i][3] = total;
        table[i][4] = target;
        table[i][5] = total - target;
        table[i][6] = total / target;
      }

      removeTable(model, "tbl_total_normal_loadshare");
      model.result().table().create("tbl_total_normal_loadshare", "Table");
      model.result().table("tbl_total_normal_loadshare").label("Total normal load sharing: contact plus tear film");
      model.result().table("tbl_total_normal_loadshare").setColumnHeaders(new String[]{
        "time_or_index", "Fn_contact_target_N", "Fn_film_N", "Fn_total_N",
        "Fn_target_N", "Fn_total_minus_target_N", "Fn_total_over_target"
      });
      model.result().table("tbl_total_normal_loadshare").setTableData(table);

      removeResult(model, "pg_total_normal_loadshare_table");
      model.result().create("pg_total_normal_loadshare_table", "PlotGroup1D");
      model.result("pg_total_normal_loadshare_table").label("Total normal load sharing table plot");
      model.result("pg_total_normal_loadshare_table").feature().create("tbl1", "Table");
      model.result("pg_total_normal_loadshare_table").feature("tbl1").label("Contact + film normal load");
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("source", "table");
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("table", "tbl_total_normal_loadshare");
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("xaxisdata", 1);
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("plotcolumninput", "manual");
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("plotcolumns", new int[]{2, 3, 4, 5});
      model.result("pg_total_normal_loadshare_table").feature("tbl1").set("legend", "on");

      double[] totalVals = new double[n];
      double[] ratioVals = new double[n];
      for (int i = 0; i < n; i++) {
        totalVals[i] = table[i][3];
        ratioVals[i] = table[i][6];
      }
      printRange("Fn_contact_target", fnContact);
      printRange("Fn_film", fnFilm);
      printRange("Fn_total", totalVals);
      printRange("Fn_total/Fn_target", ratioVals);

      model.save(localOut);
      System.out.println("Saved local: " + localOut);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
