import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_558i_stage561_jfo_diagnostics {
  private static final String INPUT = "558h_stage561_JFO_update2.mph";
  private static final String OUTPUT =
      "558i_stage561_JFO_diagnostics_results.mph";
  private static final String DATASET = "dset561_diag";
  private static final String SELECTION = "sel_film_track";

  private static void removeResult(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); } catch (Exception ignored) {}
  }

  private static void surface(
      Model model, String tag, String label,
      String expression, String unit) {
    removeResult(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", DATASET);
    model.result(tag).selection().named(SELECTION);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expression);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  private static double[][] extremum(
      Model model, String tag, String type, String table,
      String label, String expression, String unit) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    NumericalFeature feature = model.result().numerical(tag);
    feature.label(label);
    feature.set("data", DATASET);
    feature.selection().named(SELECTION);
    feature.set("expr", expression);
    feature.set("unit", unit);
    feature.set("includepos", "on");
    feature.set("table", table);
    feature.setResult();
    return feature.getReal();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);

      try { model.result().dataset().remove(DATASET); }
      catch (Exception ignored) {}
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).label(
          "Stage 561 diagnostic dataset - Solution 86");
      model.result().dataset(DATASET).set("solution", "sol86");

      surface(model, "pg561_hgeom_diag",
          "Stage 561 geometric film thickness",
          "h_geom555", "um");
      surface(model, "pg561_pfilm_diag",
          "Stage 561 JFO physical film pressure",
          "tff.p", "Pa");
      surface(model, "pg561_theta_diag",
          "Stage 561 JFO liquid fraction theta",
          "tff.theta", "1");
      surface(model, "pg561_cavitation_diag",
          "Stage 561 JFO cavitation region theta below 0.999",
          "if(tff.theta<0.999,1,0)", "1");

      String summaryTable = "tbl561_diag_summary";
      removeTable(model, summaryTable);
      model.result().table().create(summaryTable, "Table");
      model.result().table(summaryTable).label(
          "Stage 561 JFO diagnostic summary");

      String summary = "eval561_diag_summary";
      removeNumerical(model, summary);
      model.result().numerical().create(summary, "EvalGlobal");
      model.result().numerical(summary).label(
          "Stage 561 film thickness pressure and cavitation summary");
      model.result().numerical(summary).set("data", DATASET);
      model.result().numerical(summary).set("expr", new String[] {
        "intop_film(h_geom555)/intop_film(1)",
        "intop_film(max(tff.p,0))",
        "intop_film(tff.theta)/intop_film(1)",
        "intop_film(if(tff.theta<0.999,1,0))/intop_film(1)",
        "intop_film(if(tff.theta<0.95,1,0))/intop_film(1)",
        "intop_film(if(tff.theta<0.5,1,0))/intop_film(1)"
      });
      model.result().numerical(summary).set("unit", new String[] {
        "um", "N", "1", "1", "1", "1"
      });
      model.result().numerical(summary).set("descr", new String[] {
        "Area-average geometric film thickness",
        "Raw positive JFO film load",
        "Area-average liquid fraction",
        "Area fraction with theta below 0.999",
        "Area fraction with theta below 0.95",
        "Area fraction with theta below 0.5"
      });
      model.result().numerical(summary).set("table", summaryTable);
      model.result().numerical(summary).setResult();

      String extremaTable = "tbl561_diag_extrema";
      removeTable(model, extremaTable);
      model.result().table().create(extremaTable, "Table");
      model.result().table(extremaTable).label(
          "Stage 561 extrema and spatial positions");

      double[][] hmin = extremum(model, "min561_hgeom_pos",
          "MinSurface", extremaTable,
          "Stage 561 minimum film thickness and position",
          "h_geom555", "um");
      double[][] hmax = extremum(model, "max561_hgeom_pos",
          "MaxSurface", extremaTable,
          "Stage 561 maximum film thickness and position",
          "h_geom555", "um");
      double[][] pmax = extremum(model, "max561_pfilm_pos",
          "MaxSurface", extremaTable,
          "Stage 561 maximum film pressure and position",
          "tff.p", "Pa");
      double[][] thetamin = extremum(model, "min561_theta_pos",
          "MinSurface", extremaTable,
          "Stage 561 minimum liquid fraction and position",
          "tff.theta", "1");

      System.out.println("SUMMARY=" + Arrays.deepToString(
          model.result().numerical(summary).getReal()));
      System.out.println("HMIN_POS=" + Arrays.deepToString(hmin));
      System.out.println("HMAX_POS=" + Arrays.deepToString(hmax));
      System.out.println("PMAX_POS=" + Arrays.deepToString(pmax));
      System.out.println("THETAMIN_POS=" + Arrays.deepToString(thetamin));

      model.label("Stage 561 JFO diagnostics results");
      model.save(OUTPUT);
      System.out.println("OUTPUT=" + OUTPUT);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
