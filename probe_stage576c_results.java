import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576c_results {
  private static final String BASE = "576c_stage576_partitioned_constant_load_results.mph";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static double first(Model model, String numerical) {
    return model.result().numerical(numerical).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      System.out.println("MODEL=" + BASE);
      System.out.println("PARAM_Q_CURRENT=" + model.param().get("q_scale574"));
      System.out.println("PARAM_T_CURRENT=" + model.param().get("t_ctrl576c"));
      String[] numerical = model.result().numerical().tags();
      for (int node = 0; node <= 10; node++) {
        for (int iter = 0; iter < 4; iter++) {
          String c = "eval576c_c_" + node + "_" + iter;
          String f = "int576c_f_" + node + "_" + iter;
          if (!has(numerical, c) || !has(numerical, f)) continue;
          try {
            double fContact = first(model, c);
            double[][] raw = model.result().numerical(f).getReal();
            double area = raw[0][0];
            double fFilm = raw[1][0];
            double meanCore = raw[2][0] / area;
            double meanTheta = raw[3][0] / area;
            String ds = "dset576c_s_" + node + "_" + iter;
            model.result().numerical().create("tmp576c_" + node + "_" + iter, "EvalGlobal");
            model.result().numerical("tmp576c_" + node + "_" + iter).set("data", ds);
            model.result().numerical("tmp576c_" + node + "_" + iter).set("expr", new String[] {"q_scale574", "t_ctrl576c"});
            double[][] params = model.result().numerical("tmp576c_" + node + "_" + iter).getReal();
            System.out.printf(Locale.US,
                "ROW node=%d iter=%d q=%.12g time=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g MeanCore=%.12g MeanTheta=%.12g%n",
                node, iter, params[0][0], params[1][0], fContact, fFilm, fContact + fFilm, meanCore, meanTheta);
          } catch (Exception error) {
            System.out.println("ROW_FAILED node=" + node + " iter=" + iter + " error=" + error);
          }
        }
      }
      System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
