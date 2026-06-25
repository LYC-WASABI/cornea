import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576e1_staggered_history_micro {
  private static final String BASE = "576e_stage576_transient_load_controller_setup.mph";
  private static final String SETUP = "576e1_stage576_staggered_history_micro_setup.mph";
  private static final String RESULTS = "576e1_stage576_staggered_history_micro_results.mph";
  private static final String CHECKED = "576e1_stage576_staggered_history_micro_checked.mph";
  private static final String WINDOW_BASE = "576e1_stage576_staggered_history_micro_checked.mph";
  private static final String WINDOW_RESULTS = "576e2_stage576_staggered_controller_window_results.mph";
  private static final String WINDOW_CHECKED = "576e2_stage576_staggered_controller_window_checked.mph";
  private static final boolean RUN_WINDOW = true;
  private static final String FEASIBILITY_BASE = "576e2_stage576_staggered_controller_window_results.mph";
  private static final String FEASIBILITY_RESULTS = "576f_stage576_dynamic_film_load_feasibility_results.mph";
  private static final String FEASIBILITY_CHECKED = "576f_stage576_dynamic_film_load_feasibility_checked.mph";
  private static final boolean RUN_FEASIBILITY = true;
  private static final String COMPRESSION_BASE = "576f_stage576_dynamic_film_load_feasibility_checked.mph";
  private static final String COMPRESSION_RESULTS = "576f2_stage576_dynamic_compression_feasibility_results.mph";
  private static final String COMPRESSION_CHECKED = "576f2_stage576_dynamic_compression_feasibility_checked.mph";
  private static final boolean RUN_COMPRESSION_SCAN = true;
  private static int tffStudyCounter = 0;
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

  private static String buildTffStep(Model model, ModelNode comp, String pressureInit, String pressureSolnum, String solidState) {
    String study = "std576e1_tff_step_" + (++tffStudyCounter);
    model.study().create(study);
    model.study(study).label("Stage 576e1 history-preserving TFF step " + tffStudyCounter);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set("tlist", "range(t0_576e1,dt_576e1,t1_576e1)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", pressureInit);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", pressureSolnum);
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
    dep.set("solnum", pressureSolnum);
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidState);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(t0_576e1,dt_576e1,t1_576e1)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "dt_576e1");
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 120);
    return sol;
  }

  private static String buildSolid(Model model, ModelNode comp, String initSol, int iter) {
    String study = "std576e1_solid_" + iter;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576e1 solid load update " + iter);
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
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
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

  private static void runWindow(Model model, ModelNode comp) throws Exception {
    String pressureSol = "sol237";
    String solidSol = "sol239";
    double q = -9.0;
    double fContact = 0.0255926141640;
    int outside = 0;
    boolean stable = true;
    for (int stepIndex = 1; stepIndex <= 11; stepIndex++) {
      double f0 = 0.805 + (stepIndex-1)*0.005;
      double f1 = f0 + 0.005;
      model.param().set("t0_576e1", String.format(Locale.US, "T_pre572+%.12g*T_slide572", f0));
      model.param().set("t1_576e1", String.format(Locale.US, "T_pre572+%.12g*T_slide572", f1));
      model.param().set("dt_576e1", "T_slide572/200");
      String nextPressure = buildTffStep(model, comp, pressureSol, "last", solidSol);
      System.out.println("WINDOW_RUN_TFF step=" + stepIndex + " fraction=" + f1 + " init=" + pressureSol + " solid=" + solidSol + " sol=" + nextPressure);
      model.sol(nextPressure).runAll();
      pressureSol = nextPressure;
      String tffData = dataset(model, "dset576e2_tff_" + stepIndex, pressureSol);
      double fFilm = lastIntegral(model, tffData, "int576e2_film_" + stepIndex, "p_load573");
      double minTheta = lastSurface(model, tffData, "min576e2_theta_" + stepIndex, "MinSurface", "tff.theta");
      double maxP = lastSurface(model, tffData, "max576e2_p_" + stepIndex, "MaxSurface", "tff.p-p_amb573");
      double errorBefore = fContact + fFilm - TARGET;
      if (Math.abs(errorBefore) > 0.001) {
        double qStep = Math.max(-0.25, Math.min(0.25, -errorBefore/0.002));
        q = Math.max(-12.0, Math.min(-6.0, q + qStep));
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String nextSolid = buildSolid(model, comp, solidSol, 100 + stepIndex);
        System.out.println("WINDOW_RUN_SOLID step=" + stepIndex + " q=" + q + " init=" + solidSol + " sol=" + nextSolid);
        model.sol(nextSolid).runAll();
        solidSol = nextSolid;
        String solidData = dataset(model, "dset576e2_solid_" + stepIndex, solidSol);
        fContact = lastGlobal(model, solidData, "eval576e2_contact_" + stepIndex, "Fn_contact570");
      }
      double fTotal = fContact + fFilm;
      if (fTotal < 0.025 || fTotal > 0.035) outside++;
      stable = stable && Double.isFinite(fContact) && Double.isFinite(fFilm)
          && Double.isFinite(maxP) && Double.isFinite(minTheta) && minTheta >= -1e-8;
      System.out.printf(Locale.US,
          "WINDOW_ROW step=%d fraction=%.6f q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g solid=%s tff=%s%n",
          stepIndex, f1, q, fContact, fFilm, fTotal, fTotal-TARGET, maxP, minTheta, solidSol, pressureSol);
      if (stepIndex % 3 == 0) model.save(WINDOW_RESULTS);
    }
    boolean pass = stable && outside <= 3 && q >= -12 && q <= -6;
    model.save(WINDOW_RESULTS);
    System.out.println("WINDOW_OUTSIDE=" + outside);
    System.out.println("WINDOW_STATUS=" + (pass ? "PASS" : "FAIL"));
    if (pass) {
      model.label("Stage 576e2 staggered transient load-controller window checked");
      model.save(WINDOW_CHECKED);
      System.out.println("SAVED_WINDOW_CHECKED=" + WINDOW_CHECKED);
    }
    if (!pass) throw new IllegalStateException("Stage 576e2 window did not pass");
  }

  private static void runFeasibility(Model model, ModelNode comp) throws Exception {
    double[] qValues = new double[] {-9.0, -9.5, -10.0, -10.5, -10.75};
    tffStudyCounter = 100;
    model.param().set("t0_576e1", "T_pre572+0.835*T_slide572");
    model.param().set("t1_576e1", "T_pre572+0.840*T_slide572");
    model.param().set("dt_576e1", "T_slide572/200");
    model.param().set("alpha_pfb576e", "0");
    try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
    int stableCount = 0;
    boolean loadClosureAvailable = false;
    double minFilm = Double.POSITIVE_INFINITY;
    double minFilmQ = Double.NaN;
    double bestError = Double.POSITIVE_INFINITY;
    double bestQ = Double.NaN;
    for (int i = 0; i < qValues.length; i++) {
      double q = qValues[i];
      try {
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String solidSol = buildSolid(model, comp, "sol249", 200 + i);
        System.out.println("SCAN_RUN_SOLID q=" + q + " sol=" + solidSol);
        model.sol(solidSol).runAll();
        String solidData = dataset(model, "dset576f_solid_" + i, solidSol);
        double fContact = lastGlobal(model, solidData, "eval576f_contact_" + i, "Fn_contact570");
        String tffSol = buildTffStep(model, comp, "sol248", "last", solidSol);
        System.out.println("SCAN_RUN_TFF q=" + q + " pressureInit=sol248 solid=" + solidSol + " sol=" + tffSol);
        model.sol(tffSol).runAll();
        String tffData = dataset(model, "dset576f_tff_" + i, tffSol);
        double fFilm = lastIntegral(model, tffData, "int576f_film_" + i, "p_load573");
        double minTheta = lastSurface(model, tffData, "min576f_theta_" + i, "MinSurface", "tff.theta");
        double maxP = lastSurface(model, tffData, "max576f_p_" + i, "MaxSurface", "tff.p-p_amb573");
        double minH = lastSurface(model, tffData, "min576f_h_" + i, "MinSurface", "h_calc573");
        double maxH = lastSurface(model, tffData, "max576f_h_" + i, "MaxSurface", "h_calc573");
        double fTotal = fContact + fFilm;
        boolean stable = Double.isFinite(fContact) && Double.isFinite(fFilm)
            && Double.isFinite(maxP) && Double.isFinite(minH) && Double.isFinite(maxH)
            && minTheta >= -1e-8;
        if (stable) {
          stableCount++;
          if (fFilm < minFilm) { minFilm = fFilm; minFilmQ = q; }
          if (Math.abs(fTotal-TARGET) < bestError) { bestError = Math.abs(fTotal-TARGET); bestQ = q; }
          if (fTotal >= 0.025 && fTotal <= 0.035) loadClosureAvailable = true;
        }
        System.out.printf(Locale.US,
            "SCAN_ROW q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g Hrange=[%.12g,%.12g] stable=%s solid=%s tff=%s%n",
            q, fContact, fFilm, fTotal, fTotal-TARGET, maxP, minTheta, minH, maxH,
            stable ? "PASS" : "FAIL", solidSol, tffSol);
      } catch (Exception error) {
        System.out.println("SCAN_FAILED q=" + q + " error=" + error);
      }
    }
    boolean scanPass = stableCount >= 3 && Double.isFinite(minFilm);
    model.save(FEASIBILITY_RESULTS);
    System.out.printf(Locale.US, "SCAN_SUMMARY stable=%d minFilm=%.12g minFilmQ=%.12g bestError=%.12g bestQ=%.12g filmBelowTarget=%s loadClosureAvailable=%s%n",
        stableCount, minFilm, minFilmQ, bestError, bestQ,
        minFilm < TARGET ? "YES" : "NO", loadClosureAvailable ? "YES" : "NO");
    System.out.println("FEASIBILITY_SCAN_STATUS=" + (scanPass ? "PASS" : "FAIL"));
    if (scanPass) {
      model.label("Stage 576f dynamic film-load feasibility scan checked");
      model.save(FEASIBILITY_CHECKED);
      System.out.println("SAVED_FEASIBILITY_CHECKED=" + FEASIBILITY_CHECKED);
    }
    if (!scanPass) throw new IllegalStateException("Stage 576f feasibility scan insufficient stable branches");
  }

  private static void runCompressionScan(Model model, ModelNode comp) throws Exception {
    double[] qValues = new double[] {-8.5, -8.0, -7.5, -7.0, -6.5};
    tffStudyCounter = 200;
    model.param().set("t0_576e1", "T_pre572+0.835*T_slide572");
    model.param().set("t1_576e1", "T_pre572+0.840*T_slide572");
    model.param().set("dt_576e1", "T_slide572/200");
    model.param().set("alpha_pfb576e", "0");
    try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
    int stableCount = 0;
    boolean loadClosureAvailable = false;
    double bestError = Double.POSITIVE_INFINITY;
    double bestQ = Double.NaN;
    double bestContact = Double.NaN;
    double bestFilm = Double.NaN;
    for (int i = 0; i < qValues.length; i++) {
      double q = qValues[i];
      try {
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String solidSol = buildSolid(model, comp, "sol249", 300 + i);
        System.out.println("COMP_RUN_SOLID q=" + q + " sol=" + solidSol);
        model.sol(solidSol).runAll();
        String solidData = dataset(model, "dset576f2_solid_" + i, solidSol);
        double fContact = lastGlobal(model, solidData, "eval576f2_contact_" + i, "Fn_contact570");
        String tffSol = buildTffStep(model, comp, "sol248", "last", solidSol);
        System.out.println("COMP_RUN_TFF q=" + q + " pressureInit=sol248 solid=" + solidSol + " sol=" + tffSol);
        model.sol(tffSol).runAll();
        String tffData = dataset(model, "dset576f2_tff_" + i, tffSol);
        double fFilm = lastIntegral(model, tffData, "int576f2_film_" + i, "p_load573");
        double minTheta = lastSurface(model, tffData, "min576f2_theta_" + i, "MinSurface", "tff.theta");
        double maxP = lastSurface(model, tffData, "max576f2_p_" + i, "MaxSurface", "tff.p-p_amb573");
        double minH = lastSurface(model, tffData, "min576f2_h_" + i, "MinSurface", "h_calc573");
        double maxH = lastSurface(model, tffData, "max576f2_h_" + i, "MaxSurface", "h_calc573");
        double fTotal = fContact + fFilm;
        boolean stable = Double.isFinite(fContact) && Double.isFinite(fFilm)
            && Double.isFinite(maxP) && Double.isFinite(minH) && Double.isFinite(maxH)
            && minTheta >= -1e-8;
        if (stable) {
          stableCount++;
          if (Math.abs(fTotal-TARGET) < bestError) {
            bestError = Math.abs(fTotal-TARGET);
            bestQ = q;
            bestContact = fContact;
            bestFilm = fFilm;
          }
          if (fTotal >= 0.025 && fTotal <= 0.035) loadClosureAvailable = true;
        }
        System.out.printf(Locale.US,
            "COMP_ROW q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g Hrange=[%.12g,%.12g] stable=%s solid=%s tff=%s%n",
            q, fContact, fFilm, fTotal, fTotal-TARGET, maxP, minTheta, minH, maxH,
            stable ? "PASS" : "FAIL", solidSol, tffSol);
      } catch (Exception error) {
        System.out.println("COMP_FAILED q=" + q + " error=" + error);
      }
    }
    boolean scanPass = stableCount >= 3;
    model.save(COMPRESSION_RESULTS);
    System.out.printf(Locale.US,
        "COMP_SUMMARY stable=%d bestQ=%.12g bestContact=%.12g bestFilm=%.12g bestTotal=%.12g bestError=%.12g loadClosureAvailable=%s%n",
        stableCount, bestQ, bestContact, bestFilm, bestContact+bestFilm, bestError,
        loadClosureAvailable ? "YES" : "NO");
    System.out.println("COMPRESSION_SCAN_STATUS=" + (scanPass ? "PASS" : "FAIL"));
    if (scanPass) {
      model.label("Stage 576f2 dynamic compression feasibility scan checked");
      model.save(COMPRESSION_CHECKED);
      System.out.println("SAVED_COMPRESSION_CHECKED=" + COMPRESSION_CHECKED);
    }
    if (!scanPass) throw new IllegalStateException("Stage 576f2 compression scan insufficient stable branches");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", RUN_COMPRESSION_SCAN ? COMPRESSION_BASE : (RUN_FEASIBILITY ? FEASIBILITY_BASE : (RUN_WINDOW ? WINDOW_BASE : BASE)));
      ModelNode comp = model.component("comp1");
      if (RUN_COMPRESSION_SCAN) {
        runCompressionScan(model, comp);
        ModelUtil.disconnect();
        return;
      }
      if (RUN_FEASIBILITY) {
        runFeasibility(model, comp);
        ModelUtil.disconnect();
        return;
      }
      if (RUN_WINDOW) {
        runWindow(model, comp);
        ModelUtil.disconnect();
        return;
      }
      model.param().set("t0_576e1", "T_pre572+0.80*T_slide572");
      model.param().set("dt_576e1", "T_slide572/200");
      model.param().set("t1_576e1", "t0_576e1+dt_576e1");
      model.param().set("q_scale574", "-10");
      model.param().set("alpha_pfb576e", "0");
      comp.physics("ge_force_total111").active(false);
      comp.physics("solid").prop("StructuralTransientBehavior").set("StructuralTransientBehavior", "Quasistatic");
      comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
        "0",
        "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
        "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
      });
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      model.label("Stage 576e1 staggered history micro setup");
      model.save(SETUP);

      String tffSol = buildTffStep(model, comp, "sol236", "161", "sol201");
      System.out.println("RUN_TFF=" + tffSol);
      model.sol(tffSol).runAll();
      String tffData = dataset(model, "dset576e1_tff", tffSol);
      double fFilm = lastIntegral(model, tffData, "int576e1_film", "p_load573");
      double tEnd = lastGlobal(model, tffData, "eval576e1_time", "t");
      double fContact0 = 0.0222318631842;
      double q = -10.0;
      String solidSol = "sol201";
      double fContact = fContact0;
      for (int iter = 0; iter < 6; iter++) {
        double error = fContact + fFilm - TARGET;
        if (Math.abs(error) <= 0.005) break;
        double step = Math.max(-0.5, Math.min(0.5, -error/0.002));
        q = Math.max(-12.0, Math.min(-6.0, q + step));
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String nextSolid = buildSolid(model, comp, solidSol, iter);
        System.out.println("RUN_SOLID iter=" + iter + " q=" + q + " sol=" + nextSolid);
        model.sol(nextSolid).runAll();
        solidSol = nextSolid;
        String solidData = dataset(model, "dset576e1_solid_" + iter, solidSol);
        fContact = lastGlobal(model, solidData, "eval576e1_contact_" + iter, "Fn_contact570");
      }
      double fTotal = fContact + fFilm;
      boolean pass = Double.isFinite(fFilm) && Double.isFinite(fContact) && Math.abs(fTotal-TARGET) <= 0.005;
      System.out.printf(Locale.US, "MICRO_RESULT t=%.12g q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g solid=%s tff=%s%n",
          tEnd, q, fContact, fFilm, fTotal, fTotal-TARGET, solidSol, tffSol);
      System.out.println("MICRO_STATUS=" + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      if (pass) {
        model.label("Stage 576e1 staggered history micro checked");
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
