import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_force_calibrated_iteration4_setup {
  private static final String BASE_SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\25_lid8mm_force_calibrated_iteration3_setup.mph";
  private static final String BASE_RESULT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration3_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\27_lid8mm_force_calibrated_iteration4_gain030_setup.mph";
  private static final double TARGET = 0.03;
  private static final double STIFFNESS_N_PER_MM = 0.8051323301;
  private static final double RESIDUAL_GAIN = 0.30;

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model result = ModelUtil.load("Result", BASE_RESULT);
    double[][] force = result.result().numerical("int_dyn_contact_force").getReal();
    double[][] theta = result.result().numerical("eval_dyn_theta").getReal();
    result.result().numerical().create("eval_tmp_dr", "EvalGlobal");
    result.result().numerical("eval_tmp_dr").set("data", "dset_dynamic_slide");
    result.result().numerical("eval_tmp_dr").set("expr", new String[]{"dr_force"});
    result.result().numerical("eval_tmp_dr").set("unit", new String[]{"mm"});
    double[][] currentDr = result.result().numerical("eval_tmp_dr").getReal();
    List<String[]> rows = new ArrayList<>();
    double previous = -1;
    for (int i = 0; i < force[0].length; i++) {
      double slideFraction = (theta[0][i] + 35.0) / 70.0;
      if (slideFraction < -1e-10 || slideFraction > 1 + 1e-10) continue;
      slideFraction = Math.max(0, Math.min(1, slideFraction));
      if (slideFraction <= previous + 1e-8) continue;
      double smoothForce = 0;
      int smoothCount = 0;
      for (int j = Math.max(0, i - 2); j <= Math.min(force[0].length - 1, i + 2); j++) {
        smoothForce += force[0][j];
        smoothCount++;
      }
      smoothForce /= smoothCount;
      double totalCorrectionMm = currentDr[0][i]
          + RESIDUAL_GAIN * (TARGET - smoothForce) / STIFFNESS_N_PER_MM;
      rows.add(new String[]{
          String.format(Locale.US, "%.12g", slideFraction),
          String.format(Locale.US, "%.12g", totalCorrectionMm)
      });
      previous = slideFraction;
    }
    if (rows.get(rows.size() - 1)[0].equals("1") == false) {
      rows.add(new String[]{"1", rows.get(rows.size() - 1)[1]});
    }
    ModelUtil.remove("Result");

    Model model = ModelUtil.load("Model", BASE_SETUP);
    model.label("27_lid8mm_force_calibrated_iteration4_gain030_setup.mph");
    model.func("dr_sched").label("Iteration 4 radial correction for approximately constant 0.03 N");
    model.func("dr_sched").set("table", rows.toArray(new String[0][0]));
    model.save(OUT);
    System.out.println("TRAJECTORY_ROWS=" + rows.size());
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
