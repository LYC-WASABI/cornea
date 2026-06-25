import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage83_lambda_distribution_probe {
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
    System.out.printf(java.util.Locale.US, "%s %s range %.12g to %.12g [%s]%n",
        type, tag, min, max, unit);
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_constant_speed_film71");
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
    System.out.printf(java.util.Locale.US, "%s range %.12g to %.12g [%s]%n", tag, min, max, unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\161_lid8mm_stage82_local_lambda_mixed_lubrication_setup.mph");
    String lambda = "h_film_input/Rq_eq";
    String fasp = "if((" + lambda + ")<=lambda_boundary81,1,"
        + "if((" + lambda + ")>=lambda_fullfilm81,0,"
        + "0.5*(1+cos(pi*((" + lambda + ")-lambda_boundary81)/(lambda_fullfilm81-lambda_boundary81)))))";
    surface(m, "MinSurface", "min83_lambda", lambda, "1");
    surface(m, "MaxSurface", "max83_lambda", lambda, "1");
    surface(m, "MaxSurface", "max83_fasp", fasp, "1");
    global(m, "eval83_Ffilm", "F_film_shear", "N");
    global(m, "eval83_Fasp_lambda", "F_asp_lambda_budget81", "N");
    global(m, "eval83_mu_lambda", "mu_lambda81", "1");
  }
}
