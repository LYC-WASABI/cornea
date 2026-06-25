import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage192_results {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "361_lid8mm_stage192_lift_fine_scan_results_Model.mph");
    try {
      model.result().dataset().remove("dset192probe");
    } catch (Exception ignored) {
    }
    model.result().dataset().create("dset192probe", "Solution");
    model.result().dataset("dset192probe").set("solution", "sol45");
    model.result().numerical().create("eval192probe", "EvalGlobal");
    model.result().numerical("eval192probe").set("data", "dset192probe");
    model.result().numerical("eval192probe").set(
        "expr",
        new String[] {
          "delta_h_lift191",
          "intop_film(h_lift191)/intop_film(1)",
          "intop_film(max(pfilm,0))",
          "intop_film(tau_film_wall)"
        });
    model.result().numerical("eval192probe").set(
        "unit", new String[] {"um", "um", "N", "N"});
    double[][] x = model.result().numerical("eval192probe").getReal();
    double bestError = Double.POSITIVE_INFINITY;
    double bestLift = 0;
    for (int j = 0; j < x[0].length; j++) {
      double error = Math.abs(x[2][j] - 0.03);
      if (error < bestError) {
        bestError = error;
        bestLift = x[0][j];
      }
      System.out.printf(
          Locale.US,
          "lift=%.8g havg=%.8g Wpos=%.10g Fshear=%.10g%n",
          x[0][j], x[1][j], x[2][j], x[3][j]);
    }
    System.out.printf(Locale.US, "BEST lift=%.8g error=%.10g%n",
        bestLift, bestError);
    model.save("362_lid8mm_stage192_lift_fine_scan_checked_Model.mph");
    ModelUtil.disconnect();
  }
}
