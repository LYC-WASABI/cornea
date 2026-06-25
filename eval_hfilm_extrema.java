import com.comsol.model.*;
import com.comsol.model.util.*;

public class eval_hfilm_extrema {
  private static void printNumerical(Model model, String tag) {
    try {
      double[][] vals = model.result().numerical(tag).getReal();
      System.out.println(tag);
      for (int i = 0; i < vals.length; i++) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < vals[i].length; j++) {
          if (j > 0) sb.append(",");
          sb.append(vals[i][j]);
        }
        System.out.println(sb.toString());
      }
    } catch (Exception e) {
      System.out.println(tag + " ERROR: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String path = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph";
      Model model = ModelUtil.load("Model", path);

      printNumerical(model, "min92_hfilm");
      printNumerical(model, "max92_hfilm");
      printNumerical(model, "min94_hfilm");

      String tag = "max94_hfilm_tmp";
      if (model.result().numerical().tags().length > 0) {
        try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      }
      model.result().numerical().create(tag, "MaxSurface");
      model.result().numerical(tag).label("Temporary Stage 94 hfilm max");
      model.result().numerical(tag).set("data", "dset_rq0p5_film92");
      model.result().numerical(tag).set("expr", "h_film_input");
      model.result().numerical(tag).set("unit", "um");
      printNumerical(model, tag);

      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
