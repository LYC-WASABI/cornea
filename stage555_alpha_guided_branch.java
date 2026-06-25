import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_alpha_guided_branch {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557p_stage555_q_reference_shifted.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";
      model.param().set("eps_q_regular555", "100");
      model.param().set("q_ref555",
          "0.016+0.04*(alpha_gap555-0.389)");
      String study = "std555_alpha_guided";
      model.study().create(study);
      model.study(study).label(
          "Stage 555 guided film-dominant branch to alpha 1");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"alpha_gap555"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {
            "0.389 0.40 0.45 0.50 0.55 0.60 0.65 0.70 0.75"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"1"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", ge, "on"});
      String step = study + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        model.component(comp).physics("solid").feature(tag)
            .set("StudyStep", step);
      }
      for (String tag :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }
      model.component(comp).physics(ge).feature("ge1")
          .set("StudyStep", step);
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature variables = model.sol(solution).feature("v1");
      variables.set("initmethod", "sol");
      variables.set("initsol", "sol71");
      variables.set("solnum", "last");
      variables.set("notsolmethod", "sol");
      variables.set("notsol", "sol71");
      variables.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 500);
      System.out.println("RUN_GUIDED_ALPHA=" + solution);
      model.sol(solution).runAll();
      model.save("557q1_stage555_alpha0p75_guided_branch.mph");
      System.out.println("STAGE555_GUIDED_ALPHA_PASS solution=" + solution);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
