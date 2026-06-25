import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage195_official_jfo_cavitation_h3um {
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
          "Model", "356_lid8mm_stage190_physical_h3um_diagnostics_results_Model.mph");
      String comp = "comp1";
      String globalEq = "ge_force_total111";

      model.param().set("p_cav_transition195", "1[MPa]",
          "Initial smooth COMSOL Elrod-Adams cavitation transition width");
      model.param().set("beta_tear195", "4.6e-10[1/Pa]",
          "Water compressibility for mass-conserving cavitation");
      model.component(comp).physics("tff").prop("EquationType").set(
          "EquationType", "ReynoldsEquationWithCavitation");
      model.component(comp).physics("tff").prop("EquationType").set(
          "sftransition", "p_cav_transition195");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h0_tear");
      model.component(comp).physics("tff").feature("ffp1").set(
          "UseCompressibilityForDensity", "CompressibilityForm");
      model.component(comp).physics("tff").feature("ffp1").set(
          "rho_c", "rho_tear");
      model.component(comp).physics("tff").feature("ffp1").set(
          "beta", "beta_tear195");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "1[kPa]");

      String study = "std_jfo195";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label(
          "Stage 195 official Elrod-Adams cavitation at h0=3 um");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String step = study + "/stat";
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", step);
        } catch (Exception ignored) {
        }
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 200);
      model.save("369_lid8mm_stage195_official_jfo_h3um_setup_Model.mph");
      System.out.println("RUN_STAGE195 " + solution);
      model.sol(solution).runAll();
      model.save("370_lid8mm_stage195_official_jfo_h3um_results_Model.mph");

      try {
        model.result().dataset().remove("dset195");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset195", "Solution");
      model.result().dataset("dset195").set("solution", solution);
      try {
        model.result().numerical().remove("eval195");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval195", "EvalGlobal");
      model.result().numerical("eval195").set("data", "dset195");
      model.result().numerical("eval195").set(
          "expr",
          new String[] {
            "intop_film(pfilm)",
            "intop_film(max(pfilm,0))",
            "intop_film(tau_film_wall)",
            "intop_film(tff.theta)/intop_film(1)",
            "intop_film(h0_tear)/intop_film(1)"
          });
      model.result().numerical("eval195").set(
          "unit", new String[] {"N", "N", "N", "1", "um"});
      double[][] x = model.result().numerical("eval195").getReal();
      System.out.printf(
          Locale.US,
          "STAGE195 Wnet=%.12g Wpos=%.12g Fshear=%.12g"
              + " thetaAvg=%.12g havg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0], x[4][0]);
      model.save("371_lid8mm_stage195_official_jfo_h3um_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
