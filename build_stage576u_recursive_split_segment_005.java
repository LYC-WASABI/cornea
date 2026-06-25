import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576u_recursive_split_segment_005 {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String SETUP =
      "576u_stage576_recursive_split005_setup.mph";
  private static final String CHECKPOINT =
      "576u_stage576_recursive_split005_checkpoint.mph";
  private static final String RESULTS =
      "576u_stage576_recursive_split005_results.mph";
  private static final String CHECKED =
      "576u_stage576_recursive_split005_checked.mph";
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
    if (has(comp.physics().tags(), "bode576u")) return;
    try { comp.physics().remove("bode576u"); } catch (Exception ignored) {}
    comp.physics().create("bode576u", "BoundaryODE", "geom1");
    Physics bode = comp.physics("bode576u");
    bode.label("Stage 576u recursive relaxed film-pressure field");
    bode.field("dimensionless").field("rrel576u");
    bode.field("dimensionless").component(new String[] {"rrel576u"});
    bode.selection().named(PATCH);
    try { bode.feature("dode1").selection().named(PATCH); } catch (Exception ignored) {}
    try { bode.feature("init1").selection().named(PATCH); } catch (Exception ignored) {}
    bode.feature("dode1").set("da", "tau_relax576u");
    bode.feature("dode1").set("f", "-rrel576u");
    bode.feature("init1").set("rrel576u", "0");
  }

  private static String buildTff(Model model, ModelNode comp, String pressureInit, String solidState, int index) {
    String study = "std576u_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576u recursive TFF " + index);
    model.study(study).create("time", "Transient");
    String tlist = "range(t0_576u,dt_576u/4,t1_576u)";
    model.study(study).feature("time").set("tlist", tlist);
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on", "bode576u", "off",
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
    try { time.set("initialstepbdf", "dt_576u/200"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_576u/10"); } catch (Exception ignored) {}
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
    comp.physics("bode576u").feature("dode1").set(
        "f", "alpha_pfb576u*withsol('" + pressureSol + "',max(p_load573,0[Pa]))/p_scale576u-rrel576u");
    String study = "std576u_relax_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576u recursive relaxation " + index);
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set("tlist", "range(0,dt_relax576u/5,dt_relax576u)");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "off", "bode576u", "on",
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
    for (String feature : comp.physics("bode576u").feature().tags()) {
      try { comp.physics("bode576u").feature(feature).set("StudyStep", step); }
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
    time.set("tlist", "range(0,dt_relax576u/5,dt_relax576u)");
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "dt_relax576u/100"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_relax576u/5"); } catch (Exception ignored) {}
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
    String vars = "var_feedback576u";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(PATCH);
    comp.variable(vars).set("p_feedback576u", "withsol('" + relaxedSol + "',p_scale576u*rrel576u)");
    String load = "load_pfilm576u";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576u recursively relaxed film pressure");
    comp.physics("solid").feature(load).selection().named(PATCH);
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576u*nx", "-p_feedback576u*ny", "-p_feedback576u*nz"
    });
  }

  private static String buildSolid(Model model, ModelNode comp, String initSol, int index) {
    String study = "std576u_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576u recursive solid " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off", "bode576u", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String feature : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576u"}) {
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
      model.param().set("alpha_pfb576u", "0.15");
      model.param().set("beta_relax576u", "0.15");
      model.param().set("p_scale576u", "1[MPa]");
      model.param().set("tau_relax576u", "1[s]");
      model.param().set("dt_relax576u", "-log(1-beta_relax576u)*tau_relax576u");
      model.label("Stage 576u recursive split 0-2.5%-5% setup");
      model.save(SETUP);

      double[] segmentEnds = new double[] {0.025, 0.05};
      String pressureSol = INITIAL_PRESSURE;
      String solidSol = INITIAL_SOLID;
      String relaxedSol = null;
      double segStart = 0.0;
      boolean allSegmentsConverged = true;
      double finalFtotal = Double.NaN;

      for (int s = 0; s < segmentEnds.length; s++) {
        double segEnd = segmentEnds[s];
        model.param().set("t_position576p2",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", segEnd));
        model.param().set("t0_576u",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", segStart));
        model.param().set("t1_576u",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", segEnd));
        model.param().set("dt_576u",
            String.format(Locale.US, "%.12g*T_slide572", segEnd - segStart));

        double prevTotal = Double.NaN;
        double prevGap = Double.NaN;
        boolean segConverged = false;

        for (int iter = 0; iter < 6; iter++) {
          int idx = 76400 + s * 100 + iter;
          String nextPressure = buildTff(model, comp, pressureSol, solidSol, idx);
          System.out.println("RUN576U_TFF segment=" + (s + 1) + " end=" + segEnd
              + " iter=" + (iter + 1) + " pressureInit=" + pressureSol
              + " solidInit=" + solidSol + " solution=" + nextPressure);
          model.sol(nextPressure).runAll();
          pressureSol = nextPressure;
          String pressureData = dataset(model, "dset576u_p_" + idx, pressureSol);
          double fFilm = lastIntegral(model, pressureData, "int576u_film_" + idx, "max(p_load573,0[Pa])", SWEPT);
          double maxP = lastSurface(model, pressureData, "max576u_p_" + idx, "MaxSurface", "tff.p-p_amb573", SWEPT);
          double minTheta = lastSurface(model, pressureData, "min576u_theta_" + idx, "MinSurface", "tff.theta", SWEPT);

          String nextRelaxed = buildRelaxation(model, comp, pressureSol, relaxedSol, idx);
          System.out.println("RUN576U_RELAX segment=" + (s + 1) + " end=" + segEnd
              + " iter=" + (iter + 1) + " pressure=" + pressureSol
              + " previousRelaxed=" + relaxedSol + " solution=" + nextRelaxed);
          model.sol(nextRelaxed).runAll();
          relaxedSol = nextRelaxed;
          String relaxData = dataset(model, "dset576u_r_" + idx, relaxedSol);
          double fFeedback = lastIntegral(model, relaxData, "int576u_feedback_" + idx, "p_scale576u*rrel576u", PATCH);
          double residual = lastIntegral(model, relaxData, "int576u_residual_" + idx,
              "abs(p_scale576u*rrel576u-alpha_pfb576u*withsol('" + pressureSol + "',max(p_load573,0[Pa])))", PATCH);

          setStructuralLoad(comp, relaxedSol);
          String nextSolid = buildSolid(model, comp, solidSol, idx);
          System.out.println("RUN576U_SOLID segment=" + (s + 1) + " end=" + segEnd
              + " iter=" + (iter + 1) + " solidInit=" + solidSol
              + " relaxed=" + relaxedSol + " solution=" + nextSolid);
          model.sol(nextSolid).runAll();
          solidSol = nextSolid;
          String solidData = dataset(model, "dset576u_s_" + idx, solidSol);
          double fContact = lastGlobal(model, solidData, "eval576u_contact_" + idx, "Fn_contact570");
          double minGap = lastSurface(model, solidData, "min576u_gap_" + idx, "MinSurface", "geomgap_dst_cp_lid_cornea", PATCH);
          double fTotal = fContact + fFilm;
          double dTotal = Double.isFinite(prevTotal) ? Math.abs(fTotal - prevTotal) : Double.POSITIVE_INFINITY;
          double dGap = Double.isFinite(prevGap) ? Math.abs(minGap - prevGap) : Double.POSITIVE_INFINITY;

          System.out.printf(Locale.US,
              "ROW576U segment=%d end=%.3f iter=%d Fcontact=%.12g Ffilm=%.12g Ffeedback=%.12g Ftotal=%.12g error=%.12g Residual=%.12g dTotal=%.12g MaxP=%.12g MinTheta=%.12g MinGap=%.12g dGap=%.12g solid=%s tff=%s relax=%s%n",
              s + 1, segEnd, iter + 1, fContact, fFilm, fFeedback, fTotal, fTotal - TARGET,
              residual, dTotal, maxP, minTheta, minGap, dGap, solidSol, pressureSol, relaxedSol);
          model.save(CHECKPOINT);

          finalFtotal = fTotal;
          segConverged = iter >= 2
              && Double.isFinite(fContact)
              && Double.isFinite(fFilm)
              && Double.isFinite(fFeedback)
              && Double.isFinite(maxP)
              && Double.isFinite(minGap)
              && minTheta >= -1e-8
              && dTotal < 5e-3
              && dGap < 1e-6
              && residual < 2e-3;
          prevTotal = fTotal;
          prevGap = minGap;
          if (segConverged) break;
        }

        System.out.println("SEGMENT576U_SUMMARY segment=" + (s + 1)
            + " end=" + segEnd + " converged=" + (segConverged ? "PASS" : "FAIL")
            + " Ftotal=" + finalFtotal);
        allSegmentsConverged = allSegmentsConverged && segConverged;
        if (!segConverged) break;
        segStart = segEnd;
      }

      model.label(allSegmentsConverged
          ? "Stage 576u recursive split 0-2.5%-5% checked"
          : "Stage 576u recursive split 0-2.5%-5% failed");
      model.save(RESULTS);
      System.out.println("CHECKED_STATUS=" + (allSegmentsConverged ? "PASS" : "FAIL"));
      if (allSegmentsConverged && finalFtotal <= 0.045) model.save(CHECKED);
      ModelUtil.disconnect();
      if (!allSegmentsConverged) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
