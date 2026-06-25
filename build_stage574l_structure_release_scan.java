import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574l_structure_release_scan {
  private static final String BASE = "574j_stage574_fixed_structure_true_gap_jfo_checked.mph";
  private static final String SETUP = "574l_stage574_structure_release_scan_setup.mph";
  private static final String RESULTS = "574l_stage574_structure_release_scan_results.mph";
  private static final String CHECKED = "574l_stage574_structure_release_scan_checked.mph";
  private static final double[] Q_LIST = new double[] {
    0.0, -0.015, -0.025, -0.035, -0.05, -0.075, -0.10, -0.15, -0.20, -0.30, -0.40
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

  private static void setIndentedDisplacement(ModelNode comp) {
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
  }

  private static String buildStudy(Model model, String initSol, int index) {
    String study = "std574l_release_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574l structure release q step " + index);
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

  private static double[] evaluate(Model model, String sol, int index, double q) {
    String data = "dset574l_" + index;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", sol);
    double[] g = evalGlobal(model, data, "eval574l_" + index,
        new String[] {"q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"});
    double minGap = surface(model, data, "min574l_gap_" + index,
        "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxTn = surface(model, data, "max574l_tn_" + index,
        "MaxSurface", "solid.Tn");
    line(String.format(Locale.US,
        "| %.6g | %.12g | %.12g | %.12g | %.12g | %s |",
        q, g[1], g[2], minGap, maxTn, sol));
    return new double[] {q, g[1], g[2], minGap, maxTn};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      report.add("# Stage 574l structure-only release scan");
      report.add("");
      line("- Base: `" + BASE + "`");
      forceContactSettings(comp);
      setIndentedDisplacement(comp);
      model.label("Stage 574l structure release scan setup");
      model.save(SETUP);
      line("- Saved setup: `" + SETUP + "`");
      line("");
      line("## Scan Table");
      line("");
      line("| q_scale574 | displacement [model length] | Fn_contact570 [N] | min gap | max solid.Tn [Pa] | solution |");
      line("|---:|---:|---:|---:|---:|---|");

      String current = has(model.sol().tags(), "sol109") ? "sol109" : "sol110";
      double[] best = null;
      String bestSol = null;
      for (int i = 0; i < Q_LIST.length; i++) {
        double q = Q_LIST[i];
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String sol = buildStudy(model, current, i);
        line("- Running q=`" + q + "` sol=`" + sol + "` init=`" + current + "`");
        model.sol(sol).runAll();
        double[] row = evaluate(model, sol, i, q);
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
        line("- Best structural release point:");
        line("  - q_scale574: `" + best[0] + "`");
        line("  - displacement: `" + best[1] + "`");
        line("  - Fn_contact570: `" + best[2] + " N`");
        line("  - min gap: `" + best[3] + "`");
        line("  - max solid.Tn: `" + best[4] + " Pa`");
        line("  - solution: `" + bestSol + "`");
      }
      line("- checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      if (pass) {
        model.label("Stage 574l structure release scan checked");
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
