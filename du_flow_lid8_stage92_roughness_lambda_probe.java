import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage92_roughness_lambda_probe {
  private static void surface(Model m, String type, String tag, String expr, String unit) {
    m.result().numerical().create(tag, type);
    m.result().numerical(tag).set("data", "dset_constant_speed_film71");
    m.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical(tag).getReal()[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(java.util.Locale.US, "%s %.12g to %.12g [%s]%n", tag, min, max, unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\169_lid8mm_stage91_lid_roughness_0p5um_setup.mph");
    System.out.println("Rq_cornea=" + m.param().get("Rq_cornea"));
    System.out.println("Rq_lid=" + m.param().get("Rq_lid"));
    System.out.println("Rq_eq=" + m.param().get("Rq_eq"));
    surface(m, "MinSurface", "min92_lambda", "h_film_input/Rq_eq", "1");
    surface(m, "MaxSurface", "max92_lambda", "h_film_input/Rq_eq", "1");
    surface(m, "MaxSurface", "max92_fbreak", "f_break90", "1");
  }
}
