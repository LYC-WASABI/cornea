import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_run_stage562_jfo_separation_scan {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No Stage 562 solver was created");
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      String comp = "comp1";
      String study = "std_stage562";

      model.param().set(
          "delta_h562", "0[um]",
          "Stage 562 uniform separation added to the Stage 560 gap");

      String variables = "var_stage562_gap";
      try { model.component(comp).variable().remove(variables); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(variables);
      model.component(comp).variable(variables).label(
          "Stage 562 JFO separation-scan film thickness");
      model.component(comp).variable(variables)
          .selection().named("sel_film_track");
      model.component(comp).variable(variables).set(
          "h_film562", "h_geom555+delta_h562");
      model.component(comp).variable(variables).set(
          "Wfilm_raw562", "intop_film(max(tff.p,0))");
      model.component(comp).variable(variables).set(
          "Wfilm_scaled562",
          "scale_pfilm555*intop_film(max(tff.p,0))");

      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_film562");

      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label("Stage 562 JFO separation scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h562"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {"0 0.5 1 1.5 2 2.5 3 3.5 4"});
      model.study(study).feature("param").set(
          "punit", new String[] {"um"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", "ge_force_total111", "off",
            "tff", "on", "frame:spatial1", "on",
            "frame:material1", "on", "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol86");
      model.study(study).feature("stat").set("initsoluse", "sol86");

      String step = study + "/stat";
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol86");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol85");
      dependent.set("notsolnum", "last");

      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 562 JFO separation scan setup");
      model.save("558j_stage562_JFO_separation_scan_setup.mph");
      System.out.println("SETUP=558j_stage562_JFO_separation_scan_setup.mph");
      System.out.println("SOLUTION=" + solution);

      model.sol(solution).runAll();
      model.label("Stage 562 JFO separation scan results");
      model.save("558k_stage562_JFO_separation_scan_results.mph");
      System.out.println("RESULTS=558k_stage562_JFO_separation_scan_results.mph");

      String dataset = "dset562_scan";
      try { model.result().dataset().remove(dataset); }
      catch (Exception ignored) {}
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).label(
          "Stage 562 JFO separation scan");
      model.result().dataset(dataset).set("solution", solution);

      String table = "tbl562_scan";
      removeTable(model, table);
      model.result().table().create(table, "Table");
      model.result().table(table).label(
          "Stage 562 separation film-load scan");

      String eval = "eval562_scan";
      removeNumerical(model, eval);
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).label(
          "Stage 562 JFO separation scan values");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
        "delta_h562",
        "intop_film(h_film562)/intop_film(1)",
        "intop_film(max(tff.p,0))",
        "scale_pfilm555*intop_film(max(tff.p,0))",
        "intop_film(tff.theta)/intop_film(1)"
      });
      model.result().numerical(eval).set("unit", new String[] {
        "um", "um", "N", "N", "1"
      });
      model.result().numerical(eval).set("table", table);
      model.result().numerical(eval).setResult();

      String min = "min562_hfilm";
      removeNumerical(model, min);
      model.result().numerical().create(min, "MinSurface");
      model.result().numerical(min).label(
          "Stage 562 minimum film thickness");
      model.result().numerical(min).set("data", dataset);
      model.result().numerical(min).selection().named("sel_film_track");
      model.result().numerical(min).set("expr", "h_film562");
      model.result().numerical(min).set("unit", "um");

      String maxPressure = "max562_pfilm";
      removeNumerical(model, maxPressure);
      model.result().numerical().create(maxPressure, "MaxSurface");
      model.result().numerical(maxPressure).label(
          "Stage 562 maximum JFO pressure");
      model.result().numerical(maxPressure).set("data", dataset);
      model.result().numerical(maxPressure)
          .selection().named("sel_film_track");
      model.result().numerical(maxPressure).set("expr", "tff.p");
      model.result().numerical(maxPressure).set("unit", "Pa");

      String minTheta = "min562_theta";
      removeNumerical(model, minTheta);
      model.result().numerical().create(minTheta, "MinSurface");
      model.result().numerical(minTheta).label(
          "Stage 562 minimum JFO liquid fraction");
      model.result().numerical(minTheta).set("data", dataset);
      model.result().numerical(minTheta)
          .selection().named("sel_film_track");
      model.result().numerical(minTheta).set("expr", "tff.theta");
      model.result().numerical(minTheta).set("unit", "1");

      double[][] global =
          model.result().numerical(eval).getReal();
      double[][] hmin =
          model.result().numerical(min).getReal();
      double[][] pmax =
          model.result().numerical(maxPressure).getReal();
      double[][] thetaMin =
          model.result().numerical(minTheta).getReal();

      double bestDelta = Double.NaN;
      double bestError = Double.POSITIVE_INFINITY;
      for (int index = 0; index < global[0].length; index++) {
        double scaledLoad = global[3][index];
        double target = 0.0265;
        double error = Math.abs(scaledLoad - target);
        if (error < bestError) {
          bestError = error;
          bestDelta = global[0][index];
        }
        System.out.printf(
            Locale.US,
            "delta=%.6g um havg=%.9g um hmin=%.9g um "
                + "Wraw=%.12g N Wscaled=%.12g N "
                + "pmax=%.12g Pa thetaMin=%.12g thetaAvg=%.12g%n",
            global[0][index], global[1][index], hmin[0][index],
            global[2][index], scaledLoad, pmax[0][index],
            thetaMin[0][index], global[4][index]);
      }
      System.out.printf(
          Locale.US,
          "BEST_NEAR_MID_TARGET delta_h562=%.6g um error=%.12g N%n",
          bestDelta, bestError);

      model.save("558k_stage562_JFO_separation_scan_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
