import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576c_partitioned_constant_load {
  private static final String BASE = "576a_stage576_load_controller_closure_checked.mph";
  private static final String SETUP = "576c_stage576_partitioned_constant_load_setup.mph";
  private static final String RESULTS = "576c_stage576_partitioned_constant_load_results.mph";
  private static final String CHECKED = "576c_stage576_partitioned_constant_load_checked.mph";
  private static final String INIT_SOLID = "sol99";
  private static final String INIT_TFF = "sol220";
  private static final double TARGET = 0.03;
  private static final double CONTROL_TOL = 0.0025;
  private static final double ACCEPT_TOL = 0.005;
  private static final double CONTACT_SLOPE = 0.0020;
  private static final int MAX_CONTROL_ITER = 4;
  private static final double[] TIME_FRACTIONS = new double[] {
    0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
  };
  private static final double[] VELOCITY = new double[] {
    0, 1e-4, 1e-3, 0.005, 0.01, 0.02, 0.05, 0.075, 0.1,
    0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5, 0.65, 0.8, 1.0
  };
  private static final List<double[]> acceptedRows = new ArrayList<>();

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

  public static void configure(Model model, ModelNode comp) {
    model.param().set("alpha_pfb576a", "0.183");
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "1");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    model.param().set("t_ctrl576c", "T_pre572");
    model.param().set("q_scale574", "-7.5");
    comp.variable("var_dynamic_motion572").set("tau572", "t_ctrl576c");
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
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "(1-lambda_h574)*3[um]+lambda_h574*h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0", "-lambda_v574*omega_lid_rot572*Z", "lambda_v574*omega_lid_rot572*Y"
    });
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); } catch (Exception ignored) {}
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "lambda_h574*Qvent573"); } catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(1-M_drain573)*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    contact.set("pairSelection", "list");
    contact.set("pairs", new String[] {"cp_lid_cornea"});
    for (String childTag : contact.feature().tags()) {
      PhysicsFeature child = contact.feature(childTag);
      if ("Friction".equals(child.getType()) || child.label().toLowerCase(Locale.ROOT).contains("fric")) {
        child.active(false);
      }
    }
  }

  public static void setFeedbackLoad(Model model, ModelNode comp, String sourceTffSol) {
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

  public static String buildSolidStudy(Model model, String initSol, int node, int iter) {
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

  public static String runTffVelocity(Model model, String solidSol, int node, int iter) {
    String current = solidSol;
    for (int i = 0; i < VELOCITY.length; i++) {
      model.param().set("lambda_h574", "1");
      model.param().set("lambda_v574", String.format(Locale.US, "%.12g", VELOCITY[i]));
      String sol = buildTffStudy(model, current, node, iter, i);
      System.out.println("RUN_TFF node=" + node + " iter=" + iter + " lv=" + VELOCITY[i] + " init=" + current + " sol=" + sol);
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

  public static double[] evaluate(Model model, String solidSol, String tffSol, int node, int iter, double fraction, double q) {
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

  public static double updateQ(double q, double error) {
    double rawStep = -error / CONTACT_SLOPE;
    double step = Math.max(-0.75, Math.min(0.75, rawStep));
    return Math.max(-10.75, Math.min(-6.0, q + step));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOLID)) throw new IllegalStateException("Missing " + INIT_SOLID);
      if (!has(model.sol().tags(), INIT_TFF)) throw new IllegalStateException("Missing " + INIT_TFF);
      configure(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOLID=" + INIT_SOLID);
      System.out.println("INIT_TFF=" + INIT_TFF);
      System.out.println("NODES=" + TIME_FRACTIONS.length);
      System.out.println("TARGET=" + TARGET);
      model.label("Stage 576c partitioned constant-load setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      String previousSolid = INIT_SOLID;
      String previousTff = INIT_TFF;
      double q = -7.5;
      boolean allAccepted = true;
      for (int node = 0; node < TIME_FRACTIONS.length; node++) {
        double fraction = TIME_FRACTIONS[node];
        model.param().set("t_ctrl576c", String.format(Locale.US, "T_pre572+%.12g*T_slide572", fraction));
        System.out.printf(Locale.US, "NODE_START node=%d fraction=%.6f q_init=%.12g solid_init=%s tff_source=%s%n",
            node, fraction, q, previousSolid, previousTff);
        double[] best = null;
        String bestSolid = previousSolid;
        String bestTff = previousTff;
        String iterSolidInit = previousSolid;
        String feedbackTff = previousTff;
        for (int iter = 0; iter < MAX_CONTROL_ITER; iter++) {
          model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
          setFeedbackLoad(model, comp, feedbackTff);
          String solidSol = buildSolidStudy(model, iterSolidInit, node, iter);
          System.out.println("RUN_SOLID node=" + node + " iter=" + iter + " q=" + q + " init=" + iterSolidInit + " source=" + feedbackTff + " sol=" + solidSol);
          model.sol(solidSol).runAll();
          String tffSol = runTffVelocity(model, solidSol, node, iter);
          double[] row = evaluate(model, solidSol, tffSol, node, iter, fraction, q);
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
        acceptedRows.add(best);
        previousSolid = bestSolid;
        previousTff = bestTff;
        q = best[3];
        boolean accepted = Math.abs(best[6] - TARGET) <= ACCEPT_TOL
            && best[10] >= -1e-8
            && Double.isFinite(best[6]) && Double.isFinite(best[7]) && Double.isFinite(best[11]);
        allAccepted = allAccepted && accepted;
        System.out.printf(Locale.US,
            "NODE_ACCEPT node=%d fraction=%.6f q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g status=%s solid=%s tff=%s%n",
            node, fraction, best[3], best[4], best[5], best[6], best[6] - TARGET,
            accepted ? "PASS" : "FAIL", bestSolid, bestTff);
      }

      double maxAbsError = 0;
      double qMin = Double.POSITIVE_INFINITY;
      double qMax = Double.NEGATIVE_INFINITY;
      for (double[] row : acceptedRows) {
        maxAbsError = Math.max(maxAbsError, Math.abs(row[6] - TARGET));
        qMin = Math.min(qMin, row[3]);
        qMax = Math.max(qMax, row[3]);
      }
      System.out.printf(Locale.US, "SUMMARY nodes=%d max_abs_error=%.12g q_range=[%.12g,%.12g]%n",
          acceptedRows.size(), maxAbsError, qMin, qMax);
      System.out.println("CHECKED_STATUS=" + (allAccepted ? "PASS" : "FAIL"));
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
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
