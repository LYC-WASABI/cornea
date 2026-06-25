import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574i_constant_velocity_jfo {
  private static final String BASE = "574h_stage574_fixed_structure_constant_zero_jfo_checked.mph";
  private static final String SETUP = "574i_stage574_fixed_structure_constant_velocity_jfo_setup.mph";
  private static final String RESULTS = "574i_stage574_fixed_structure_constant_velocity_jfo_results.mph";
  private static final String CHECKED = "574i_stage574_fixed_structure_constant_velocity_jfo_checked.mph";
  private static final double[] LAMBDAS = new double[] {
    0, 1e-5, 1e-4, 1e-3, 1e-2, 0.05, 0.1, 0.2, 0.4, 0.7, 1.0
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

  private static String initialSolution(Model model) {
    if (has(model.sol().tags(), "sol110")) return "sol110";
    if (has(model.sol().tags(), "sol109")) return "sol109";
    String[] tags = model.sol().tags();
    if (tags.length == 0) throw new IllegalStateException("No solution exists");
    return tags[tags.length - 1];
  }

  private static void forceNoFeedback(ModelNode comp) {
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
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
  }

  private static void configureConstantVelocityTff(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("time_offset572", "T_pre572+0.5*T_slide572");
    model.param().set("lambda_v574", "0");
    try {
      comp.variable("var_dynamic_motion572").set("tau572", "time_offset572");
    } catch (Exception ignored) {}
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "3[um]");
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
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "0"); }
    catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573")
          .set("weakExpression", "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
  }

  private static String buildStudy(Model model, String initSol, int index) {
    String study = "std574i_velocity_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574i constant-film velocity JFO step " + index);
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
    stationary.feature("fc1").set("damp", "0.8");
    stationary.feature("fc1").set("maxiter", 150);
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

  private static double maxEdgeAbs(Model model, String data, String prefix) {
    double best = 0;
    for (String selection : new String[] {
        "sel_local_leading574", "sel_local_trailing574",
        "sel_local_left574", "sel_local_right574"
    }) {
      try {
        String tag = prefix + "_" + selection;
        removeNumerical(model, tag);
        model.result().numerical().create(tag, "MaxLine");
        model.result().numerical(tag).set("data", data);
        model.result().numerical(tag).selection().named(selection);
        model.result().numerical(tag).set("expr", "abs(tff.p-p_amb573)");
        double value = model.result().numerical(tag).getReal()[0][0];
        if (Double.isFinite(value) && value > best) best = value;
      } catch (Exception error) {
        System.out.println("EDGE_EVAL_SKIPPED=" + selection + "|" + error.getMessage());
      }
    }
    return best;
  }

  private static double[] evaluate(Model model, String solution, double lambda, int index) {
    String data = "dset574i_velocity_" + index;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);

    double[][] patch = patchInt(model, data, "int574i_patch_" + index,
        new String[] {
          "1",
          "if(isdefined(geomgap_dst_cp_lid_cornea),"
              + "if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)",
          "tff.p-p_amb573",
          "abs(tff.p-p_amb573)",
          "max(tff.p-p_amb573,0[Pa])",
          "tff.theta"
        });
    double area = patch[0][0];
    double gapCoverage = patch[1][0] / area;
    double signedLoad = patch[2][0];
    double absLoad = patch[3][0];
    double positiveLoad = patch[4][0];
    double meanTheta = patch[5][0] / area;
    double minP = surface(model, data, "min574i_p_" + index, "MinSurface", "tff.p-p_amb573");
    double maxP = surface(model, data, "max574i_p_" + index, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, data, "min574i_theta_" + index, "MinSurface", "tff.theta");
    double maxTheta = surface(model, data, "max574i_theta_" + index, "MaxSurface", "tff.theta");
    double minGap = surface(model, data, "min574i_gap_" + index, "MinSurface", "geomgap_dst_cp_lid_cornea");
    double edgeAbsMax = maxEdgeAbs(model, data, "max574i_edge_" + index);
    System.out.printf(Locale.US,
        "STEP=%d LAMBDA=%.12g MINP=%.12g MAXP=%.12g SIGNED_LOAD=%.12g POS_LOAD=%.12g THETA=[%.12g,%.12g] EDGE_ABS_MAX=%.12g GAP_COVERAGE=%.12g%n",
        index, lambda, minP, maxP, signedLoad, positiveLoad,
        minTheta, maxTheta, edgeAbsMax, gapCoverage);
    return new double[] {
      lambda, area, gapCoverage, signedLoad, absLoad, positiveLoad,
      minP, maxP, minTheta, maxTheta, meanTheta, minGap, edgeAbsMax
    };
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      String current = initialSolution(model);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOL=" + current);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(
          comp.selection("sel_local_cornea_patch574").entities(2)));

      forceNoFeedback(comp);
      configureConstantVelocityTff(model, comp);
      model.label("Stage 574i fixed-structure constant-film velocity JFO setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      for (int i = 0; i < LAMBDAS.length; i++) {
        double lambda = LAMBDAS[i];
        model.param().set("lambda_v574", String.format(Locale.US, "%.12g", lambda));
        String sol = buildStudy(model, current, i);
        System.out.println("RUN_STEP=" + i + " LAMBDA=" + lambda + " SOL=" + sol + " INIT=" + current);
        model.sol(sol).runAll();
        double[] m = evaluate(model, sol, lambda, i);
        history.add(m);
        current = sol;
      }

      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      double[] last = history.get(history.size() - 1);
      boolean pass = true;
      if (Math.abs(last[0] - 1.0) > 1e-12) pass = false;
      if (last[2] < 0.95) pass = false;
      if (!Double.isFinite(last[6]) || !Double.isFinite(last[7])
          || !Double.isFinite(last[5])) pass = false;
      if (last[8] < -1e-8 || last[9] > 1.000001) pass = false;
      if (Math.abs(last[10] - 1.0) > 1e-5 && last[8] > 0.999) pass = false;
      if (Math.abs(last[5]) > 0.3) pass = false;
      double maxPatchAbs = Math.max(Math.abs(last[6]), Math.abs(last[7]));
      if (maxPatchAbs > 0 && last[12] / maxPatchAbs > 0.99) pass = false;

      System.out.println("FINAL_LAMBDA=" + last[0]);
      System.out.println("FINAL_SIGNED_LOAD=" + last[3]);
      System.out.println("FINAL_ABS_LOAD=" + last[4]);
      System.out.println("FINAL_POSITIVE_LOAD=" + last[5]);
      System.out.println("FINAL_MINP=" + last[6]);
      System.out.println("FINAL_MAXP=" + last[7]);
      System.out.println("FINAL_EDGE_ABS_MAX=" + last[12]);
      System.out.println("FINAL_EDGE_TO_PATCH_RATIO=" + (maxPatchAbs > 0 ? last[12] / maxPatchAbs : 0));
      System.out.println("FINAL_THETA_MIN=" + last[8]);
      System.out.println("FINAL_THETA_MAX=" + last[9]);
      System.out.println("FINAL_THETA_MEAN=" + last[10]);
      System.out.println("FINAL_MIN_GAP=" + last[11]);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (!pass) {
        throw new IllegalStateException("Stage 574i velocity pressure-field acceptance failed");
      }

      model.label("Stage 574i fixed-structure constant-film velocity JFO checked");
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
