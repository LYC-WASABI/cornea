import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574_velocity_params {
  private static void show(Model model, String name) {
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
      Model model = ModelUtil.load(
          "Model", "574h_stage574_fixed_structure_constant_zero_jfo_checked.mph");
      ModelNode comp = model.component("comp1");
      for (String name : new String[] {
          "v_blink_avg", "L_slide", "T_slide572", "theta_slide_total",
          "T_pre572", "T_hold572", "time_offset572"
      }) {
        show(model, name);
      }
      try {
        System.out.println("tau572.expr=" + comp.variable("var_dynamic_motion572").get("tau572"));
        System.out.println("omega_lid_rot572.expr="
            + comp.variable("var_dynamic_motion572").get("omega_lid_rot572"));
      } catch (Exception error) {
        System.out.println("motion_var.error=" + error.getMessage());
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
