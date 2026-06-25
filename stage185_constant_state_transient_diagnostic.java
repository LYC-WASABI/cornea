import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage185_constant_state_transient_diagnostic {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "341_lid8mm_stage181_exact_static_results_Model.mph");
      String comp = "comp1";
      String globalEq = "ge_force_total111";
      model.param().set("d_indent_diag185", "-0.0181708879609[mm]");
      model.component(comp).variable("var_partitioned_local_pfilm").set(
          "dr_indent119", "d_indent_diag185");
      String study = "std_diag185";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label("Stage 185 constant-state transient diagnostic");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set("tlist", "range(0,0.00001,0.0001)");
      model.study(study).feature("time").set("geometricNonlinearity", "on");
      model.study(study).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "off"});
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", "sol45");
      model.study(study).feature("time").set("initsoluse", "sol45");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      String step = study + "/time";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set("StudyStep", step);
      }
      model.component(comp).physics("solid").feature("dcnt1").set("useCutback", "1");

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature timeSolver = model.sol(solution).feature("t1");
      timeSolver.set("consistent", "off");
      timeSolver.set("initialstepbdfactive", "on");
      timeSolver.set("initialstepbdf", "1e-7");
      timeSolver.set("maxstepconstraintbdf", "const");
      timeSolver.set("maxstepbdf", "1e-5");
      try {
        timeSolver.feature().remove("se1");
      } catch (Exception ignored) {
      }
      try {
        timeSolver.feature().remove("fc1");
      } catch (Exception ignored) {
      }
      timeSolver.create("fc1", "FullyCoupled");
      timeSolver.feature("fc1").set("linsolver", "dDef");
      timeSolver.feature("fc1").set("maxiter", 120);
      model.save("348_lid8mm_stage185_constant_transient_diagnostic_setup_Model.mph");
      System.out.println("RUN_STAGE185 " + solution);
      model.sol(solution).runAll();
      model.save("349_lid8mm_stage185_constant_transient_diagnostic_results_Model.mph");
      System.out.println("STAGE185_SUCCESS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
