import com.comsol.model.*;
import com.comsol.model.util.*;

public class eval_houtside3_extrema {
  private static void printFeature(Model model, String tag) {
    try {
      double[][] vals = model.result().numerical(tag).getReal();
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double[] row : vals) {
        for (double v : row) {
          if (!Double.isNaN(v)) {
            min = Math.min(min, v);
            max = Math.max(max, v);
          }
        }
      }
      System.out.println(tag + " min_series=" + min + " max_series=" + max);
    } catch (Exception e) {
      System.out.println(tag + " ERROR: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\173_lid8mm_stage95_houtside3um_postprocess.mph";
      Model model = ModelUtil.load("Model", path);
      System.out.println("h_outside_track=" + model.param().get("h_outside_track"));
      System.out.println("h0_tear=" + model.param().get("h0_tear"));
      printFeature(model, "min94_hfilm");
      printFeature(model, "min92_hfilm");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
