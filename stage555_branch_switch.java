import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_branch_switch {
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
          "Model", "557k_stage555_q_regularization_1.mph");
      model.param().set("alpha_gap555", "0.389");
      model.param().set("h_gap_smooth555", "1[um]");
      model.param().set("fp_contact555", "0.05");
      model.param().set("q_ref555", "-0.008352444024");

      String stiffSol = run(
          model, "std555_qstiff",
          "Stage 555 anchor indentation for branch switch",
          "eps_q_regular555", "1 2 5 10 20 50 100",
          "1", "sol69");
      model.save("557o_stage555_q_anchor_100.mph");
      System.out.println("STAGE555_Q_ANCHOR_PASS solution=" + stiffSol);

      model.param().set("eps_q_regular555", "100");
      String shiftSol = run(
          model, "std555_qshift",
          "Stage 555 shift indentation reference",
          "q_ref555",
          "-0.008352444 -0.006 -0.004 -0.002 0 0.002 0.004 0.006 0.008 0.010 0.012 0.014 0.016",
          "1", stiffSol);
      model.save("557p_stage555_q_reference_shifted.mph");
      System.out.println("STAGE555_Q_SHIFT_PASS solution=" + shiftSol);

      model.param().set("q_ref555", "0.016");
      String alphaSol = run(
          model, "std555_alpha_branch2",
          "Stage 555 enter film-dominant branch",
          "alpha_gap555",
          "0.389 0.40 0.45 0.50 0.60 0.70 0.80 0.90 1.00",
          "1", shiftSol);
      model.save("557q_stage555_alpha1_branch2.mph");
      System.out.println("STAGE555_ALPHA_BRANCH_PASS solution=" + alphaSol);

      model.param().set("alpha_gap555", "1");
      String releaseSol = run(
          model, "std555_qrelease",
          "Stage 555 release indentation anchor",
          "eps_q_regular555",
          "100 50 20 10 5 2 1 0.5 0.2 0.1 0.05 0.02 0.01 0.005 0.001 0.0005 0.0001",
          "1", alphaSol);
      model.save("557r_stage555_alpha1_load_control_restored.mph");
      System.out.println("STAGE555_Q_RELEASE_PASS solution=" + releaseSol);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
