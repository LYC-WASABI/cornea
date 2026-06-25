import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class continue_stage574_contact_load {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static String solve(
      Model model, String initialSolution,
      String suffix, double loadFraction) throws java.io.IOException {
    ModelNode comp = model.component("comp1");
    model.param().set(
        "lambda_load574", Double.toString(loadFraction));
    String study = "std574_load_" + suffix;
    try { model.study().remove(study); } catch (Exception ignored) {}
    model.study().create(study);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat")
        .set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "on", "ge_force_total111", "on", "tff", "off",
          "frame:spatial1", "on", "frame:material1", "on",
          "comp1", "on"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat")
        .set("initsol", initialSolution);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
      comp.physics("solid").feature(tag).set("StudyStep", step);
    }
    comp.physics("ge_force_total111").feature("ge1")
        .set("StudyStep", step);

    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", initialSolution);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", initialSolution);
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
    stationary.feature("fc1").set("maxiter", 500);
    System.out.println(
        "RUN LOAD=" + loadFraction + " SOLUTION=" + solution
        + " INITIAL=" + initialSolution);
    model.sol(solution).runAll();
    model.save(
        "574_load_" + suffix + "_stage574_contact_results.mph");
    return solution;
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double value(
      Model model, String tag, String type,
      String dataset, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named("sel_lid_film574");
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model",
          "build_stage574_inset_source_film_output2_Model.mph");
      model.param().set(
          "F_total_target", "lambda_load574*0.03[N]");
      String solution = "sol95";
      solution = solve(model, solution, "080", 0.80);
      solution = solve(model, solution, "090", 0.90);
      solution = solve(model, solution, "100", 1.00);

      String dataset = "dset574_contact_final";
      removeDataset(model, dataset);
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);
      Pair pair = model.component("comp1").pair("cp_lid_cornea");
      String gap = pair.gapName(false);
      double area = value(
          model, "load_area", "IntSurface", dataset, "1");
      double defined = value(
          model, "load_defined", "IntSurface", dataset,
          "isdefined(" + gap + ")");
      double minimum = value(
          model, "load_min", "MinSurface", dataset, gap);
      double maximum = value(
          model, "load_max", "MaxSurface", dataset, gap);
      System.out.println("FINAL_SOLUTION=" + solution);
      System.out.println("DEFINED_FRACTION=" + defined / area);
      System.out.println("MIN_GAP=" + minimum);
      System.out.println("MAX_GAP=" + maximum);
      model.label(
          "Stage 574 inset source gap checked after load continuation");
      model.save("574e_stage574_inset_source_gap_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
