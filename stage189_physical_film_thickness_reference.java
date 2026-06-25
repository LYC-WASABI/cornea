import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage189_physical_film_thickness_reference {
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
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      String comp = "comp1";
      String vars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("h_offset170", "0[um]",
          "Removed artificial film-thickness offset");
      model.param().set("h_residual189", "0.1[um]",
          "Residual numerical film thickness after local depletion");
      model.param().set("alpha_h189", "0.02",
          "Film-thickness iteration relaxation, not a physical parameter");
      model.param().set("h_gap_reg189", "0.005[um]",
          "Smooth positive-part regularization for gap increment");

      model.component(comp).variable(vars).set(
          "gap_ref189",
          "0[um]");
      model.component(comp).variable(vars).set(
          "gap_current189",
          "0[um]");
      model.component(comp).variable(vars).set(
          "delta_gap189",
          "gap_current189-gap_ref189");
      model.component(comp).variable(vars).set(
          "delta_gap_pos189",
          "0.5*(delta_gap189+sqrt(delta_gap189^2+h_gap_reg189^2))");
      model.component(comp).variable(vars).set(
          "h_local_target189",
          "max(h_residual189,h0_tear+delta_gap_pos189)");
      model.component(comp).variable(vars).set(
          "h_physical189",
          "h0_tear");
      model.component(comp).variable(vars).set(
          "h_old189", "h0_tear");
      model.component(comp).variable(vars).set(
          "h_iter189",
          "max(h_residual189,(1-alpha_h189)*h_old189"
              + "+alpha_h189*h_physical189)");
      model.component(comp).variable(vars).set(
          "lambda189", "h_iter189/Rq_eq");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h_iter189");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "0[Pa]");

      String study = "std_hphysical189";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label(
          "Stage 189 physical 3 um reference film thickness");
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
      model.save("353_lid8mm_stage189_physical_h3um_reference_setup_Model.mph");
      System.out.println("RUN_STAGE189 " + solution);
      model.sol(solution).runAll();
      model.save("354_lid8mm_stage189_physical_h3um_reference_results_Model.mph");

      try {
        model.result().dataset().remove("dset189");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset189", "Solution");
      model.result().dataset("dset189").set("solution", solution);
      try {
        model.result().numerical().remove("eval189");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval189", "EvalGlobal");
      model.result().numerical("eval189").set("data", "dset189");
      model.result().numerical("eval189").set(
          "expr",
          new String[] {
            "intop_film(h_physical189)/intop_film(1)",
            "intop_film(h_iter189)/intop_film(1)",
            "intop_film(delta_gap189)/intop_film(1)",
            "intop_film(max(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)",
            "intop_film(lambda189)/intop_film(1)"
          });
      model.result().numerical("eval189").set(
          "unit",
          new String[] {"um", "um", "um", "N", "N", "N", "1"});
      double[][] values = model.result().numerical("eval189").getReal();
      System.out.printf(
          Locale.US,
          "STAGE189 hphysical=%.12g hiter=%.12g dgap=%.12g"
              + " Wpos=%.12g Wnet=%.12g Fshear=%.12g lambda=%.12g%n",
          values[0][0], values[1][0], values[2][0], values[3][0],
          values[4][0], values[5][0], values[6][0]);
      model.save("355_lid8mm_stage189_physical_h3um_reference_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
