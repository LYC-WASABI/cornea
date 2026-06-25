import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage86_local_lambda_film_only_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\162_lid8mm_stage84_local_lambda_inline_setup.mph");
    m.label("164_lid8mm_stage86_local_lambda_film_only_setup.mph");

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("F_asp_lambda_budget81", "0[N]");
    m.component("comp1").variable(mv).set("F_total_lambda_shear81", "F_film_shear");
    m.component("comp1").variable(mv).set("mu_lambda81", "F_film_shear/F_total_target");

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("F_asp_lambda_replay81", "0[N]");
    m.component("comp1").variable(pv).set("F_total_lambda_replay81",
        "shear_speed_window73*F_film_shear_replay81");
    m.component("comp1").variable(pv).set("mu_lambda_replay81",
        "F_total_lambda_replay81/F_total_target");
    m.component("comp1").variable(pv).set("tau_asp_lambda81", "0[Pa]");
    m.component("comp1").variable(pv).set("tau_total_lambda81",
        "scale_shear_lambda81*shear_speed_window73*tau_film_replay81");
    m.component("comp1").variable(pv).set("tau_lid_nominal_lambda81",
        "scale_shear_lambda81*F_total_lambda_replay81/A_contact_nominal73");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\164_lid8mm_stage86_local_lambda_film_only_setup.mph");
    System.out.println("SAVED_STAGE86_SETUP=164_lid8mm_stage86_local_lambda_film_only_setup.mph");
  }
}
