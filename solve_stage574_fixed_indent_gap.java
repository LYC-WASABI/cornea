import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class solve_stage574_fixed_indent_gap {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double surface(
      Model model, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574_fixed_gap");
    model.result().numerical(tag).selection().named("sel_lid_film574");
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574a_stage574_inset_source_film_setup.mph");
      ModelNode comp = model.component("comp1");
      model.param().set(
          "q_indent_fixed574", "0.001775886331199642");
      comp.variable("var_dynamic_clean570").set(
          "dr_indent570", "q_indent_fixed574*1[mm]");

      String study = "std574_fixed_indent";
      model.study().create(study);
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "off", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol94");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol94");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol94");
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
      stationary.feature("fc1").set("damp", "0.2");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 574 fixed indentation gap setup");
      model.save("574f_stage574_fixed_indent_gap_setup.mph");
      model.sol(solution).runAll();

      removeDataset(model, "dset574_fixed_gap");
      model.result().dataset().create(
          "dset574_fixed_gap", "Solution");
      model.result().dataset("dset574_fixed_gap")
          .set("solution", solution);
      Pair pair = comp.pair("cp_lid_cornea");
      String gap = pair.gapName(false);
      double area = surface(model, "fix_area", "IntSurface", "1");
      double defined = surface(
          model, "fix_defined", "IntSurface",
          "isdefined(" + gap + ")");
      double minimum = surface(
          model, "fix_min", "MinSurface", gap);
      double maximum = surface(
          model, "fix_max", "MaxSurface", gap);
      removeNumerical(model, "fix_global");
      model.result().numerical().create("fix_global", "EvalGlobal");
      model.result().numerical("fix_global")
          .set("data", "dset574_fixed_gap");
      model.result().numerical("fix_global").set(
          "expr", new String[] {"Fn_contact570"});
      double contactLoad =
          model.result().numerical("fix_global").getReal()[0][0];
      System.out.println("SOLUTION=" + solution);
      System.out.println("DEFINED_FRACTION=" + defined / area);
      System.out.println("MIN_GAP=" + minimum);
      System.out.println("MAX_GAP=" + maximum);
      System.out.println("CONTACT_LOAD=" + contactLoad);
      model.label("Stage 574 fixed indentation source gap checked");
      model.save("574g_stage574_fixed_indent_gap_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
