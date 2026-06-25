import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage196_jfo_transition_continuation {
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
          "Model", "371_lid8mm_stage195_official_jfo_h3um_checked_Model.mph");
      String comp = "comp1";
      model.param().set("beta_tear195", "1e-7[1/Pa]",
          "Elrod-Adams cavitation regularization compressibility");
      String study = "std_jfo196";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label(
          "Stage 196 JFO cavitation transition continuation");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"p_cav_transition195"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {
            "1e6 7e5 5e5 3e5 2e5 1.5e5 1e5 8e4 6e4 5e4"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"Pa"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", "tff", "on", "ge_force_total111", "off"
          });
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
      model.save("372_lid8mm_stage196_jfo_transition_setup_Model.mph");
      System.out.println("RUN_STAGE196 " + solution);
      model.sol(solution).runAll();
      model.save("373_lid8mm_stage196_jfo_transition_results_Model.mph");

      try {
        model.result().dataset().remove("dset196");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset196", "Solution");
      model.result().dataset("dset196").set("solution", solution);
      try {
        model.result().numerical().remove("eval196");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval196", "EvalGlobal");
      model.result().numerical("eval196").set("data", "dset196");
      model.result().numerical("eval196").set(
          "expr",
          new String[] {
            "p_cav_transition195",
            "intop_film(tff.p)",
            "intop_film(pfilm)",
            "intop_film(tff.theta)/intop_film(1)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval196").set(
          "unit", new String[] {"Pa", "N", "N", "1", "N"});
      double[][] x = model.result().numerical("eval196").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(
            Locale.US,
            "transition=%.8g Wphysical=%.12g Winternal=%.12g"
                + " thetaAvg=%.12g Fshear=%.12g%n",
            x[0][j], x[1][j], x[2][j], x[3][j], x[4][j]);
      }
      model.save("374_lid8mm_stage196_jfo_transition_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
