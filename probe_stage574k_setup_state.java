import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574k_setup_state {
  private static void showVar(ModelNode comp, String name) {
    try {
      System.out.println(name + "=" + comp.variable("var_cornea_dynamic_regions573").get(name));
    } catch (Exception error) {
      System.out.println(name + ".error=" + error.getMessage());
    }
  }
  private static void showParam(Model model, String name) {
    try {
      System.out.println(name + ".expr=" + model.param().get(name));
      System.out.println(name + ".value=" + model.param().evaluate(name));
    } catch (Exception error) {
      System.out.println(name + ".error=" + error.getMessage());
    }
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "574k_stage574_gap_regularized_release_scan_setup.mph");
      ModelNode comp = model.component("comp1");
      for (String p : new String[] {"q_scale574", "q_fixed574", "h_active_max573", "dh_active573"}) {
        showParam(model, p);
      }
      for (String v : new String[] {
        "g_pair_native573", "g_pair_valid573", "g_pair_safe573",
        "B_low573", "B_high573", "Bfilm573",
        "g_pair_physical573", "h_wet573", "Afilm573", "h_calc573",
        "p_load573"
      }) {
        showVar(comp, v);
      }
      try {
        System.out.println("disp_lid_time.U0=" + java.util.Arrays.toString(
            comp.physics("solid").feature("disp_lid_time").getStringArray("U0")));
      } catch (Exception error) {
        System.out.println("disp.error=" + error.getMessage());
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
