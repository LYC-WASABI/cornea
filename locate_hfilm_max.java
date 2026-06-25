import com.comsol.model.*;
import com.comsol.model.util.*;

public class locate_hfilm_max {
  private static void dump(String title, double[][] vals) {
    System.out.println(title);
    for (int i = 0; i < vals.length; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < vals[i].length; j++) {
        if (j > 0) sb.append(",");
        sb.append(vals[i][j]);
      }
      System.out.println(sb.toString());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\174_lid8mm_stage96_houtside3um_no_hfeedback_recomputed.mph";
      Model model = ModelUtil.load("Model", path);
      String expr = "(max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq))+(1-lid_mask)*(h_outside_track-(max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)))";

      String tag = "tmp_locate_hmax";
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, "MaxSurface");
      model.result().numerical(tag).set("data", "dset_rq0p5_film92");
      model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
      model.result().numerical(tag).set("expr", expr);
      model.result().numerical(tag).set("unit", "um");
      model.result().numerical(tag).set("includepos", true);
      dump("MAX_SURFACE_WITH_POS", model.result().numerical(tag).getReal());

      String tag2 = "tmp_eval_at_expected_max";
      try { model.result().numerical().remove(tag2); } catch (Exception ignore) {}
      model.result().numerical().create(tag2, "EvalPoint");
      model.result().numerical(tag2).set("data", "dset_rq0p5_film92");
      model.result().numerical(tag2).set("expr", new String[] {
        "t_replay", expr, "gap_replay_tear", "lid_mask", "x", "y", "z"
      });
      model.result().numerical(tag2).set("unit", new String[] {
        "s", "um", "um", "1", "mm", "mm", "mm"
      });
      try {
        model.result().numerical(tag2).selection().named("sel_cornea_anterior_surface");
        dump("EVAL_POINT", model.result().numerical(tag2).getReal());
      } catch (Exception e) {
        System.out.println("EvalPoint failed: " + e.getMessage());
      }

      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
