import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage530_motion_params {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "523_stage520_local_tff_drainage_checked.mph");
    for (String name : new String[] {
        "t_replay", "v_blink_avg", "h0_tear", "h_residual189",
        "delta_h_jfo197", "T_structure_pre", "T_structure_slide",
        "T_speed_ramp", "omega_slide_const"
        , "L_lid_arc", "L_lid_chord", "W_lid_arc", "W_lid_chord",
        "s_lid", "w_lid", "R_cor",
        "film_track_half_width", "film_track_end_buffer",
        "film_track_theta_max", "Rcor"
    }) {
      try { System.out.println(name + "=" + model.param().get(name)); }
      catch (Exception error) { System.out.println(name + "=MISSING"); }
    }
    for (String name : new String[] {
        "lid_mask_length", "lid_mask_width", "lid_mask",
        "theta_geom_dyn", "lid_length_coord", "lid_width_coord",
        "omega_lid", "vwall_x", "vwall_y", "vwall_z", "h_jfo197"
    }) {
      try {
        System.out.println(name + "="
            + model.component("comp1").variable("var_mixed_lub").get(name));
      } catch (Exception error) { System.out.println(name + "=MISSING"); }
    }
    ModelUtil.disconnect();
  }
}
