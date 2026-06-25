import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_scaled_pressure_completion {
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
          "Model", "557q5_stage555_alpha0p825_guided_branch.mph");
      model.param().set("scale_pfilm555", "1");
      model.component("comp1").variable("var_load_coupled555")
          .set("Wfilm555",
              "scale_pfilm555*intop_film(max(tff.p,0))");
      model.component("comp1").physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-scale_pfilm555*max(tff.p,0)*nx",
                "-scale_pfilm555*max(tff.p,0)*ny",
                "-scale_pfilm555*max(tff.p,0)*nz"
              });

      String scaleSol = run(
          model, "std555_pscale",
          "Stage 555 calibrate local film pressure feedback",
          "scale_pfilm555",
          "1 0.8 0.6 0.4 0.3 0.2 0.15 0.10 0.08 0.06 0.05",
          "1", "sol75");
      model.save("557t_stage555_pressure_scale0p05.mph");
      System.out.println("STAGE555_PSCALE_PASS solution=" + scaleSol);

      model.param().set("scale_pfilm555", "0.05");
      model.param().set("q_ref555", "0.061");
      String releaseSol = run(
          model, "std555_qrelease_scaled",
          "Stage 555 restore true load control with scaled film pressure",
          "eps_q_regular555",
          "100 50 20 10 5 2 1 0.5 0.2 0.1 0.05 0.02 0.01 0.005 0.002 0.001 0.0005 0.0001",
          "1", scaleSol);
      model.save("557u_stage555_alpha0p825_true_load_scaled.mph");
      System.out.println("STAGE555_RELEASE_SCALED_PASS solution=" + releaseSol);

      model.param().set("eps_q_regular555", "0.0001");
      String alphaSol = run(
          model, "std555_alpha_final_scaled",
          "Stage 555 complete geometric gap coupling",
          "alpha_gap555",
          "0.825 0.84 0.86 0.88 0.90 0.92 0.94 0.96 0.98 1.00",
          "1", releaseSol);
      model.param().set("stage555_phase", "4");
      model.save("558_stage555_midpoint_results.mph");
      System.out.println("STAGE555_ALPHA1_SCALED_PASS solution=" + alphaSol);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
