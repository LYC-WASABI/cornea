import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage27_qs_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\82_lid8mm_stage27_force_controlled_qs_scan_short_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("DATASETS=" + Arrays.toString(model.result().dataset().tags()));
    for (String tag : model.result().dataset().tags()) {
      try {
        System.out.println("  " + tag + " label=" + model.result().dataset(tag).label()
            + " solution=" + model.result().dataset(tag).getString("solution"));
      } catch (Exception ignored) {}
    }
    try { model.result().numerical().remove("gev_stage27"); } catch (Exception ignored) {}
    model.result().numerical().create("gev_stage27", "EvalGlobal");
    model.result().numerical("gev_stage27").set("data", "dset6");
    model.result().numerical("gev_stage27").set("expr",
        new String[]{"q_force", "comp1.W_contact_qs27", "comp1.W_film_qs27", "comp1.W_total_qs27"});
    double[][] values = model.result().numerical("gev_stage27").getReal();
    for (int i = 0; i < values.length; i++) {
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double value : values[i]) {
        min = Math.min(min, value);
        max = Math.max(max, value);
      }
      System.out.println("EXPR_" + i + "_MIN=" + min + " MAX=" + max);
    }
  }
}
