import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_branch_finish2 {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  private static String run(
      Model model, String study, String label, String parameter,
      String values, String unit, String initialSolution) {
    String comp = "comp1";
    String ge = "ge_force_total111";
    model.study().create(study);
    model.study(study).label(label);
    model.study(study).create("param", "Parametric");
    model.study(study).feature("param").set(
        "pname", new String[] {parameter});
    model.study(study).feature("param").set(
        "plistarr", new String[] {values});
    model.study(study).feature("param").set(
        "punit", new String[] {unit});
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
    variables.set("initsol", initialSolution);
    variables.set("solnum", "last");
    variables.set("notsolmethod", "sol");
    variables.set("notsol", initialSolution);
    variables.set("notsolnum", "last");
    SolverFeature stationary = model.sol(solution).feature("s1");
    if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("maxiter", 500);
    System.out.println("RUN " + label + " solution=" + solution);
    model.sol(solution).runAll();
    return solution;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557q2_stage555_alpha0p75_qshift.mph");
      model.param().set("q_ref555",
          "0.055+0.08*(alpha_gap555-0.75)");
      String alpha82 = run(
          model, "std555_alpha082",
          "Stage 555 guided branch to alpha 0.82",
          "alpha_gap555", "0.75 0.78 0.80 0.82",
          "1", "sol73");
      model.save("557q3_stage555_alpha0p82_guided_branch.mph");
      System.out.println("STAGE555_ALPHA082_PASS solution=" + alpha82);

      model.param().set("alpha_gap555", "0.82");
      String shift82 = run(
          model, "std555_qshift082",
          "Stage 555 shift q reference at alpha 0.82",
          "q_ref555",
          "0.0606 0.063 0.066 0.069 0.072 0.075",
          "1", alpha82);
      model.save("557q4_stage555_alpha0p82_qshift.mph");
      System.out.println("STAGE555_QSHIFT082_PASS solution=" + shift82);

      model.param().set("q_ref555",
          "0.075+0.10*(alpha_gap555-0.82)");
      String alpha1 = run(
          model, "std555_alpha_finish2",
          "Stage 555 guided branch 0.82 to 1",
          "alpha_gap555",
          "0.82 0.84 0.86 0.88 0.90 0.92 0.94 0.96 0.98 1.00",
          "1", shift82);
      model.save("557q_stage555_alpha1_guided_branch.mph");
      System.out.println("STAGE555_ALPHA1_PASS solution=" + alpha1);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
