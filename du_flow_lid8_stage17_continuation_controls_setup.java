import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage17_continuation_controls_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\57_lid8mm_stage16_local_pressure_strong_coupled_fullycoupled_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\59_lid8mm_stage17_strong_coupling_continuation_controls_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("59_lid8mm_stage17_strong_coupling_continuation_controls_setup.mph");
    model.param().set("scale_structure_motion", "1", "Continuation scale for whole-lid displacement");
    model.param().set("scale_wall_velocity", "0", "Continuation scale for thin-film wall velocity");
    model.param().set("scale_pfilm_feedback", "0", "Continuation scale for local pfilm feedback");
    model.component("comp1").variable("var_mixed_lub").set("omega_lid",
        "scale_wall_velocity*theta_slide_total*0.5*pi/T_slide"
            + "*sin(pi*min(1,tau_motion_strong/T_slide))*dtau_motion_strong");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "scale_structure_motion*(Y*(cos(phi_lid_dyn)-1)-Z*sin(phi_lid_dyn)"
              + "-dr_force*(Y*cos(phi_lid_dyn)-Z*sin(phi_lid_dyn))/sqrt(Y^2+Z^2))",
          "scale_structure_motion*(Y*sin(phi_lid_dyn)+Z*(cos(phi_lid_dyn)-1)"
              + "-dr_force*(Y*sin(phi_lid_dyn)+Z*cos(phi_lid_dyn))/sqrt(Y^2+Z^2))"
        });
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback")
        .set("FperArea", new String[]{
            "-scale_pfilm_feedback*film_feedback_ramp*max(pfilm,0)*nx",
            "-scale_pfilm_feedback*film_feedback_ramp*max(pfilm,0)*ny",
            "-scale_pfilm_feedback*film_feedback_ramp*max(pfilm,0)*nz"});
    model.save(OUT);
    System.out.println("SAVED_STAGE17_CONTROLS=" + OUT);
  }
}
