import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576g_velocity_q_feasibility {
  private static final String BASE = "576f2_stage576_dynamic_compression_feasibility_checked.mph";
  private static final String SETUP = "576g_stage576_velocity_q_feasibility_setup.mph";
  private static final String RESULTS = "576g_stage576_velocity_q_feasibility_results.mph";
  private static final String CHECKED = "576g_stage576_velocity_q_feasibility_checked.mph";
  private static final String PRESSURE_INIT = "sol119";
  private static final String SOLID_INIT = "sol249";
  private static final double[] Q_VALUES = new double[] {-9.0, -8.0, -7.0};
  private static final double[] SPEEDS = new double[] {0.03, 0.05, 0.075, 0.10, 0.15};
  private static final String REFINE_BASE = "576g_stage576_velocity_q_feasibility_checked.mph";
  private static final String REFINE_RESULTS = "576g2_stage576_velocity_threshold_refined_results.mph";
  private static final String REFINE_CHECKED = "576g2_stage576_velocity_threshold_refined_checked.mph";
  private static final double[] REFINE_SPEEDS = new double[] {0.035, 0.040, 0.045};
  private static final boolean RUN_REFINE = true;
  private static final double TARGET = 0.03;

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

  private static String buildSolid(Model model, ModelNode comp, double q, int index) {
    model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
    String study = "std576g_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576g structural q branch " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", SOLID_INIT);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
      try { comp.physics("solid").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", SOLID_INIT);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", SOLID_INIT);
    dep.set("notsolnum", "last");
    SolverFeature stat = model.sol(sol).feature("s1");
    for (String tag : stat.feature().tags()) if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.05");
    stat.feature("fc1").set("maxiter", 400);
    return sol;
  }

  private static String buildTffHistory(Model model, ModelNode comp, String solidSol, int qIndex, int speedIndex) {
    String study = "std576g_tff_" + qIndex + "_" + speedIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576g TFF history q" + qIndex + " speed" + speedIndex);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set(
        "tlist", "range(T_pre572,T_slide572/200,T_pre572+0.84*T_slide572)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", PRESSURE_INIT);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", "last");
    String step = study + "/time";
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", PRESSURE_INIT);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidSol);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(T_pre572,T_slide572/200,T_pre572+0.84*T_slide572)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "T_slide572/200");
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return sol;
  }

  private static String dataset(Model model, String tag, String sol) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", sol);
    return tag;
  }

  private static double lastGlobal(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  private static double lastSurface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  private static double lastIntegral(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", RUN_REFINE ? REFINE_BASE : BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), PRESSURE_INIT) || !has(model.sol().tags(), SOLID_INIT)) {
        throw new IllegalStateException("Missing required initial solutions");
      }
      comp.physics("ge_force_total111").active(false);
      comp.physics("solid").prop("StructuralTransientBehavior").set("StructuralTransientBehavior", "Quasistatic");
      comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
        "0",
        "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
        "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
      });
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      comp.variable("var_dynamic_motion572").set("tau572", "t");
      model.param().set("lambda_h574", "1");
      model.param().set("lambda_v574", "1");
      String resultsFile = RUN_REFINE ? REFINE_RESULTS : RESULTS;
      String checkedFile = RUN_REFINE ? REFINE_CHECKED : CHECKED;
      double[] activeSpeeds = RUN_REFINE ? REFINE_SPEEDS : SPEEDS;
      model.label(RUN_REFINE ? "Stage 576g2 refined velocity threshold setup" : "Stage 576g velocity-q feasibility setup");
      if (!RUN_REFINE) {
        model.save(SETUP);
        System.out.println("SAVED_SETUP=" + SETUP);
      }

      int stableCount = 0;
      int closedCount = 0;
      double bestError = Double.POSITIVE_INFINITY;
      double bestQ = Double.NaN;
      double bestSpeed = Double.NaN;
      double bestContact = Double.NaN;
      double bestFilm = Double.NaN;
      for (int qi = 0; qi < Q_VALUES.length; qi++) {
        double q = Q_VALUES[qi];
        int branchIndex = RUN_REFINE ? 10 + qi : qi;
        String solidSol = buildSolid(model, comp, q, branchIndex);
        System.out.println("RUN_SOLID q=" + q + " sol=" + solidSol);
        model.sol(solidSol).runAll();
        String solidData = dataset(model, "dset576g_solid_" + qi, solidSol);
        double fContact = lastGlobal(model, solidData, "eval576g_contact_" + qi, "Fn_contact570");
        for (int si = 0; si < activeSpeeds.length; si++) {
          double speed = activeSpeeds[si];
          try {
            model.param().set("v_blink_avg", String.format(Locale.US, "%.12g[m/s]", speed));
            int speedIndex = RUN_REFINE ? 10 + si : si;
            String tffSol = buildTffHistory(model, comp, solidSol, branchIndex, speedIndex);
            System.out.println("RUN_TFF q=" + q + " speed=" + speed + " sol=" + tffSol);
            model.sol(tffSol).runAll();
            String suffix = branchIndex + "_" + speedIndex;
            String data = dataset(model, "dset576g_tff_" + suffix, tffSol);
            double fFilm = lastIntegral(model, data, "int576g_film_" + suffix, "p_load573");
            double minTheta = lastSurface(model, data, "min576g_theta_" + suffix, "MinSurface", "tff.theta");
            double maxP = lastSurface(model, data, "max576g_p_" + suffix, "MaxSurface", "tff.p-p_amb573");
            double minH = lastSurface(model, data, "min576g_h_" + suffix, "MinSurface", "h_calc573");
            double maxH = lastSurface(model, data, "max576g_h_" + suffix, "MaxSurface", "h_calc573");
            double fTotal = fContact + fFilm;
            boolean stable = Double.isFinite(fFilm) && Double.isFinite(fTotal) && Double.isFinite(maxP)
                && Double.isFinite(minH) && Double.isFinite(maxH) && minTheta >= -1e-8;
            boolean closed = stable && fTotal >= 0.025 && fTotal <= 0.035;
            if (stable) stableCount++;
            if (closed) closedCount++;
            if (stable && Math.abs(fTotal-TARGET) < bestError) {
              bestError = Math.abs(fTotal-TARGET);
              bestQ = q;
              bestSpeed = speed;
              bestContact = fContact;
              bestFilm = fFilm;
            }
            System.out.printf(Locale.US,
                "SCAN_ROW q=%.12g speed=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g Hrange=[%.12g,%.12g] stable=%s closed=%s solid=%s tff=%s%n",
                q, speed, fContact, fFilm, fTotal, fTotal-TARGET, maxP, minTheta, minH, maxH,
                stable ? "PASS" : "FAIL", closed ? "YES" : "NO", solidSol, tffSol);
          } catch (Exception error) {
            System.out.println("SCAN_FAILED q=" + q + " speed=" + speed + " error=" + error);
          }
        }
        model.save(resultsFile);
      }
      boolean scanPass = stableCount >= 10;
      if (RUN_REFINE) scanPass = stableCount >= 7;
      model.save(resultsFile);
      System.out.printf(Locale.US,
          "SCAN_SUMMARY stable=%d closed=%d bestQ=%.12g bestSpeed=%.12g bestContact=%.12g bestFilm=%.12g bestTotal=%.12g bestError=%.12g%n",
          stableCount, closedCount, bestQ, bestSpeed, bestContact, bestFilm,
          bestContact+bestFilm, bestError);
      System.out.println("VELOCITY_SCAN_STATUS=" + (scanPass ? "PASS" : "FAIL"));
      if (scanPass) {
        model.label(RUN_REFINE ? "Stage 576g2 refined velocity threshold checked" : "Stage 576g velocity-q feasibility scan checked");
        model.save(checkedFile);
        System.out.println("SAVED_CHECKED=" + checkedFile);
      }
      ModelUtil.disconnect();
      if (!scanPass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
