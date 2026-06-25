import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558o_load_balance {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558o_stage565_pressure_relaxation_setup.mph");
      ModelNode comp = model.component("comp1");

      for (String physicsTag : comp.physics().tags()) {
        System.out.println(
            "PHYSICS|" + physicsTag + "|"
                + comp.physics(physicsTag).label());
        for (String featureTag :
            comp.physics(physicsTag).feature().tags()) {
          System.out.println(
              "FEATURE|" + physicsTag + "|" + featureTag + "|"
                  + comp.physics(physicsTag).feature(featureTag).label());
        }
      }

      System.out.println(
          "GE_LABEL|" + comp.physics("ge_force_total111").label());
      System.out.println(
          "GE1_LABEL|"
              + comp.physics("ge_force_total111").feature("ge1").label());
      System.out.println(
          "GE1_EQUATION|" + Arrays.toString(
              comp.physics("ge_force_total111").feature("ge1")
                  .getStringArray("equation")));
      System.out.println("GE1_NAME|" + Arrays.toString(
          comp.physics("ge_force_total111").feature("ge1")
              .getStringArray("name")));
      System.out.println(
          "GE1_INITIAL|" + Arrays.toString(
              comp.physics("ge_force_total111").feature("ge1")
                  .getStringArray("initialValueU")));
      System.out.println(
          "GE1_DESCRIPTION|"
              + Arrays.toString(
                  comp.physics("ge_force_total111").feature("ge1")
                      .getStringArray("description")));
      System.out.println("GE1_STUDYSTEP|"
          + comp.physics("ge_force_total111").feature("ge1")
              .getString("StudyStep"));

      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
