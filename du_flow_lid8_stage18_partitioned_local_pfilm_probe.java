import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage18_partitioned_local_pfilm_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\75_lid8mm_stage22_disconnect_reconnect_transient_complete_postprocessing_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
    System.out.println("DATASETS=" + Arrays.toString(model.result().dataset().tags()));

    try { model.result().numerical().remove("gev_stage18_probe"); } catch (Exception ignored) {}
    model.result().numerical().create("gev_stage18_probe", "EvalGlobal");
    model.result().numerical("gev_stage18_probe").set("data", "dset5");
    model.result().numerical("gev_stage18_probe").set("expr", new String[]{
        "comp1.W_film_replay",
        "comp1.W_contact_partitioned",
        "comp1.W_total_partitioned_local",
        "comp1.mu_app_partitioned_local"
    });
    model.result().numerical("gev_stage18_probe").set("descr", new String[]{
        "Replayed film normal load", "Contact normal load", "Partitioned total supported load",
        "Reported apparent friction coefficient"
    });
    double[][] values = model.result().numerical("gev_stage18_probe").getReal();
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
