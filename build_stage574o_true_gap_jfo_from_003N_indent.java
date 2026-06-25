import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574o_true_gap_jfo_from_003N_indent {
  private static final String BASE = "574n_stage574_quasistatic_indent_contact_003N_checked.mph";
  private static final String SETUP = "574o_stage574_fixed_structure_true_gap_from_003N_setup.mph";
  private static final String RESULTS = "574o_stage574_fixed_structure_true_gap_from_003N_results.mph";
  private static final String CHECKED = "574o_stage574_fixed_structure_true_gap_from_003N_checked.mph";
  private static final String INIT_SOL = "sol99";
  private static final double[] VELOCITY = new double[] {
    0, 1e-4, 1e-3, 0.005, 0.01, 0.02, 0.05, 0.075, 0.1,
    0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5, 0.65, 0.8, 1.0
  };
  private static final double[] TRUE_GAP = new double[] {
    1e-4, 1e-3, 1e-2, 0.05, 0.1, 0.2, 0.4, 0.7, 1.0
  };
  private static final List<double[]> history = new ArrayList<>();

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) latest = tag;
    }
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void forceNoFeedback(ModelNode comp) {
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
    try {
      PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
      for (String childTag : contact.feature().tags()) {
        PhysicsFeature child = contact.feature(childTag);
        String type = child.getType();
        String label = child.label();
        if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) {
          child.active(false);
          System.out.println("FRICTION_OFF=" + childTag + "|" + label);
        }
      }
    } catch (Exception ignored) {}
  }

  private static void configureTff(Model model, ModelNode comp) {
    model.param().set("q_scale574", "-7.5");
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("time_offset572", "T_pre572+0.5*T_slide572");
    model.param().set("lambda_v574", "0");
    model.param().set("lambda_h574", "0");
    try { comp.variable("var_dynamic_motion572").set("tau572", "time_offset572"); }
    catch (Exception ignored) {}

    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "0");
    } catch (Exception error) {
      System.out.println("LOCAL_MASK_OVERRIDE_FAILED=" + error.getMessage());
    }

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "(1-lambda_h574)*3[um]+lambda_h574*h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0",
      "-lambda_v574*omega_lid_rot572*Z",
      "lambda_v574*omega_lid_rot572*Y"
    });
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); }
    catch (Exception ignored) {
      try { comp.physics("tff").feature("init1").set("pfilm", "0[Pa]"); }
      catch (Exception ignored2) {}
    }
    for (String tag : new String[] {
        "bdr_inlet520", "bdr_outlet520", "bdr_left520", "bdr_right520"
    }) {
      try { comp.physics("tff").feature(tag).set("pf0", "p_amb573"); }
      catch (Exception ignored) {}
      try { comp.physics("tff").feature(tag).set("theta_0", "1"); }
      catch (Exception ignored) {}
    }
    try {
      comp.physics("tff").feature("ms_vent573").set(
          "QudR", "lambda_h574*Qvent573");
    } catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
  }

  private static String buildStudy(Model model, String initSol, String prefix, int index) {
    String study = "std574o_" + prefix + "_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574o true-gap JFO from 0.03N indent " + prefix + " step " + index);
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
    SolverFeature dependent = model.sol(sol).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", initSol);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", initSol);
    dependent.set("notsolnum", "last");
    SolverFeature stationary = model.sol(sol).feature("s1");
    for (String tag : stationary.feature().tags()) {
      if (tag.startsWith("se")) {
        try { stationary.feature().remove(tag); } catch (Exception ignored) {}
      }
    }
    if (!has(stationary.feature().tags(), "fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", "0.3");
    stationary.feature("fc1").set("maxiter", 300);
    return sol;
  }

  private static double[][] patchInt(
      Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double surface(
      Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double[] evaluate(
      Model model, String solution, String phase, int index,
      double lambdaV, double lambdaH) {
    String data = "dset574o_" + phase + "_" + index;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);
    double[][] patch = patchInt(model, data, "int574o_" + phase + "_" + index,
        new String[] {
          "1",
          "if(isdefined(geomgap_dst_cp_lid_cornea),"
              + "if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)",
          "max(tff.p-p_amb573,0[Pa])",
          "abs(tff.p-p_amb573)",
          "p_load573",
          "Bfilm573",
          "Afilm573",
          "h_calc573",
          "tff.theta"
        });
    double area = patch[0][0];
    double gapCoverage = patch[1][0] / area;
    double positiveLoad = patch[2][0];
    double absLoad = patch[3][0];
    double physicalLoad = patch[4][0];
    double meanB = patch[5][0] / area;
    double meanA = patch[6][0] / area;
    double meanH = patch[7][0] / area;
    double meanTheta = patch[8][0] / area;
    double minP = surface(model, data, "min574o_p_" + phase + "_" + index,
        "MinSurface", "tff.p-p_amb573");
    double maxP = surface(model, data, "max574o_p_" + phase + "_" + index,
        "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, data, "min574o_theta_" + phase + "_" + index,
        "MinSurface", "tff.theta");
    double maxTheta = surface(model, data, "max574o_theta_" + phase + "_" + index,
        "MaxSurface", "tff.theta");
    double minH = surface(model, data, "min574o_h_" + phase + "_" + index,
        "MinSurface", "h_calc573");
    double maxH = surface(model, data, "max574o_h_" + phase + "_" + index,
        "MaxSurface", "h_calc573");
    double minB = surface(model, data, "min574o_b_" + phase + "_" + index,
        "MinSurface", "Bfilm573");
    double maxB = surface(model, data, "max574o_b_" + phase + "_" + index,
        "MaxSurface", "Bfilm573");
    System.out.printf(Locale.US,
        "STEP=%s/%d LV=%.12g LH=%.12g POS_LOAD=%.12g PLOAD=%.12g MINP=%.12g MAXP=%.12g MEAN_B=%.12g MEAN_A=%.12g H=[%.12g,%.12g] THETA=[%.12g,%.12g] GAPCOV=%.12g%n",
        phase, index, lambdaV, lambdaH, positiveLoad, physicalLoad,
        minP, maxP, meanB, meanA, minH, maxH, minTheta, maxTheta, gapCoverage);
    return new double[] {
      lambdaV, lambdaH, area, gapCoverage, positiveLoad, absLoad, physicalLoad,
      meanB, meanA, meanH, meanTheta, minP, maxP, minTheta, maxTheta,
      minH, maxH, minB, maxB
    };
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOL)) {
        throw new IllegalStateException("Required initial solution missing: " + INIT_SOL);
      }
      String current = INIT_SOL;
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOL=" + current);
      System.out.println("Q_SCALE_FOR_INIT=-7.5");
      System.out.println("LOCAL_PATCH=" + Arrays.toString(
          comp.selection("sel_local_cornea_patch574").entities(2)));

      forceNoFeedback(comp);
      configureTff(model, comp);
      model.label("Stage 574o fixed-structure true-gap JFO setup from 0.03N indentation");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      for (int i = 0; i < TRUE_GAP.length; i++) {
        double lh = TRUE_GAP[i];
        model.param().set("lambda_v574", "0");
        model.param().set("lambda_h574", String.format(Locale.US, "%.12g", lh));
        String sol = buildStudy(model, current, "gap", i);
        System.out.println("RUN_PHASE=gap STEP=" + i + " LH=" + lh + " INIT=" + current + " SOL=" + sol);
        model.sol(sol).runAll();
        history.add(evaluate(model, sol, "gap", i, 0, lh));
        current = sol;
      }

      for (int i = 0; i < VELOCITY.length; i++) {
        double lv = VELOCITY[i];
        model.param().set("lambda_v574", String.format(Locale.US, "%.12g", lv));
        model.param().set("lambda_h574", "1");
        String sol = buildStudy(model, current, "vel", i);
        System.out.println("RUN_PHASE=vel STEP=" + i + " LV=" + lv + " INIT=" + current + " SOL=" + sol);
        model.sol(sol).runAll();
        history.add(evaluate(model, sol, "vel", i, lv, 1));
        current = sol;
      }

      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      double[] last = history.get(history.size() - 1);
      boolean pass = true;
      if (Math.abs(last[0] - 1.0) > 1e-12 || Math.abs(last[1] - 1.0) > 1e-12) pass = false;
      if (last[3] < 0.95) pass = false;
      for (int i = 4; i < last.length; i++) {
        if (!Double.isFinite(last[i])) pass = false;
      }
      if (last[13] < -1e-8 || last[14] > 1.000001) pass = false;
      if (last[4] > 0.3 || Math.abs(last[6]) > 0.3) pass = false;
      System.out.println("FINAL_LAMBDA_V=" + last[0]);
      System.out.println("FINAL_LAMBDA_H=" + last[1]);
      System.out.println("FINAL_POSITIVE_LOAD=" + last[4]);
      System.out.println("FINAL_PHYSICAL_PLOAD=" + last[6]);
      System.out.println("FINAL_MINP=" + last[11]);
      System.out.println("FINAL_MAXP=" + last[12]);
      System.out.println("FINAL_MEAN_BFILM=" + last[7]);
      System.out.println("FINAL_MEAN_AFILM=" + last[8]);
      System.out.println("FINAL_MEAN_H=" + last[9]);
      System.out.println("FINAL_THETA_MEAN=" + last[10]);
      System.out.println("FINAL_H_MIN=" + last[15]);
      System.out.println("FINAL_H_MAX=" + last[16]);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (!pass) {
        throw new IllegalStateException("Stage 574o true-gap pressure-field acceptance failed");
      }
      model.label("Stage 574o fixed-structure true-gap JFO checked from 0.03N indentation");
      model.save(CHECKED);
      System.out.println("SAVED_CHECKED=" + CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
