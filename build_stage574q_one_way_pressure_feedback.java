import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574q_one_way_pressure_feedback {
  private static final String BASE = "574o_stage574_fixed_structure_true_gap_from_003N_checked.mph";
  private static final String SETUP = "574q_stage574_one_way_pressure_feedback_setup.mph";
  private static final String RESULTS = "574q_stage574_one_way_pressure_feedback_results.mph";
  private static final String CHECKED = "574q_stage574_one_way_pressure_feedback_checked.mph";
  private static final double TARGET = 0.03;
  private static final double[] ALPHAS = new double[] {0.12, 0.13};
  private static final double[] VELOCITY = new double[] {
    0, 1e-4, 1e-3, 0.005, 0.01, 0.02, 0.05, 0.075, 0.1,
    0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5, 0.65, 0.8, 1.0
  };
  private static final List<double[]> rows = new ArrayList<>();

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
    model.param().set("q_scale574", "-7.5");
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("time_offset572", "T_pre572+0.5*T_slide572");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "0");
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
    try { comp.variable("var_dynamic_motion572").set("tau572", "time_offset572"); }
    catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "0");
    } catch (Exception ignored) {}

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "(1-lambda_h574)*3[um]+lambda_h574*h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0",
      "-lambda_v574*omega_lid_rot572*Z",
      "lambda_v574*omega_lid_rot572*Y"
    });
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "lambda_h574*Qvent573"); }
    catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}

    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    contact.set("pairSelection", "list");
    contact.set("pairs", new String[] {"cp_lid_cornea"});
    for (String childTag : contact.feature().tags()) {
      PhysicsFeature child = contact.feature(childTag);
      String type = child.getType();
      String label = child.label();
      if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) {
        child.active(false);
      }
    }
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
  }

  private static void setFeedbackLoad(Model model, ModelNode comp, String sourceTffSol, double alpha) {
    model.param().set("alpha_pfb574q", String.format(Locale.US, "%.12g", alpha));
    String vars = "var_pressure_feedback574q";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback574q",
        "alpha_pfb574q*withsol('" + sourceTffSol + "',p_load573)");

    String load = "load_pfilm574q";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 574q one-way film pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback574q*nx",
      "-p_feedback574q*ny",
      "-p_feedback574q*nz"
    });
  }

  private static String buildSolidStudy(Model model, String initSol, int index) {
    String study = "std574q_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574q one-way pressure feedback solid " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "on", "ge_force_total111", "off", "tff", "off",
          "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm574q"}) {
      try { comp.physics("solid").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
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

  private static String buildTffStudy(Model model, String initSol, int index, int vIndex) {
    String study = "std574q_tff_" + index + "_" + vIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574q TFF after feedback " + index + " velocity " + vIndex);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "off", "ge_force_total111", "off", "tff", "on",
          "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
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
    stat.feature("fc1").set("damp", "0.3");
    stat.feature("fc1").set("maxiter", 300);
    return sol;
  }

  private static String dataset(Model model, String tag, String sol) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", sol);
    return tag;
  }

  private static double[] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static double[] intPatch(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static double surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double[] evaluate(Model model, String solidSol, String tffSol, int index, double alpha) {
    String solidData = dataset(model, "dset574q_solid_" + index, solidSol);
    String tffData = dataset(model, "dset574q_tff_" + index, tffSol);
    double[] solid = evalGlobal(model, solidData, "eval574q_solid_" + index,
        new String[] {"Fn_contact570"});
    double[] feedback = intPatch(model, solidData, "int574q_feedback_" + index,
        new String[] {"p_feedback574q"});
    double[] patch = intPatch(model, tffData, "int574q_tff_" + index,
        new String[] {"p_load573", "max(tff.p-p_amb573,0[Pa])", "Bfilm573", "tff.theta", "1"});
    double area = patch[4];
    double filmLoad = patch[0];
    double positiveLoad = patch[1];
    double meanB = patch[2] / area;
    double meanTheta = patch[3] / area;
    double total = solid[0] + filmLoad;
    double maxP = surface(model, tffData, "max574q_p_" + index, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, tffData, "min574q_theta_" + index, "MinSurface", "tff.theta");
    double minGap = surface(model, solidData, "min574q_gap_" + index, "MinSurface", "geomgap_dst_cp_lid_cornea");
    System.out.printf(Locale.US,
        "ROW alpha=%.12g Fcontact=%.12g Ffeedback=%.12g Ffilm=%.12g Ftotal=%.12g PosLoad=%.12g MaxP=%.12g MeanB=%.12g MeanTheta=%.12g MinTheta=%.12g MinGap=%.12g solid=%s tff=%s%n",
        alpha, solid[0], feedback[0], filmLoad, total, positiveLoad, maxP, meanB, meanTheta, minTheta, minGap, solidSol, tffSol);
    return new double[] {alpha, solid[0], feedback[0], filmLoad, total, positiveLoad, maxP, meanB, meanTheta, minTheta, minGap};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      configure(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      String solidInit = has(model.sol().tags(), "sol99") ? "sol99" : "sol138";
      String sourceTff = has(model.sol().tags(), "sol138") ? "sol138" : solidInit;
      System.out.println("INITIAL_SOLID=" + solidInit);
      System.out.println("INITIAL_TFF_PRESSURE_SOURCE=" + sourceTff);
      model.label("Stage 574q one-way pressure feedback setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      for (int i = 0; i < ALPHAS.length; i++) {
        double alpha = ALPHAS[i];
        setFeedbackLoad(model, comp, sourceTff, alpha);
        String solidSol = buildSolidStudy(model, solidInit, i);
        System.out.println("RUN_SOLID alpha=" + alpha + " init=" + solidInit + " sourceTff=" + sourceTff + " sol=" + solidSol);
        model.sol(solidSol).runAll();
        model.save(RESULTS);
        String tffInit = solidSol;
        String tffSol = null;
        for (int v = 0; v < VELOCITY.length; v++) {
          double lv = VELOCITY[v];
          model.param().set("lambda_v574", String.format(Locale.US, "%.12g", lv));
          model.param().set("lambda_h574", "1");
          tffSol = buildTffStudy(model, tffInit, i, v);
          System.out.println("RUN_TFF alpha=" + alpha + " lv=" + lv + " init=" + tffInit + " sol=" + tffSol);
          model.sol(tffSol).runAll();
          model.save(RESULTS);
          tffInit = tffSol;
        }
        rows.add(evaluate(model, solidSol, tffSol, i, alpha));
        solidInit = solidSol;
        sourceTff = tffSol;
        model.save(RESULTS);
        System.out.println("SAVED_RESULTS_STEP=" + RESULTS);
      }

      double[] best = null;
      for (double[] row : rows) if (best == null || Math.abs(row[4] - TARGET) < Math.abs(best[4] - TARGET)) best = row;
      boolean pass = best != null && Math.abs(best[4] - TARGET) <= 0.005
          && best[9] >= -1e-8 && Double.isFinite(best[4]);
      if (best != null) {
        System.out.println("BEST_ALPHA=" + best[0]);
        System.out.println("BEST_FCONTACT=" + best[1]);
        System.out.println("BEST_FFILM=" + best[3]);
        System.out.println("BEST_FTOTAL=" + best[4]);
        System.out.println("BEST_ERROR=" + (best[4] - TARGET));
      }
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      if (pass) {
        model.label("Stage 574q one-way pressure feedback checked");
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
