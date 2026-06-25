import com.comsol.model.*;
import com.comsol.model.util.*;

public class inspect_comsol_model {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph";
      Model model = ModelUtil.load("Model", path);
      System.out.println("DATASETS");
      for (String tag : model.result().dataset().tags()) {
        try {
          System.out.println(tag + " :: " + model.result().dataset(tag).label());
        } catch (Exception e) {
          System.out.println(tag);
        }
      }
      System.out.println("SOLUTIONS");
      for (String tag : model.sol().tags()) {
        try {
          System.out.println(tag + " :: " + model.sol(tag).label());
        } catch (Exception e) {
          System.out.println(tag);
        }
      }
      System.out.println("NUMERICAL");
      for (String tag : model.result().numerical().tags()) {
        try {
          System.out.println(tag + " :: " + model.result().numerical(tag).label());
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
