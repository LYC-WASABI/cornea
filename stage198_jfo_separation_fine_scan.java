import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage198_jfo_separation_fine_scan {
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
          "Model", "377_lid8mm_stage197_jfo_separation_scan_checked_Model.mph");
      String study = "std_jfo198";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label("Stage 198 JFO separation fine scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h_jfo197"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {"range(1.55,0.025,1.85)"});
      model.study(study).feature("param").set(
          "punit", new String[] {"um"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", "tff", "on", "ge_force_total111", "off"
          });
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
      model.save("378_lid8mm_stage198_jfo_separation_fine_setup_Model.mph");
      System.out.println("RUN_STAGE198 " + solution);
      model.sol(solution).runAll();
      model.save("379_lid8mm_stage198_jfo_separation_fine_results_Model.mph");

      try { model.result().dataset().remove("dset198"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset198", "Solution");
      model.result().dataset("dset198").set("solution", solution);
      try { model.result().numerical().remove("eval198"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval198", "EvalGlobal");
      model.result().numerical("eval198").set("data", "dset198");
      model.result().numerical("eval198").set(
          "expr",
          new String[] {
            "delta_h_jfo197",
            "intop_film(h_jfo197)/intop_film(1)",
            "intop_film(tff.p)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval198").set(
          "unit", new String[] {"um", "um", "N", "N"});
      double[][] x = model.result().numerical("eval198").getReal();
      double best = 0;
      double bestError = Double.POSITIVE_INFINITY;
      for (int j = 0; j < x[0].length; j++) {
        double error = Math.abs(x[2][j] - 0.0285);
        if (error < bestError) {
          bestError = error;
          best = x[0][j];
        }
        System.out.printf(
            Locale.US,
            "separation=%.8g havg=%.8g Wphysical=%.12g Fshear=%.12g%n",
            x[0][j], x[1][j], x[2][j], x[3][j]);
      }
      System.out.printf(
          Locale.US, "BEST_95PCT separation=%.8g error=%.12g%n",
          best, bestError);
      model.save("380_lid8mm_stage198_jfo_separation_fine_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
