import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_mixed_lubrication_stage10_calibration_iteration2_setup {
  private static final String BASE_SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\43_lid8mm_mixed_lubrication_stage8_pseudotime_setup.mph";
  private static final String BASE_RESULT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\46_lid8mm_mixed_lubrication_stage9_calibration_iteration1_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\47_lid8mm_mixed_lubrication_stage10_calibration_iteration2_setup.mph";
  private static final double TARGET = 0.03;
  private static final double STIFFNESS_N_PER_MM = 0.8051323301;
  private static final double RESIDUAL_GAIN = 0.01;

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model result = ModelUtil.load("Result", BASE_RESULT);
    double[][] fraction = result.result().numerical("eval_struct_fraction").getReal();
    double[][] currentDr = result.result().numerical("eval_struct_dr").getReal();
    double[][] total = result.result().numerical("eval_struct_W_total").getReal();
    List<String[]> rows = new ArrayList<>();
    double previous = -1;
    for (int i = 0; i < total[0].length; i++) {
      double f = Math.max(0, Math.min(1, fraction[0][i]));
      if (f <= previous + 1e-9) continue;
      double left = Math.max(0, Math.min(1, (f - 0.15) / 0.20));
      double right = Math.max(0, Math.min(1, (0.85 - f) / 0.20));
      double envelope = left * left * (3 - 2 * left) * right * right * (3 - 2 * right);
      double corrected = envelope * RESIDUAL_GAIN * (TARGET - total[0][i]) / STIFFNESS_N_PER_MM;
      rows.add(new String[]{
          String.format(Locale.US, "%.12g", f),
          String.format(Locale.US, "%.12g", corrected)
      });
      previous = f;
    }
    if (rows.get(0)[0].equals("0") == false) rows.add(0, new String[]{"0", rows.get(0)[1]});
    if (rows.get(rows.size() - 1)[0].equals("1") == false) {
      rows.add(new String[]{"1", rows.get(rows.size() - 1)[1]});
    }
    ModelUtil.remove("Result");

    Model model = ModelUtil.load("Model", BASE_SETUP);
    model.label("47_lid8mm_mixed_lubrication_stage10_calibration_iteration2_setup.mph");
    try { model.func().remove("dr_mixed_sched"); } catch (Exception ignored) {}
    model.func().create("dr_mixed_sched", "Interpolation");
    model.func("dr_mixed_sched").label("Mixed-load calibration iteration 2 smooth residual correction");
    model.func("dr_mixed_sched").set("funcname", "dr_mixed_resid_sched");
    model.func("dr_mixed_sched").set("table", rows.toArray(new String[0][0]));
    model.func("dr_mixed_sched").set("argunit", new String[]{"1"});
    model.func("dr_mixed_sched").set("fununit", "mm");
    model.func("dr_mixed_sched").set("interp", "piecewisecubic");
    model.func("dr_mixed_sched").set("extrap", "const");
    model.component("comp1").variable("var_structure_pseudotime").set("dr_mixed_structure",
        "dr_force_sched(slide_fraction_structure)"
            + "-mixed_correction_gain_structure*W_film_structure/K_contact_structure"
            + "+dr_mixed_resid_sched(slide_fraction_structure)");
    model.component("comp1").variable("var_structure_pseudotime").set("asp_shear_density_structure",
        "if(isdefined(solid.Tn),if(solid.Tn>0,tau0_asp+alpha_asp*solid.Tn,0),0)");
    model.save(OUT);
    System.out.println("TRAJECTORY_ROWS=" + rows.size());
    System.out.println("SAVED_STAGE10_ITER2_SETUP=" + OUT);
  }
}
