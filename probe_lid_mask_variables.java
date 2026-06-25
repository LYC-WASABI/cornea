import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_lid_mask_variables {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "237_lid8mm_stage136_slow_motion_ramp_short_bridge_results_Model.mph");
    String[] names = {
        "lid_mask", "lid_mask_length", "lid_mask_width", "theta_geom_dyn",
        "theta_local", "theta_lid", "arc_local", "x_local_lid",
        "lid_width_coord", "L_lid_chord", "W_lid_chord", "footprint_smooth",
        "phi_lid_film_replay", "A_contact_nominal73", "p_boundary_nominal90",
        "x_lid_local", "s_lid", "w_lid"
    };
    for (String tag : m.component("comp1").variable().tags()) {
      for (String n : names) {
        try {
          String value = m.component("comp1").variable(tag).get(n);
          if (value != null && !value.isEmpty())
            System.out.println(tag + "." + n + "=" + value);
        } catch (Exception ignore) {}
      }
    }
    ModelUtil.disconnect();
  }
}
