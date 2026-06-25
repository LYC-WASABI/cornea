import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576p_swept_domain_gated_jfo {
  private static final String BASE =
      "575d_stage575_dynamic_active_gap_regularized_checked.mph";
  private static final String SETUP =
      "576p_stage576_swept_domain_gated_jfo_setup.mph";
  private static final String RESULTS =
      "576p_stage576_swept_domain_gated_jfo_results.mph";
  private static final String CHECKED =
      "576p_stage576_swept_domain_gated_jfo_checked.mph";
  private static final String STRUCTURE = "sol119";
  private static final String SWEPT = "sel_film_swept571";
  private static final double[] VENTS = {1e-6, 1e-5, 1e-4};

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

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String solution) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double[][] integrate(
      Model model, String data, String tag, String[] expressions) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expressions);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] extrema(
      Model model, String data, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal();
  }

  private static double value(double[][] rows, int row, int index) {
    return rows[row][Math.min(index, rows[row].length - 1)];
  }

  private static double ratio(double numerator, double denominator) {
    return Math.abs(denominator) > 1e-30 ? numerator / denominator : Double.NaN;
  }

  private static void configureFullSwept(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "1");
    model.param().set("gate_speed576p", "0");
    model.param().set("kvent576p", "1e-6[kg/(m^2*s*Pa)]");
    model.param().set("kanchor576p", "1e-6[kg/(m^2*s*Pa)]");
    model.param().set("t_init576p", "T_pre572");

    comp.physics("tff").selection().named(SWEPT);
    comp.variable("var_dynamic_motion572").selection().named(SWEPT);
    comp.variable("var_cornea_dynamic_regions573").selection().named(SWEPT);
    comp.cpl("intop_film").selection().named(SWEPT);

    comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
    comp.variable("var_cornea_dynamic_regions573").set(
        "M_drain573", "M_lid_x572*M_drain_a573");
    comp.variable("var_cornea_dynamic_regions573").set(
        "M_open573", "max(1-M_drain573,0)");
    comp.variable("var_cornea_dynamic_regions573").set(
        "Qvent573", "-kvent576p*(1-Afilm573)*(tff.p-p_amb573)");
    comp.variable("var_cornea_dynamic_regions573").set(
        "p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");
    comp.variable("var_dynamic_motion572").set("tau572", "t_init576p");

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0",
      "-gate_speed576p*lambda_v574*M_drain573*omega_lid_rot572*Z",
      "gate_speed576p*lambda_v574*M_drain573*omega_lid_rot572*Y"
    });
    comp.physics("tff").feature("ms_vent573").set("QudR", "Qvent573");
    comp.physics("tff").feature("wc_open_anchor573").set(
        "weakExpression",
        "-kanchor576p*(1-M_drain573)*(pfilm-p_amb573)*test(pfilm)");
    try { comp.physics("tff").feature("init1").selection().named(SWEPT); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); }
    catch (Exception ignored) {}
    try { comp.physics("solid").active(false); } catch (Exception ignored) {}
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
  }

  private static String buildEnvironmentInitial(Model model) {
    String study = "std576p_environment";
    try { model.study().remove(study); } catch (Exception ignored) {}
    model.study().create(study);
    model.study(study).label("Stage 576p full-swept environment pressure initial state");
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
    dependent.set("notsol", STRUCTURE);
    dependent.set("notsolnum", "last");
    return solution;
  }

  private static String buildDynamic(
      Model model, String environment, int index) {
    String study = "std576p_dynamic_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p full-swept gated JFO vent scan " + index);
    model.study(study).create("time", "Transient");
    String outputTimes =
        "range(T_pre572,T_slide572/4,T_pre572+T_slide572)";
    model.study(study).feature("time").set("tlist", outputTimes);
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", environment);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", "last");
    String step = study + "/time";
    ModelNode comp = model.component("comp1");
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", environment);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", STRUCTURE);
    dependent.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", outputTimes);
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "T_slide572/200");
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      try { time.create("fc1", "FullyCoupled"); } catch (Exception ignored) {}
    }
    try {
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.5");
      time.feature("fc1").set("maxiter", 150);
    } catch (Exception ignored) {}
    return solution;
  }

  private static boolean evaluate(Model model, String solution, double vent, int scan) {
    String data = dataset(model, "dset576p", solution);
    String[] expressions = {
      "1",
      "M_core573", "M_core573*Y", "M_core573*Z",
      "M_drain573", "M_drain573*Y", "M_drain573*Z",
      "max(tff.p-p_amb573,0[Pa])",
      "max(tff.p-p_amb573,0[Pa])*Y",
      "max(tff.p-p_amb573,0[Pa])*Z",
      "M_drain573*max(tff.p-p_amb573,0[Pa])",
      "max(p_load573,0[Pa])",
      "max(p_load573,0[Pa])*Y",
      "max(p_load573,0[Pa])*Z"
    };
    double[][] rows = integrate(model, data, "int576p", expressions);
    double[][] maxPressure = extrema(
        model, data, "max576pPressure", "MaxSurface", "tff.p-p_amb573");
    double[][] minTheta = extrema(
        model, data, "min576pTheta", "MinSurface", "tff.theta");
    int count = rows[0].length;
    boolean pass = count >= 5;
    double firstCoreY = Double.NaN, firstCoreZ = Double.NaN;
    double lastCoreY = Double.NaN, lastCoreZ = Double.NaN;
    double firstPressureY = Double.NaN, firstPressureZ = Double.NaN;
    double lastPressureY = Double.NaN, lastPressureZ = Double.NaN;
    for (int i = 0; i < count; i++) {
      double area = value(rows, 0, i);
      double core = value(rows, 1, i);
      double drain = value(rows, 4, i);
      double positivePressure = value(rows, 7, i);
      double load = value(rows, 11, i);
      double coreY = ratio(value(rows, 2, i), core);
      double coreZ = ratio(value(rows, 3, i), core);
      double pressureY = ratio(value(rows, 8, i), positivePressure);
      double pressureZ = ratio(value(rows, 9, i), positivePressure);
      double pressureInDrain = ratio(value(rows, 10, i), positivePressure);
      double loadY = ratio(value(rows, 12, i), load);
      double loadZ = ratio(value(rows, 13, i), load);
      double fraction = i / Math.max(1.0, count - 1.0);
      double maxP = value(maxPressure, 0, i);
      double theta = value(minTheta, 0, i);
      System.out.printf(Locale.US,
          "SCAN vent=%.1e fraction=%.3f coreAreaFrac=%.12g drainAreaFrac=%.12g coreY=%.12g coreZ=%.12g pressureY=%.12g pressureZ=%.12g pressureInDrain=%.12g loadY=%.12g loadZ=%.12g Ffilm=%.12g MaxP=%.12g MinTheta=%.12g%n",
          vent, fraction, ratio(core, area), ratio(drain, area), coreY, coreZ,
          pressureY, pressureZ, pressureInDrain, loadY, loadZ, load, maxP, theta);
      if (!Double.isFinite(coreY) || !Double.isFinite(coreZ)
          || !Double.isFinite(maxP) || !Double.isFinite(theta) || theta < -1e-8) pass = false;
      if (Double.isFinite(pressureInDrain) && pressureInDrain < 0.90) pass = false;
      if (i == 0) {
        firstCoreY = coreY; firstCoreZ = coreZ;
        firstPressureY = pressureY; firstPressureZ = pressureZ;
      }
      if (i == count - 1) {
        lastCoreY = coreY; lastCoreZ = coreZ;
        lastPressureY = pressureY; lastPressureZ = pressureZ;
      }
    }
    if (Double.isFinite(firstPressureY) && Double.isFinite(lastPressureY)) {
      double coreDY = lastCoreY - firstCoreY;
      double coreDZ = lastCoreZ - firstCoreZ;
      double pressureDY = lastPressureY - firstPressureY;
      double pressureDZ = lastPressureZ - firstPressureZ;
      double dot = coreDY * pressureDY + coreDZ * pressureDZ;
      System.out.printf(Locale.US,
          "MOVEMENT vent=%.1e coreDistance=%.12g pressureDistance=%.12g directionDot=%.12g%n",
          vent, Math.hypot(coreDY, coreDZ), Math.hypot(pressureDY, pressureDZ), dot);
      if (dot <= 0) pass = false;
    }
    System.out.println("SCAN_STATUS vent=" + vent + " status=" + (pass ? "PASS" : "FAIL"));
    return pass;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      configureFullSwept(model, comp);
      model.label("Stage 576p full-swept gated JFO setup");
      model.save(SETUP);
      System.out.println("SWEPT_ENTITIES="
          + Arrays.toString(comp.selection(SWEPT).entities(2)));
      System.out.println("TFF_ENTITIES="
          + Arrays.toString(comp.physics("tff").selection().entities()));

      String environment = buildEnvironmentInitial(model);
      model.sol(environment).runAll();
      System.out.println("ENVIRONMENT_SOLUTION=" + environment);

      comp.variable("var_dynamic_motion572").set("tau572", "t");
      model.param().set("gate_speed576p", "1");
      boolean anyPass = false;
      for (int scan = 0; scan < VENTS.length; scan++) {
        double vent = VENTS[scan];
        model.param().set("kvent576p", String.format(Locale.US,
            "%.12g[kg/(m^2*s*Pa)]", vent));
        model.param().set("kanchor576p", String.format(Locale.US,
            "%.12g[kg/(m^2*s*Pa)]", vent));
        String solution = buildDynamic(model, environment, scan + 1);
        System.out.println("RUN_SCAN vent=" + vent + " solution=" + solution);
        try {
          model.sol(solution).runAll();
          boolean pass = evaluate(model, solution, vent, scan + 1);
          anyPass = anyPass || pass;
          model.save(RESULTS);
        } catch (Exception error) {
          System.out.println("SCAN_ERROR vent=" + vent + " message=" + error.getMessage());
        }
      }
      model.label(anyPass
          ? "Stage 576p full-swept gated JFO checked"
          : "Stage 576p full-swept gated JFO failed");
      model.save(RESULTS);
      System.out.println("CHECKED_STATUS=" + (anyPass ? "PASS" : "FAIL"));
      if (anyPass) model.save(CHECKED);
      ModelUtil.disconnect();
      if (!anyPass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
