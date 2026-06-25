import com.comsol.model.*;
import com.comsol.model.util.*;

public class remove_h_feedback_from_hfilm {
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
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\173_lid8mm_stage95_houtside3um_postprocess.mph";
      String outPath = "174_lid8mm_stage96_houtside3um_no_hfeedback_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      model.component("comp1").variable("var_mixed_lub").set(
        "h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)"
      );
      model.component("comp1").variable("var_mixed_lub").descr(
        "h_inside_lid",
        "Tear-film thickness without old separation feedback"
      );

      model.save(outPath);
      System.out.println("Saved local: " + outPath);
      System.out.println("h_outside_track=" + model.param().get("h_outside_track"));
      System.out.println("h_inside_lid=max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)");
      printFeature(model, "min94_hfilm");
      printFeature(model, "min92_hfilm");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
