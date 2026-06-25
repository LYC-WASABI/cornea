import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_contact_homotopy {
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
          "Model", "557f2_stage555_smoothing_1um.mph");
      model.param().set("alpha_gap555", "0.389");
      model.param().set("h_gap_smooth555", "1[um]");
      model.param().set("fp_contact555", "0.3");
      model.component("comp1").physics("solid").feature("dcnt1")
          .set("penaltyCtrlPenalty", "userDefined");
      model.component("comp1").physics("solid").feature("dcnt1")
          .set("fp_penalty", "fp_contact555");

      String softSol = run(
          model, "std_coupled555_contactsoft",
          "Stage 555 temporary contact softening",
          "fp_contact555", "0.30 0.25 0.20 0.15 0.10 0.075 0.05",
          "1", "sol67");
      model.save("557h_stage555_contact_penalty_0p05.mph");
      System.out.println("STAGE555_CONTACT_SOFT_PASS solution=" + softSol);

      model.param().set("fp_contact555", "0.05");
      String alphaSol = run(
          model, "std_coupled555_alpha_full2",
          "Stage 555 full gap coupling with softened contact",
          "alpha_gap555",
          "0.389 0.390 0.395 0.400 0.410 0.420 0.440 0.460 0.480 0.500 0.550 0.600 0.650 0.700 0.750 0.800 0.850 0.900 0.950 1.000",
          "1", softSol);
      model.save("557i_stage555_alpha1_soft_contact.mph");
      System.out.println("STAGE555_ALPHA1_SOFT_PASS solution=" + alphaSol);

      model.param().set("alpha_gap555", "1");
      String hardSol = run(
          model, "std_coupled555_contactrestore",
          "Stage 555 restore contact penalty",
          "fp_contact555", "0.05 0.075 0.10 0.15 0.20 0.25 0.30",
          "1", alphaSol);
      model.save("557j_stage555_alpha1_contact_restored.mph");
      System.out.println("STAGE555_CONTACT_RESTORE_PASS solution=" + hardSol);

      model.param().set("fp_contact555", "0.3");
      String smoothSol = run(
          model, "std_coupled555_smoothrestore",
          "Stage 555 restore physical thickness smoothing",
          "h_gap_smooth555",
          "1.00 0.80 0.60 0.50 0.40 0.30 0.20 0.16 0.12 0.10 0.08 0.06 0.05 0.04 0.03 0.025 0.02",
          "um", hardSol);
      model.param().set("stage555_phase", "4");
      model.save("558_stage555_midpoint_results.mph");
      System.out.println("STAGE555_FINAL_RESTORE_PASS solution=" + smoothSol);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
