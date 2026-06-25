import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558u_sol90_source {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558u_stage567_structure_balance_setup.mph");
      for (String solTag : new String[] {"sol89", "sol90", "sol91"}) {
        try {
          System.out.println("SOL|" + solTag + "|study="
              + model.sol(solTag).study() + "|empty="
              + model.sol(solTag).isEmpty());
          System.out.println("SOL_FEATURES|" + solTag + "|"
              + Arrays.toString(model.sol(solTag).feature().tags()));
          try {
            System.out.println("PVARS|" + solTag + "|"
                + Arrays.toString(model.sol(solTag).getPVals()));
          } catch (Exception e) {
            System.out.println("PVARS_ERROR|" + solTag + "|" + e.getMessage());
          }
        } catch (Exception e) {
          System.out.println("SOL_ERROR|" + solTag + "|" + e.getMessage());
        }
      }

      for (String studyTag : model.study().tags()) {
        String label = model.study(studyTag).label();
        if (label.contains("566") || label.contains("567")) {
          System.out.println("STUDY|" + studyTag + "|" + label);
        }
      }

      try {
        SolverFeature v1 = model.sol("sol91").feature("v1");
        for (String prop : new String[] {
            "initmethod", "initsol", "solnum",
            "notsolmethod", "notsol", "notsolnum"
        }) {
          try {
            System.out.println("V1|" + prop + "|" + v1.getString(prop));
          } catch (Exception e) {
            System.out.println("V1ERR|" + prop + "|" + e.getMessage());
          }
        }
      } catch (Exception e) {
        System.out.println("V1_ERROR|" + e.getMessage());
      }

      ModelNode comp = model.component("comp1");
      for (String tag : comp.variable().tags()) {
        try {
          String expr = comp.variable(tag).get("p_feedback567");
          if (expr != null && !expr.isEmpty()) {
            System.out.println("P567_VAR|" + tag + "|" + expr);
            System.out.println("P567_NAMED|"
                + comp.variable(tag).selection().named());
            System.out.println("P567_ENTITIES|"
                + Arrays.toString(comp.variable(tag)
                    .selection().entities(2)));
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
