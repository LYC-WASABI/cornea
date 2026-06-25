import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576d_time_window {
  private static final String BASE = "576b_stage576_dynamic_load_verification_results.mph";
  private static final String SOL = "sol236";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double[][] global(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] integrate(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String data = "dset576d_window";
      removeDataset(model, data);
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", SOL);
      double[] time = global(model, data, "eval576d_window_t", new String[] {"t"})[0];
      double[][] patch = integrate(model, data, "int576d_window",
          new String[] {"1", "p_load573", "M_core573", "M_drain573", "tff.theta"});
      double[] maxP = surface(model, data, "max576d_window_p", "MaxSurface", "tff.p-p_amb573")[0];
      double area = patch[0][0];
      double t0 = time[0];
      double t1 = time[time.length-1];
      double windowMax = 0;
      double windowMaxFraction = 0;
      for (int i = 0; i < time.length; i++) {
        double fraction = (time[i]-t0)/(t1-t0);
        if (fraction < 0.80-1e-9 || fraction > 0.96+1e-9) continue;
        double film = patch[1][i];
        if (film > windowMax) { windowMax = film; windowMaxFraction = fraction; }
        System.out.printf(Locale.US,
            "WINDOW fraction=%.6f time=%.12g Ffilm=%.12g MeanCore=%.12g MeanDrain=%.12g MeanTheta=%.12g MaxP=%.12g%n",
            fraction, time[i], film, patch[2][i]/area, patch[3][i]/area, patch[4][i]/area, maxP[i]);
      }
      System.out.printf(Locale.US, "WINDOW_MAX fraction=%.6f Ffilm=%.12g%n", windowMaxFraction, windowMax);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
