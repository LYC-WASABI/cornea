import com.comsol.model.*;
import com.comsol.model.util.*;

public class eval_no_hfeedback_temp_extrema {
  private static void eval(Model model, String tag, String type) {
    try {
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, type);
      model.result().numerical(tag).set("data", "dset_rq0p5_film92");
      model.result().numerical(tag).set("expr", "(max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq))+(1-lid_mask)*(h_outside_track-(max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)))");
      model.result().numerical(tag).set("unit", "um");
      model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
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
      System.out.println(tag + " " + type + " min_series=" + min + " max_series=" + max);
    } catch (Exception e) {
      System.out.println(tag + " ERROR: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "174_lid8mm_stage96_houtside3um_no_hfeedback_recomputed_Model.mph";
      Model model = ModelUtil.load("Model", path);
      System.out.println("h_inside_lid expression forced without h_feedback_sep53");
      eval(model, "tmp_min_hfilm_nohf", "MinSurface");
      eval(model, "tmp_max_hfilm_nohf", "MaxSurface");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
