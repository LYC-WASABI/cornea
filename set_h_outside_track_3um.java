import com.comsol.model.*;
import com.comsol.model.util.*;

public class set_h_outside_track_3um {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph";
      String outPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\173_lid8mm_stage95_houtside3um_postprocess.mph";
      Model model = ModelUtil.load("Model", inPath);

      model.param().set("h_outside_track", "3[um]", "Tear-film thickness outside lid footprint/track");

      try {
        model.result().numerical("min94_hfilm").run();
      } catch (Exception ignore) {
      }

      model.save(outPath);
      System.out.println("Saved: " + outPath);
      System.out.println("h_outside_track = " + model.param().get("h_outside_track"));
      System.out.println("h0_tear = " + model.param().get("h0_tear"));
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
