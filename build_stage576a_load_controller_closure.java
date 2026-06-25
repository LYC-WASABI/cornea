import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576a_load_controller_closure {
  private static final String BASE = "575d_stage575_dynamic_active_gap_regularized_checked.mph";
  private static final String SETUP = "576a_stage576_load_controller_closure_setup.mph";
  private static final String RESULTS = "576a_stage576_load_controller_closure_results.mph";
  private static final String CHECKED = "576a_stage576_load_controller_closure_checked.mph";
  private static final String INIT_SOLID = "sol99";
  private static final String T_ACTIVE = "0.0690828858385121[s]";
  private static final double ALPHA = 0.183;
  private static final double TARGET = 0.03;
  private static final double[] Q_LIST = new double[] {-8.5, -9.5, -10.0, -11.0, -12.5, -15.0};
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

  private static void configure(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("t_active576a", T_ACTIVE);
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "0");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    model.param().set("alpha_pfb576a", String.format(Locale.US, "%.12g", ALPHA));
    try { comp.variable("var_dynamic_motion572").set("tau572", "t_active576a"); } catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "M_lid_x572*M_drain_a573");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "max(1-M_drain573,0)");
      comp.variable("var_cornea_dynamic_regions573").set("B_low573", "0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))");
      comp.variable("var_cornea_dynamic_regions573").set("B_high573", "0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))");
      comp.variable("var_cornea_dynamic_regions573").set("Bfilm573", "g_pair_valid573*B_low573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set("g_pair_physical573", "min(g_pair_safe573,h_active_max573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_wet573",
          "h_num573+0.5*((g_pair_physical573-h_num573)+sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Afilm573", "M_core573*Bfilm573+max(M_drain573-M_core573,0)*g_pair_valid573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set("h_calc573", "Afilm573*h_wet573+(1-Afilm573)*h_background573");
      comp.variable("var_cornea_dynamic_regions573").set("Qvent573", "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
      comp.variable("var_cornea_dynamic_regions573").set("p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");
    } catch (Exception error) {
      System.out.println("ACTIVE_GAP_CONFIG_FAILED=" + error.getMessage());
    }
    try {
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
            "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
          });
    } catch (Exception error) {
      System.out.println("DISP_CONFIG_FAILED=" + error.getMessage());
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

  private static void setFeedbackLoad(Model model, ModelNode comp, String sourceTffSol) {
    String vars = "var_pressure_feedback576a";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback576a",
        "alpha_pfb576a*withsol('" + sourceTffSol + "',p_load573)");
    String load = "load_pfilm576a";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576a load controller film pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576a*nx",
      "-p_feedback576a*ny",
      "-p_feedback576a*nz"
    });
  }

  private static String buildSolidStudy(Model model, String initSol, int index) {
    String study = "std576a_qctrl_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576a q-load controller solid " + index);
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
    String study = "std576a_qctrl_tff_" + index + "_" + vIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576a q-load controller TFF " + index + " velocity " + vIndex);
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

  private static double[] evaluate(Model model, String solidSol, String tffSol, int index, double q) {
    String solidData = dataset(model, "dset576a_qctrl_solid_" + index, solidSol);
    String tffData = dataset(model, "dset576a_qctrl_tff_" + index, tffSol);
    double fContact = evalGlobal(model, solidData, "eval576a_qctrl_solid_" + index, new String[] {"Fn_contact570"})[0];
    double[] patch = intPatch(model, tffData, "int576a_qctrl_tff_" + index,
        new String[] {"p_load573", "max(tff.p-p_amb573,0[Pa])", "Bfilm573", "B_high573", "M_core573", "tff.theta", "1"});
    double area = patch[6];
    double fFilm = patch[0];
    double fTotal = fContact + fFilm;
    double meanCore = patch[4] / area;
    double meanTheta = patch[5] / area;
    double maxP = surface(model, tffData, "max576a_qctrl_p_" + index, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, tffData, "min576a_qctrl_theta_" + index, "MinSurface", "tff.theta");
    double minGap = surface(model, solidData, "min576a_qctrl_gap_" + index, "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxH = surface(model, tffData, "max576a_qctrl_h_" + index, "MaxSurface", "h_calc573");
    System.out.printf(Locale.US,
        "ROW q=%.12g alpha=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g PosLoad=%.12g MaxP=%.12g MeanCore=%.12g MeanTheta=%.12g MinTheta=%.12g MinGap=%.12g MaxH=%.12g solid=%s tff=%s%n",
        q, ALPHA, fContact, fFilm, fTotal, patch[1], maxP, meanCore, meanTheta, minTheta, minGap, maxH, solidSol, tffSol);
    return new double[] {q, ALPHA, fContact, fFilm, fTotal, patch[1], maxP, meanCore, meanTheta, minTheta, minGap, maxH};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOLID)) throw new IllegalStateException("Missing initial solid solution: " + INIT_SOLID);
      configure(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOLID=" + INIT_SOLID);
      System.out.println("T_ACTIVE=" + T_ACTIVE);
      System.out.println("ALPHA=" + ALPHA);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      model.label("Stage 576a q-load controller setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      model.param().set("q_scale574", "-7.5");
      String sourceTff = runTffVelocity(model, INIT_SOLID, 0);
      rows.add(evaluate(model, INIT_SOLID, sourceTff, 0, -7.5));
      model.save(RESULTS);

      String solidInit = INIT_SOLID;
      for (int i = 0; i < Q_LIST.length; i++) {
        double q = Q_LIST[i];
        int index = i + 1;
        try {
          model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
          setFeedbackLoad(model, comp, sourceTff);
          String solidSol = buildSolidStudy(model, solidInit, index);
          System.out.println("RUN_SOLID q=" + q + " init=" + solidInit + " sourceTff=" + sourceTff + " sol=" + solidSol);
          model.sol(solidSol).runAll();
          model.save(RESULTS);
          String tffSol = runTffVelocity(model, solidSol, index);
          rows.add(evaluate(model, solidSol, tffSol, index, q));
          solidInit = solidSol;
          sourceTff = tffSol;
          model.save(RESULTS);
        } catch (Exception error) {
          failures.add("q=" + q + " failed: " + error.toString());
          System.out.println("Q_FAILED=" + q + " ERROR=" + error);
          model.save(RESULTS);
          break;
        }
      }

      double[] best = null;
      for (double[] row : rows) {
        if (best == null || Math.abs(row[4] - TARGET) < Math.abs(best[4] - TARGET)) best = row;
      }
      boolean pass = best != null
          && Math.abs(best[4] - TARGET) <= 0.005
          && best[3] > 1e-6
          && best[7] > 0.1
          && best[9] >= -1e-8
          && Double.isFinite(best[4])
          && Double.isFinite(best[6]);
      if (best != null) {
        System.out.println("BEST_Q=" + best[0]);
        System.out.println("BEST_ALPHA=" + best[1]);
        System.out.println("BEST_FCONTACT=" + best[2]);
        System.out.println("BEST_FFILM=" + best[3]);
        System.out.println("BEST_FTOTAL=" + best[4]);
        System.out.println("BEST_ERROR=" + (best[4] - TARGET));
        System.out.println("BEST_MAXP=" + best[6]);
        System.out.println("BEST_MEANCORE=" + best[7]);
        System.out.println("BEST_MINTHETA=" + best[9]);
      }
      for (String failure : failures) System.out.println("FAILURE_NOTE=" + failure);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
      if (pass) {
        model.label("Stage 576a q-load controller checked");
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
