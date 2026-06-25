import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_swept_edge_pressure {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String OUTPUT =
      "576p2e_stage576_swept_edge_pressure_results.mph";
  private static final String SWEPT = "sel_film_swept571";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void configure(Model model) {
    ModelNode comp = model.component("comp1");
    int[] allEdges = comp.physics("tff").feature("bdr1")
        .selection().entities();
    comp.physics("tff").feature("bdr_inlet520").selection().set(allEdges);
    comp.physics("tff").feature("bdr_outlet520").active(false);
    comp.physics("tff").feature("bdr_left520").active(false);
    comp.physics("tff").feature("bdr_right520").active(false);
    comp.physics("tff").feature("wc_open_anchor573").active(false);
    comp.physics("tff").feature("ffp1").set("hw1", "3[um]");
  }

  private static double integrate(Model model, String data, String tag,
      String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double extreme(Model model, String data, String tag,
      String type, String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static String buildJfo(Model model, int index, String solid) {
    String study = "std576p2e_jfo_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p2 swept-edge JFO " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "init");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", solid);
    dependent.set("notsolnum", "last");
    SolverFeature stationary = model.sol(solution).feature("s1");
    for (String feature : stationary.feature().tags()) {
      if (feature.startsWith("se")) {
        try { stationary.feature().remove(feature); } catch (Exception ignored) {}
      }
    }
    if (!has(stationary.feature().tags(), "fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", 0.3);
    stationary.feature("fc1").set("maxiter", 400);
    return solution;
  }

  private static void run(Model model, int index, double fraction,
      String solid) {
    model.param().set("t_position576p2", String.format(Locale.US,
        "T_pre572+%.12g*T_slide572", fraction));
    String solution = buildJfo(model, index, solid);
    System.out.println("EDGE_PRESSURE_START fraction=" + fraction
        + " solution=" + solution);
    model.sol(solution).runAll();
    String data = "dset576p2Edge";
    try { model.result().dataset().remove(data); } catch (Exception ignored) {}
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);
    double film = integrate(model, data, "int576p2EdgeFilm",
        "max(p_load573,0[Pa])");
    double positive = integrate(model, data, "int576p2EdgePositive",
        "max(tff.p-p_amb573,0[Pa])");
    double pressureY = integrate(model, data, "int576p2EdgeY",
        "max(tff.p-p_amb573,0[Pa])*Y");
    double maxP = extreme(model, data, "max576p2EdgeP", "MaxSurface",
        "tff.p-p_amb573");
    double minTheta = extreme(model, data, "min576p2EdgeTheta", "MinSurface",
        "tff.theta");
    System.out.printf(Locale.US,
        "EDGE_PRESSURE_RESULT fraction=%.3f Ffilm=%.12g MaxP=%.12g"
            + " MinTheta=%.12g pressureY=%.12g%n",
        fraction, film, maxP, minTheta,
        Math.abs(positive) > 1e-30 ? pressureY / positive : Double.NaN);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      configure(model);
      run(model, 0, 0.0, "sol143");
      run(model, 5, 0.25, "sol149");
      run(model, 10, 0.5, "sol155");
      run(model, 15, 0.75, "sol171");
      run(model, 20, 1.0, "sol182");
      model.label("Stage 576p2 swept-edge ambient pressure results");
      model.save(OUTPUT);
      System.out.println("EDGE_PRESSURE_STATUS=COMPLETE");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
