import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576a_active_time_feedback_closure {
  private static final String BASE = "576a_stage576_active_time_feedback_closure_branch3_results.mph";
  private static final String SETUP = "576a_stage576_active_time_feedback_closure_branch5_setup.mph";
  private static final String RESULTS = "576a_stage576_active_time_feedback_closure_branch5_results.mph";
  private static final String CHECKED = "576a_stage576_active_time_feedback_closure_checked.mph";
  private static final String INIT_SOLID = "sol441";
  private static final String INIT_TFF = "sol460";
  private static final String T_ACTIVE = "0.0690828858385121[s]";
  private static final double TARGET = 0.03;
  private static final double[] ALPHAS = new double[] {0.1832, 0.1834, 0.1836};
  private static final double[] VELOCITY = new double[] {
    0, 1e-4, 1e-3, 0.005, 0.01, 0.02, 0.05, 0.075, 0.1,
    0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5, 0.65, 0.8, 1.0
  };
  private static final List<double[]> rows = new ArrayList<>();
  private static final List<String> failures = new ArrayList<>();

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

  private static void configureActiveTime(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("t_active576a", T_ACTIVE);
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "0");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    try { comp.variable("var_dynamic_motion572").set("tau572", "t_active576a"); } catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "M_lid_x572*M_drain_a573");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "max(1-M_drain573,0)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_low573", "0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_high573", "0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Bfilm573", "g_pair_valid573*B_low573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "g_pair_physical573", "min(g_pair_safe573,h_active_max573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_wet573",
          "h_num573+0.5*((g_pair_physical573-h_num573)"
              + "+sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Afilm573",
          "M_core573*Bfilm573+max(M_drain573-M_core573,0)*g_pair_valid573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_calc573", "Afilm573*h_wet573+(1-Afilm573)*h_background573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Qvent573", "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");
    } catch (Exception error) {
      System.out.println("ACTIVE_GAP_CONFIG_FAILED=" + error.getMessage());
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
    try {
      PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
      contact.set("pairSelection", "list");
      contact.set("pairs", new String[] {"cp_lid_cornea"});
      for (String childTag : contact.feature().tags()) {
        PhysicsFeature child = contact.feature(childTag);
        String type = child.getType();
        String label = child.label();
        if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) child.active(false);
      }
    } catch (Exception ignored) {}
  }

  private static void setFeedbackLoad(Model model, ModelNode comp, String sourceTffSol, double alpha) {
    model.param().set("alpha_pfb576a", String.format(Locale.US, "%.12g", alpha));
    String vars = "var_pressure_feedback576a";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback576a",
        "alpha_pfb576a*withsol('" + sourceTffSol + "',p_load573)");

    String load = "load_pfilm576a";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576a one-way active-time film pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576a*nx",
      "-p_feedback576a*ny",
      "-p_feedback576a*nz"
    });
  }

  private static String buildSolidStudy(Model model, String initSol, int index) {
    String study = "std576a_branch5_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576a branch5 feedback solid " + index);
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
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576a"}) {
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

  private static String buildTffStudy(Model model, String initSol, int index, int vIndex) {
    String study = "std576a_branch5_tff_" + index + "_" + vIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576a branch5 TFF " + index + " velocity " + vIndex);
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
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
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

  private static String runTffVelocity(Model model, String initSol, int index) {
    String current = initSol;
    for (int i = 0; i < VELOCITY.length; i++) {
      double lv = VELOCITY[i];
      model.param().set("lambda_h574", "1");
      model.param().set("lambda_v574", String.format(Locale.US, "%.12g", lv));
      String sol = buildTffStudy(model, current, index, i);
      System.out.println("RUN_TFF index=" + index + " lv=" + lv + " init=" + current + " sol=" + sol);
      model.sol(sol).runAll();
      current = sol;
    }
    return current;
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
    String solidData = dataset(model, "dset576a_active_solid_" + index, solidSol);
    String tffData = dataset(model, "dset576a_active_tff_" + index, tffSol);
    double[] solid = evalGlobal(model, solidData, "eval576a_active_solid_" + index, new String[] {"Fn_contact570"});
    double[] patch = intPatch(model, tffData, "int576a_active_tff_" + index,
        new String[] {"p_load573", "max(tff.p-p_amb573,0[Pa])", "Bfilm573", "B_high573", "M_core573", "tff.theta", "1"});
    double area = patch[6];
    double filmLoad = patch[0];
    double positiveLoad = patch[1];
    double meanB = patch[2] / area;
    double meanBHigh = patch[3] / area;
    double meanCore = patch[4] / area;
    double meanTheta = patch[5] / area;
    double total = solid[0] + filmLoad;
    double maxP = surface(model, tffData, "max576a_active_p_" + index, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, tffData, "min576a_active_theta_" + index, "MinSurface", "tff.theta");
    double minGap = surface(model, solidData, "min576a_active_gap_" + index, "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxH = surface(model, tffData, "max576a_active_h_" + index, "MaxSurface", "h_calc573");
    System.out.printf(Locale.US,
        "ROW alpha=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g PosLoad=%.12g MaxP=%.12g MeanB=%.12g MeanBHigh=%.12g MeanCore=%.12g MeanTheta=%.12g MinTheta=%.12g MinGap=%.12g MaxH=%.12g solid=%s tff=%s%n",
        alpha, solid[0], filmLoad, total, positiveLoad, maxP, meanB, meanBHigh, meanCore, meanTheta, minTheta, minGap, maxH, solidSol, tffSol);
    return new double[] {alpha, solid[0], filmLoad, total, positiveLoad, maxP, meanB, meanBHigh, meanCore, meanTheta, minTheta, minGap, maxH};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOLID)) throw new IllegalStateException("Missing initial solid solution: " + INIT_SOLID);
      if (!INIT_TFF.isEmpty() && !has(model.sol().tags(), INIT_TFF)) {
        throw new IllegalStateException("Missing initial TFF solution: " + INIT_TFF);
      }
      configureActiveTime(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOLID=" + INIT_SOLID);
      if (!INIT_TFF.isEmpty()) System.out.println("INIT_TFF=" + INIT_TFF);
      System.out.println("T_ACTIVE=" + T_ACTIVE);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      model.label("Stage 576a active-time feedback closure refined setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      String sourceTff = INIT_TFF.isEmpty() ? runTffVelocity(model, INIT_SOLID, 0) : INIT_TFF;
      System.out.println("SOURCE_TFF=" + sourceTff);
      rows.add(evaluate(model, INIT_SOLID, sourceTff, 0, INIT_TFF.isEmpty() ? 0.0 : 0.182));
      model.save(RESULTS);

      String solidInit = INIT_SOLID;
      for (int i = 0; i < ALPHAS.length; i++) {
        double alpha = ALPHAS[i];
        int rowIndex = i + 1;
        try {
          setFeedbackLoad(model, comp, sourceTff, alpha);
          String solidSol = buildSolidStudy(model, solidInit, rowIndex);
          System.out.println("RUN_SOLID alpha=" + alpha + " init=" + solidInit + " sourceTff=" + sourceTff + " sol=" + solidSol);
          model.sol(solidSol).runAll();
          model.save(RESULTS);
          String tffSol = runTffVelocity(model, solidSol, rowIndex);
          rows.add(evaluate(model, solidSol, tffSol, rowIndex, alpha));
          solidInit = solidSol;
          sourceTff = tffSol;
          model.save(RESULTS);
        } catch (Exception error) {
          failures.add("alpha=" + alpha + " failed: " + error.toString());
          System.out.println("ALPHA_FAILED=" + alpha + " ERROR=" + error);
          model.save(RESULTS);
          break;
        }
      }

      double[] best = null;
      for (double[] row : rows) {
        if (best == null || Math.abs(row[3] - TARGET) < Math.abs(best[3] - TARGET)) best = row;
      }
      boolean pass = best != null
          && Math.abs(best[3] - TARGET) <= 0.005
          && best[2] > 1e-6
          && best[8] > 0.1
          && best[10] >= -1e-8
          && Double.isFinite(best[3])
          && Double.isFinite(best[5]);
      if (best != null) {
        System.out.println("BEST_ALPHA=" + best[0]);
        System.out.println("BEST_FCONTACT=" + best[1]);
        System.out.println("BEST_FFILM=" + best[2]);
        System.out.println("BEST_FTOTAL=" + best[3]);
        System.out.println("BEST_ERROR=" + (best[3] - TARGET));
        System.out.println("BEST_MAXP=" + best[5]);
        System.out.println("BEST_MINTHETA=" + best[10]);
        System.out.println("BEST_MEANCORE=" + best[8]);
      }
      for (String failure : failures) System.out.println("FAILURE_NOTE=" + failure);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
      if (pass) {
        model.label("Stage 576a active-time feedback closure checked");
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
