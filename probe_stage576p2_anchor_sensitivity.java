import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_anchor_sensitivity {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String SWEPT = "sel_film_swept571";

  private static double integrate(Model model, String data, String tag,
      String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double extreme(Model model, String data, String tag,
      String type, String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      model.param().set("t_position576p2", "T_pre572+0.5*T_slide572");
      String data = "dset576p2Anchor";
      try { model.result().dataset().remove(data); } catch (Exception ignored) {}
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", "sol156");
      String[] values = new String[] {"1e-4", "2e-4", "5e-4", "1e-3"};
      for (String value : values) {
        model.param().set("kanchor576p", value + "[kg/(m^2*s*Pa)]");
        model.sol("sol156").clearSolutionData();
        System.out.println("ANCHOR_START=" + value);
        model.sol("sol156").runAll();
        double film = integrate(model, data, "int576p2AnchorFilm",
            "max(p_load573,0[Pa])");
        double maxP = extreme(model, data, "max576p2AnchorP", "MaxSurface",
            "tff.p-p_amb573");
        double minTheta = extreme(model, data, "min576p2AnchorTheta",
            "MinSurface", "tff.theta");
        System.out.printf(Locale.US,
            "ANCHOR_RESULT value=%s Ffilm=%.12g MaxP=%.12g MinTheta=%.12g%n",
            value, film, maxP, minTheta);
      }
      model.save("probe_stage576p2_anchor_sensitivity_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
