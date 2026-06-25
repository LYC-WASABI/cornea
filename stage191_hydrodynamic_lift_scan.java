import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage191_hydrodynamic_lift_scan {
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
      String vars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("delta_h_lift191", "0[um]",
          "Hydrodynamic separation solved from normal-load balance");
      model.component(comp).variable(vars).set(
          "h_lift191",
          "max(h_residual189,h0_tear+lid_mask*delta_h_lift191)");
      model.component(comp).variable(vars).set(
          "lambda191", "h_lift191/Rq_eq");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h_lift191");

      String study = "std_liftscan191";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label(
          "Stage 191 physical hydrodynamic lift scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h_lift191"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {
            "0 0.5 1 1.5 2 2.5 3 3.5 4 4.5 5 5.5 6"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"um"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String step = study + "/stat";
      for (String feature : model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", step);
        } catch (Exception ignored) {
        }
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      model.save("357_lid8mm_stage191_hydrodynamic_lift_scan_setup_Model.mph");
      System.out.println("RUN_STAGE191 " + solution);
      model.sol(solution).runAll();
      model.save("358_lid8mm_stage191_hydrodynamic_lift_scan_results_Model.mph");

      try {
        model.result().dataset().remove("dset191");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset191", "Solution");
      model.result().dataset("dset191").set("solution", solution);
      try {
        model.result().numerical().remove("eval191");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval191", "EvalGlobal");
      model.result().numerical("eval191").set("data", "dset191");
      model.result().numerical("eval191").set(
          "expr",
          new String[] {
            "delta_h_lift191",
            "intop_film(h_lift191)/intop_film(1)",
            "intop_film(max(pfilm,0))",
            "-intop_film(min(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval191").set(
          "unit", new String[] {"um", "um", "N", "N", "N", "N"});
      double[][] x = model.result().numerical("eval191").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(
            Locale.US,
            "lift=%.8g havg=%.8g Wpos=%.8g Wneg=%.8g"
                + " Wnet=%.8g Fshear=%.8g%n",
            x[0][j], x[1][j], x[2][j], x[3][j], x[4][j], x[5][j]);
      }
      model.save("359_lid8mm_stage191_hydrodynamic_lift_scan_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
