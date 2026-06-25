import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage119_motion_law {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "202_lid8mm_stage119_two_phase_variable_indent_setup_Model.mph");
      String[] names = {
          "slide_fraction", "slide_fraction_structure", "phi_lid_structure",
          "theta_lid_physical", "v_lid_structure", "omega_lid_structure"
      };
      for (String vtag : m.component("comp1").variable().tags()) {
        for (String name : names) {
          try {
            String value = m.component("comp1").variable(vtag).get(name);
            if (value != null && !value.isEmpty()) {
              System.out.println(vtag + "." + name + "=" + value);
            }
          } catch (Exception ignore) {}
        }
      }
      for (String p : new String[]{
          "T_pre", "T_slide", "T_structure_pre", "T_structure_slide",
          "T_force_transition119", "T_speed_ramp", "t_ramp", "t_accel", "v_blink_avg"
      }) {
        try { System.out.println("PARAM " + p + "=" + m.param().get(p)); }
        catch (Exception ignore) {}
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
