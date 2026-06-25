import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage575c_dynamic_moving_mask_jfo {
  private static final String BASE = "575a_stage575_dynamic_fixed_patch_jfo_checked.mph";
  private static final String SETUP = "575c_stage575_dynamic_moving_mask_jfo_setup.mph";
  private static final String RESULTS = "575c_stage575_dynamic_moving_mask_jfo_results.mph";
  private static final String CHECKED = "575c_stage575_dynamic_moving_mask_jfo_checked.mph";
  private static final String INIT_SOL = "sol119";
  private static final String STUDY = "std575c_dynamic_moving_mask_jfo";
  private static final String DATASET = "dset575c_dynamic_moving_mask_jfo";

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
    try { comp.variable("var_dynamic_motion572").set("tau572", "t"); } catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "M_lid_x572*M_drain_a573");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "max(1-M_drain573,0)");
    } catch (Exception error) {
      System.out.println("MOVING_MASK_RESTORE_FAILED=" + error.getMessage());
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
    model.study(STUDY).label("Stage 575c dynamic moving-mask JFO");
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
      model.label("Stage 575c dynamic moving-mask JFO setup");
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
      double[][] time = evalGlobal(model, DATASET, "eval575c_time", new String[] {"t"});
      double[][] patch = intPatch(model, DATASET, "int575c_patch",
          new String[] {"1", "p_load573", "Bfilm573", "M_core573", "M_drain573", "tff.theta"});
      double area = patch[0][0];
      double[] fFilm = patch[1];
      double[] meanB = new double[patch[2].length];
      double[] meanCore = new double[patch[3].length];
      double[] meanDrain = new double[patch[4].length];
      double[] meanTheta = new double[patch[5].length];
      for (int i = 0; i < fFilm.length; i++) {
        meanB[i] = patch[2][i] / area;
        meanCore[i] = patch[3][i] / area;
        meanDrain[i] = patch[4][i] / area;
        meanTheta[i] = patch[5][i] / area;
      }
      double[][] minTheta = surface(model, DATASET, "min575c_theta", "MinSurface", "tff.theta");
      double[][] maxP = surface(model, DATASET, "max575c_p", "MaxSurface", "tff.p-p_amb573");
      boolean pass = finite(fFilm) && finite(meanB) && finite(meanCore) && finite(meanDrain)
          && finite(meanTheta) && finite(minTheta[0]) && finite(maxP[0])
          && rowMin(minTheta[0]) >= -1e-8;
      // Moving-mask stage is allowed to have low activity on the local patch, but it must not be identically inactive.
      if (rowMax(meanCore) < 1e-6) pass = false;
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "F_FILM_RANGE=[%.12g,%.12g]%n", rowMin(fFilm), rowMax(fFilm));
      System.out.printf(Locale.US, "MAXP_RANGE=[%.12g,%.12g]%n", rowMin(maxP[0]), rowMax(maxP[0]));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minTheta[0]), rowMax(minTheta[0]));
      System.out.printf(Locale.US, "MEAN_CORE_RANGE=[%.12g,%.12g]%n", rowMin(meanCore), rowMax(meanCore));
      System.out.printf(Locale.US, "MEAN_DRAIN_RANGE=[%.12g,%.12g]%n", rowMin(meanDrain), rowMax(meanDrain));
      System.out.printf(Locale.US, "MEAN_BFILM_RANGE=[%.12g,%.12g]%n", rowMin(meanB), rowMax(meanB));
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (!pass) throw new IllegalStateException("Stage 575c moving-mask acceptance failed");
      model.label("Stage 575c dynamic moving-mask JFO checked");
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
