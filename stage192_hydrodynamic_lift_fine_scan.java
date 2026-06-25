import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage192_hydrodynamic_lift_fine_scan {
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
          "Model", "359_lid8mm_stage191_hydrodynamic_lift_scan_checked_Model.mph");
      String study = "std_liftfine192";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label("Stage 192 fine hydrodynamic lift calibration");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h_lift191"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {"range(1.25,0.025,1.45)"});
      model.study(study).feature("param").set("punit", new String[] {"um"});
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
        } catch (Exception ignored) {
        }
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      model.save("360_lid8mm_stage192_lift_fine_scan_setup_Model.mph");
      System.out.println("RUN_STAGE192 " + solution);
      model.sol(solution).runAll();
      model.save("361_lid8mm_stage192_lift_fine_scan_results_Model.mph");

      model.result().dataset().create("dset192", "Solution");
      model.result().dataset("dset192").set("solution", solution);
      model.result().numerical().create("eval192", "EvalGlobal");
      model.result().numerical("eval192").set("data", "dset192");
      model.result().numerical("eval192").set(
          "expr",
          new String[] {
            "delta_h_lift191",
            "intop_film(h_lift191)/intop_film(1)",
            "intop_film(max(pfilm,0))",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval192").set(
          "unit", new String[] {"um", "um", "N", "N"});
      double[][] x = model.result().numerical("eval192").getReal();
      double bestError = Double.POSITIVE_INFINITY;
      double bestLift = 0;
      for (int j = 0; j < x[0].length; j++) {
        double error = Math.abs(x[2][j] - 0.03);
        if (error < bestError) {
          bestError = error;
          bestLift = x[0][j];
        }
        System.out.printf(
            Locale.US,
            "lift=%.8g havg=%.8g Wpos=%.10g Fshear=%.10g%n",
            x[0][j], x[1][j], x[2][j], x[3][j]);
      }
      System.out.printf(
          Locale.US, "BEST lift=%.8g error=%.10g%n", bestLift, bestError);
      model.save("362_lid8mm_stage192_lift_fine_scan_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
