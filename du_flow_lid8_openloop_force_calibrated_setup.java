import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_openloop_force_calibrated_setup {
  private static final String BASE_SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\20_lid8mm_quasistatic_dynamic_sliding_material_frame_setup.mph";
  private static final String BASE_RESULT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_quasistatic_dynamic_sliding_minus35_to_plus35_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\23_lid8mm_force_calibrated_smoothed_gain050_setup.mph";
  private static final double TARGET = 0.03;
  private static final double STIFFNESS_N_PER_MM = 0.8051323301;
  private static final double GAIN = 0.50;

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model result = ModelUtil.load("Result", BASE_RESULT);
    double[][] force = result.result().numerical("int_dyn_contact_force").getReal();
    double[][] theta = result.result().numerical("eval_dyn_theta").getReal();
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
      double correctionMm = GAIN * (TARGET - smoothForce) / STIFFNESS_N_PER_MM;
      rows.add(new String[]{
          String.format(Locale.US, "%.12g", slideFraction),
          String.format(Locale.US, "%.12g", correctionMm)
      });
      previous = slideFraction;
    }
    if (rows.get(rows.size() - 1)[0].equals("1") == false) {
      rows.add(new String[]{"1", rows.get(rows.size() - 1)[1]});
    }
    ModelUtil.remove("Result");

    Model model = ModelUtil.load("Model", BASE_SETUP);
    model.label("23_lid8mm_force_calibrated_smoothed_gain050_setup.mph");
    try { model.func().remove("dr_sched"); } catch (Exception ignored) {}
    model.func().create("dr_sched", "Interpolation");
    model.func("dr_sched").label("Iteration 1 radial correction for approximately constant 0.03 N");
    model.func("dr_sched").set("funcname", "dr_force_sched");
    model.func("dr_sched").set("table", rows.toArray(new String[0][0]));
    model.func("dr_sched").set("argunit", new String[]{"1"});
    model.func("dr_sched").set("fununit", "mm");
    model.func("dr_sched").set("interp", "linear");
    model.func("dr_sched").set("extrap", "const");
    model.component("comp1").variable("var_dynamic_lid_motion")
        .set("dr_force", "dr_force_sched(slide_fraction)");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_dyn)-1)-Z*sin(phi_lid_dyn)"
              + "-dr_force*(Y*cos(phi_lid_dyn)-Z*sin(phi_lid_dyn))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_dyn)+Z*(cos(phi_lid_dyn)-1)"
              + "-dr_force*(Y*sin(phi_lid_dyn)+Z*cos(phi_lid_dyn))/sqrt(Y^2+Z^2)"
        });
    model.save(OUT);
    System.out.println("TRAJECTORY_ROWS=" + rows.size());
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
