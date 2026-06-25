import com.comsol.model.*;
import com.comsol.model.util.*;

public class validate_stage97_results {
  private static void printNumerical(Model model, String tag) {
    try {
      double[][] vals = model.result().numerical(tag).getReal();
      int n = 0;
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double[] row : vals) {
        for (double v : row) {
          if (!Double.isNaN(v)) {
            n++;
            min = Math.min(min, v);
            max = Math.max(max, v);
          }
        }
      }
      System.out.println(tag + " n=" + n + " min=" + min + " max=" + max);
    } catch (Exception e) {
      System.out.println(tag + " ERROR: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "175_lid8mm_stage97_rq0p5um_break0p5to1_results_Model.mph");
      System.out.println("Rq_lid=" + model.param().get("Rq_lid"));
      System.out.println("h_break_low=" + model.param().get("h_break_low"));
      System.out.println("h_break_high=" + model.param().get("h_break_high"));
      System.out.println("PLOT_GROUPS=" + model.result().tags().length);
      for (String tag : model.result().tags()) {
        System.out.println("PG " + tag + " :: " + model.result(tag).label());
      }
      printNumerical(model, "eval_key_force_mu");
      printNumerical(model, "eval_key_hfilm");
      printNumerical(model, "min_hfilm");
      printNumerical(model, "max_hfilm");
      printNumerical(model, "int_film_shear");
      printNumerical(model, "int_boundary_shear");
      printNumerical(model, "int_cornea_contact_pressure");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
