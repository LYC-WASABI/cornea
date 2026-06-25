import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_joint_solver_groups {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution");
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
      String study = "std574_joint_groups";
      model.study().create(study);
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "on", "tff", "on",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      String step = study + "/stat";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }
      comp.physics("ge_force_total111").feature("ge1")
          .set("StudyStep", step);
      for (String tag : comp.physics("tff").feature().tags()) {
        try { comp.physics("tff").feature(tag).set("StudyStep", step); }
        catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature stationary = model.sol(solution).feature("s1");
      System.out.println("SOLUTION=" + solution);
      System.out.println("CHILDREN="
          + Arrays.toString(stationary.feature().tags()));
      for (String tag : stationary.feature().tags()) {
        SolverFeature feature = stationary.feature(tag);
        System.out.println("FEATURE|" + tag + "|" + feature.label()
            + "|" + feature.getType());
        for (String child : feature.feature().tags()) {
          SolverFeature nested = feature.feature(child);
          System.out.println("  CHILD|" + child + "|" + nested.label()
              + "|" + nested.getType());
          for (String prop : nested.properties()) {
            if (prop.toLowerCase(Locale.ROOT).contains("seg")
                || prop.toLowerCase(Locale.ROOT).contains("var")) {
              try {
                System.out.println("    " + prop + "="
                    + Arrays.toString(nested.getStringArray(prop)));
              } catch (Exception ignored) {}
            }
          }
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
