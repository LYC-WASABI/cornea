import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class continue_stage574_midpoint_velocity {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574a_stage574_midpoint_source_jfo_setup.mph");
      ModelNode comp = model.component("comp1");
      try { model.sol().remove("sol95"); } catch (Exception ignored) {}
      try { model.study().remove("std574_mid_source_jfo"); }
      catch (Exception ignored) {}
      model.param().set("lambda_v574", "1e-4");
      comp.physics("tff").feature("ffp1")
          .set("hw1", "withsol('sol94',h_true573)");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0", "-lambda_v574*omega_qs574*Z",
            "lambda_v574*omega_qs574*Y"
          });
      comp.physics("tff").feature("init1").set("pfilm", "0[Pa]");
      for (String tag : new String[] {
          "bdr_inlet520", "bdr_outlet520",
          "bdr_left520", "bdr_right520"
      }) {
        comp.physics("tff").feature(tag).set("theta_0", "1");
      }

      String previous = "sol94";
      double[] factors = new double[] {
        1e-4, 3e-4, 1e-3, 3e-3, 1e-2,
        3e-2, 0.1, 0.2, 0.4, 0.7, 1.0
      };
      for (int i = 0; i < factors.length; i++) {
        double factor = factors[i];
        model.param().set(
            "lambda_v574",
            String.format(Locale.US, "%.12g", factor));
        String study = "std574_v" + (i + 1);
        model.study().create(study);
        model.study(study).label(
            "Stage 574 midpoint velocity continuation " + factor);
        model.study(study).create("stat", "Stationary");
        model.study(study).feature("stat").set(
            "activate", new String[] {
              "solid", "off", "ge_force_total111", "off", "tff", "on",
              "frame:spatial1", "on", "frame:material1", "on",
              "comp1", "on"
            });
        model.study(study).feature("stat").set("useinitsol", "on");
        model.study(study).feature("stat").set("initmethod", "sol");
        model.study(study).feature("stat").set("initsol", previous);
        model.study(study).feature("stat").set("initsoluse", "current");
        String step = study + "/stat";
        for (String tag : comp.physics("tff").feature().tags()) {
          try { comp.physics("tff").feature(tag).set("StudyStep", step); }
          catch (Exception ignored) {}
        }
        String[] before = model.sol().tags();
        model.study(study).createAutoSequences("sol");
        String solution = newest(model, before);
        SolverFeature dependent = model.sol(solution).feature("v1");
        dependent.set("initmethod", "sol");
        dependent.set("initsol", previous);
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
        stationary.feature("fc1").set("damp", "0.5");
        stationary.feature("fc1").set("maxiter", 100);
        System.out.println("RUN factor=" + factor + " solution=" + solution);
        model.sol(solution).runAll();
        previous = solution;
      }

      removeDataset(model, "dset574_mid_cont");
      model.result().dataset().create(
          "dset574_mid_cont", "Solution");
      model.result().dataset("dset574_mid_cont")
          .set("solution", previous);
      removeNumerical(model, "eval574_mid_cont");
      model.result().numerical().create(
          "eval574_mid_cont", "EvalGlobal");
      model.result().numerical("eval574_mid_cont")
          .set("data", "dset574_mid_cont");
      model.result().numerical("eval574_mid_cont").set(
          "expr", new String[] {
            "intop_film(tff.p)",
            "intop_film(max(tff.p,0[Pa]))",
            "intop_film(tff.theta)/intop_film(1)",
            "Fn_contact570", "lambda_v574"
          });
      removeNumerical(model, "min574_mid_cont");
      model.result().numerical().create(
          "min574_mid_cont", "MinSurface");
      model.result().numerical("min574_mid_cont")
          .set("data", "dset574_mid_cont");
      model.result().numerical("min574_mid_cont")
          .selection().named("sel_lid_film573");
      model.result().numerical("min574_mid_cont").set("expr", "tff.p");
      removeNumerical(model, "max574_mid_cont");
      model.result().numerical().create(
          "max574_mid_cont", "MaxSurface");
      model.result().numerical("max574_mid_cont")
          .set("data", "dset574_mid_cont");
      model.result().numerical("max574_mid_cont")
          .selection().named("sel_lid_film573");
      model.result().numerical("max574_mid_cont").set("expr", "tff.p");
      System.out.println("FINAL_SOLUTION=" + previous);
      System.out.println("FINAL_VALUES=" + Arrays.deepToString(
          model.result().numerical("eval574_mid_cont").getReal()));
      System.out.println("MIN_P=" + Arrays.deepToString(
          model.result().numerical("min574_mid_cont").getReal()));
      System.out.println("MAX_P=" + Arrays.deepToString(
          model.result().numerical("max574_mid_cont").getReal()));
      model.label("Stage 574 midpoint source JFO velocity continuation");
      model.save("574c_stage574_midpoint_velocity_continuation.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
