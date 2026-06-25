import com.comsol.model.*;
import com.comsol.model.util.*;

public class recompute_no_hfeedback_film {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "174_lid8mm_stage96_houtside3um_no_hfeedback_Model.mph";
      String outPath = "174_lid8mm_stage96_houtside3um_no_hfeedback_recomputed_Model.mph";
      Model model = ModelUtil.load("Model", inPath);
      model.study("std_tff_gap_qs45").run();
      model.save(outPath);
      System.out.println("Saved local recomputed: " + outPath);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
