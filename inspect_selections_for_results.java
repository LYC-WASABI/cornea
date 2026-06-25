import com.comsol.model.*;
import com.comsol.model.util.*;

public class inspect_selections_for_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\174_lid8mm_stage96_houtside3um_no_hfeedback_recomputed.mph";
      Model model = ModelUtil.load("Model", path);
      System.out.println("COMPONENT SELECTIONS");
      for (String tag : model.component("comp1").selection().tags()) {
        try {
          System.out.println(tag + " :: " + model.component("comp1").selection(tag).label());
        } catch (Exception e) {
          System.out.println(tag);
        }
      }
      System.out.println("DATASETS");
      for (String tag : model.result().dataset().tags()) {
        try {
          System.out.println(tag + " :: " + model.result().dataset(tag).label());
        } catch (Exception e) {
          System.out.println(tag);
        }
      }
      System.out.println("VARIABLES");
      for (String tag : model.component("comp1").variable().tags()) {
        try {
          System.out.println(tag + " :: " + model.component("comp1").variable(tag).label());
        } catch (Exception e) {
          System.out.println(tag);
        }
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
