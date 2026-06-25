import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class fix_stage574_rebind_pair {
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

  private static double value(
      Model model, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574_gap_rebound");
    model.result().numerical(tag).selection().named("sel_lid_film574");
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574b_stage574_inset_source_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      comp.pair().remove("cp_lid_cornea");
      comp.pair().create("cp_lid_cornea", "Contact");
      Pair pair = comp.pair("cp_lid_cornea");
      pair.label(
          "Stage 574 rebuilt contact pair after lid face partition");
      pair.source().named("sel_lid_source_full574");
      pair.destination().named("sel_cornea_anterior_surface");
      comp.physics("solid").feature("dcnt1").set(
          "pairs", new String[] {"cp_lid_cornea"});
      comp.mesh("mesh1").run();

      model.param().set("phi_probe574", "0[deg]");
      String[] dynamicU0 = comp.physics("solid").feature("disp_lid_time")
          .getStringArray("U0");
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "Y*(cos(phi_probe574)-1)-Z*sin(phi_probe574)"
                + "-dr_indent570*(Y*cos(phi_probe574)"
                + "-Z*sin(phi_probe574))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_probe574)+Z*(cos(phi_probe574)-1)"
                + "-dr_indent570*(Y*sin(phi_probe574)"
                + "+Z*cos(phi_probe574))/sqrt(Y^2+Z^2)"
          });

      String study = "std574_pair_rebound";
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
      model.study(study).feature("stat").set("initsol", "sol94");
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
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 574 rebound contact pair setup");
      model.save("574c_stage574_pair_rebound_setup.mph");
      model.sol(solution).runAll();
      comp.physics("solid").feature("disp_lid_time").set("U0", dynamicU0);

      removeDataset(model, "dset574_gap_rebound");
      model.result().dataset().create(
          "dset574_gap_rebound", "Solution");
      model.result().dataset("dset574_gap_rebound")
          .set("solution", solution);
      String gap = pair.gapName(false);
      double area = value(model, "reb_area", "IntSurface", "1");
      double defined = value(
          model, "reb_defined", "IntSurface",
          "isdefined(" + gap + ")");
      double minimum = value(
          model, "reb_min", "MinSurface", gap);
      double maximum = value(
          model, "reb_max", "MaxSurface", gap);
      System.out.println("SOLUTION=" + solution);
      System.out.println("GAP=" + gap);
      System.out.println("DEFINED_FRACTION=" + defined / area);
      System.out.println("MIN_GAP=" + minimum);
      System.out.println("MAX_GAP=" + maximum);
      model.label("Stage 574 rebound contact pair checked");
      model.save("574d_stage574_pair_rebound_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
