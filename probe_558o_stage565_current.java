import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558o_stage565_current {
  private static void printFeature(ModelNode comp, String physicsTag, String tag) {
    try {
      System.out.println("FEATURE|" + physicsTag + "|" + tag + "|"
          + comp.physics(physicsTag).feature(tag).label());
      System.out.println("ACTIVE|" + tag + "|"
          + comp.physics(physicsTag).feature(tag).isActive());
      System.out.println("NAMED|" + tag + "|"
          + comp.physics(physicsTag).feature(tag).selection().named());
      System.out.println("ENTITIES|" + tag + "|"
          + Arrays.toString(
              comp.physics(physicsTag).feature(tag).selection().entities(2)));
      try {
        System.out.println("FPERAREA|" + tag + "|"
            + Arrays.toString(comp.physics(physicsTag).feature(tag)
                .getStringArray("FperArea")));
      } catch (Exception ignored) {}
    } catch (Exception error) {
      System.out.println("FEATURE_ERROR|" + tag + "|" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558o_stage565_pressure_relaxation_setup.mph");
      ModelNode comp = model.component("comp1");

      System.out.println("MODEL_LABEL|" + model.label());
      System.out.println("PARAM_GAMMA|" + model.param().get("gamma_p565"));
      try {
        System.out.println("PARAM_DELTA565|"
            + model.param().get("delta_h565"));
      } catch (Exception error) {
        System.out.println("PARAM_DELTA565|MISSING");
      }

      for (String tag : comp.variable().tags()) {
        String label = comp.variable(tag).label();
        boolean relevant = label.contains("565")
            || tag.toLowerCase().contains("565")
            || label.equals("Variables 555a");
        if (!relevant) continue;
        System.out.println("VARIABLE|" + tag + "|" + label);
        System.out.println("VAR_NAMED|" + tag + "|"
            + comp.variable(tag).selection().named());
        System.out.println("VAR_ENTITIES|" + tag + "|"
            + Arrays.toString(comp.variable(tag).selection().entities(2)));
        for (String name : new String[] {
            "p_old565", "p_new565", "p_feedback565",
            "Wfilm565", "Ftotal565", "Ferr565"
        }) {
          try {
            String expression = comp.variable(tag).get(name);
            if (expression != null && !expression.isEmpty()) {
              System.out.println(
                  "VAR_EXPR|" + tag + "|" + name + "|" + expression);
            }
          } catch (Exception ignored) {}
        }
      }

      printFeature(comp, "solid", "press_iop");
      printFeature(comp, "solid", "press_iop1");
      printFeature(comp, "solid", "load_partitioned_pfilm");

      System.out.println("GE_ACTIVE|"
          + comp.physics("ge_force_total111").isActive());
      System.out.println("GE1_EQUATION|" + Arrays.toString(
          comp.physics("ge_force_total111").feature("ge1")
              .getStringArray("equation")));
      System.out.println("GE1_NAME|" + Arrays.toString(
          comp.physics("ge_force_total111").feature("ge1")
              .getStringArray("name")));
      System.out.println("GE1_STUDYSTEP|"
          + comp.physics("ge_force_total111").feature("ge1")
              .getString("StudyStep"));

      for (String studyTag : model.study().tags()) {
        String label = model.study(studyTag).label();
        if (!label.contains("565")) continue;
        System.out.println("STUDY|" + studyTag + "|" + label);
        for (String featureTag : model.study(studyTag).feature().tags()) {
          System.out.println("STUDY_FEATURE|" + featureTag + "|"
              + model.study(studyTag).feature(featureTag).label());
          try {
            System.out.println("PLIST|" + featureTag + "|"
                + model.study(studyTag).feature(featureTag)
                    .getString("plistarr"));
          } catch (Exception ignored) {}
          try {
            System.out.println("PNAME|" + featureTag + "|"
                + Arrays.toString(model.study(studyTag).feature(featureTag)
                    .getStringArray("pname")));
            System.out.println("PLISTARR|" + featureTag + "|"
                + Arrays.toString(model.study(studyTag).feature(featureTag)
                    .getStringArray("plistarr")));
          } catch (Exception ignored) {}
          try {
            System.out.println("ACTIVATE|" + featureTag + "|"
                + Arrays.toString(model.study(studyTag).feature(featureTag)
                    .getStringArray("activate")));
          } catch (Exception ignored) {}
          try {
            System.out.println("INITSOL|" + featureTag + "|"
                + model.study(studyTag).feature(featureTag)
                    .getString("initsol"));
          } catch (Exception ignored) {}
        }
      }

      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
