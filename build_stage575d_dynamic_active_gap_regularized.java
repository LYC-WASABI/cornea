import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage575d_dynamic_active_gap_regularized {
  private static final String BASE = "575c_stage575_dynamic_moving_mask_jfo_checked.mph";
  private static final String SETUP = "575d_stage575_dynamic_active_gap_regularized_setup.mph";
  private static final String RESULTS = "575d_stage575_dynamic_active_gap_regularized_results.mph";
  private static final String CHECKED = "575d_stage575_dynamic_active_gap_regularized_checked.mph";
  private static final String INIT_SOL = "sol119";
  private static final String STUDY = "std575d_dynamic_active_gap_regularized";
  private static final String DATASET = "dset575d_dynamic_active_gap_regularized";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void configure(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_v574", "1");
    model.param().set("lambda_h574", "1");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    try { comp.variable("var_dynamic_motion572").set("tau572", "t"); } catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "M_lid_x572*M_drain_a573");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "max(1-M_drain573,0)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_low573", "0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_high573", "0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Bfilm573", "g_pair_valid573*B_low573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "g_pair_physical573", "min(g_pair_safe573,h_active_max573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_wet573",
          "h_num573+0.5*((g_pair_physical573-h_num573)"
              + "+sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Afilm573",
          "M_core573*Bfilm573+max(M_drain573-M_core573,0)*g_pair_valid573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_calc573", "Afilm573*h_wet573+(1-Afilm573)*h_background573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Qvent573", "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");
    } catch (Exception error) {
      System.out.println("ACTIVE_GAP_CONFIG_FAILED=" + error.getMessage());
    }
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "(1-lambda_h574)*3[um]+lambda_h574*h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0",
      "-lambda_v574*omega_lid_rot572*Z",
      "lambda_v574*omega_lid_rot572*Y"
    });
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); } catch (Exception ignored) {}
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "lambda_h574*Qvent573"); } catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(1-M_drain573)*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
  }

  private static String buildTransient(Model model, String initSol) {
    removeStudy(model, STUDY);
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 575d dynamic active-gap regularized JFO");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
    model.study(STUDY).feature("time").set(
        "activate", new String[] {
          "solid", "off", "ge_force_total111", "off", "tff", "on",
          "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
        });
    model.study(STUDY).feature("time").set("useinitsol", "on");
    model.study(STUDY).feature("time").set("initmethod", "sol");
    model.study(STUDY).feature("time").set("initsol", initSol);
    model.study(STUDY).feature("time").set("initsoluse", "current");
    model.study(STUDY).feature("time").set("initsolusesolnum", "last");
    String step = STUDY + "/time";
    ModelNode comp = model.component("comp1");
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(STUDY).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", initSol);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", initSol);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      try { time.create("fc1", "FullyCoupled"); } catch (Exception ignored) {}
    }
    try {
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.5");
      time.feature("fc1").set("maxiter", 120);
    } catch (Exception ignored) {}
    return sol;
  }

  private static double[][] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] intPatch(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double rowMin(double[] values) {
    double result = Double.POSITIVE_INFINITY;
    for (double value : values) result = Math.min(result, value);
    return result;
  }

  private static double rowMax(double[] values) {
    double result = Double.NEGATIVE_INFINITY;
    for (double value : values) result = Math.max(result, value);
    return result;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOL)) throw new IllegalStateException("Missing " + INIT_SOL);
      configure(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOL=" + INIT_SOL);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      model.label("Stage 575d dynamic active-gap regularized setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);
      String sol = buildTransient(model, INIT_SOL);
      System.out.println("RUN_TRANSIENT_SOL=" + sol);
      model.sol(sol).runAll();
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", sol);
      double[][] time = evalGlobal(model, DATASET, "eval575d_time", new String[] {"t"});
      double[][] patch = intPatch(model, DATASET, "int575d_patch",
          new String[] {"1", "p_load573", "Bfilm573", "B_high573", "M_core573", "M_drain573", "tff.theta"});
      double area = patch[0][0];
      double[] fFilm = patch[1];
      double[] meanB = new double[patch[2].length];
      double[] meanBHigh = new double[patch[3].length];
      double[] meanCore = new double[patch[4].length];
      double[] meanDrain = new double[patch[5].length];
      double[] meanTheta = new double[patch[6].length];
      for (int i = 0; i < fFilm.length; i++) {
        meanB[i] = patch[2][i] / area;
        meanBHigh[i] = patch[3][i] / area;
        meanCore[i] = patch[4][i] / area;
        meanDrain[i] = patch[5][i] / area;
        meanTheta[i] = patch[6][i] / area;
      }
      double[][] minTheta = surface(model, DATASET, "min575d_theta", "MinSurface", "tff.theta");
      double[][] maxP = surface(model, DATASET, "max575d_p", "MaxSurface", "tff.p-p_amb573");
      double[][] minH = surface(model, DATASET, "min575d_h", "MinSurface", "h_calc573");
      double[][] maxH = surface(model, DATASET, "max575d_h", "MaxSurface", "h_calc573");
      boolean pass = finite(fFilm) && finite(meanB) && finite(meanBHigh) && finite(meanCore)
          && finite(meanDrain) && finite(meanTheta) && finite(minTheta[0]) && finite(maxP[0])
          && finite(minH[0]) && finite(maxH[0]) && rowMin(minTheta[0]) >= -1e-8;
      if (rowMax(meanCore) < 1e-6) pass = false;
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "F_FILM_RANGE=[%.12g,%.12g]%n", rowMin(fFilm), rowMax(fFilm));
      System.out.printf(Locale.US, "MAXP_RANGE=[%.12g,%.12g]%n", rowMin(maxP[0]), rowMax(maxP[0]));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minTheta[0]), rowMax(minTheta[0]));
      System.out.printf(Locale.US, "MEAN_CORE_RANGE=[%.12g,%.12g]%n", rowMin(meanCore), rowMax(meanCore));
      System.out.printf(Locale.US, "MEAN_DRAIN_RANGE=[%.12g,%.12g]%n", rowMin(meanDrain), rowMax(meanDrain));
      System.out.printf(Locale.US, "MEAN_BFILM_RANGE=[%.12g,%.12g]%n", rowMin(meanB), rowMax(meanB));
      System.out.printf(Locale.US, "MEAN_BHIGH_RANGE=[%.12g,%.12g]%n", rowMin(meanBHigh), rowMax(meanBHigh));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minH[0]), rowMax(minH[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxH[0]), rowMax(maxH[0]));
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (!pass) throw new IllegalStateException("Stage 575d active-gap acceptance failed");
      model.label("Stage 575d dynamic active-gap regularized checked");
      model.save(CHECKED);
      System.out.println("SAVED_CHECKED=" + CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
