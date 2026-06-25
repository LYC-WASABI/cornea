import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574m_direct_normal_release {
  private static final String BASE = "574j_stage574_fixed_structure_true_gap_jfo_checked.mph";
  private static final String SETUP = "574m_stage574_direct_normal_release_setup.mph";
  private static final String RESULTS = "574m_stage574_direct_normal_release_results.mph";
  private static final String CHECKED = "574m_stage574_direct_normal_release_checked.mph";
  private static final double[] RELEASE_UM = new double[] {
    0, 5, 10, 20, 30, 50, 75, 100, 150, 200, 300, 400, 600
  };
  private static final List<String> report = new ArrayList<>();

  private static void line(String text) {
    report.add(text);
    System.out.println(text);
  }

  private static boolean has(String[] tags, String tag) {
    for (String value : tags) if (value.equals(tag)) return true;
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

  private static void setBaseDisplacement(ModelNode comp) {
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
  }

  private static void createDirectRelease(Model model, ModelNode comp) {
    model.param().set("q_scale574", "0");
    model.param().set("delta_lid_normal574m", "0[um]");
    try { comp.physics("solid").feature().remove("disp_lid_normal574m"); }
    catch (Exception ignored) {}
    comp.physics("solid").create("disp_lid_normal574m", "Displacement2", 2);
    PhysicsFeature release = comp.physics("solid").feature("disp_lid_normal574m");
    release.label("Stage 574m direct lid-source normal release");
    release.selection().set(new int[] {42, 44, 47, 48});
    release.set("U0", new String[] {
      "0",
      "delta_lid_normal574m*Y/sqrt(Y^2+Z^2)",
      "delta_lid_normal574m*Z/sqrt(Y^2+Z^2)"
    });
  }

  private static String buildStudy(Model model, String initSol, int index) {
    String study = "std574m_release_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574m direct normal release step " + index);
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
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "disp_lid_normal574m"}) {
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

  private static double surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
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

  private static double[] evaluate(Model model, String sol, int index, double releaseUm) {
    String data = "dset574m_" + index;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", sol);
    double[] g = evalGlobal(model, data, "eval574m_" + index,
        new String[] {"delta_lid_normal574m", "Fn_contact570"});
    double minGap = surface(model, data, "min574m_gap_" + index,
        "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxTn = surface(model, data, "max574m_tn_" + index,
        "MaxSurface", "solid.Tn");
    double[] patch = intPatch(model, data, "int574m_patch_" + index,
        new String[] {
          "1",
          "if(isdefined(solid.Tn),solid.Tn,0)",
          "if(isdefined(solid.Tn),if(solid.Tn>1[Pa],1,0),0)"
        });
    line(String.format(Locale.US,
        "| %.6g | %.12g | %.12g | %.12g | %.12g | %.12g | %s |",
        releaseUm, g[0], g[1], minGap, maxTn, patch[2], sol));
    return new double[] {releaseUm, g[0], g[1], minGap, maxTn, patch[2]};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      report.add("# Stage 574m direct normal release preload calibration");
      report.add("");
      line("- Base: `" + BASE + "`");
      line("- Direct release source boundaries: `[42, 44, 47, 48]`");
      forceContactSettings(comp);
      setBaseDisplacement(comp);
      createDirectRelease(model, comp);
      model.label("Stage 574m direct normal release setup");
      model.save(SETUP);
      line("- Saved setup: `" + SETUP + "`");
      line("");
      line("## Scan Table");
      line("");
      line("| release [um] | delta_lid_normal574m [m] | Fn_contact570 [N] | min gap | max solid.Tn [Pa] | active area | solution |");
      line("|---:|---:|---:|---:|---:|---:|---|");

      String current = has(model.sol().tags(), "sol109") ? "sol109" : "sol110";
      double[] best = null;
      String bestSol = null;
      for (int i = 0; i < RELEASE_UM.length; i++) {
        double release = RELEASE_UM[i];
        model.param().set("delta_lid_normal574m", String.format(Locale.US, "%.12g[um]", release));
        String sol = buildStudy(model, current, i);
        line("- Running direct release=`" + release + "um` sol=`" + sol + "` init=`" + current + "`");
        model.sol(sol).runAll();
        double[] row = evaluate(model, sol, i, release);
        current = sol;
        if (best == null || Math.abs(row[2] - 0.03) < Math.abs(best[2] - 0.03)) {
          best = row;
          bestSol = sol;
        }
      }
      model.save(RESULTS);
      line("");
      line("## Selection");
      line("");
      boolean pass = best != null && best[2] > 0.02 && best[2] < 0.035;
      if (best != null) {
        line("- Best structural preload point:");
        line("  - release: `" + best[0] + " um`");
        line("  - delta_lid_normal574m: `" + best[1] + " m`");
        line("  - Fn_contact570: `" + best[2] + " N`");
        line("  - min gap: `" + best[3] + "`");
        line("  - max solid.Tn: `" + best[4] + " Pa`");
        line("  - active area: `" + best[5] + "`");
        line("  - solution: `" + bestSol + "`");
      }
      line("- checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      if (pass) {
        model.label("Stage 574m direct normal release preload calibrated checked");
        model.save(CHECKED);
        line("- Saved checked: `" + CHECKED + "`");
      } else {
        line("- Checked model not saved.");
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
