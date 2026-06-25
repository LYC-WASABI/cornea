import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574h_constant_zero_jfo {
  private static final String BASE = "574g_stage574_local_contact_gap_checked.mph";
  private static final String SETUP = "574h_stage574_fixed_structure_constant_zero_jfo_setup.mph";
  private static final String RESULTS = "574h_stage574_fixed_structure_constant_zero_jfo_results.mph";
  private static final String CHECKED = "574h_stage574_fixed_structure_constant_zero_jfo_checked.mph";

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

  private static String finalStructureSolution(Model model) {
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

  private static void configureConstantZeroTff(ModelNode comp) {
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "3[um]");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {"0", "0", "0"});
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

  private static String buildStudy(Model model, String initSol) {
    String study = "std574h_constant_zero_jfo";
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574h fixed-structure constant-film zero-speed JFO");
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
    stationary.feature("fc1").set("damp", "1");
    stationary.feature("fc1").set("maxiter", 100);
    return sol;
  }

  private static double[][] globalEval(
      Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
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

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      String initSol = finalStructureSolution(model);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_STRUCTURE_SOL=" + initSol);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(
          comp.selection("sel_local_cornea_patch574").entities(2)));

      forceNoFeedback(comp);
      configureConstantZeroTff(comp);
      model.label("Stage 574h fixed-structure constant-film zero-speed JFO setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      String sol = buildStudy(model, initSol);
      System.out.println("RUN_SOLUTION=" + sol);
      model.sol(sol).runAll();
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      String data = "dset574h_constant_zero";
      removeDataset(model, data);
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", sol);

      double[][] global = globalEval(model, data, "eval574h_global",
          new String[] {"q_scale574", "Fn_contact570"});
      double[][] patch = patchInt(model, data, "int574h_patch",
          new String[] {
            "1",
            "if(isdefined(geomgap_dst_cp_lid_cornea),"
                + "if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)",
            "tff.p-p_amb573",
            "abs(tff.p-p_amb573)",
            "max(tff.p-p_amb573,0[Pa])",
            "tff.theta"
          });
      double minP = surface(model, data, "min574h_p", "MinSurface", "tff.p-p_amb573");
      double maxP = surface(model, data, "max574h_p", "MaxSurface", "tff.p-p_amb573");
      double minTheta = surface(model, data, "min574h_theta", "MinSurface", "tff.theta");
      double maxTheta = surface(model, data, "max574h_theta", "MaxSurface", "tff.theta");
      double minGap = surface(model, data, "min574h_gap", "MinSurface", "geomgap_dst_cp_lid_cornea");

      double area = patch[0][0];
      double gapCoverage = patch[1][0] / area;
      double signedLoad = patch[2][0];
      double absLoad = patch[3][0];
      double positiveLoad = patch[4][0];
      double meanTheta = patch[5][0] / area;

      System.out.println("GLOBAL=" + Arrays.deepToString(global));
      System.out.println("PATCH=" + Arrays.deepToString(patch));
      System.out.printf(Locale.US, "AREA=%.16g%n", area);
      System.out.printf(Locale.US, "GAP_COVERAGE=%.16g%n", gapCoverage);
      System.out.printf(Locale.US, "SIGNED_FILM_LOAD=%.16g%n", signedLoad);
      System.out.printf(Locale.US, "ABS_FILM_LOAD=%.16g%n", absLoad);
      System.out.printf(Locale.US, "POSITIVE_FILM_LOAD=%.16g%n", positiveLoad);
      System.out.printf(Locale.US, "MIN_P=%.16g%n", minP);
      System.out.printf(Locale.US, "MAX_P=%.16g%n", maxP);
      System.out.printf(Locale.US, "MIN_THETA=%.16g%n", minTheta);
      System.out.printf(Locale.US, "MAX_THETA=%.16g%n", maxTheta);
      System.out.printf(Locale.US, "MEAN_THETA=%.16g%n", meanTheta);
      System.out.printf(Locale.US, "MIN_GAP=%.16g%n", minGap);

      boolean pass = true;
      if (gapCoverage < 0.95) pass = false;
      if (Math.abs(signedLoad) > 1e-8) pass = false;
      if (absLoad > 1e-8) pass = false;
      if (Math.max(Math.abs(minP), Math.abs(maxP)) > 1e-2) pass = false;
      if (Math.abs(meanTheta - 1.0) > 1e-6) pass = false;
      if (minTheta < 0.999999 || maxTheta > 1.000001) pass = false;
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (!pass) {
        throw new IllegalStateException("Stage 574h pressure-field acceptance failed");
      }

      model.label("Stage 574h fixed-structure constant-film zero-speed JFO checked");
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
