import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage572_motion {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "571_stage571_swept_film_domain_checked.mph");
      ModelNode comp = model.component("comp1");
      for (String tag : new String[] {
          "var_dynamic_lid_motion", "var_partitioned_local_pfilm"
      }) {
        System.out.println("VARIABLE|" + tag + "|" + comp.variable(tag).label());
        for (String name : comp.variable(tag).varnames()) {
          String low = name.toLowerCase(Locale.ROOT);
          if (low.contains("slide") || low.contains("phi")
              || low.contains("omega") || low.contains("velocity")
              || low.contains("v_lid") || low.contains("indent")) {
            System.out.println("VAR|" + name + "|" + comp.variable(tag).get(name));
          }
        }
      }
      for (String name : new String[] {
          "T_pre", "T_slide", "T_structure_pre", "T_structure_slide",
          "T_speed_ramp", "v_blink_avg", "theta_start", "theta_end",
          "theta_lid", "theta_slide_total", "phi_qs142", "L_slide", "T_hold"
      }) {
        try { System.out.println("PARAM|" + name + "|" + model.param().get(name)); }
        catch (Exception ignored) {}
      }
      System.out.println("DISP_U0=" + Arrays.toString(
          comp.physics("solid").feature("disp_lid_time")
              .getStringArray("U0")));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
