import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_stage66_footprint_time_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    for (String p : new String[] {
        "theta_lid", "theta_slide_total", "T_pre", "T_slide", "T_hold",
        "T_structure_pre", "T_structure_slide", "T_structure_hold",
        "L_slide", "v_blink_avg", "L_lid_chord", "W_lid_chord",
        "footprint_smooth", "h0_tear", "h_outside_track"
    }) {
      try { System.out.println("PARAM " + p + "=" + m.param().get(p)); }
      catch (Exception ignored) {}
    }
    String group = "var_mixed_lub";
    for (String v : new String[] {
        "slide_fraction_film_replay", "phi_lid_film_replay", "theta_geom_dyn",
        "lid_width_coord", "lid_mask_length", "lid_mask_width", "lid_mask",
        "omega_lid", "vwall_x", "vwall_y", "vwall_z", "gap_replay_tear",
        "h_inside_lid", "h_film_input"
    }) {
      try { System.out.println("VAR " + v + "=" + m.component("comp1").variable(group).get(v)); }
      catch (Exception ignored) {}
    }
  }
}
