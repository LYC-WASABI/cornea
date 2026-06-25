import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage41_gap_and_tff_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";

  private static void surface(Model model, String tag, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "MinSurface");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("data", "dset5");
    model.result().numerical(tag).set("expr", new String[]{expr});
    double[][] min = model.result().numerical(tag).getReal();
    model.result().numerical().remove(tag);
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("data", "dset5");
    model.result().numerical(tag).set("expr", new String[]{expr});
    double[][] max = model.result().numerical(tag).getReal();
    double lo = Double.POSITIVE_INFINITY, hi = Double.NEGATIVE_INFINITY;
    for (double x : min[0]) lo = Math.min(lo, x);
    for (double x : max[0]) hi = Math.max(hi, x);
    System.out.println(tag + " expr=" + expr + " min=" + lo + " max=" + hi);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
    System.out.println("DATASETS=" + Arrays.toString(model.result().dataset().tags()));
    System.out.println("h0_tear=" + model.param().get("h0_tear"));
    System.out.println("h_min_tear=" + model.param().get("h_min_tear"));
    System.out.println("Rq_eq=" + model.param().get("Rq_eq"));
    System.out.println("h_film_input="
        + model.component("comp1").variable("var_mixed_lub").get("h_film_input"));
    System.out.println("ffp1.hw1="
        + model.component("comp1").physics("tff").feature("ffp1").getString("hw1"));
    System.out.println("bdr1.BorderCondition="
        + model.component("comp1").physics("tff").feature("bdr1").getString("BorderCondition"));
    System.out.println("PAIR_GAP_DST="
        + model.component("comp1").pair("cp_lid_cornea").gapName(true));
    surface(model, "gap_dst", "geomgap_dst_cp_lid_cornea");
    surface(model, "gap_dst_pos", "max(geomgap_dst_cp_lid_cornea,0)");
  }
}
