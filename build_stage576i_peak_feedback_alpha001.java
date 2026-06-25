import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576i_peak_feedback_alpha001 {
  private static final String BASE = "576h2_stage576_full_dynamic_safe_speed_checked.mph";
  private static final String SETUP = "576i_stage576_peak_feedback_alpha001_setup.mph";
  private static final String RESULTS = "576i_stage576_peak_feedback_alpha001_results.mph";
  private static final String CHECKED = "576i_stage576_peak_feedback_alpha001_checked.mph";
  private static final boolean RUN_ALPHA002 = true;
  private static final String ALPHA002_BASE = "576i_stage576_peak_feedback_alpha001_checked.mph";
  private static final String ALPHA002_SETUP = "576i2_stage576_peak_feedback_alpha002_setup.mph";
  private static final String ALPHA002_RESULTS = "576i2_stage576_peak_feedback_alpha002_results.mph";
  private static final String ALPHA002_CHECKED = "576i2_stage576_peak_feedback_alpha002_checked.mph";
  private static final boolean RUN_ALPHA005 = true;
  private static final String ALPHA005_SETUP = "576i3_stage576_peak_feedback_alpha005_setup.mph";
  private static final String ALPHA005_RESULTS = "576i3_stage576_peak_feedback_alpha005_results.mph";
  private static final String ALPHA005_CHECKED = "576i3_stage576_peak_feedback_alpha005_checked.mph";
  private static final boolean RUN_ALPHA004 = true;
  private static final String ALPHA004_SETUP = "576i4_stage576_peak_feedback_alpha004_setup.mph";
  private static final String ALPHA004_RESULTS = "576i4_stage576_peak_feedback_alpha004_results.mph";
  private static final String ALPHA004_CHECKED = "576i4_stage576_peak_feedback_alpha004_checked.mph";
  private static final boolean RUN_FULL_ALPHA004 = true;
  private static final String FULL_ALPHA004_SETUP = "576l_stage576_full_dynamic_feedback_alpha015_setup.mph";
  private static final String FULL_ALPHA004_RESULTS = "576l_stage576_full_dynamic_feedback_alpha015_results.mph";
  private static final String FULL_ALPHA004_CHECKED = "576l_stage576_full_dynamic_feedback_alpha015_checked.mph";
  private static final String INITIAL_PRESSURE = "sol310";
  private static final String INITIAL_PRESSURE_NUM = "165";
  private static final String INITIAL_SOLID = "sol298";
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

  private static double lastSurface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  private static String buildTffStep(
      Model model, ModelNode comp, String pressureInit, String pressureNum,
      String solidState, int index) {
    String study = "std576i_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576i alpha001 TFF step " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set("tlist", "range(t0_576i,dt_576i,t1_576i)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", pressureInit);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", pressureNum);
    String step = study + "/time";
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", pressureInit);
    dep.set("solnum", pressureNum);
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidState);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(t0_576i,dt_576i,t1_576i)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "dt_576i");
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return sol;
  }

  private static void setFeedback(Model model, ModelNode comp, String pressureSol) {
    String vars = "var_feedback576i";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set(
        "p_feedback576i", "alpha_pfb576i*withsol('" + pressureSol + "',p_load573)");
    String load = "load_pfilm576i";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576i alpha001 film-pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576i*nx", "-p_feedback576i*ny", "-p_feedback576i*nz"
    });
  }

  private static String buildSolid(
      Model model, ModelNode comp, String initSol, int index) {
    String study = "std576i_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576i alpha001 solid step " + index);
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
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576i"}) {
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
    for (String tag : stat.feature().tags()) if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
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
      String setupFile = RUN_FULL_ALPHA004 ? FULL_ALPHA004_SETUP : (RUN_ALPHA004 ? ALPHA004_SETUP : (RUN_ALPHA005 ? ALPHA005_SETUP : (RUN_ALPHA002 ? ALPHA002_SETUP : SETUP)));
      String resultsFile = RUN_FULL_ALPHA004 ? FULL_ALPHA004_RESULTS : (RUN_ALPHA004 ? ALPHA004_RESULTS : (RUN_ALPHA005 ? ALPHA005_RESULTS : (RUN_ALPHA002 ? ALPHA002_RESULTS : RESULTS)));
      String checkedFile = RUN_FULL_ALPHA004 ? FULL_ALPHA004_CHECKED : (RUN_ALPHA004 ? ALPHA004_CHECKED : (RUN_ALPHA005 ? ALPHA005_CHECKED : (RUN_ALPHA002 ? ALPHA002_CHECKED : CHECKED)));
      int indexOffset = RUN_FULL_ALPHA004 ? 400 : (RUN_ALPHA004 ? 300 : (RUN_ALPHA005 ? 200 : (RUN_ALPHA002 ? 100 : 0)));
      model.param().set("q_scale574", "-9");
      model.param().set("v_blink_avg", "0.03[m/s]");
      model.param().set("alpha_pfb576i", RUN_FULL_ALPHA004 ? "0.15" : (RUN_ALPHA004 ? "0.04" : (RUN_ALPHA005 ? "0.05" : (RUN_ALPHA002 ? "0.02" : "0.01"))));
      model.param().set("dt_576i", "T_slide572/200");
      comp.physics("ge_force_total111").active(false);
      comp.physics("solid").prop("StructuralTransientBehavior").set("StructuralTransientBehavior", "Quasistatic");
      comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
        "0",
        "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
        "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
      });
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      model.label(RUN_FULL_ALPHA004 ? "Stage 576l full dynamic feedback alpha015 setup" : (RUN_ALPHA004 ? "Stage 576i4 peak feedback alpha004 setup" : (RUN_ALPHA005 ? "Stage 576i3 peak feedback alpha005 setup" : (RUN_ALPHA002 ? "Stage 576i2 peak feedback alpha002 setup" : "Stage 576i peak feedback alpha001 setup"))));
      model.save(setupFile);

      String pressureSol = RUN_FULL_ALPHA004 ? "sol119" : INITIAL_PRESSURE;
      String pressureNum = RUN_FULL_ALPHA004 ? "last" : INITIAL_PRESSURE_NUM;
      String solidSol = INITIAL_SOLID;
      int outside = 0;
      boolean stable = true;
      double maxTotal = Double.NEGATIVE_INFINITY;
      double minTotal = Double.POSITIVE_INFINITY;
      int stepCount = RUN_FULL_ALPHA004 ? 200 : 16;
      double startFraction = RUN_FULL_ALPHA004 ? 0.0 : 0.82;
      for (int i = 0; i < stepCount; i++) {
        int idx = indexOffset + i;
        double f0 = startFraction + i*0.005;
        double f1 = f0 + 0.005;
        model.param().set("t0_576i", String.format(Locale.US, "T_pre572+%.12g*T_slide572", f0));
        model.param().set("t1_576i", String.format(Locale.US, "T_pre572+%.12g*T_slide572", f1));
        String nextPressure = buildTffStep(model, comp, pressureSol, pressureNum, solidSol, idx);
        System.out.println("RUN_TFF step=" + i + " fraction=" + f1 + " init=" + pressureSol + " solid=" + solidSol + " sol=" + nextPressure);
        model.sol(nextPressure).runAll();
        pressureSol = nextPressure;
        pressureNum = "last";
        String tffData = dataset(model, "dset576i_tff_" + idx, pressureSol);
        double fFilm = lastIntegral(model, tffData, "int576i_film_" + idx, "p_load573");
        double minTheta = lastSurface(model, tffData, "min576i_theta_" + idx, "MinSurface", "tff.theta");
        double maxP = lastSurface(model, tffData, "max576i_p_" + idx, "MaxSurface", "tff.p-p_amb573");
        setFeedback(model, comp, pressureSol);
        String nextSolid = buildSolid(model, comp, solidSol, idx);
        System.out.println("RUN_SOLID step=" + i + " init=" + solidSol + " pressure=" + pressureSol + " sol=" + nextSolid);
        model.sol(nextSolid).runAll();
        solidSol = nextSolid;
        String solidData = dataset(model, "dset576i_solid_" + idx, solidSol);
        double fContact = lastGlobal(model, solidData, "eval576i_contact_" + idx, "Fn_contact570");
        double minGap = lastSurface(model, solidData, "min576i_gap_" + idx, "MinSurface", "geomgap_dst_cp_lid_cornea");
        double fTotal = fContact + fFilm;
        minTotal = Math.min(minTotal, fTotal);
        maxTotal = Math.max(maxTotal, fTotal);
        if (fTotal < 0.025 || fTotal > 0.035) outside++;
        stable = stable && Double.isFinite(fContact) && Double.isFinite(fFilm)
            && Double.isFinite(maxP) && Double.isFinite(minGap) && minTheta >= -1e-8;
        System.out.printf(Locale.US,
            "ROW step=%d fraction=%.6f Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g MinGap=%.12g solid=%s tff=%s%n",
            i, f1, fContact, fFilm, fTotal, fTotal-TARGET, maxP, minTheta, minGap, solidSol, pressureSol);
        if ((i+1)%(RUN_FULL_ALPHA004 ? 20 : 4) == 0) model.save(resultsFile);
      }
      boolean pass = stable && outside == 0;
      model.save(resultsFile);
      System.out.printf(Locale.US, "SUMMARY FtotalRange=[%.12g,%.12g] outside=%d stable=%s%n",
          minTotal, maxTotal, outside, stable ? "PASS" : "FAIL");
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) {
        model.label(RUN_FULL_ALPHA004 ? "Stage 576l full dynamic feedback alpha015 checked" : (RUN_ALPHA004 ? "Stage 576i4 peak feedback alpha004 checked" : (RUN_ALPHA005 ? "Stage 576i3 peak feedback alpha005 checked" : (RUN_ALPHA002 ? "Stage 576i2 peak feedback alpha002 checked" : "Stage 576i peak feedback alpha001 checked"))));
        model.save(checkedFile);
        System.out.println("SAVED_CHECKED=" + checkedFile);
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
