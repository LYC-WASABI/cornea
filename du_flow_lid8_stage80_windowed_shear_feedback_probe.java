import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage80_windowed_shear_feedback_probe {
  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi && i < values.length; i++) {
      sum += 0.5 * dt * (values[i - 1] + values[i]);
    }
    return sum;
  }

  private static void range(Model m, String tag, String unit) {
    double[] values = m.result().numerical(tag).getReal()[0];
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
    if (values.length > 51) {
      double avg = trapz(values, .01, 1, 51) / .50;
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n", tag, avg, unit);
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
    range(m, "eval76_Fshear_target", "N");
    range(m, "eval76_mu_target", "1");
    range(m, "eval76_tau_nominal", "Pa");
    range(m, "int76_cornea_applied_shear", "N");
    range(m, "int76_lid_applied_shear", "N");
    range(m, "max76_cornea_disp", "mm");
    range(m, "max76_cornea_mises", "Pa");
    range(m, "max76_lid_disp", "mm");
    range(m, "max76_lid_mises", "Pa");
    System.out.println("HAS_RESULTS_FILE=160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
  }
}
