import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574_midpoint_source_jfo {
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
          "Model", "573_stage573_source_true_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      model.param().set("stage574_revision", "574");
      model.param().set(
          "omega_qs574",
          "theta_slide_total*0.5*pi/T_slide572",
          "Midpoint angular velocity for source-side JFO test");

      comp.physics("tff").selection().named("sel_lid_film573");
      comp.physics("tff").feature("ffp1").set("hw1", "h_true573");
      comp.physics("tff").feature("ffp1").set("hb1", "0");
      comp.physics("tff").feature("ffp1")
          .set("TangentialBaseVelocity", "Off");
      comp.physics("tff").feature("ffp1")
          .set("TangentialWallVelocity", "userdef");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0", "-omega_qs574*Z", "omega_qs574*Y"
          });
      comp.physics("tff").feature("init1").set("pfilm", "0[Pa]");
      comp.physics("tff").feature("bdr_inlet520")
          .selection().named("sel_lid_leading573");
      comp.physics("tff").feature("bdr_outlet520")
          .selection().named("sel_lid_trailing573");
      comp.physics("tff").feature("bdr_left520")
          .selection().named("sel_lid_side_left573");
      comp.physics("tff").feature("bdr_right520")
          .selection().named("sel_lid_side_right573");
      comp.cpl("intop_film").selection().named("sel_lid_film573");

      String study = "std574_mid_source_jfo";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 574 midpoint source-side JFO test");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "off", "ge_force_total111", "off", "tff", "on",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol94");
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
      dependent.set("initsol", "sol94");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol94");
      dependent.set("notsolnum", "last");

      model.label("Stage 574 midpoint source JFO setup");
      model.save("574a_stage574_midpoint_source_jfo_setup.mph");
      model.sol(solution).runAll();

      removeDataset(model, "dset574_mid");
      model.result().dataset().create("dset574_mid", "Solution");
      model.result().dataset("dset574_mid").set("solution", solution);
      removeNumerical(model, "eval574_mid");
      model.result().numerical().create("eval574_mid", "EvalGlobal");
      model.result().numerical("eval574_mid").set("data", "dset574_mid");
      model.result().numerical("eval574_mid").set("expr", new String[] {
          "intop_film(tff.p)",
          "intop_film(max(tff.p,0[Pa]))",
          "intop_film(tff.theta)/intop_film(1)",
          "Fn_contact570", "h_residual573", "omega_qs574"
      });
      removeNumerical(model, "min574_p");
      model.result().numerical().create("min574_p", "MinSurface");
      model.result().numerical("min574_p").set("data", "dset574_mid");
      model.result().numerical("min574_p")
          .selection().named("sel_lid_film573");
      model.result().numerical("min574_p").set("expr", "tff.p");
      removeNumerical(model, "max574_p");
      model.result().numerical().create("max574_p", "MaxSurface");
      model.result().numerical("max574_p").set("data", "dset574_mid");
      model.result().numerical("max574_p")
          .selection().named("sel_lid_film573");
      model.result().numerical("max574_p").set("expr", "tff.p");

      System.out.println("SOLUTION=" + solution);
      System.out.println("MIDPOINT=" + Arrays.deepToString(
          model.result().numerical("eval574_mid").getReal()));
      System.out.println("MIN_P=" + Arrays.deepToString(
          model.result().numerical("min574_p").getReal()));
      System.out.println("MAX_P=" + Arrays.deepToString(
          model.result().numerical("max574_p").getReal()));
      model.label("Stage 574 midpoint source JFO results");
      model.save("574b_stage574_midpoint_source_jfo_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
