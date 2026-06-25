import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576k_peak_feedback_continuation {
  private static final String BASE = "576k3_stage576_peak_feedback_relaxed_015_020_results.mph";
  private static final String RESULTS = "576k4_stage576_peak_feedback_relaxed_020_results.mph";
  private static final String CHECKED = "576k4_stage576_peak_feedback_relaxed_020_checked.mph";
  private static final String HISTORY_PRESSURE = "sol649"; // fraction 0.850
  private static final String INITIAL_SOLID = "sol812";    // fraction 0.855, converged alpha 0.15
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

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
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
    return values[values.length - 1];
  }

  private static double lastIntegral(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double lastSurface(
      Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static String buildTff(Model model, ModelNode comp, String solidState, int index) {
    String study = "std576k_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576k peak TFF iteration " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set(
        "tlist", "range(T_pre572+0.85*T_slide572,dt_576k,T_pre572+0.855*T_slide572)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", HISTORY_PRESSURE);
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
    dep.set("initsol", HISTORY_PRESSURE);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidState);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(T_pre572+0.85*T_slide572,dt_576k,T_pre572+0.855*T_slide572)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "dt_576k");
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return sol;
  }

  private static void setFeedback(
      ModelNode comp, String pressureSol, String previousPressureSol) {
    String vars = "var_feedback576k";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback576k",
        "alpha_pfb576k*(0.75*withsol('" + previousPressureSol
        + "',p_load573)+0.25*withsol('" + pressureSol + "',p_load573))");
    String load = "load_pfilm576k";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576k continued film-pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576k*nx", "-p_feedback576k*ny", "-p_feedback576k*nz"
    });
  }

  private static String buildSolid(Model model, ModelNode comp, String initSol, int index) {
    String study = "std576k_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576k peak solid iteration " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576k"}) {
      try { comp.physics("solid").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", initSol);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", initSol);
    dep.set("notsolnum", "last");
    SolverFeature stat = model.sol(sol).feature("s1");
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.05");
    stat.feature("fc1").set("maxiter", 400);
    return sol;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      model.param().set("q_scale574", "-9");
      model.param().set("v_blink_avg", "0.03[m/s]");
      model.param().set("dt_576k", "T_slide572/200");
      comp.physics("ge_force_total111").active(false);
      try { comp.physics("solid").feature("load_pfilm576i").active(false); } catch (Exception ignored) {}
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      comp.physics("solid").prop("StructuralTransientBehavior").set(
          "StructuralTransientBehavior", "Quasistatic");

      double[] alphas = new double[] {0.20};
      String solidSol = INITIAL_SOLID;
      boolean stable = true;
      boolean loadPass = true;
      int runIndex = 1100;
      for (int a = 0; a < alphas.length; a++) {
        model.param().set("alpha_pfb576k", String.format(Locale.US, "%.8g", alphas[a]));
        double previousTotal = Double.NaN;
        double previousGap = Double.NaN;
        String previousPressureSol = null;
        boolean converged = false;
        for (int iter = 0; iter < 10; iter++) {
          int idx = runIndex++;
          String pressureSol = buildTff(model, comp, solidSol, idx);
          System.out.printf(Locale.US, "RUN_TFF alpha=%.4f iter=%d solid=%s pressure=%s%n",
              alphas[a], iter + 1, solidSol, pressureSol);
          model.sol(pressureSol).runAll();
          String tffData = dataset(model, "dset576k_tff_" + idx, pressureSol);
          double fFilm = lastIntegral(model, tffData, "int576k_film_" + idx, "p_load573");
          double minTheta = lastSurface(
              model, tffData, "min576k_theta_" + idx, "MinSurface", "tff.theta");
          double maxP = lastSurface(
              model, tffData, "max576k_p_" + idx, "MaxSurface", "tff.p-p_amb573");
          setFeedback(comp, pressureSol,
              previousPressureSol == null ? pressureSol : previousPressureSol);
          String nextSolid = buildSolid(model, comp, solidSol, idx);
          System.out.printf(Locale.US, "RUN_SOLID alpha=%.4f iter=%d init=%s solid=%s%n",
              alphas[a], iter + 1, solidSol, nextSolid);
          model.sol(nextSolid).runAll();
          solidSol = nextSolid;
          String solidData = dataset(model, "dset576k_solid_" + idx, solidSol);
          double fContact = lastGlobal(
              model, solidData, "eval576k_contact_" + idx, "Fn_contact570");
          double minGap = lastSurface(model, solidData, "min576k_gap_" + idx,
              "MinSurface", "geomgap_dst_cp_lid_cornea");
          double fTotal = fContact + fFilm;
          double deltaTotal = Double.isFinite(previousTotal)
              ? Math.abs(fTotal - previousTotal) : Double.POSITIVE_INFINITY;
          double deltaGap = Double.isFinite(previousGap)
              ? Math.abs(minGap - previousGap) : Double.POSITIVE_INFINITY;
          stable = stable && Double.isFinite(fContact) && Double.isFinite(fFilm)
              && Double.isFinite(maxP) && Double.isFinite(minGap) && minTheta >= -1e-8;
          System.out.printf(Locale.US,
              "ROW alpha=%.4f iter=%d Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g dF=%.12g MaxP=%.12g MinTheta=%.12g MinGap=%.12g dGap=%.12g solid=%s tff=%s%n",
              alphas[a], iter + 1, fContact, fFilm, fTotal, fTotal - TARGET,
              deltaTotal, maxP, minTheta, minGap, deltaGap, solidSol, pressureSol);
          if (iter >= 1 && deltaTotal < 1e-5 && deltaGap < 1e-8) {
            converged = true;
            break;
          }
          previousTotal = fTotal;
          previousGap = minGap;
          previousPressureSol = pressureSol;
        }
        String finalPressure = "sol" + (Integer.parseInt(solidSol.substring(3)) - 1);
        String finalTffData = dataset(model, "dset576k_final_tff_" + a, finalPressure);
        String finalSolidData = dataset(model, "dset576k_final_solid_" + a, solidSol);
        double finalFilm = lastIntegral(
            model, finalTffData, "int576k_final_film_" + a, "p_load573");
        double finalContact = lastGlobal(
            model, finalSolidData, "eval576k_final_contact_" + a, "Fn_contact570");
        double finalTotal = finalContact + finalFilm;
        boolean alphaPass = converged && finalTotal >= 0.025 && finalTotal <= 0.035;
        loadPass = loadPass && alphaPass;
        System.out.printf(Locale.US,
            "ALPHA_SUMMARY alpha=%.4f converged=%s Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g loadBand=%s%n",
            alphas[a], converged ? "PASS" : "FAIL", finalContact, finalFilm, finalTotal,
            alphaPass ? "PASS" : "FAIL");
        model.save(RESULTS);
      }
      boolean pass = stable && loadPass;
      model.label(pass
          ? "Stage 576k4 relaxed peak feedback alpha 0.20 checked"
          : "Stage 576k4 relaxed peak feedback alpha 0.20 diagnostic results");
      model.save(RESULTS);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) {
        model.save(CHECKED);
        System.out.println("SAVED_CHECKED=" + CHECKED);
      }
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
