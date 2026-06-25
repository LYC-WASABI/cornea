import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576s_early_feedback_piecewise {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String SETUP =
      "576s_stage576_early_feedback_piecewise_setup.mph";
  private static final String CHECKPOINT =
      "576s_stage576_early_feedback_piecewise_checkpoint.mph";
  private static final String RESULTS =
      "576s_stage576_early_feedback_piecewise_results.mph";
  private static final String SWEPT = "sel_film_swept571";
  private static final double TARGET = 0.03;

  private static boolean has(String[] values, String value) {
    for (String candidate : values) {
      if (candidate.equals(value)) return true;
    }
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) latest = tag;
    }
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

  private static double lastGlobal(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double lastSurface(
      Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double lastIntegral(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
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

  private static void configure(Model model) {
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

  private static void setFeedback(ModelNode comp, String pressureSol) {
    String vars = "var_feedback576s";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set(
        "p_feedback576s",
        "alpha_pfb576s*withsol('" + pressureSol + "',max(p_load573,0[Pa]))");
    String load = "load_pfilm576s";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576s early film-pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576s*nx", "-p_feedback576s*ny", "-p_feedback576s*nz"
    });
  }

  private static String buildTffSegment(
      Model model, ModelNode comp, String pressureInit, String solidState, int index) {
    String study = "std576s_tff_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576s early-feedback TFF segment " + index);
    model.study(study).create("time", "Transient");
    String tlist = "range(t0_576s,dt_576s/4,t1_576s)";
    model.study(study).feature("time").set("tlist", tlist);
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
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
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", pressureInit);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", solidState);
    dependent.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", tlist);
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "dt_576s/200"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_576s/10"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) time.create("fc1", "FullyCoupled");
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return solution;
  }

  private static String buildSolidStep(
      Model model, ModelNode comp, String initSolid, int index) {
    String study = "std576s_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576s early-feedback solid step " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSolid);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String feature : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576s"}) {
      try { comp.physics("solid").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", initSolid);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", initSolid);
    dependent.set("notsolnum", "last");
    SolverFeature stat = model.sol(solution).feature("s1");
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) {
        try { stat.feature().remove(tag); } catch (Exception ignored) {}
      }
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.05");
    stat.feature("fc1").set("maxiter", 400);
    return solution;
  }

  private static void logStep(
      Model model, String pressureSol, String solidSol, double fraction) {
    String pressureData = dataset(model, "dset576s_p_" + ((int) Math.round(1000 * fraction)),
        pressureSol);
    String solidData = dataset(model, "dset576s_s_" + ((int) Math.round(1000 * fraction)),
        solidSol);
    double fFilm = lastIntegral(model, pressureData, "int576s_film", "max(p_load573,0[Pa])");
    double positive = lastIntegral(model, pressureData, "int576s_positive", "max(tff.p-p_amb573,0[Pa])");
    double yLoad = lastIntegral(model, pressureData, "int576s_y", "max(tff.p-p_amb573,0[Pa])*Y");
    double coreMass = lastIntegral(model, pressureData, "int576s_core", "M_core573");
    double coreYInt = lastIntegral(model, pressureData, "int576s_corey", "M_core573*Y");
    double maxP = lastSurface(model, pressureData, "max576s_p", "MaxSurface", "tff.p-p_amb573");
    double minTheta = lastSurface(model, pressureData, "min576s_theta", "MinSurface", "tff.theta");
    double minGap = lastSurface(model, solidData, "min576s_gap", "MinSurface", "geomgap_dst_cp_lid_cornea");
    double gapValidNum = lastIntegral(model, pressureData, "int576s_gapvalid_num", "g_pair_valid573");
    double gapValidDen = lastIntegral(model, pressureData, "int576s_gapvalid_den", "1");
    double gapValid = Math.abs(gapValidDen) > 1e-30 ? gapValidNum / gapValidDen : Double.NaN;
    double fContact = lastGlobal(model, solidData, "eval576s_contact", "Fn_contact570");
    double fTotal = fContact + fFilm;
    double pressureY = Math.abs(positive) > 1e-30 ? yLoad / positive : Double.NaN;
    double coreY = Math.abs(coreMass) > 1e-30 ? coreYInt / coreMass : Double.NaN;
    System.out.printf(Locale.US,
        "ROW576S fraction=%.3f Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g MinGap=%.12g GapValid=%.12g coreY=%.12g pressureY=%.12g solid=%s tff=%s%n",
        fraction, fContact, fFilm, fTotal, fTotal - TARGET, maxP, minTheta, minGap,
        gapValid, coreY, pressureY, solidSol, pressureSol);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      ModelNode comp = model.component("comp1");
      configure(model);
      model.param().set("alpha_pfb576s", "0.02");
      model.label("Stage 576s early-feedback piecewise setup");
      model.save(SETUP);

      double[] fractions = new double[] {0.05, 0.10, 0.15, 0.20, 0.25};
      String pressureSol = "sol142";
      String solidSol = "sol143";
      double previous = 0.0;

      for (int i = 0; i < fractions.length; i++) {
        double fraction = fractions[i];
        model.param().set("t_position576p2",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", fraction));
        model.param().set("t0_576s",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", previous));
        model.param().set("t1_576s",
            String.format(Locale.US, "T_pre572+%.12g*T_slide572", fraction));
        model.param().set("dt_576s",
            String.format(Locale.US, "%.12g*T_slide572", fraction - previous));

        String nextPressure = buildTffSegment(model, comp, pressureSol, solidSol, i);
        System.out.println("RUN576S_TFF index=" + i + " fraction=" + fraction
            + " pressureInit=" + pressureSol + " solidInit=" + solidSol
            + " solution=" + nextPressure);
        model.sol(nextPressure).runAll();
        pressureSol = nextPressure;

        setFeedback(comp, pressureSol);
        String nextSolid = buildSolidStep(model, comp, solidSol, i);
        System.out.println("RUN576S_SOLID index=" + i + " fraction=" + fraction
            + " solidInit=" + solidSol + " pressure=" + pressureSol
            + " solution=" + nextSolid);
        model.sol(nextSolid).runAll();
        solidSol = nextSolid;

        logStep(model, pressureSol, solidSol, fraction);
        previous = fraction;
        model.save(CHECKPOINT);
      }

      model.label("Stage 576s early-feedback piecewise results");
      model.save(RESULTS);
      System.out.println("STATUS576S=COMPLETE");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
