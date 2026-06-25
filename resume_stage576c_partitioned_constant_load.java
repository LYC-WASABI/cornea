import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class resume_stage576c_partitioned_constant_load {
  private static final String BASE = "576c_stage576_partitioned_constant_load_results.mph";
  private static final String RESULTS = "576c_stage576_partitioned_constant_load_results.mph";
  private static final String CHECKED = "576c_stage576_partitioned_constant_load_checked.mph";
  private static final double TARGET = 0.03;
  private static final double CONTROL_TOL = 0.0025;
  private static final double ACCEPT_TOL = 0.005;
  private static final double CONTACT_SLOPE = 0.0020;
  private static final double[] VELOCITY = new double[] {
    0, 1e-4, 1e-3, 0.005, 0.01, 0.02, 0.05, 0.075, 0.1,
    0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5, 0.65, 0.8, 1.0
  };
  private static final boolean EXTEND_NODE9 = true;
  private static final boolean REGULARIZE_NODE9 = true;
  private static final boolean ALTERNATE_NODE9_BRANCH = true;

  private static void runAlternateNode9Branch(Model model, ModelNode comp) throws Exception {
    String out = "576d_stage576_node9_branch_continuation_results.mph";
    String checked = "576d_stage576_node9_branch_continuation_checked.mph";
    model.param().set("t_ctrl576c", "T_pre572+0.9*T_slide572");
    model.param().set("q_scale574", "-10");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    model.param().set("dh_break573", "0.005[um]");
    model.param().set("kvent573", "1e-7[kg/(m^2*s*Pa)]");
    model.param().set("kanchor573", "1e-7[kg/(m^2*s*Pa)]");
    String solidSol = "sol201";
    String tffSol = runTffVelocity(model, solidSol, 9, 40);
    double[] initial = evaluate(model, solidSol, tffSol, 9, 40, 0.9, -10.0);
    System.out.printf(Locale.US,
        "BRANCH_INITIAL Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g solid=%s tff=%s%n",
        initial[4], initial[5], initial[6], initial[6]-TARGET, solidSol, tffSol);
    boolean pass = false;
    for (int i = 0; i < 3; i++) {
      int iter = 41 + i;
      setFeedbackLoad(model, comp, tffSol);
      String nextSolid = buildSolidStudy(model, solidSol, 9, iter);
      System.out.println("BRANCH_RUN_SOLID iteration=" + i + " init=" + solidSol + " feedback=" + tffSol + " sol=" + nextSolid);
      model.sol(nextSolid).runAll();
      String nextTff = runTffVelocity(model, nextSolid, 9, iter);
      double[] row = evaluate(model, nextSolid, nextTff, 9, iter, 0.9, -10.0);
      model.save(out);
      boolean accepted = Math.abs(row[6] - TARGET) <= ACCEPT_TOL
          && row[5] > 0 && row[10] >= -1e-8
          && Double.isFinite(row[6]) && Double.isFinite(row[7]) && Double.isFinite(row[11]);
      System.out.printf(Locale.US,
          "BRANCH_ROW iteration=%d Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g status=%s solid=%s tff=%s%n",
          i+1, row[4], row[5], row[6], row[6]-TARGET, accepted ? "PASS" : "FAIL", nextSolid, nextTff);
      solidSol = nextSolid;
      tffSol = nextTff;
      if (accepted) {
        pass = true;
        model.label("Stage 576d node 9 calibrated-branch continuation checked");
        model.save(checked);
        System.out.println("SAVED_CHECKED=" + checked);
        break;
      }
    }
    model.save(out);
    System.out.println("SAVED_RESULTS=" + out);
    System.out.println("ALTERNATE_NODE9_STATUS=" + (pass ? "PASS" : "FAIL"));
    if (!pass) throw new IllegalStateException("Calibrated node 9 branch did not remain load-closed");
  }

  private static void runRegularizedNode9(Model model, ModelNode comp) throws Exception {
    String out = "576d_stage576_node9_regularization_results.mph";
    String checked = "576d_stage576_node9_regularization_checked.mph";
    String[] names = new String[] {"mild", "strong"};
    String[] hmax = new String[] {"30[um]", "30[um]"};
    String[] dhactive = new String[] {"5[um]", "10[um]"};
    String[] dhbreak = new String[] {"0.01[um]", "0.02[um]"};
    String[] kvent = new String[] {"1e-6[kg/(m^2*s*Pa)]", "1e-5[kg/(m^2*s*Pa)]"};
    double[] qValues = new double[] {-11.0, -11.5, -12.0};
    boolean pass = false;
    model.param().set("t_ctrl576c", "T_pre572+0.9*T_slide572");
    for (int variant = 0; variant < names.length && !pass; variant++) {
      model.param().set("h_active_max573", hmax[variant]);
      model.param().set("dh_active573", dhactive[variant]);
      model.param().set("dh_break573", dhbreak[variant]);
      model.param().set("kvent573", kvent[variant]);
      model.param().set("kanchor573", kvent[variant]);
      String solidInit = "sol339";
      String feedbackTff = "sol358";
      System.out.println("REG_VARIANT name=" + names[variant]
          + " hmax=" + hmax[variant] + " dhactive=" + dhactive[variant]
          + " dhbreak=" + dhbreak[variant] + " kvent=" + kvent[variant]);
      for (int qi = 0; qi < qValues.length; qi++) {
        double q = qValues[qi];
        int iter = 20 + 10*variant + qi;
        try {
          model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
          setFeedbackLoad(model, comp, feedbackTff);
          String solidSol = buildSolidStudy(model, solidInit, 9, iter);
          System.out.println("REG_RUN_SOLID variant=" + names[variant] + " q=" + q + " sol=" + solidSol);
          model.sol(solidSol).runAll();
          String tffSol = runTffVelocity(model, solidSol, 9, iter);
          double[] row = evaluate(model, solidSol, tffSol, 9, iter, 0.9, q);
          model.save(out);
          boolean accepted = Math.abs(row[6] - TARGET) <= ACCEPT_TOL
              && row[5] > 0 && row[10] >= -1e-8
              && Double.isFinite(row[6]) && Double.isFinite(row[7]) && Double.isFinite(row[11]);
          System.out.printf(Locale.US,
              "REG_ROW variant=%s q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g status=%s solid=%s tff=%s%n",
              names[variant], q, row[4], row[5], row[6], row[6]-TARGET, row[7], row[10],
              accepted ? "PASS" : "FAIL", solidSol, tffSol);
          solidInit = solidSol;
          feedbackTff = tffSol;
          if (accepted) {
            pass = true;
            model.label("Stage 576d node 9 regularization checked");
            model.save(checked);
            System.out.println("SAVED_CHECKED=" + checked);
            break;
          }
          if (row[6] < TARGET - ACCEPT_TOL) break;
        } catch (Exception error) {
          System.out.println("REG_FAILED variant=" + names[variant] + " q=" + q + " error=" + error);
          break;
        }
      }
    }
    model.save(out);
    System.out.println("SAVED_RESULTS=" + out);
    System.out.println("REGULARIZED_NODE9_STATUS=" + (pass ? "PASS" : "FAIL"));
    if (!pass) throw new IllegalStateException("Node 9 regularization scan did not close load");
  }

  private static void runExtendedNode9(Model model, ModelNode comp) throws Exception {
    double[] qValues = new double[] {-11.0, -11.25, -11.5, -12.0, -12.5};
    String solidInit = "sol339";
    String feedbackTff = "sol358";
    boolean pass = false;
    model.param().set("t_ctrl576c", "T_pre572+0.9*T_slide572");
    for (int i = 0; i < qValues.length; i++) {
      double q = qValues[i];
      int iter = 10 + i;
      try {
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        setFeedbackLoad(model, comp, feedbackTff);
        String solidSol = buildSolidStudy(model, solidInit, 9, iter);
        System.out.println("EXTEND_RUN_SOLID q=" + q + " init=" + solidInit + " feedback=" + feedbackTff + " sol=" + solidSol);
        model.sol(solidSol).runAll();
        String tffSol = runTffVelocity(model, solidSol, 9, iter);
        double[] row = evaluate(model, solidSol, tffSol, 9, iter, 0.9, q);
        model.save(RESULTS);
        boolean accepted = Math.abs(row[6] - TARGET) <= ACCEPT_TOL
            && row[10] >= -1e-8
            && Double.isFinite(row[6]) && Double.isFinite(row[7]) && Double.isFinite(row[11]);
        System.out.printf(Locale.US,
            "EXTEND_ROW q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g status=%s solid=%s tff=%s%n",
            q, row[4], row[5], row[6], row[6] - TARGET, accepted ? "PASS" : "FAIL", solidSol, tffSol);
        solidInit = solidSol;
        feedbackTff = tffSol;
        if (accepted) {
          pass = true;
          break;
        }
      } catch (Exception error) {
        System.out.println("EXTEND_FAILED q=" + q + " error=" + error);
        break;
      }
    }
    System.out.println("EXTENDED_NODE9_STATUS=" + (pass ? "PASS" : "FAIL"));
    model.save(RESULTS);
    if (pass) {
      model.label("Stage 576c partitioned constant-load checked");
      model.save(CHECKED);
      System.out.println("SAVED_CHECKED=" + CHECKED);
    }
    if (!pass) throw new IllegalStateException("Node 9 extended release scan did not close load");
  }

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

  private static void setFeedbackLoad(Model model, ModelNode comp, String sourceTffSol) {
    String vars = "var_pressure_feedback576c";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named("sel_local_cornea_patch574");
    comp.variable(vars).set("p_feedback576c", "alpha_pfb576a*withsol('" + sourceTffSol + "',p_load573)");
    String load = "load_pfilm576c";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    try { comp.physics("solid").feature("load_pfilm576a").active(false); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576c partitioned film pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576c*nx", "-p_feedback576c*ny", "-p_feedback576c*nz"
    });
  }

  private static String buildSolidStudy(Model model, String initSol, int node, int iter) {
    String study = "std576c_s_" + node + "_" + iter;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576c solid node " + node + " iteration " + iter);
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
    ModelNode comp = model.component("comp1");
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576c"}) {
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

  private static String buildTffStudy(Model model, String initSol, int node, int iter, int velocityIndex) {
    String study = "std576c_f_" + node + "_" + iter + "_" + velocityIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576c TFF node " + node + " iteration " + iter + " velocity " + velocityIndex);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
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

  private static String runTffVelocity(Model model, String solidSol, int node, int iter) {
    String current = solidSol;
    for (int i = 0; i < VELOCITY.length; i++) {
      model.param().set("lambda_h574", "1");
      model.param().set("lambda_v574", String.format(Locale.US, "%.12g", VELOCITY[i]));
      String sol = buildTffStudy(model, current, node, iter, i);
      System.out.println("RUN_TFF node=" + node + " iter=" + iter + " lv=" + VELOCITY[i] + " sol=" + sol);
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

  private static double evalGlobal(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
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

  private static double[] evaluate(Model model, String solidSol, String tffSol, int node, int iter, double fraction, double q) {
    String solidData = dataset(model, "dset576c_s_" + node + "_" + iter, solidSol);
    String tffData = dataset(model, "dset576c_f_" + node + "_" + iter, tffSol);
    double fContact = evalGlobal(model, solidData, "eval576c_c_" + node + "_" + iter, "Fn_contact570");
    double[] patch = intPatch(model, tffData, "int576c_f_" + node + "_" + iter,
        new String[] {"1", "p_load573", "M_core573", "tff.theta"});
    double area = patch[0];
    double fFilm = patch[1];
    double fTotal = fContact + fFilm;
    double meanCore = patch[2] / area;
    double meanTheta = patch[3] / area;
    double maxP = surface(model, tffData, "max576c_p_" + node + "_" + iter, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, tffData, "min576c_th_" + node + "_" + iter, "MinSurface", "tff.theta");
    double minGap = surface(model, solidData, "min576c_g_" + node + "_" + iter, "MinSurface", "geomgap_dst_cp_lid_cornea");
    System.out.printf(Locale.US,
        "CTRL_ROW node=%d fraction=%.6f iter=%d q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MeanCore=%.12g MeanTheta=%.12g MinTheta=%.12g MinGap=%.12g solid=%s tff=%s%n",
        node, fraction, iter, q, fContact, fFilm, fTotal, fTotal - TARGET, maxP,
        meanCore, meanTheta, minTheta, minGap, solidSol, tffSol);
    return new double[] {node, fraction, iter, q, fContact, fFilm, fTotal, maxP, meanCore, meanTheta, minTheta, minGap};
  }

  private static double updateQ(double q, double error) {
    double rawStep = -error / CONTACT_SLOPE;
    double step = Math.max(-0.75, Math.min(0.75, rawStep));
    return Math.max(-10.75, Math.min(-6.0, q + step));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (ALTERNATE_NODE9_BRANCH) {
        runAlternateNode9Branch(model, comp);
        ModelUtil.disconnect();
        return;
      }
      if (REGULARIZE_NODE9) {
        runRegularizedNode9(model, comp);
        ModelUtil.disconnect();
        return;
      }
      if (EXTEND_NODE9) {
        runExtendedNode9(model, comp);
        ModelUtil.disconnect();
        return;
      }
      String previousSolid = "sol292";
      String previousTff = "sol298";
      double q = -10.0;
      boolean allAccepted = true;
      for (int node = 9; node <= 10; node++) {
        double fraction = node / 10.0;
        model.param().set("t_ctrl576c", String.format(Locale.US, "T_pre572+%.12g*T_slide572", fraction));
        String iterSolidInit = previousSolid;
        String feedbackTff = previousTff;
        double[] best = null;
        String bestSolid = previousSolid;
        String bestTff = previousTff;
        System.out.printf(Locale.US, "RESUME_NODE_START node=%d fraction=%.6f q_init=%.12g%n", node, fraction, q);
        for (int iter = 0; iter < 4; iter++) {
          model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
          setFeedbackLoad(model, comp, feedbackTff);
          String solidSol = buildSolidStudy(model, iterSolidInit, node, iter);
          System.out.println("RESUME_RUN_SOLID node=" + node + " iter=" + iter + " q=" + q + " sol=" + solidSol);
          model.sol(solidSol).runAll();
          String tffSol = runTffVelocity(model, solidSol, node, iter);
          double[] row = evaluate(
              model, solidSol, tffSol, node, iter, fraction, q);
          if (best == null || Math.abs(row[6] - TARGET) < Math.abs(best[6] - TARGET)) {
            best = row;
            bestSolid = solidSol;
            bestTff = tffSol;
          }
          model.save(RESULTS);
          if (Math.abs(row[6] - TARGET) <= CONTROL_TOL) break;
          q = updateQ(q, row[6] - TARGET);
          iterSolidInit = solidSol;
          feedbackTff = tffSol;
        }
        if (best == null) throw new IllegalStateException("No result at node " + node);
        previousSolid = bestSolid;
        previousTff = bestTff;
        q = best[3];
        boolean accepted = Math.abs(best[6] - TARGET) <= ACCEPT_TOL
            && best[10] >= -1e-8
            && Double.isFinite(best[6]) && Double.isFinite(best[7]) && Double.isFinite(best[11]);
        allAccepted = allAccepted && accepted;
        System.out.printf(Locale.US,
            "RESUME_NODE_ACCEPT node=%d q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g status=%s solid=%s tff=%s%n",
            node, best[3], best[4], best[5], best[6], best[6] - TARGET,
            accepted ? "PASS" : "FAIL", bestSolid, bestTff);
      }
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
      System.out.println("CHECKED_STATUS=" + (allAccepted ? "PASS" : "FAIL"));
      if (allAccepted) {
        model.label("Stage 576c partitioned constant-load checked");
        model.save(CHECKED);
        System.out.println("SAVED_CHECKED=" + CHECKED);
      }
      ModelUtil.disconnect();
      if (!allAccepted) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
