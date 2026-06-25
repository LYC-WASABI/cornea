import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage153_physical_variables {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "273_lid8mm_stage153_complete_qs_mixed_shear_results_Model.mph");
    String[] vars = {
        "h_film_input", "Rq_eq", "lambda_local81", "C_film_break95",
        "f_break95", "tau_film_wall", "tau_film_replay81",
        "F_film_shear", "p_boundary_nominal95", "tau_boundary_break95",
        "F_boundary_break95", "tau_total_break95", "F_total_break95",
        "mu_break95", "mu_boundary_break90", "mu_asp_cal72",
        "p_asp_nominal81", "f_asp_lambda81", "tau_asp_lambda81",
        "tau_total_lambda81"
    };
    for (String vt : m.component("comp1").variable().tags()) {
      System.out.println("VARTAG " + vt);
      for (String name : vars) {
        try {
          String expr = m.component("comp1").variable(vt).get(name);
          if (expr != null && !expr.isEmpty())
            System.out.println(vt + " :: " + name + " = " + expr);
        } catch (Exception ignore) {}
      }
    }
    String pv = "var_partitioned_local_pfilm";
    for (String name : new String[]{"h_replay81", "tau_film_replay81",
        "W_film_replay81", "F_film_shear_replay81",
        "F_asp_lambda_replay81", "F_total_lambda_replay81",
        "mu_lambda_replay81", "p_asp_nominal81", "tau_asp_lambda81",
        "t_film_replay_grid", "shear_speed_window73", "lid_mask",
        "ty_shear73", "tz_shear73"}) {
      try { System.out.println("DIRECT " + name + " = "
          + m.component("comp1").variable(pv).get(name)); }
      catch (Exception e) { System.out.println("MISSING " + name); }
    }
    System.out.println("SOLS " + Arrays.toString(m.sol().tags()));
    for (String p : new String[]{"Rq_cornea", "Rq_lid", "h_break_low",
        "h_break_high", "mu_boundary_break90", "mu_asp_cal72",
        "A_contact_nominal73", "F_total_target"}) {
      try { System.out.println("PARAM " + p + " = " + m.param().get(p)); }
      catch (Exception ignore) {}
    }
    ModelUtil.disconnect();
  }
}
