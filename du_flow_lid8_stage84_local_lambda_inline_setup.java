import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage84_local_lambda_inline_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\161_lid8mm_stage82_local_lambda_mixed_lubrication_setup.mph");
    m.label("162_lid8mm_stage84_local_lambda_inline_setup.mph");

    String fAsp = "if(lambda_local81<=1,1,"
        + "if(lambda_local81>=3,0,0.5*(1+cos(pi*(lambda_local81-1)/2))))";
    String fAspFilm = "if(lambda_film81<=1,1,"
        + "if(lambda_film81>=3,0,0.5*(1+cos(pi*(lambda_film81-1)/2))))";

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("f_asp_film81", fAspFilm);
    m.component("comp1").variable(mv).set("f_asp_area_avg81",
        "min(1,max(0,intop_film(f_asp_film81)/A_contact_nominal73))");
    m.component("comp1").variable(mv).set("F_asp_lambda_budget81",
        "mu_asp_cal72*max(F_total_target-W_film,0)*f_asp_area_avg81");
    m.component("comp1").variable(mv).set("F_total_lambda_shear81",
        "F_film_shear+F_asp_lambda_budget81");
    m.component("comp1").variable(mv).set("mu_lambda81",
        "F_total_lambda_shear81/F_total_target");

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("f_asp_lambda81", fAsp);
    m.component("comp1").variable(pv).set("tau_asp_lambda81",
        "mu_asp_cal72*p_asp_nominal81*f_asp_lambda81");
    m.component("comp1").variable(pv).set("tau_total_lambda81",
        "scale_shear_lambda81*shear_speed_window73*(tau_film_replay81+tau_asp_lambda81)");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\162_lid8mm_stage84_local_lambda_inline_setup.mph");
    System.out.println("SAVED_STAGE84_SETUP=162_lid8mm_stage84_local_lambda_inline_setup.mph");
  }
}
