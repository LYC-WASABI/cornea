import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_stage65_compare_process_average_shear_probe {
  private static final String OLD =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";
  private static final String NEW =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph";
  private static final double NOMINAL_AREA = 8e-6;

  private static double[] integrate(Model m, String data, String expr, String tag) {
    try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    m.result().numerical().create(tag, "IntSurface");
    m.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    m.result().numerical(tag).set("data", data);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {"N"});
    return m.result().numerical(tag).getReal()[0];
  }

  private static double[] global(Model m, String data, String expr, String tag) {
    try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", data);
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {"N"});
    return m.result().numerical(tag).getReal()[0];
  }

  private static void summarize(String label, double[] values, double dt, double slideStart, double slideEnd) {
    double sumAll = 0, integralAll = 0, integralSlide = 0, peak = Double.NEGATIVE_INFINITY;
    int nSlide = 0;
    for (int i = 0; i < values.length; i++) {
      double t = dt * i;
      double v = Double.isFinite(values[i]) ? values[i] : 0;
      sumAll += v;
      peak = Math.max(peak, v);
      if (i > 0) integralAll += 0.5 * dt * (values[i - 1] + v);
      if (t >= slideStart - 1e-12 && t <= slideEnd + 1e-12) {
        nSlide++;
        if (i > 0 && dt * (i - 1) >= slideStart - 1e-12) {
          integralSlide += 0.5 * dt * (values[i - 1] + v);
        }
      }
    }
    double durationAll = dt * (values.length - 1);
    double durationSlide = slideEnd - slideStart;
    double meanSamplesAll = sumAll / values.length;
    double meanTimeAll = integralAll / durationAll;
    double meanTimeSlide = integralSlide / durationSlide;
    System.out.printf(Locale.US,
        "%s count=%d dt=%.6g peakForce=%.12g meanSampleForceAll=%.12g meanTimeForceAll=%.12g meanTimeForceSlide=%.12g peakNominalTau=%.12g meanNominalTauAll=%.12g meanNominalTauSlide=%.12g%n",
        label, values.length, dt, peak, meanSamplesAll, meanTimeAll, meanTimeSlide,
        peak / NOMINAL_AREA, meanTimeAll / NOMINAL_AREA, meanTimeSlide / NOMINAL_AREA);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model old = ModelUtil.load("Old", OLD);
    double[] oldForce = integrate(old, "dset_dynamic_slide",
        "sqrt(solid.Ttx^2+solid.Tty^2+solid.Ttz^2)", "int65_old_contact_shear");
    summarize("OLD_FIXED_MU01", oldForce, 0.01, 0.01, 0.51);

    Model newer = ModelUtil.load("New", NEW);
    double[] filmForce = integrate(newer, "dset_closedloop_film62",
        "tau_film_wall", "int65_new_film_shear");
    summarize("NEW_FILM_ONLY", filmForce, 0.01, 0.01, 0.51);

    double[] contactPlaceholder = global(newer, "dset_closedloop_film62",
        "0.02*max(F_total_target-W_film,0)", "eval65_placeholder_shear");
    double[] mixedForce = new double[filmForce.length];
    for (int i = 0; i < mixedForce.length; i++) mixedForce[i] = filmForce[i] + contactPlaceholder[i];
    summarize("NEW_FILM_PLUS_PLACEHOLDER", mixedForce, 0.01, 0.01, 0.51);
  }
}
