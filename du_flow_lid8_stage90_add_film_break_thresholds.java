import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage90_add_film_break_thresholds {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\167_lid8mm_stage89_light_lambda_film_shear_results.mph");
    m.label("168_lid8mm_stage90_film_break_thresholds_setup.mph");

    m.param().set("h_break_low", "0.5[um]",
        "Local tear-film rupture lower threshold: film fully broken below this thickness");
    m.param().set("h_break_high", "1.0[um]",
        "Local tear-film rupture upper threshold: film fully intact above this thickness");
    m.param().set("mu_boundary_break90", "0.1",
        "Boundary shear coefficient used if local film rupture is active");

    String cFilm = "if(h_film_input<=h_break_low,0,"
        + "if(h_film_input>=h_break_high,1,"
        + "0.5-0.5*cos(pi*(h_film_input-h_break_low)/(h_break_high-h_break_low))))";
    String cReplay = "if(h_replay88<=h_break_low,0,"
        + "if(h_replay88>=h_break_high,1,"
        + "0.5-0.5*cos(pi*(h_replay88-h_break_low)/(h_break_high-h_break_low))))";

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("C_film_break90", cFilm);
    m.component("comp1").variable(mv).set("f_break90", "1-C_film_break90");
    m.component("comp1").variable(mv).set("p_boundary_nominal90",
        "F_total_target/A_contact_nominal73");
    m.component("comp1").variable(mv).set("tau_boundary_break90",
        "mu_boundary_break90*p_boundary_nominal90*f_break90");

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("C_film_break_replay90", cReplay);
    m.component("comp1").variable(pv).set("f_break_replay90", "1-C_film_break_replay90");
    m.component("comp1").variable(pv).set("p_boundary_nominal90",
        "F_total_target/A_contact_nominal73");
    m.component("comp1").variable(pv).set("tau_boundary_break_replay90",
        "mu_boundary_break90*p_boundary_nominal90*f_break_replay90");
    m.component("comp1").variable(pv).set("tau_total_break_replay90",
        "shear_speed_window73*(C_film_break_replay90*tau_nominal_film88+tau_boundary_break_replay90)");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\168_lid8mm_stage90_film_break_thresholds_setup.mph");
    System.out.println("SAVED_STAGE90=168_lid8mm_stage90_film_break_thresholds_setup.mph");
    System.out.println("h_break_low=" + m.param().get("h_break_low"));
    System.out.println("h_break_high=" + m.param().get("h_break_high"));
  }
}
