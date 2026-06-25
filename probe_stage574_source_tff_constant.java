import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_source_tff_constant {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
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
      comp.physics("tff").feature("ffp1").set("hw1", "0.05[um]");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {"0", "0", "0"});
      comp.physics("tff").feature("init1").set("pfilm", "0[Pa]");
      for (String tag : new String[] {
          "bdr_inlet520", "bdr_outlet520",
          "bdr_left520", "bdr_right520"
      }) {
        comp.physics("tff").feature(tag).set("theta_0", "1");
      }
      String study = "std574_constant_probe";
      model.study().create(study);
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
      System.out.println("RUN=" + solution);
      model.sol(solution).runAll();
      System.out.println("CONSTANT_SOURCE_TFF=PASS");
      model.save("probe_stage574_source_tff_constant.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
