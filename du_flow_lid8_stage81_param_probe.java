import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage81_param_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
    String[] names = {"h0_tear", "h_min_tear", "Rq_eq", "Rq_cornea", "Rq_lid",
        "mu_asp_cal72", "A_contact_nominal73", "F_total_target", "T_speed_ramp"};
    for (String n : names) {
      try {
        System.out.println(n + "=" + m.param().get(n));
      } catch (Exception e) {
        System.out.println(n + "=<missing>");
      }
    }
    String[] vars = {"h_film_input", "gap_replay_tear", "tau_film_wall", "F_film_shear",
        "W_film", "omega_lid", "lid_mask", "W_film_replay53", "t_film_replay_grid"};
    for (String v : vars) {
      try {
        System.out.println(v + "=" + m.component("comp1").variable("var_mixed_lub").get(v));
      } catch (Exception e1) {
        try {
          System.out.println(v + "=" + m.component("comp1").variable("var_partitioned_local_pfilm").get(v));
        } catch (Exception e2) {
          System.out.println(v + "=<missing>");
        }
      }
    }
  }
}
