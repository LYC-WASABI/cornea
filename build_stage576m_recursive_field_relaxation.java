import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576m_recursive_field_relaxation {
  private static final String BASE =
      "576n11_stage576_local_halfstep_resume180_results.mph";
  private static final String SETUP =
      "576n12_stage576_halfstep_iter60_resume_setup.mph";
  private static final String CHECKPOINT =
      "576n12_stage576_halfstep_iter60_resume_checkpoint.mph";
  private static final String RESULTS =
      "576n12_stage576_halfstep_iter60_resume_results.mph";
  private static final String CHECKED =
      "576n12_stage576_full_dynamic_recursive_checked.mph";
  private static final String HISTORY_PRESSURE = "sol3412";
  private static final String INITIAL_TARGET_PRESSURE = "sol119";
  private static final String INITIAL_SOLID = "sol3594";
  private static final String INITIAL_RELAXED = "sol3593";
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

  private static void createRelaxationPhysics(Model model, ModelNode comp) {
    if (has(comp.physics().tags(), "bode576m")) return;
    try { comp.physics().remove("bode576m"); } catch (Exception ignored) {}
    comp.physics().create("bode576m", "BoundaryODE", "geom1");
    Physics bode = comp.physics("bode576m");
    bode.label("Stage 576m recursive relaxed film-pressure field");
    bode.field("dimensionless").field("rrel576m");
    bode.field("dimensionless").component(new String[] {"rrel576m"});
    bode.selection().named("sel_local_cornea_patch574");
    try { bode.feature("dode1").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    try { bode.feature("init1").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    bode.feature("dode1").set("da", "tau_relax576m");
    bode.feature("dode1").set("f", "-rrel576m");
    bode.feature("init1").set("rrel576m", "0");
  }

  private static String buildTff(
      Model model, ModelNode comp, String historyPressure, String solidState, int index) {
    String study = "std576m_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576m peak TFF iteration " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set(
        "tlist", "range(t0_576n,dt_phys576n,t1_576n)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on", "bode576m", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", historyPressure);
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
    dep.set("initsol", historyPressure);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidState);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(t0_576n,dt_phys576n,t1_576n)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-8");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "dt_phys576n");
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

  private static String buildRelaxation(
      Model model, ModelNode comp, String pressureSol, String previousRelaxed, int index) {
    comp.physics("bode576m").feature("dode1").set("f",
        "alpha_pfb576m*withsol('" + pressureSol
        + "',p_load573)/p_scale576m-rrel576m");
    String study = "std576m_relax_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576m recursive field relaxation " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set(
        "tlist", "range(0,dt_relax576m/5,dt_relax576m)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "off", "bode576m", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    if (previousRelaxed != null) {
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", previousRelaxed);
      model.study(study).feature("time").set("initsoluse", "current");
      model.study(study).feature("time").set("initsolusesolnum", "last");
    }
    String step = study + "/time";
    for (String tag : comp.physics("bode576m").feature().tags()) {
      try { comp.physics("bode576m").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    if (previousRelaxed != null) {
      dep.set("initmethod", "sol");
      dep.set("initsol", previousRelaxed);
      dep.set("solnum", "last");
    }
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(0,dt_relax576m/5,dt_relax576m)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "dt_relax576m/100");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "dt_relax576m/5");
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "1");
    time.feature("fc1").set("maxiter", 50);
    return sol;
  }

  private static String buildInitialRelaxation(Model model, ModelNode comp) {
    comp.physics("bode576m").feature("dode1").set("f",
        "0.15*withsol('" + INITIAL_TARGET_PRESSURE
        + "',p_load573)/p_scale576m-rrel576m");
    String study = "std576m_relax_initial";
    model.study().create(study);
    model.study(study).label("Stage 576m exact initial alpha015 relaxed field");
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "off", "bode576m", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    String step = study + "/stat";
    for (String tag : comp.physics("bode576m").feature().tags()) {
      try { comp.physics("bode576m").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature stat = model.sol(sol).feature("s1");
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "1");
    stat.feature("fc1").set("maxiter", 50);
    return sol;
  }

  private static void setStructuralLoad(ModelNode comp, String relaxedSol) {
    String vars = "var_feedback576m";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback576m",
        "withsol('" + relaxedSol + "',p_scale576m*rrel576m)");
    String load = "load_pfilm576m";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576m recursively relaxed film pressure");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576m*nx", "-p_feedback576m*ny", "-p_feedback576m*nz"
    });
  }

  private static String buildSolid(Model model, ModelNode comp, String initSol, int index) {
    String study = "std576m_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576m peak solid iteration " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off", "bode576m", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576m"}) {
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
    stat.feature("fc1").set("damp", "0.02");
    stat.feature("fc1").set("maxiter", 800);
    return sol;
  }

  private static void removeSolution(Model model, String tag, Set<String> keep) {
    if (tag == null || keep.contains(tag) || tag.equals("sol119") || tag.equals("sol298")) return;
    try { model.sol().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void compactResumeModel(Model model) {
    System.out.println("PRESERVED_STUDY_COUNT=" + model.study().tags().length);
    System.out.println("PRESERVED_SOLUTION_COUNT=" + model.sol().tags().length);
  }

  public static void main(String[] args) {
    final int maxInner = 140;
    try {
      ModelUtil.initStandalone(false);
      System.out.printf(Locale.US, "JAVA_MAX_HEAP_GB=%.3f%n",
          Runtime.getRuntime().maxMemory()/1024.0/1024.0/1024.0);
      Model model = ModelUtil.load("Model", BASE);
      compactResumeModel(model);
      ModelNode comp = model.component("comp1");
      model.param().set("q_scale574", "-9");
      model.param().set("v_blink_avg", "0.03[m/s]");
      model.param().set("alpha_pfb576m", "0.20");
      model.param().set("beta_relax576m", "0.10");
      model.param().set("p_scale576m", "1[MPa]");
      model.param().set("tau_relax576m", "1[s]");
      model.param().set("dt_relax576m", "-log(1-beta_relax576m)*tau_relax576m");
      model.param().set("dt_phys576n", "T_slide572/400");
      comp.physics("ge_force_total111").active(false);
      for (String load : new String[] {"load_pfilm576i", "load_pfilm576k", "load_pfilm576e"}) {
        try { comp.physics("solid").feature(load).active(false); } catch (Exception ignored) {}
      }
      comp.physics("solid").prop("StructuralTransientBehavior").set(
          "StructuralTransientBehavior", "Quasistatic");
      createRelaxationPhysics(model, comp);
      model.label("Stage 576n full dynamic recursive setup");
      model.save(SETUP);

      String historyPressure = HISTORY_PRESSURE;
      String solidSol = INITIAL_SOLID;
      String relaxedSol = INITIAL_RELAXED;
      Set<String> snapshots = new HashSet<String>();
      snapshots.add("sol119");
      snapshots.add("sol298");
      boolean stableAll = true;
      int outside = 0;
      int failedTarget = -1;
      double minTotal = 0.0255953907551;
      double maxTotal = 0.0257347778189;

      List<Double> targets = new ArrayList<Double>();
      for (int halfStep = 359; halfStep <= 368; halfStep++) {
        targets.add(halfStep / 400.0);
      }
      for (int physicalStep = 185; physicalStep <= 200; physicalStep++) {
        targets.add(physicalStep / 200.0);
      }
      double f0 = 179.0 / 200.0;
      for (int targetIndex = 0; targetIndex < targets.size(); targetIndex++) {
        double f1 = targets.get(targetIndex);
        model.param().set("t0_576n", String.format(Locale.US,
            "T_pre572+%.12g*T_slide572", f0));
        model.param().set("t1_576n", String.format(Locale.US,
            "T_pre572+%.12g*T_slide572", f1));
        model.param().set("dt_phys576n", String.format(Locale.US,
            "%.12g*T_slide572", f1-f0));
        double prevContact = Double.NaN, prevFilm = Double.NaN;
        double prevFeedback = Double.NaN, prevTotal = Double.NaN, prevGap = Double.NaN;
        boolean stepConverged = false;
        String acceptedPressure = null;
        double acceptedContact = Double.NaN, acceptedFilm = Double.NaN;
        double acceptedFeedback = Double.NaN, acceptedTotal = Double.NaN;
        double acceptedResidual = Double.NaN, acceptedTheta = Double.NaN;
        double acceptedMaxP = Double.NaN, acceptedGap = Double.NaN;
        int usedInner = 0;

        for (int inner = 0; inner < maxInner; inner++) {
          int idx = 60000 + targetIndex * maxInner + inner;
          String oldSolid = solidSol;
          String oldRelaxed = relaxedSol;
          String pressureSol = buildTff(model, comp, historyPressure, solidSol, idx);
          model.sol(pressureSol).runAll();
          String tffData = dataset(model, "dset576n_tff", pressureSol);
          double fFilm = lastIntegral(model, tffData, "int576n_film", "p_load573");
          double minTheta = lastSurface(model, tffData, "min576n_theta",
              "MinSurface", "tff.theta");
          double maxP = lastSurface(model, tffData, "max576n_p",
              "MaxSurface", "tff.p-p_amb573");

          String nextRelaxed = buildRelaxation(model, comp, pressureSol, relaxedSol, idx);
          model.sol(nextRelaxed).runAll();
          relaxedSol = nextRelaxed;
          String relaxedData = dataset(model, "dset576n_relax", relaxedSol);
          double fFeedback = lastIntegral(model, relaxedData, "int576n_feedback",
              "p_scale576m*rrel576m");
          double fieldResidual = lastIntegral(model, relaxedData, "int576n_residual",
              "abs(p_scale576m*rrel576m-alpha_pfb576m*withsol('"
              + pressureSol + "',p_load573))");

          setStructuralLoad(comp, relaxedSol);
          String nextSolid = buildSolid(model, comp, solidSol, idx);
          model.sol(nextSolid).runAll();
          solidSol = nextSolid;
          String solidData = dataset(model, "dset576n_solid", solidSol);
          double fContact = lastGlobal(model, solidData, "eval576n_contact", "Fn_contact570");
          double minGap = lastSurface(model, solidData, "min576n_gap",
              "MinSurface", "geomgap_dst_cp_lid_cornea");
          double fTotal = fContact + fFilm;
          double dContact = Double.isFinite(prevContact) ? Math.abs(fContact-prevContact) : Double.POSITIVE_INFINITY;
          double dFilm = Double.isFinite(prevFilm) ? Math.abs(fFilm-prevFilm) : Double.POSITIVE_INFINITY;
          double dFeedback = Double.isFinite(prevFeedback) ? Math.abs(fFeedback-prevFeedback) : Double.POSITIVE_INFINITY;
          double dTotal = Double.isFinite(prevTotal) ? Math.abs(fTotal-prevTotal) : Double.POSITIVE_INFINITY;
          double dGap = Double.isFinite(prevGap) ? Math.abs(minGap-prevGap) : Double.POSITIVE_INFINITY;
          boolean finite = Double.isFinite(fContact) && Double.isFinite(fFilm)
              && Double.isFinite(fFeedback) && Double.isFinite(maxP)
              && Double.isFinite(minGap) && minTheta >= -1e-8;
          stepConverged = inner >= 2 && finite && dContact < 1e-5 && dFilm < 1e-5
              && dFeedback < 1e-5 && dTotal < 1e-5 && dGap < 1e-8
              && fieldResidual < 1e-5;
          usedInner = inner + 1;
          acceptedPressure = pressureSol;
          acceptedContact = fContact; acceptedFilm = fFilm;
          acceptedFeedback = fFeedback; acceptedTotal = fTotal;
          acceptedResidual = fieldResidual; acceptedTheta = minTheta;
          acceptedMaxP = maxP; acceptedGap = minGap;
          System.out.printf(Locale.US,
              "INNER target=%d/%d fraction=%.6f iter=%d Fcontact=%.12g Ffilm=%.12g Ffeedback=%.12g Ftotal=%.12g FieldResidual=%.12g dTotal=%.12g dGap=%.12g converged=%s%n",
              targetIndex+1, targets.size(), f1, inner+1, fContact, fFilm, fFeedback, fTotal,
              fieldResidual, dTotal, dGap, stepConverged ? "YES" : "NO");
          removeSolution(model, oldSolid, snapshots);
          removeSolution(model, oldRelaxed, snapshots);
          if (stepConverged) break;
          removeSolution(model, pressureSol, snapshots);
          prevContact=fContact; prevFilm=fFilm; prevFeedback=fFeedback;
          prevTotal=fTotal; prevGap=minGap;
        }

        stableAll = stableAll && stepConverged;
        if (!stepConverged) { failedTarget = targetIndex + 1; break; }
        removeSolution(model, historyPressure, snapshots);
        historyPressure = acceptedPressure;
        if (targetIndex + 1 == targets.size()) {
          snapshots.add(historyPressure); snapshots.add(solidSol); snapshots.add(relaxedSol);
        }
        minTotal = Math.min(minTotal, acceptedTotal);
        maxTotal = Math.max(maxTotal, acceptedTotal);
        if (acceptedTotal < 0.025 || acceptedTotal > 0.035) outside++;
        System.out.printf(Locale.US,
            "TARGET_SUMMARY target=%d/%d fraction=%.6f inner=%d Fcontact=%.12g Ffilm=%.12g Ffeedback=%.12g Ftotal=%.12g residual=%.12g MinTheta=%.12g MaxP=%.12g MinGap=%.12g%n",
            targetIndex+1, targets.size(), f1, usedInner, acceptedContact, acceptedFilm,
            acceptedFeedback, acceptedTotal, acceptedResidual, acceptedTheta,
            acceptedMaxP, acceptedGap);
        // This rolling file always contains the last fully converged physical step.
        model.save(CHECKPOINT);
        f0 = f1;
      }

      boolean pass = stableAll && failedTarget < 0 && outside == 0;
      model.label(pass ? "Stage 576n full dynamic recursive checked" : "Stage 576n full dynamic recursive failed");
      model.save(RESULTS);
      System.out.printf(Locale.US,
          "SUMMARY targets=%d stable=%s failedTarget=%d outside=%d FtotalRange=[%.12g,%.12g]%n",
          targets.size(), stableAll ? "PASS" : "FAIL", failedTarget, outside, minTotal, maxTotal);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) model.save(CHECKED);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
