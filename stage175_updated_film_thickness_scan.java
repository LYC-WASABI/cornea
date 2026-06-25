import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage175_updated_film_thickness_scan {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  static void removeDataset(Model model, String tag) {
    try {
      model.result().dataset().remove(tag);
    } catch (Exception ignored) {
    }
  }

  static void removeNumerical(Model model, String tag) {
    try {
      model.result().numerical().remove(tag);
    } catch (Exception ignored) {
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "318_lid8mm_stage174_true_balance_results_Model.mph");
      String comp = "comp1";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("beta_h175", "0");
      model.component(comp).variable(mixedVars).set(
          "gap_actual175",
          "min(max(withsol('sol39',geomgap_dst_cp_lid_cornea),0),gap_cap_tear)");
      model.component(comp).variable(mixedVars).set(
          "gap_pos175",
          "0.5*(gap_actual175+sqrt(gap_actual175^2+h_gap_reg169^2))");
      model.component(comp).variable(mixedVars).set(
          "h_target175",
          "max(h_min_tear,h0_tear+Rq_eq+gap_pos175)");
      model.component(comp).variable(mixedVars).set(
          "h_updated175",
          "max(h_min_tear,(1-beta_h175)*h_relaxed173+beta_h175*h_target175)");
      model.component(comp).physics("tff").feature("ffp1").set("hw1", "h_updated175");

      String study = "std_hupdate175";
      try {
        model.study().remove(study);
      } catch (Exception ignored) {
      }
      model.study().create(study);
      model.study(study).label("Stage 175 updated film thickness relaxation scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set("pname", new String[] {"beta_h175"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {"0 0.0002 0.0005 0.001 0.002 0.005 0.01 0.02"});
      model.study(study).feature("param").set("punit", new String[] {"1"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String step = study + "/stat";
      for (String feature : model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set("StudyStep", step);
        } catch (Exception ignored) {
        }
      }

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      model.save("319_lid8mm_stage175_updated_thickness_scan_setup_Model.mph");
      System.out.println("RUN_STAGE175 " + solution);
      model.sol(solution).runAll();

      removeDataset(model, "dset175");
      model.result().dataset().create("dset175", "Solution");
      model.result().dataset("dset175").set("solution", solution);
      removeNumerical(model, "eval175");
      model.result().numerical().create("eval175", "EvalGlobal");
      model.result().numerical("eval175").set("data", "dset175");
      model.result().numerical("eval175").set(
          "expr",
          new String[] {
            "beta_h175",
            "intop_film(max(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)",
            "intop_film(h_updated175)/intop_film(1)"
          });
      double[][] values = model.result().numerical("eval175").getReal();
      for (int index = 0; index < values[0].length; index++) {
        System.out.printf(
            Locale.US,
            "beta=%.7g Wpos=%.10g Wnet=%.10g Fshear=%.10g"
                + " havg=%.10g%n",
            values[0][index],
            values[1][index],
            values[2][index],
            values[3][index],
            values[4][index]);
      }

      model.save("320_lid8mm_stage175_updated_thickness_scan_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
