import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class scan_stage574_rebuilt_pair_indentation {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static double evaluateForce(
      Model model, String solution, String suffix) {
    String dataset = "dset574scan_" + suffix;
    String eval = "eval574scan_" + suffix;
    try { model.result().dataset().remove(dataset); }
    catch (Exception ignored) {}
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).set("solution", solution);
    try { model.result().numerical().remove(eval); }
    catch (Exception ignored) {}
    model.result().numerical().create(eval, "EvalGlobal");
    model.result().numerical(eval).set("data", dataset);
    model.result().numerical(eval).set("expr", "Fn_contact570");
    return model.result().numerical(eval).getReal()[0][0];
  }

  private static String solve(
      Model model, ModelNode comp, int index, String initial) {
    String study = "std574scan_" + index;
    model.study().create(study);
    model.study(study).label(
        "Stage 574 rebuilt-pair indentation scan " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat")
        .set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "on", "ge_force_total111", "off", "tff", "off",
          "frame:spatial1", "on", "frame:material1", "on",
          "comp1", "on"
        });
    if (index == 0) {
      model.study(study).feature("stat").set("useinitsol", "off");
    } else {
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", initial);
      model.study(study).feature("stat").set("initsoluse", "current");
    }
    String step = study + "/stat";
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
      comp.physics("solid").feature(tag).set("StudyStep", step);
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    if (index == 0) {
      dependent.set("initmethod", "init");
    } else {
      dependent.set("initmethod", "sol");
      dependent.set("initsol", initial);
      dependent.set("solnum", "last");
    }
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", "sol93");
    dependent.set("notsolnum", "last");
    SolverFeature stationary = model.sol(solution).feature("s1");
    for (String tag : stationary.feature().tags()) {
      if (tag.startsWith("se")) {
        try { stationary.feature().remove(tag); }
        catch (Exception ignored) {}
      }
    }
    if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", "0.1");
    stationary.feature("fc1").set("maxiter", 300);
    model.sol(solution).runAll();
    return solution;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574f_stage574_local_cornea_patch_structure_setup.mph");
      ModelNode comp = model.component("comp1");
      comp.physics("solid").feature("dcnt1")
          .feature("fric_partitioned_stabilizer").active(false);
      double[] qValues = new double[] {
          -0.050, -0.030, -0.020, -0.015, -0.010,
          -0.0075, -0.0050, -0.0030, -0.0020, -0.0010,
          0.0, 0.00025, 0.00050, 0.00075, 0.0010
      };
      model.param().set("q_fixed574", "-0.050");
      comp.physics("solid").feature("dcnt1")
          .set("pairSelection", "list");
      comp.physics("solid").feature("dcnt1")
          .set("pairs", new String[] {});
      String initial = solve(model, comp, 0, "sol93");
      comp.physics("solid").feature("dcnt1")
          .set("pairs", new String[] {"cp_lid_cornea"});
      System.out.println("SEPARATED_INITIAL=" + initial);
      double bestError = Double.POSITIVE_INFINITY;
      double bestQ = Double.NaN;
      String bestSolution = null;
      for (int i = 0; i < qValues.length; i++) {
        double q = qValues[i];
        model.param().set(
            "q_fixed574", String.format(Locale.US, "%.12g", q));
        String solution = solve(model, comp, i + 1, initial);
        double force = evaluateForce(
            model, solution, Integer.toString(i + 1));
        System.out.printf(Locale.US,
            "SCAN q=%.12g force=%.12g solution=%s%n",
            q, force, solution);
        double error = Math.abs(force - 0.03);
        if (error < bestError) {
          bestError = error;
          bestQ = q;
          bestSolution = solution;
        }
        initial = solution;
      }
      model.param().set(
          "q_fixed574", String.format(Locale.US, "%.12g", bestQ));
      System.out.printf(Locale.US,
          "BEST q=%.12g error=%.12g solution=%s%n",
          bestQ, bestError, bestSolution);
      model.label("Stage 574 rebuilt contact-pair indentation scan");
      model.save("574k_stage574_rebuilt_pair_indentation_scan.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
