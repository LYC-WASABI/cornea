import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_phase1_to_005 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557_stage555_joint_solver_setup.mph");
      String study = "std_coupled555";
      try { model.sol().remove("sol61"); } catch (Exception ignored) {}
      model.study(study).feature("param").set(
          "plistarr", new String[] {
            "0.001 0.002 0.005 0.01 0.02 0.05"
          });
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = null;
      Set<String> old = new HashSet<>(Arrays.asList(before));
      for (String tag : model.sol().tags()) {
        if (!old.contains(tag)) solution = tag;
      }
      if (solution == null) {
        solution = model.sol().tags()[model.sol().tags().length - 1];
      }
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 500);
      System.out.println("RUN_PHASE1=" + solution);
      model.sol(solution).runAll();
      model.param().set("stage555_phase", "1");
      model.save("557a_stage555_phase1_alpha0p05.mph");
      System.out.println("STAGE555_PHASE1_PASS solution=" + solution);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
