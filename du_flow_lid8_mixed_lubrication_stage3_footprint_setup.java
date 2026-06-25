import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage3_footprint_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\32_lid8mm_mixed_lubrication_stage2_oneway_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\34_lid8mm_mixed_lubrication_stage3_moving_footprint_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("34_lid8mm_mixed_lubrication_stage3_fixed_wetted_track_setup.mph");
    model.param().set("h_outside_track", "30[um]",
        "Large numerical gap outside the blink wetted track");
    model.param().set("footprint_smooth", "0.20[mm]",
        "Smooth transition width at wetted-track edges");
    model.component("comp1").variable("var_mixed_lub").set("theta_geom_dyn",
        "-theta_lid+phi_lid_dyn");
    model.component("comp1").variable("var_mixed_lub").set("lid_width_coord",
        "Y*cos(theta_geom_dyn)+Z*sin(theta_geom_dyn)");
    model.component("comp1").variable("var_mixed_lub").set("lid_mask_length",
        "flc2hs(L_lid_chord/2-abs(X),footprint_smooth)");
    model.component("comp1").variable("var_mixed_lub").set("lid_mask_width",
        "flc2hs(W_lid_chord/2-abs(lid_width_coord),footprint_smooth)");
    model.component("comp1").variable("var_mixed_lub").set("lid_mask",
        "lid_mask_length");
    model.component("comp1").variable("var_mixed_lub").set("h_inside_lid",
        "max(h_min_tear,h0_tear+Rq_eq)");
    model.component("comp1").variable("var_mixed_lub").set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    model.component("comp1").variable("var_mixed_lub").set("vwall_x", "0");
    model.component("comp1").variable("var_mixed_lub").set("vwall_y",
        "lid_mask*(-omega_lid*Z)");
    model.component("comp1").variable("var_mixed_lub").set("vwall_z",
        "lid_mask*(omega_lid*Y)");
    model.save(OUT);
    System.out.println("SAVED_STAGE3_SETUP=" + OUT);
  }
}
