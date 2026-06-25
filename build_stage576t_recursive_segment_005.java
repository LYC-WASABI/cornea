import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576t_recursive_segment_005 {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String SETUP =
      "576t_stage576_recursive_segment005_setup.mph";
  private static final String CHECKPOINT =
      "576t_stage576_recursive_segment005_checkpoint.mph";
  private static final String RESULTS =
      "576t_stage576_recursive_segment005_results.mph";
  private static final String CHECKED =
      "576t_stage576_recursive_segment005_checked.mph";
  private static final String SWEPT = "sel_film_swept571";
  private static final String PATCH = "sel_local_cornea_patch574";
  private static final String INITIAL_PRESSURE = "sol142";
  private static final String INITIAL_SOLID = "sol143";
  private static final double TARGET = 0.03;

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

  private static double lastIntegral(Model model, String data, String tag, String expr, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double lastSurface(
      Model model, String data, String tag, String type, String expr, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static void deactivateLegacyFeedback(ModelNode comp) {
    for (String feature : comp.physics("solid").feature().tags()) {
      if (feature.startsWith("load_pfilm576")) {
        try { comp.physics("solid").feature(feature).active(false); }
        catch (Exception ignored) {}
      }
    }
    for (String vars : comp.variable().tags()) {
      if (vars.startsWith("var_feedback576")) {
        try { comp.variable().remove(vars); } catch (Exception ignored) {}
      }
    }
  }

  private static void configureBase(Model model) {
    ModelNode comp = model.component("comp1");
    deactivateLegacyFeedback(comp);
    comp.physics("ge_force_total111").active(false);
    comp.physics("solid").prop("StructuralTransientBehavior").set(
        "StructuralTransientBehavior", "Quasistatic");
    int[] edges = comp.physics("tff").feature("bdr1").selection().entities();
    comp.physics("tff").feature("bdr_inlet520").active(true);
    comp.physics("tff").feature("bdr_inlet520").selection().set(edges);
    comp.physics("tff").feature("bdr_outlet520").active(false);
    comp.physics("tff").feature("bdr_left520").active(false);
    comp.physics("tff").feature("bdr_right520").active(false);
    comp.physics("tff").feature("wc_open_anchor573").active(false);
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
    ffp.set("vw", new String[] {
      "0",
      "-lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Z",
      "lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Y"
    });
  }

  private static void createRelaxationPhysics(Model model, ModelNode comp) {
    if (has(comp.physics().tags(), "bode576t")) return;
    try { comp.physics().remove("bode576t"); } catch (Exception ignored) {}
    comp.physics().create("bode576t", "BoundaryODE", "geom1");
    Physics bode = comp.physics("bode576t");
    bode.label("Stage 576t recursive relaxed film-pressure field");
    bode.field("dimensionless").field("rrel576t");
    bode.field("dimensionless").component(new String[] {"rrel576t"});
    bode.selection().named(PATCH);
    try { bode.feature("dode1").selection().named(PATCH); } catch (Exception ignored) {}
    try { bode.feature("init1").selection().named(PATCH); } catch (Exception ignored) {}
    bode.feature("dode1").set("da", "tau_relax576t");
    bode.feature("dode1").set("f", "-rrel576t");
    bode.feature("init1").set("rrel576t", "0");
  }

  private static String buildTff(Model model, ModelNode comp, String pressureInit, String solidState, int index) {
    String study = "std576t_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576t recursive TFF " + index);
    model.study(study).create("time", "Transient");
    String tlist = "range(t0_576t,dt_576t/4,t1_576t)";
    model.study(study).feature("time").set("tlist", tlist);
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on", "bode576t", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", pressureInit);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", "last");
    String step = study + "/time";
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", pressureInit);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", solidState);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", tlist);
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "dt_576t/200"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_576t/10"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return solution;
  }

  private static String buildRelaxation(Model model, ModelNode comp, String pressureSol, String previousRelaxed, int index) {
    comp.physics("bode576t").feature("dode1").set(
        "f", "alpha_pfb576t*withsol('" + pressureSol + "',max(p_load573,0[Pa]))/p_scale576t-rrel576t");
    String study = "std576t_relax_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576t recursive relaxation " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set("tlist", "range(0,dt_relax576t/5,dt_relax576t)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "off", "bode576t", "on",
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
    for (String feature : comp.physics("bode576t").feature().tags()) {
      try { comp.physics("bode576t").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    if (previousRelaxed != null) {
      dep.set("initmethod", "sol");
      dep.set("initsol", previousRelaxed);
      dep.set("solnum", "last");
    }
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", "range(0,dt_relax576t/5,dt_relax576t)");
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "dt_relax576t/100"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_relax576t/5"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "1");
    time.feature("fc1").set("maxiter", 50);
    return solution;
  }

  private static void setStructuralLoad(ModelNode comp, String relaxedSol) {
    String vars = "var_feedback576t";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(PATCH);
    comp.variable(vars).set("p_feedback576t", "withsol('" + relaxedSol + "',p_scale576t*rrel576t)");
    String load = "load_pfilm576t";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576t recursively relaxed film pressure");
    comp.physics("solid").feature(load).selection().named(PATCH);
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576t*nx", "-p_feedback576t*ny", "-p_feedback576t*nz"
    });
  }

  private static String buildSolid(Model model, ModelNode comp, String initSol, int index) {
    String study = "std576t_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576t recursive solid " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off", "bode576t", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String feature : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576t"}) {
      try { comp.physics("solid").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", initSol);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", initSol);
    dep.set("notsolnum", "last");
    SolverFeature stat = model.sol(solution).feature("s1");
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.03");
    stat.feature("fc1").set("maxiter", 500);
    return solution;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      ModelNode comp = model.component("comp1");
      configureBase(model);
      createRelaxationPhysics(model, comp);
      model.param().set("alpha_pfb576t", "0.02");
      model.param().set("beta_relax576t", "0.15");
      model.param().set("p_scale576t", "1[MPa]");
      model.param().set("tau_relax576t", "1[s]");
      model.param().set("dt_relax576t", "-log(1-beta_relax576t)*tau_relax576t");
      model.param().set("t_position576p2", "T_pre572+0.05*T_slide572");
      model.param().set("t0_576t", "T_pre572");
      model.param().set("t1_576t", "T_pre572+0.05*T_slide572");
      model.param().set("dt_576t", "0.05*T_slide572");
      model.label("Stage 576t recursive segment 0-5% setup");
      model.save(SETUP);

      String pressureSol = INITIAL_PRESSURE;
      String solidSol = INITIAL_SOLID;
      String relaxedSol = null;
      boolean converged = false;
      double prevTotal = Double.NaN;
      double prevGap = Double.NaN;

      for (int iter = 0; iter < 6; iter++) {
        int idx = 76000 + iter;
        String nextPressure = buildTff(model, comp, pressureSol, solidSol, idx);
        System.out.println("RUN576T_TFF iter=" + (iter + 1) + " pressureInit=" + pressureSol
            + " solidInit=" + solidSol + " solution=" + nextPressure);
        model.sol(nextPressure).runAll();
        pressureSol = nextPressure;
        String pressureData = dataset(model, "dset576t_p_" + idx, pressureSol);
        double fFilm = lastIntegral(model, pressureData, "int576t_film_" + idx, "max(p_load573,0[Pa])", SWEPT);
        double maxP = lastSurface(model, pressureData, "max576t_p_" + idx, "MaxSurface", "tff.p-p_amb573", SWEPT);
        double minTheta = lastSurface(model, pressureData, "min576t_theta_" + idx, "MinSurface", "tff.theta", SWEPT);
        double positive = lastIntegral(model, pressureData, "int576t_positive_" + idx, "max(tff.p-p_amb573,0[Pa])", SWEPT);
        double yLoad = lastIntegral(model, pressureData, "int576t_y_" + idx, "max(tff.p-p_amb573,0[Pa])*Y", SWEPT);

        String nextRelaxed = buildRelaxation(model, comp, pressureSol, relaxedSol, idx);
        System.out.println("RUN576T_RELAX iter=" + (iter + 1) + " pressure=" + pressureSol
            + " previousRelaxed=" + relaxedSol + " solution=" + nextRelaxed);
        model.sol(nextRelaxed).runAll();
        relaxedSol = nextRelaxed;
        String relaxData = dataset(model, "dset576t_r_" + idx, relaxedSol);
        double fFeedback = lastIntegral(model, relaxData, "int576t_feedback_" + idx, "p_scale576t*rrel576t", PATCH);
        double residual = lastIntegral(model, relaxData, "int576t_residual_" + idx,
            "abs(p_scale576t*rrel576t-alpha_pfb576t*withsol('" + pressureSol + "',max(p_load573,0[Pa])))", PATCH);

        setStructuralLoad(comp, relaxedSol);
        String nextSolid = buildSolid(model, comp, solidSol, idx);
        System.out.println("RUN576T_SOLID iter=" + (iter + 1) + " solidInit=" + solidSol
            + " relaxed=" + relaxedSol + " solution=" + nextSolid);
        model.sol(nextSolid).runAll();
        solidSol = nextSolid;
        String solidData = dataset(model, "dset576t_s_" + idx, solidSol);
        double fContact = lastGlobal(model, solidData, "eval576t_contact_" + idx, "Fn_contact570");
        double minGap = lastSurface(model, solidData, "min576t_gap_" + idx, "MinSurface", "geomgap_dst_cp_lid_cornea", PATCH);
        double fTotal = fContact + fFilm;
        double dTotal = Double.isFinite(prevTotal) ? Math.abs(fTotal - prevTotal) : Double.POSITIVE_INFINITY;
        double dGap = Double.isFinite(prevGap) ? Math.abs(minGap - prevGap) : Double.POSITIVE_INFINITY;
        double pressureY = Math.abs(positive) > 1e-30 ? yLoad / positive : Double.NaN;
        System.out.printf(Locale.US,
            "ROW576T iter=%d Fcontact=%.12g Ffilm=%.12g Ffeedback=%.12g Ftotal=%.12g error=%.12g Residual=%.12g dTotal=%.12g MaxP=%.12g MinTheta=%.12g MinGap=%.12g dGap=%.12g pressureY=%.12g solid=%s tff=%s relax=%s%n",
            iter + 1, fContact, fFilm, fFeedback, fTotal, fTotal - TARGET, residual, dTotal,
            maxP, minTheta, minGap, dGap, pressureY, solidSol, pressureSol, relaxedSol);
        model.save(CHECKPOINT);

        converged = iter >= 2
            && Double.isFinite(fContact)
            && Double.isFinite(fFilm)
            && Double.isFinite(fFeedback)
            && Double.isFinite(maxP)
            && Double.isFinite(minGap)
            && minTheta >= -1e-8
            && dTotal < 5e-3
            && dGap < 1e-6
            && residual < 1e-3;
        prevTotal = fTotal;
        prevGap = minGap;
        if (converged) break;
      }

      model.label(converged
          ? "Stage 576t recursive segment 0-5% checked"
          : "Stage 576t recursive segment 0-5% failed");
      model.save(RESULTS);
      System.out.println("CHECKED_STATUS=" + (converged ? "PASS" : "FAIL"));
      if (converged) model.save(CHECKED);
      ModelUtil.disconnect();
      if (!converged) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
