import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage197_jfo_physical_separation_scan {
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
          "Model", "374_lid8mm_stage196_jfo_transition_checked_Model.mph");
      String comp = "comp1";
      model.param().set("p_cav_transition195", "50[kPa]");
      model.param().set("delta_h_jfo197", "0[um]",
          "Physical lid-cornea normal separation for JFO load calibration");
      model.component(comp).variable("var_mixed_lub").set(
          "h_jfo197",
          "max(h_residual189,h0_tear+lid_mask*delta_h_jfo197)");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h_jfo197");

      String study = "std_jfo197";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label(
          "Stage 197 JFO physical separation load scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h_jfo197"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {"0 0.5 1 1.5 2 2.5 3 4 5"});
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
      model.save("375_lid8mm_stage197_jfo_separation_scan_setup_Model.mph");
      System.out.println("RUN_STAGE197 " + solution);
      model.sol(solution).runAll();
      model.save("376_lid8mm_stage197_jfo_separation_scan_results_Model.mph");

      try {
        model.result().dataset().remove("dset197");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset197", "Solution");
      model.result().dataset("dset197").set("solution", solution);
      try {
        model.result().numerical().remove("eval197");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval197", "EvalGlobal");
      model.result().numerical("eval197").set("data", "dset197");
      model.result().numerical("eval197").set(
          "expr",
          new String[] {
            "delta_h_jfo197",
            "intop_film(h_jfo197)/intop_film(1)",
            "intop_film(tff.p)",
            "intop_film(tff.theta)/intop_film(1)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval197").set(
          "unit", new String[] {"um", "um", "N", "1", "N"});
      double[][] x = model.result().numerical("eval197").getReal();
      double best = 0;
      double bestError = Double.POSITIVE_INFINITY;
      for (int j = 0; j < x[0].length; j++) {
        double error = Math.abs(x[2][j] - 0.03);
        if (error < bestError) {
          bestError = error;
          best = x[0][j];
        }
        System.out.printf(
            Locale.US,
            "separation=%.8g havg=%.8g Wphysical=%.12g"
                + " thetaAvg=%.12g Fshear=%.12g%n",
            x[0][j], x[1][j], x[2][j], x[3][j], x[4][j]);
      }
      System.out.printf(
          Locale.US, "BEST separation=%.8g error=%.12g%n",
          best, bestError);
      model.save("377_lid8mm_stage197_jfo_separation_scan_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
