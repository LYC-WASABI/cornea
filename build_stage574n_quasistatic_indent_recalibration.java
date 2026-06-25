import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574n_quasistatic_indent_recalibration {
  private static final String BASE = "574g_stage574_local_contact_gap_setup.mph";
  private static final String SETUP = "574n_stage574_quasistatic_indent_recalibration_setup.mph";
  private static final String RESULTS = "574n_stage574_quasistatic_indent_recalibration_results.mph";
  private static final String CHECKED = "574n_stage574_quasistatic_indent_contact_003N_checked.mph";
  private static final double[] Q_LIST = new double[] {
    -20, -15, -10, -7.5, -5, -3, -2, -1.5, -1, -0.5, 0, 0.25, 0.5, 0.75, 1.0
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

  private static String initialSolution(Model model) {
    if (has(model.sol().tags(), "sol94")) return "sol94";
    if (has(model.sol().tags(), "sol93")) return "sol93";
    String[] tags = model.sol().tags();
    if (tags.length == 0) throw new IllegalStateException("No initial solution exists");
    return tags[tags.length - 1];
  }

  private static String buildStudy(Model model, String initSol, int index) {
    String study = "std574n_indent_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 574n quasistatic indentation q step " + index);
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

  private static double[] evaluate(Model model, String sol, int index, double q) {
    String data = "dset574n_" + index;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", sol);
    double[] g = evalGlobal(model, data, "eval574n_" + index,
        new String[] {"q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"});
    double minGap = surface(model, data, "min574n_gap_" + index,
        "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxTn = surface(model, data, "max574n_tn_" + index,
        "MaxSurface", "solid.Tn");
    double[] patch = intPatch(model, data, "int574n_patch_" + index,
        new String[] {
          "1",
          "if(isdefined(solid.Tn),solid.Tn,0)",
          "if(isdefined(solid.Tn),if(solid.Tn>1[Pa],1,0),0)",
          "if(isdefined(geomgap_dst_cp_lid_cornea),if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)"
        });
    line(String.format(Locale.US,
        "| %.8g | %.12g | %.12g | %.12g | %.12g | %.12g | %.12g | %s |",
        q, g[1], g[2], minGap, maxTn, patch[1], patch[2], sol));
    return new double[] {q, g[1], g[2], minGap, maxTn, patch[1], patch[2], patch[3]};
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      report.add("# Stage 574n quasistatic indentation recalibration");
      report.add("");
      line("- Base: `" + BASE + "`");
      line("- Initial solution: `" + initialSolution(model) + "`");
      line("- Local patch: `" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)) + "`");
      forceContactSettings(comp);
      setIndentedDisplacement(comp);
      model.label("Stage 574n quasistatic indentation recalibration setup");
      model.save(SETUP);
      line("- Saved setup: `" + SETUP + "`");
      line("");
      line("## Scan Table");
      line("");
      line("| q_scale574 | displacement [model length] | Fn_contact570 [N] | min gap | max solid.Tn [Pa] | patch Tn integral [N] | active area | solution |");
      line("|---:|---:|---:|---:|---:|---:|---:|---|");

      String current = initialSolution(model);
      double[] best = null;
      String bestSol = null;
      for (int i = 0; i < Q_LIST.length; i++) {
        double q = Q_LIST[i];
        model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
        String sol = buildStudy(model, current, i);
        line("- Running q=`" + q + "` sol=`" + sol + "` init=`" + current + "`");
        try {
          model.sol(sol).runAll();
          double[] row = evaluate(model, sol, i, q);
          current = sol;
          if (best == null || Math.abs(row[2] - 0.03) < Math.abs(best[2] - 0.03)) {
            best = row;
            bestSol = sol;
          }
        } catch (Exception error) {
          line("| " + q + " | FAILED | FAILED | FAILED | FAILED | FAILED | FAILED | " + sol + " |");
          line("- Failed q=`" + q + "`: `" + error.toString().replace("`", "'") + "`");
        }
      }
      model.save(RESULTS);
      line("");
      line("## Selection");
      line("");
      boolean pass = best != null && best[2] > 0.025 && best[2] < 0.035;
      if (best != null) {
        line("- Best indentation point:");
        line("  - q_scale574: `" + best[0] + "`");
        line("  - displacement: `" + best[1] + "`");
        line("  - Fn_contact570: `" + best[2] + " N`");
        line("  - min gap: `" + best[3] + "`");
        line("  - max solid.Tn: `" + best[4] + " Pa`");
        line("  - patch Tn integral: `" + best[5] + " N`");
        line("  - active area: `" + best[6] + "`");
        line("  - solution: `" + bestSol + "`");
      }
      line("- checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      if (pass) {
        model.label("Stage 574n quasistatic indentation contact 0.03N checked");
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
