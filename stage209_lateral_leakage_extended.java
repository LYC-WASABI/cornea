import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage209_lateral_leakage_extended {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!oldTags.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "410_lid8mm_stage208_leakage_micro_checked_Model.mph");
      String study = "std_leak209";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 209 extended lateral drainage continuation");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"k_leak207"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {"8e-7 9e-7"});
      model.study(study).feature("param").set(
          "punit", new String[] {"kg/(m^2*s*Pa)"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", "tff", "on", "ge_force_total111", "off"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol52");
      model.study(study).feature("stat").set("initsoluse", "sol52");
      model.study(study).feature("stat").set("initsolusesolnum", "last");
      String step = study + "/stat";
      for (String feature :
          model.component("comp1").physics("tff").feature().tags()) {
        try {
          model.component("comp1").physics("tff").feature(feature).set(
              "StudyStep", step);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      model.save(
          "411_lid8mm_stage209_leakage_extended_setup_Model.mph");
      System.out.println("RUN_STAGE209 " + solution);
      model.sol(solution).runAll();
      model.save(
          "412_lid8mm_stage209_leakage_extended_results_Model.mph");

      try { model.result().dataset().remove("dset209"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset209", "Solution");
      model.result().dataset("dset209").set("solution", solution);
      try { model.result().numerical().remove("eval209"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval209", "EvalGlobal");
      model.result().numerical("eval209").set("data", "dset209");
      model.result().numerical("eval209").set(
          "expr",
          new String[] {
            "k_leak207", "intop_film(tff.p)",
            "intop_film(tau_film_wall)",
            "intop_film(tff.theta)/intop_film(1)"
          });
      model.result().numerical("eval209").set(
          "unit",
          new String[] {"kg/(m^2*s*Pa)", "N", "N", "1"});
      double[][] x = model.result().numerical("eval209").getReal();
      double best = 0;
      double bestError = Double.POSITIVE_INFINITY;
      for (int j = 0; j < x[0].length; j++) {
        double error = Math.abs(x[1][j] - 0.0285);
        if (error < bestError) {
          bestError = error;
          best = x[0][j];
        }
        System.out.printf(
            Locale.US,
            "kleak=%.8g Wf=%.12g Fshear=%.12g theta=%.12g%n",
            x[0][j], x[1][j], x[2][j], x[3][j]);
      }
      System.out.printf(
          Locale.US, "BEST kleak=%.8g error=%.12g%n", best, bestError);
      model.save(
          "413_lid8mm_stage209_leakage_extended_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
