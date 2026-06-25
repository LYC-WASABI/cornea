import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage208_lateral_leakage_micro_continuation {
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
          "Model", "407_lid8mm_stage207_lateral_leakage_scan_checked_Model.mph");
      String study = "std_leak208";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 208 lateral drainage micro-continuation");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"k_leak207"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {
            "3e-7 3.5e-7 4e-7 4.5e-7 5e-7 5.5e-7"
                + " 6e-7 6.5e-7 7e-7 7.5e-7 8e-7"
          });
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
      model.study(study).feature("stat").set("initsol", "sol51");
      model.study(study).feature("stat").set("initsoluse", "sol51");
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
          "408_lid8mm_stage208_leakage_micro_setup_Model.mph");
      System.out.println("RUN_STAGE208 " + solution);
      model.sol(solution).runAll();
      model.save(
          "409_lid8mm_stage208_leakage_micro_results_Model.mph");

      try { model.result().dataset().remove("dset208"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset208", "Solution");
      model.result().dataset("dset208").set("solution", solution);
      try { model.result().numerical().remove("eval208"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval208", "EvalGlobal");
      model.result().numerical("eval208").set("data", "dset208");
      model.result().numerical("eval208").set(
          "expr",
          new String[] {
            "k_leak207", "intop_film(tff.p)",
            "intop_film(tau_film_wall)",
            "intop_film(tff.theta)/intop_film(1)"
          });
      model.result().numerical("eval208").set(
          "unit",
          new String[] {"kg/(m^2*s*Pa)", "N", "N", "1"});
      double[][] x = model.result().numerical("eval208").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(
            Locale.US,
            "kleak=%.8g Wf=%.12g Fshear=%.12g theta=%.12g%n",
            x[0][j], x[1][j], x[2][j], x[3][j]);
      }
      model.save(
          "410_lid8mm_stage208_leakage_micro_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
