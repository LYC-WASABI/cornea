import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage31_final_load_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";

  private static double[][] surface(Model model, String tag, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).selection().named("sel_lid_outer_support");
    model.result().numerical(tag).set("data", "dset5");
    model.result().numerical(tag).set("expr", new String[]{expr});
    return model.result().numerical(tag).getReal();
  }

  private static double[][] global(Model model, String tag, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset5");
    model.result().numerical(tag).set("expr", new String[]{expr});
    return model.result().numerical(tag).getReal();
  }

  private static void range(String name, double[] values) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double x : values) { min = Math.min(min, x); max = Math.max(max, x); }
    System.out.println(name + "_MIN=" + min + " MAX=" + max);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    double[] total = surface(model, "int_stage31_rf",
        "-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)")[0];
    double[] film = global(model, "gev_stage31_film", "comp1.W_film_replay")[0];
    double[] shear = global(model, "gev_stage31_shear", "comp1.F_film_shear_replay")[0];
    double[] friction = new double[total.length];
    double[] mu = new double[total.length];
    for (int i = 0; i < total.length; i++) {
      friction[i] = shear[i] + 0.02*Math.max(total[i] - film[i], 0);
      mu[i] = friction[i]/0.03;
    }
    range("TOTAL_SUPPORT_REACTION_N", total);
    range("FILM_NORMAL_LOAD_N", film);
    range("FILM_SHEAR_FORCE_N", shear);
    range("REPORTED_FRICTION_FORCE_N", friction);
    range("REPORTED_MU", mu);
  }
}
