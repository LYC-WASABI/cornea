import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558u_stage567 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558u_stage567_structure_balance_setup.mph");
      ModelNode comp = model.component("comp1");

      System.out.println("GE_EQ|" + Arrays.toString(
          comp.physics("ge_force_total111").feature("ge1")
              .getStringArray("equation")));
      System.out.println("GE_NAME|" + Arrays.toString(
          comp.physics("ge_force_total111").feature("ge1")
              .getStringArray("name")));
      System.out.println("GE_STEP|"
          + comp.physics("ge_force_total111").feature("ge1")
              .getString("StudyStep"));

      for (String tag : comp.variable().tags()) {
        for (String name : new String[] {
            "p_feedback567", "Wfilm567", "Ftotal567", "Ferr567"
        }) {
          try {
            String value = comp.variable(tag).get(name);
            if (value != null && !value.isEmpty()) {
              System.out.println("VAR|" + tag + "|" + name + "|" + value);
            }
          } catch (Exception ignored) {}
        }
      }

      System.out.println("LOAD|" + Arrays.toString(
          comp.physics("solid").feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));

      for (String studyTag : model.study().tags()) {
        if (!model.study(studyTag).label().contains("567")) continue;
        System.out.println("STUDY|" + studyTag + "|"
            + model.study(studyTag).label());
        for (String ft : model.study(studyTag).feature().tags()) {
          System.out.println("STUDY_FEATURE|" + ft + "|"
              + model.study(studyTag).feature(ft).label());
          try {
            System.out.println("ACTIVATE|" + Arrays.toString(
                model.study(studyTag).feature(ft)
                    .getStringArray("activate")));
          } catch (Exception ignored) {}
          try {
            System.out.println("INITSOL|"
                + model.study(studyTag).feature(ft).getString("initsol"));
          } catch (Exception ignored) {}
        }
      }

      for (String solTag : model.sol().tags()) {
        try {
          String study = model.sol(solTag).study();
          if (!study.isEmpty() && model.study(study).label().contains("567")) {
            System.out.println("SOL|" + solTag + "|study=" + study);
            for (String ft : model.sol(solTag).feature().tags()) {
              System.out.println("SOL_FEATURE|" + ft + "|"
                  + model.sol(solTag).feature(ft).label());
            }
            if (Arrays.asList(model.sol(solTag).feature().tags()).contains("s1")) {
              for (String ft :
                  model.sol(solTag).feature("s1").feature().tags()) {
                SolverFeature sf = model.sol(solTag).feature("s1").feature(ft);
                System.out.println("S1_FEATURE|" + ft + "|" + sf.label()
                    + "|active=" + sf.isActive());
              }
            }
          }
        } catch (Exception ignored) {}
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
