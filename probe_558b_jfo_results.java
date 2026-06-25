import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558b_jfo_results {
  private static void printMatrix(String label, double[][] values) {
    System.out.println(label + "=" + Arrays.deepToString(values));
  }

  private static void evalSurface(
      Model model, String tag, String type, String dataset,
      String expression, String selection) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    NumericalFeature feature = model.result().numerical(tag);
    feature.set("expr", expression);
    feature.set("unit", expression.equals("pfilm") ? "Pa" : "1");
    feature.set("probetag", "none");
    feature.set("data", dataset);
    if (selection != null && !selection.isEmpty()) {
      feature.selection().named(selection);
    }
    printMatrix(tag + "_" + expression, feature.getReal());
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558b_stage555_JFO_only_alpha0p964_results.mph");

      System.out.println("MODEL_LABEL=" + model.label());
      System.out.println("alpha_gap555=" + model.param().get("alpha_gap555"));
      for (String parameter : new String[] {
          "scale_pfilm555", "Ftarget", "F_target", "F_total_target"
      }) {
        try {
          System.out.println(parameter + "=" + model.param().get(parameter));
        } catch (Exception ignored) {}
      }
      System.out.println("STUDIES");
      for (String tag : model.study().tags()) {
        System.out.println("  " + tag + " label=" + model.study(tag).label());
      }
      System.out.println("SOLUTIONS");
      for (String tag : model.sol().tags()) {
        ModelEntity solution = model.sol(tag);
        System.out.println("  " + tag + " label=" + solution.label());
      }

      String equation = model.component("comp1").physics("tff")
          .prop("EquationType").getString("EquationType");
      String transition = model.component("comp1").physics("tff")
          .prop("EquationType").getString("sftransition");
      System.out.println("EquationType=" + equation);
      System.out.println("sftransition=" + transition);

      String solution = "sol82";
      try {
        System.out.println("sol82_hasSolution="
            + model.sol(solution).isEmpty());
      } catch (Exception error) {
        System.out.println("sol82_error=" + error.getMessage());
      }

      System.out.println("DATASETS");
      for (String tag : model.result().dataset().tags()) {
        System.out.println("  " + tag + " label="
            + model.result().dataset(tag).label());
      }
      String dataset = "probe558b_sol82";
      try { model.result().dataset().remove(dataset); } catch (Exception ignored) {}
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);

      String selection = "sel_film_track";
      evalSurface(model, "probe558b_pmin", "MinSurface",
          dataset, "pfilm", selection);
      evalSurface(model, "probe558b_pmax", "MaxSurface",
          dataset, "pfilm", selection);
      evalSurface(model, "probe558b_thetamin", "MinSurface",
          dataset, "tff.theta", selection);
      evalSurface(model, "probe558b_thetamax", "MaxSurface",
          dataset, "tff.theta", selection);
      evalSurface(model, "probe558b_hmin", "MinSurface",
          dataset, "h_geom555", selection);
      evalSurface(model, "probe558b_hmax", "MaxSurface",
          dataset, "h_geom555", selection);
      evalSurface(model, "probe558b_wfilm", "IntSurface",
          dataset, "pfilm", selection);

      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
