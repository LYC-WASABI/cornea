import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574k_gap_regularized_release_scan {
  private static final String BASE = "574j_stage574_fixed_structure_true_gap_jfo_checked.mph";
  private static final String SETUP = "574k_stage574_gap_regularized_release_scan_setup.mph";
  private static final String RESULTS = "574k_stage574_gap_regularized_release_scan_results.mph";
  private static final String CHECKED = "574k_stage574_gap_regularized_release_scan_checked.mph";
  private static final double[] Q_LIST = new double[] {
    -0.02, -0.015, -0.01, -0.0075, -0.005, -0.0025, 0.0
  };
  private static final double[] HMAX_UM = new double[] {100.0, 50.0, 30.0};
  private static final List<String> report = new ArrayList<>();

  private static void line(String text) {
    report.add(text);
    System.out.println(text);
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

  private static void forceContactSettings(ModelNode comp) {
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

  private static void configureRegularizedTff(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("time_offset572", "T_pre572+0.5*T_slide572");
    model.param().set("lambda_v574", "1");
    model.param().set("lambda_h574", "1");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    try { comp.variable("var_dynamic_motion572").set("tau572", "time_offset572"); }
    catch (Exception ignored) {}
    try {
      comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573").set("M_core573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "1");
      comp.variable("var_cornea_dynamic_regions573").set("M_open573", "0");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_low573",
          "g_pair_valid573*0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "B_high573",
          "0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Bfilm573", "B_low573*B_high573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "g_pair_physical573", "min(g_pair_safe573,h_active_max573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_wet573",
          "h_num573+0.5*((g_pair_physical573-h_num573)"
              + "+sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Afilm573", "M_core573*Bfilm573+max(M_drain573-M_core573,0)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "h_calc573", "Afilm573*h_wet573+(1-Afilm573)*h_background573");
      comp.variable("var_cornea_dynamic_regions573").set(
          "Qvent573", "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
      comp.variable("var_cornea_dynamic_regions573").set(
          "p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");
    } catch (Exception error) {
      line("- regularization variable override failed: `" + error.getMessage().replace("`", "'") + "`");
    }

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
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
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "Qvent573"); }
    catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
  }

  private static String buildSolidStudy(Model model, String initSol, int index) {
    String study = "std574k_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574k release solid q step " + index);
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
      if (tag.startsWith("se")) {
        try { stat.feature().remove(tag); } catch (Exception ignored) {}
      }
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.05");
    stat.feature("fc1").set("maxiter", 300);
    return sol;
  }

  private static String buildTffStudy(Model model, String initSol, int qIndex, int hIndex) {
    String study = "std574k_tff_" + qIndex + "_" + hIndex;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574k regularized TFF q " + qIndex + " h " + hIndex);
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
      if (tag.startsWith("se")) {
        try { stat.feature().remove(tag); } catch (Exception ignored) {}
      }
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.6");
    stat.feature("fc1").set("maxiter", 200);
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

  private static double[] evaluate(
      Model model, String solidSol, String tffSol, int qIndex, int hIndex,
      double q, double hmaxUm) {
    String dSolid = "dset574k_solid_" + qIndex + "_" + hIndex;
    String dTff = "dset574k_tff_" + qIndex + "_" + hIndex;
    removeDataset(model, dSolid);
    removeDataset(model, dTff);
    model.result().dataset().create(dSolid, "Solution");
    model.result().dataset(dSolid).set("solution", solidSol);
    model.result().dataset().create(dTff, "Solution");
    model.result().dataset(dTff).set("solution", tffSol);
    double[] g = evalGlobal(model, dSolid, "eval574k_g_" + qIndex + "_" + hIndex,
        new String[] {"q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"});
    double[] p = intPatch(model, dTff, "int574k_p_" + qIndex + "_" + hIndex,
        new String[] {
          "1",
          "p_load573",
          "max(tff.p-p_amb573,0[Pa])",
          "Bfilm573",
          "Afilm573",
          "h_calc573",
          "tff.theta"
        });
    double area = p[0];
    double film = p[1];
    double positive = p[2];
    double meanB = p[3] / area;
    double meanA = p[4] / area;
    double meanH = p[5] / area;
    double meanTheta = p[6] / area;
    double maxH = surface(model, dTff, "max574k_h_" + qIndex + "_" + hIndex,
        "MaxSurface", "h_calc573");
    double minH = surface(model, dTff, "min574k_h_" + qIndex + "_" + hIndex,
        "MinSurface", "h_calc573");
    double maxP = surface(model, dTff, "max574k_p_" + qIndex + "_" + hIndex,
        "MaxSurface", "tff.p-p_amb573");
    double maxPload = surface(model, dTff, "max574k_pload_" + qIndex + "_" + hIndex,
        "MaxSurface", "p_load573");
    double minTheta = surface(model, dTff, "min574k_theta_" + qIndex + "_" + hIndex,
        "MinSurface", "tff.theta");
    double minGap = surface(model, dSolid, "min574k_gap_" + qIndex + "_" + hIndex,
        "MinSurface", "geomgap_dst_cp_lid_cornea");
    double total = g[2] + film;
    line(String.format(Locale.US,
        "| %.6g | %.3f | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g |",
        q, hmaxUm, g[1], g[2], film, total, positive, minH, maxH, maxP, meanB, minTheta));
    return new double[] {
      q, hmaxUm, g[1], g[2], film, total, positive,
      minH, maxH, maxP, maxPload, meanB, meanA, meanH, meanTheta, minTheta, minGap
    };
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      report.add("# Stage 574k gap-regularized release scan");
      report.add("");
      line("- Base: `" + BASE + "`");
      line("- Local patch: `" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)) + "`");
      forceContactSettings(comp);
      setIndentedDisplacement(comp);
      configureRegularizedTff(model, comp);
      model.label("Stage 574k gap-regularized release scan setup");
      model.save(SETUP);
      line("- Saved setup: `" + SETUP + "`");
      line("");
      line("## Scan Table");
      line("");
      line("| q_scale574 | h_active_max [um] | disp [model length] | F_contact [N] | F_film [N] | F_total [N] | positive pressure integral [N] | min h [model length] | max h [model length] | max p [Pa] | mean Bfilm | min theta |");
      line("|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|");

      String solidInit = has(model.sol().tags(), "sol109") ? "sol109" : "sol110";
      double[] best = null;
      String bestSolid = null;
      String bestTff = null;
      for (int qi = 0; qi < Q_LIST.length; qi++) {
        double q = Q_LIST[qi];
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String solid = buildSolidStudy(model, solidInit, qi);
        line("- Running solid q=`" + q + "` sol=`" + solid + "` init=`" + solidInit + "`");
        model.sol(solid).runAll();
        solidInit = solid;
        for (int hi = 0; hi < HMAX_UM.length; hi++) {
          double h = HMAX_UM[hi];
          model.param().set("h_active_max573", String.format(Locale.US, "%.12g[um]", h));
          String tff = buildTffStudy(model, solid, qi, hi);
          line("- Running TFF q=`" + q + "` hmax=`" + h + "um` sol=`" + tff + "` init=`" + solid + "`");
          try {
            model.sol(tff).runAll();
            double[] row = evaluate(model, solid, tff, qi, hi, q, h);
            if (h == 50.0) {
              if (best == null || Math.abs(row[5] - 0.03) < Math.abs(best[5] - 0.03)) {
                best = row;
                bestSolid = solid;
                bestTff = tff;
              }
            }
          } catch (Exception error) {
            line(String.format(Locale.US,
                "| %.6g | %.3f | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED |",
                q, h));
            line("- TFF failed for q=`" + q + "`, hmax=`" + h + "um`: `"
                + error.toString().replace("`", "'") + "`");
          }
        }
      }
      model.save(RESULTS);
      line("");
      line("## Selection");
      line("");
      if (best != null) {
        line("- Best h_active_max=50um row by total-load distance to 0.03 N:");
        line("  - q_scale574: `" + best[0] + "`");
        line("  - displacement: `" + best[2] + " m`");
        line("  - F_contact: `" + best[3] + " N`");
        line("  - F_film: `" + best[4] + " N`");
        line("  - F_total: `" + best[5] + " N`");
        line("  - solid solution: `" + bestSolid + "`");
        line("  - TFF solution: `" + bestTff + "`");
      }
      boolean pass = best != null
          && best[5] > 0.025 && best[5] < 0.04
          && best[8] < 1e-3
          && Double.isFinite(best[9])
          && best[15] > -1e-8;
      line("- Checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      if (pass) {
        model.label("Stage 574k gap-regularized release scan checked");
        model.save(CHECKED);
        line("- Saved checked: `" + CHECKED + "`");
      } else {
        line("- Checked model not saved because acceptance failed.");
      }
      for (String text : report) {
        // Report is mirrored on stdout; COMSOL Java file writes can be blocked.
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
