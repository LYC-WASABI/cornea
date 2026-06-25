import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574p_load_closed_true_gap_scan {
  private static final String BASE = "574o_stage574_fixed_structure_true_gap_from_003N_checked.mph";
  private static final String SETUP = "574p_stage574_load_closed_true_gap_setup.mph";
  private static final String RESULTS = "574p_stage574_load_closed_true_gap_results.mph";
  private static final String CHECKED = "574p_stage574_load_closed_true_gap_checked.mph";
  private static final String REPORT = "574p_stage574_load_closed_true_gap_checked.md";
  private static final double TARGET = 0.03;
  private static final double[] Q_LIST = new double[] {
    -6.5, -5.5, -5.0, -4.0, -3.0
  };
  private static final double[] VELOCITY = new double[] {
    0, 0.02, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4, 0.65, 1.0
  };
  private static final List<String> report = new ArrayList<>();
  private static final List<double[]> rows = new ArrayList<>();

  private static void line(String text) {
    report.add(text);
    System.out.println(text);
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

  private static void writeReport() {
    try {
      java.nio.file.Files.write(
          java.nio.file.Paths.get(REPORT),
          report,
          java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception error) {
      System.out.println("REPORT_WRITE_FAILED=" + error.getMessage());
    }
  }

  private static void forceContactSettings(ModelNode comp) {
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    contact.set("pairSelection", "list");
    contact.set("pairs", new String[] {"cp_lid_cornea"});
    try { contact.set("useCutback", "1"); } catch (Exception ignored) {}
    for (String childTag : contact.feature().tags()) {
      PhysicsFeature child = contact.feature(childTag);
      String type = child.getType();
      String label = child.label();
      if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) {
        child.active(false);
        line("- friction off: `" + childTag + "`");
      }
    }
  }

  private static void setIndentedDisplacement(ModelNode comp) {
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
  }

  private static void configureTff(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("time_offset572", "T_pre572+0.5*T_slide572");
    model.param().set("lambda_v574", "0");
    model.param().set("lambda_h574", "1");
    try { comp.variable("var_dynamic_motion572").set("tau572", "time_offset572"); }
    catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "0");
    } catch (Exception error) {
      line("- local mask override failed: `" + error.getMessage() + "`");
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
    catch (Exception ignored) {}
    for (String tag : new String[] {
        "bdr_inlet520", "bdr_outlet520", "bdr_left520", "bdr_right520"
    }) {
      try { comp.physics("tff").feature(tag).set("pf0", "p_amb573"); }
      catch (Exception ignored) {}
      try { comp.physics("tff").feature(tag).set("theta_0", "1"); }
      catch (Exception ignored) {}
    }
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "lambda_h574*Qvent573"); }
    catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
  }

  private static String initialSolidSolution(Model model) {
    if (has(model.sol().tags(), "sol99")) return "sol99";
    if (has(model.sol().tags(), "sol94")) return "sol94";
    String[] tags = model.sol().tags();
    if (tags.length == 0) throw new IllegalStateException("No initial solution exists");
    return tags[tags.length - 1];
  }

  private static String buildSolidStudy(Model model, String initSol, int index) {
    String study = "std574p_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574p solid contact q step " + index);
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
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
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
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.05");
    stat.feature("fc1").set("maxiter", 300);
    return sol;
  }

  private static String buildTffStudy(Model model, String initSol, int qIndex, int vIndex) {
    String study = "std574p_tff_" + qIndex + "_" + vIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574p true-gap TFF q " + qIndex + " velocity " + vIndex);
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
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.3");
    stat.feature("fc1").set("maxiter", 300);
    return sol;
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

  private static String dataset(Model model, String tag, String sol) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", sol);
    return tag;
  }

  private static double[] evaluateRow(Model model, String solidSol, String tffSol, int index, double q) {
    String solidData = dataset(model, "dset574p_solid_" + index, solidSol);
    String tffData = dataset(model, "dset574p_tff_" + index, tffSol);
    double[] g = evalGlobal(model, solidData, "eval574p_solid_" + index,
        new String[] {"q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"});
    double[] patch = intPatch(model, tffData, "int574p_tff_" + index,
        new String[] {
          "1",
          "if(isdefined(geomgap_dst_cp_lid_cornea),if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)",
          "max(tff.p-p_amb573,0[Pa])",
          "p_load573",
          "Bfilm573",
          "Afilm573",
          "tff.theta",
          "h_calc573"
        });
    double area = patch[0];
    double gapCoverage = patch[1] / area;
    double positiveLoad = patch[2];
    double filmLoad = patch[3];
    double meanB = patch[4] / area;
    double meanA = patch[5] / area;
    double meanTheta = patch[6] / area;
    double meanH = patch[7] / area;
    double maxP = surface(model, tffData, "max574p_p_" + index, "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, tffData, "min574p_theta_" + index, "MinSurface", "tff.theta");
    double minGap = surface(model, solidData, "min574p_gap_" + index, "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxTn = surface(model, solidData, "max574p_tn_" + index, "MaxSurface", "solid.Tn");
    double total = g[2] + filmLoad;
    line(String.format(Locale.US,
        "| %.8g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %s | %s |",
        q, g[1], g[2], filmLoad, total, positiveLoad, maxP, meanB, meanTheta,
        minTheta, minGap, solidSol, tffSol));
    return new double[] {
      q, g[1], g[2], filmLoad, total, positiveLoad, maxP, meanB, meanA,
      meanTheta, minTheta, gapCoverage, minGap, maxTn, meanH
    };
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      report.add("# Stage 574p fixed-structure true-gap load-closure scan");
      report.add("");
      line("- Base: `" + BASE + "`");
      line("- Initial solid solution: `" + initialSolidSolution(model) + "`");
      line("- Local patch: `" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)) + "`");
      line("- Target total load: `" + TARGET + " N`");
      forceContactSettings(comp);
      setIndentedDisplacement(comp);
      configureTff(model, comp);
      model.label("Stage 574p load-closed true-gap setup");
      model.save(SETUP);
      line("- Saved setup: `" + SETUP + "`");
      line("");
      line("## Scan Table");
      line("");
      line("| q_scale574 | displacement | F_contact [N] | F_film [N] | F_total [N] | positive pressure integral [N] | max pressure [Pa] | mean Bfilm | mean theta | min theta | min gap | solid sol | tff sol |");
      line("|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|---|");

      String solidInit = initialSolidSolution(model);
      for (int i = 0; i < Q_LIST.length; i++) {
        double q = Q_LIST[i];
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String solidSol = buildSolidStudy(model, solidInit, i);
        line("- Running solid q=`" + q + "` init=`" + solidInit + "` sol=`" + solidSol + "`");
        model.sol(solidSol).runAll();
        String tffInit = solidSol;
        String tffSol = null;
        for (int v = 0; v < VELOCITY.length; v++) {
          double lv = VELOCITY[v];
          model.param().set("lambda_h574", "1");
          model.param().set("lambda_v574", String.format(Locale.US, "%.12g", lv));
          tffSol = buildTffStudy(model, tffInit, i, v);
          line("- Running TFF q=`" + q + "` lambda_v=`" + lv + "` init=`" + tffInit + "` sol=`" + tffSol + "`");
          model.sol(tffSol).runAll();
          tffInit = tffSol;
        }
        rows.add(evaluateRow(model, solidSol, tffSol, i, q));
        solidInit = solidSol;
        model.save(RESULTS);
      }

      double[] best = null;
      for (double[] row : rows) {
        if (best == null || Math.abs(row[4] - TARGET) < Math.abs(best[4] - TARGET)) best = row;
      }
      boolean bracket = false;
      for (int i = 1; i < rows.size(); i++) {
        double a = rows.get(i - 1)[4] - TARGET;
        double b = rows.get(i)[4] - TARGET;
        if (a == 0 || b == 0 || a * b < 0) bracket = true;
      }
      boolean pass = best != null
          && Math.abs(best[4] - TARGET) <= 0.005
          && best[10] >= -1e-8
          && best[11] > 0.95
          && Double.isFinite(best[4])
          && Double.isFinite(best[6]);
      line("");
      line("## Selection");
      line("");
      if (best != null) {
        line("- Best q_scale574: `" + best[0] + "`");
        line("- displacement: `" + best[1] + "`");
        line("- F_contact: `" + best[2] + " N`");
        line("- F_film: `" + best[3] + " N`");
        line("- F_total: `" + best[4] + " N`");
        line("- error from 0.03 N: `" + (best[4] - TARGET) + " N`");
        line("- max pressure: `" + best[6] + " Pa`");
        line("- mean Bfilm573: `" + best[7] + "`");
        line("- mean theta: `" + best[9] + "`");
        line("- min theta: `" + best[10] + "`");
        line("- gap coverage: `" + best[11] + "`");
      }
      line("- bracketed target: `" + bracket + "`");
      line("- checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      model.save(RESULTS);
      line("- Saved results: `" + RESULTS + "`");
      if (pass) {
        model.label("Stage 574p load-closed true-gap checked");
        model.save(CHECKED);
        line("- Saved checked: `" + CHECKED + "`");
      }
      writeReport();
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      line("");
      line("## Failure");
      line("");
      line("```text");
      line(error.toString().replace("`", "'"));
      line("```");
      writeReport();
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
