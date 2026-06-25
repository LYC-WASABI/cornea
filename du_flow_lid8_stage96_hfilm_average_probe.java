import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage96_hfilm_average_probe {
  private static double trapz(double[] v, double dt, int lo, int hi) {
    double s = 0;
    for (int i = lo + 1; i <= hi && i < v.length; i++) s += 0.5 * dt * (v[i - 1] + v[i]);
    return s;
  }

  private static void range(String label, double[] v, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double x : v) {
      if (Double.isFinite(x)) {
        min = Math.min(min, x);
        max = Math.max(max, x);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", label, min, unit, max, unit);
    if (v.length > 51) {
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n", label, trapz(v, .01, 1, 51) / .50, unit);
    }
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    range(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surface(Model m, String type, String tag, String expr, String unit) {
    m.result().numerical().create(tag, type);
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    range(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\174_lid8mm_stage96_rq0p5um_high_break_threshold_sweep_results.mph");
    global(m, "eval96_hfilm_area_avg_actual", "intop_film(h_film_input)/intop_film(1)", "um");
    global(m, "eval96_film_area_actual", "intop_film(1)", "mm^2");
    surface(m, "MinSurface", "min96_hfilm_surface", "h_film_input", "um");
    surface(m, "MaxSurface", "max96_hfilm_surface", "h_film_input", "um");
  }
}
