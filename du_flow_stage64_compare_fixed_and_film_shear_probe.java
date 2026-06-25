import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_stage64_compare_fixed_and_film_shear_probe {
  private static final String OLD =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";
  private static final String NEW =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph";

  private static boolean has(String[] tags, String wanted) {
    for (String tag : tags) if (tag.equals(wanted)) return true;
    return false;
  }

  private static void tryEval(Model m, String data, String expr, String unit, String suffix) {
    String safe = suffix.replaceAll("[^A-Za-z0-9_]", "_");
    try {
      String maxTag = "max_" + safe;
      String intTag = "int_" + safe;
      if (has(m.result().numerical().tags(), maxTag)) m.result().numerical().remove(maxTag);
      if (has(m.result().numerical().tags(), intTag)) m.result().numerical().remove(intTag);
      m.result().numerical().create(maxTag, "MaxSurface");
      m.result().numerical(maxTag).selection().named("sel_cornea_anterior_surface");
      m.result().numerical(maxTag).set("data", data);
      m.result().numerical(maxTag).set("expr", new String[] {expr});
      m.result().numerical(maxTag).set("unit", new String[] {unit});
      double[][] maxValues = m.result().numerical(maxTag).getReal();
      m.result().numerical().create(intTag, "IntSurface");
      m.result().numerical(intTag).selection().named("sel_cornea_anterior_surface");
      m.result().numerical(intTag).set("data", data);
      m.result().numerical(intTag).set("expr", new String[] {expr});
      m.result().numerical(intTag).set("unit", new String[] {unit.equals("Pa") ? "N" : unit});
      double[][] intValues = m.result().numerical(intTag).getReal();
      double maxPeak = Double.NEGATIVE_INFINITY, intPeak = Double.NEGATIVE_INFINITY;
      int maxIndex = -1, intIndex = -1;
      for (int i = 0; i < maxValues[0].length; i++) {
        if (Double.isFinite(maxValues[0][i]) && maxValues[0][i] > maxPeak) {
          maxPeak = maxValues[0][i]; maxIndex = i;
        }
      }
      for (int i = 0; i < intValues[0].length; i++) {
        if (Double.isFinite(intValues[0][i]) && intValues[0][i] > intPeak) {
          intPeak = intValues[0][i]; intIndex = i;
        }
      }
      System.out.printf(Locale.US,
          "OK data=%s expr=%s maxPeak=%.12g index=%d integralPeak=%.12g index=%d%n",
          data, expr, maxPeak, maxIndex, intPeak, intIndex);
    } catch (Exception e) {
      System.out.println("FAIL data=" + data + " expr=" + expr + " reason=" + e.getMessage());
    }
  }

  private static void inspectOld() throws Exception {
    Model m = ModelUtil.load("Old", OLD);
    System.out.println("OLD DATASETS=" + Arrays.toString(m.result().dataset().tags()));
    System.out.println("OLD PARAM mu_friction=" + m.param().get("mu_friction"));
    System.out.println("OLD SOLID FEATURES=" + Arrays.toString(m.component("comp1").physics("solid").feature().tags()));
    try {
      System.out.println("OLD FRICTION mu_fric=" +
          m.component("comp1").physics("solid").feature("dcnt1").feature("fric1").getString("mu_fric"));
    } catch (Exception e) {
      System.out.println("OLD FRICTION probe failed=" + e.getMessage());
    }
    String data = "dset_dynamic_slide";
    for (String expr : new String[] {
        "sqrt(solid.Ttx^2+solid.Tty^2+solid.Ttz^2)",
        "sqrt(solid.Tx^2+solid.Ty^2+solid.Tz^2-solid.Tn^2)",
        "sqrt(solid.dcnt1.Ttx^2+solid.dcnt1.Tty^2+solid.dcnt1.Ttz^2)",
        "abs(solid.Tt)",
        "mu_friction*max(solid.Tn,0)"
    }) tryEval(m, data, expr, "Pa", "old_" + Math.abs(expr.hashCode()));
  }

  private static void inspectNew() throws Exception {
    Model m = ModelUtil.load("New", NEW);
    System.out.println("NEW DATASETS=" + Arrays.toString(m.result().dataset().tags()));
    tryEval(m, "dset_closedloop_film62", "tau_film_wall", "Pa", "new_tau_film");
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    inspectOld();
    inspectNew();
  }
}
