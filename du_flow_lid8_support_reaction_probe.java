import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_support_reaction_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph";

  private static void eval(Model model, String tag, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).selection().named("sel_lid_outer_support");
    model.result().numerical(tag).set("data", "dset_dynamic_slide");
    model.result().numerical(tag).set("expr", new String[]{expr});
    double[][] v = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double x : v[0]) { min = Math.min(min, x); max = Math.max(max, x); }
    System.out.println(tag + " expr=" + expr + " min=" + min + " max=" + max);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    eval(model, "int_rf_radial", "-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)");
    eval(model, "int_rf_radial_pos", "(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)");
    eval(model, "int_rf_y", "solid.RFy");
    eval(model, "int_rf_z", "solid.RFz");
  }
}
