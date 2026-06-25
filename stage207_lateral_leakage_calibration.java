import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage207_lateral_leakage_calibration {
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
          "Model", "399_lid8mm_stage205_partitioned_first_step_film_results_Model.mph");
      String comp = "comp1";
      model.param().set("delta_h_jfo197", "0[um]");
      model.param().set(
          "k_leak207", "0[kg/(m^2*s*Pa)]",
          "Effective lateral drainage coefficient for moving local film");
      String source = "ms_leak207";
      try {
        model.component(comp).physics("tff").feature().remove(source);
      } catch (Exception ignored) {}
      model.component(comp).physics("tff").create(source, "MassSource", 2);
      model.component(comp).physics("tff").feature(source).label(
          "Effective lateral drainage from moving lid footprint");
      model.component(comp).physics("tff").feature(source).selection().all();
      model.component(comp).physics("tff").feature(source).set(
          "QudR", "-k_leak207*tff.p*lid_mask");

      String study = "std_leak207";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 207 local lateral drainage calibration");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"k_leak207"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {
            "0 1e-8 3e-8 1e-7 3e-7"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"kg/(m^2*s*Pa)"});
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
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      model.save(
          "405_lid8mm_stage207_lateral_leakage_scan_setup_Model.mph");
      System.out.println("RUN_STAGE207 " + solution);
      model.sol(solution).runAll();
      model.save(
          "406_lid8mm_stage207_lateral_leakage_scan_results_Model.mph");

      try { model.result().dataset().remove("dset207"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset207", "Solution");
      model.result().dataset("dset207").set("solution", solution);
      try { model.result().numerical().remove("eval207"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval207", "EvalGlobal");
      model.result().numerical("eval207").set("data", "dset207");
      model.result().numerical("eval207").set(
          "expr",
          new String[] {
            "k_leak207", "intop_film(tff.p)",
            "intop_film(tau_film_wall)",
            "intop_film(tff.theta)/intop_film(1)"
          });
      model.result().numerical("eval207").set(
          "unit",
          new String[] {"kg/(m^2*s*Pa)", "N", "N", "1"});
      double[][] x = model.result().numerical("eval207").getReal();
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
          "407_lid8mm_stage207_lateral_leakage_scan_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
