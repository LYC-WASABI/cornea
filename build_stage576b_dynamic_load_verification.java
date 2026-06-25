import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.io.*;
import java.util.*;

public class build_stage576b_dynamic_load_verification {
  private static final String BASE = "576a_stage576_load_controller_closure_checked.mph";
  private static final String SETUP = "576b_stage576_dynamic_load_verification_setup.mph";
  private static final String RESULTS = "576b_stage576_dynamic_load_verification_results.mph";
  private static final String CHECKED = "576b_stage576_dynamic_load_verification_checked.mph";
  private static final String REPORT = "576b_stage576_dynamic_load_verification_checked.md";
  private static final String INIT_SOLID = "sol201";
  private static final String STUDY = "std576b_dynamic_load_verification";
  private static final String DATASET = "dset576b_dynamic_load_verification";
  private static final double TARGET = 0.03;

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
    model.param().set("q_scale574", "-10");
    model.param().set("alpha_pfb576a", "0.183");
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "1");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    try { comp.variable("var_dynamic_motion572").set("tau572", "t"); } catch (Exception ignored) {}
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
      System.out.println("REGION_CONFIG_FAILED=" + error.getMessage());
    }
    try {
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
            "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
          });
    } catch (Exception ignored) {}
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
      for (String childTag : contact.feature().tags()) {
        PhysicsFeature child = contact.feature(childTag);
        String type = child.getType();
        String label = child.label();
        if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) child.active(false);
      }
    } catch (Exception ignored) {}
  }

  private static String buildTransient(Model model, String initSol) {
    removeStudy(model, STUDY);
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 576b dynamic load verification");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
    model.study(STUDY).feature("time").set(
        "activate", new String[] {
          "solid", "off", "ge_force_total111", "off", "tff", "on",
          "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
        });
    model.study(STUDY).feature("time").set("useinitsol", "on");
    model.study(STUDY).feature("time").set("initmethod", "sol");
    model.study(STUDY).feature("time").set("initsol", initSol);
    model.study(STUDY).feature("time").set("initsoluse", "current");
    model.study(STUDY).feature("time").set("initsolusesolnum", "last");
    String step = STUDY + "/time";
    ModelNode comp = model.component("comp1");
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(STUDY).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", initSol);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", initSol);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      try { time.create("fc1", "FullyCoupled"); } catch (Exception ignored) {}
    }
    try {
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.5");
      time.feature("fc1").set("maxiter", 150);
    } catch (Exception ignored) {}
    return sol;
  }

  private static double[][] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] intPatch(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double min(double[] values) {
    double result = Double.POSITIVE_INFINITY;
    for (double value : values) result = Math.min(result, value);
    return result;
  }

  private static double max(double[] values) {
    double result = Double.NEGATIVE_INFINITY;
    for (double value : values) result = Math.max(result, value);
    return result;
  }

  private static double mean(double[] values) {
    double result = 0;
    for (double value : values) result += value;
    return result / values.length;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  private static int countOutside(double[] values, double lo, double hi) {
    int count = 0;
    for (double value : values) if (value < lo || value > hi) count++;
    return count;
  }

  private static void writeReport(
      String sol, double fContact, double[] time, double[] fFilm, double[] fTotal,
      double[] maxP, double[] minTheta, double[] meanTheta, double[] meanCore,
      double[] minH, double[] maxH, boolean pass, int outsideCount) throws IOException {
    try (PrintWriter out = new PrintWriter(new FileWriter(REPORT))) {
      out.println("# Stage 576b: Dynamic Load Verification");
      out.println();
      out.println("Base: `" + BASE + "`");
      out.println();
      out.println("Output files:");
      out.println();
      out.println("```text");
      out.println(SETUP);
      out.println(RESULTS);
      if (pass) out.println(CHECKED);
      out.println(REPORT);
      out.println("```");
      out.println();
      out.println("Settings:");
      out.println();
      out.println("```text");
      out.println("q_scale574 = -10");
      out.println("alpha_pfb576a = 0.183");
      out.println("tau572 = t");
      out.println("lambda_h574 = 1");
      out.println("lambda_v574 = 1");
      out.println("solid = off");
      out.println("tff = on");
      out.println("fixed structural solution = " + INIT_SOLID);
      out.println("dynamic tff solution = " + sol);
      out.println("```");
      out.println();
      out.println("Summary:");
      out.println();
      out.println("| quantity | value |");
      out.println("|---|---:|");
      out.printf(Locale.US, "| time range | %.12g to %.12g s |%n", min(time), max(time));
      out.printf(Locale.US, "| steps | %d |%n", time.length);
      out.printf(Locale.US, "| F_contact constant | %.12g N |%n", fContact);
      out.printf(Locale.US, "| F_film min/max/mean | %.12g / %.12g / %.12g N |%n", min(fFilm), max(fFilm), mean(fFilm));
      out.printf(Locale.US, "| F_total min/max/mean | %.12g / %.12g / %.12g N |%n", min(fTotal), max(fTotal), mean(fTotal));
      out.printf(Locale.US, "| F_total outside 0.025-0.035 N | %d steps |%n", outsideCount);
      out.printf(Locale.US, "| max pressure range | %.12g to %.12g Pa |%n", min(maxP), max(maxP));
      out.printf(Locale.US, "| min theta range | %.12g to %.12g |%n", min(minTheta), max(minTheta));
      out.printf(Locale.US, "| mean theta range | %.12g to %.12g |%n", min(meanTheta), max(meanTheta));
      out.printf(Locale.US, "| mean M_core range | %.12g to %.12g |%n", min(meanCore), max(meanCore));
      out.printf(Locale.US, "| h_calc min range | %.12g to %.12g |%n", min(minH), max(minH));
      out.printf(Locale.US, "| h_calc max range | %.12g to %.12g |%n", min(maxH), max(maxH));
      out.println();
      out.println("Check:");
      out.println();
      out.println("```text");
      out.println(pass ? "PASS" : "FAIL");
      out.println("```");
      out.println();
      if (!pass) {
        out.println("The dynamic solution is retained as a diagnostic result, but not a checked dynamic load-control base.");
      }
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), INIT_SOLID)) throw new IllegalStateException("Missing initial solid solution " + INIT_SOLID);
      configure(model, comp);
      System.out.println("BASE=" + BASE);
      System.out.println("INIT_SOLID=" + INIT_SOLID);
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      model.label("Stage 576b dynamic load verification setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);

      String sol = buildTransient(model, INIT_SOLID);
      System.out.println("RUN_TRANSIENT_SOL=" + sol);
      model.sol(sol).runAll();
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", sol);
      double[] time = evalGlobal(model, DATASET, "eval576b_time", new String[] {"t"})[0];
      double fContact = evalGlobal(model, DATASET, "eval576b_contact", new String[] {"withsol('" + INIT_SOLID + "',Fn_contact570)"})[0][0];
      double[][] patch = intPatch(model, DATASET, "int576b_patch",
          new String[] {"1", "p_load573", "M_core573", "tff.theta"});
      double area = patch[0][0];
      double[] fFilm = patch[1];
      double[] meanCore = new double[fFilm.length];
      double[] meanTheta = new double[fFilm.length];
      double[] fTotal = new double[fFilm.length];
      for (int i = 0; i < fFilm.length; i++) {
        meanCore[i] = patch[2][i] / area;
        meanTheta[i] = patch[3][i] / area;
        fTotal[i] = fContact + fFilm[i];
      }
      double[] maxP = surface(model, DATASET, "max576b_p", "MaxSurface", "tff.p-p_amb573")[0];
      double[] minTheta = surface(model, DATASET, "min576b_theta", "MinSurface", "tff.theta")[0];
      double[] minH = surface(model, DATASET, "min576b_h", "MinSurface", "h_calc573")[0];
      double[] maxH = surface(model, DATASET, "max576b_h", "MaxSurface", "h_calc573")[0];
      int outsideCount = countOutside(fTotal, 0.025, 0.035);
      boolean numericallyStable = finite(fFilm) && finite(fTotal) && finite(maxP) && finite(minTheta)
          && finite(meanTheta) && finite(meanCore) && finite(minH) && finite(maxH)
          && min(minTheta) >= -1e-8 && max(meanCore) > 0.1;
      boolean loadPass = outsideCount == 0;
      boolean pass = numericallyStable && loadPass;
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", min(time), max(time), time.length);
      System.out.printf(Locale.US, "F_CONTACT=%.12g%n", fContact);
      System.out.printf(Locale.US, "F_FILM_RANGE=[%.12g,%.12g] MEAN=%.12g%n", min(fFilm), max(fFilm), mean(fFilm));
      System.out.printf(Locale.US, "F_TOTAL_RANGE=[%.12g,%.12g] MEAN=%.12g OUTSIDE=%d%n", min(fTotal), max(fTotal), mean(fTotal), outsideCount);
      System.out.printf(Locale.US, "MAXP_RANGE=[%.12g,%.12g]%n", min(maxP), max(maxP));
      System.out.printf(Locale.US, "MIN_THETA_RANGE=[%.12g,%.12g]%n", min(minTheta), max(minTheta));
      System.out.printf(Locale.US, "MEAN_CORE_RANGE=[%.12g,%.12g]%n", min(meanCore), max(meanCore));
      System.out.println("NUMERIC_STATUS=" + (numericallyStable ? "PASS" : "FAIL"));
      System.out.println("LOAD_BAND_STATUS=" + (loadPass ? "PASS" : "FAIL"));
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      writeReport(sol, fContact, time, fFilm, fTotal, maxP, minTheta, meanTheta, meanCore, minH, maxH, pass, outsideCount);
      System.out.println("SAVED_REPORT=" + REPORT);
      if (pass) {
        model.label("Stage 576b dynamic load verification checked");
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
