import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage42_h3um_gap_continuation_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\112_lid8mm_stage41_h3um_dynamic_gap_film_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\114_lid8mm_stage42_h3um_gap_continuation025_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("114_lid8mm_stage42_h3um_gap_continuation025_setup.mph");
    model.param().set("gap_feedback_scale", "0.25", "Continuation scale for replayed dynamic gap");
    model.param().set("gap_smooth_tear", "0.20[um]", "Regularization width for positive dynamic gap");
    model.component("comp1").variable("var_mixed_lub").set("gap_raw_replay_tear",
        "min(max(withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,t)),0),gap_cap_tear)");
    model.component("comp1").variable("var_mixed_lub").set("gap_smooth_replay_tear",
        "0.5*(gap_raw_replay_tear+sqrt(gap_raw_replay_tear^2+gap_smooth_tear^2))");
    model.component("comp1").variable("var_mixed_lub").set("gap_replay_tear",
        "gap_feedback_scale*gap_smooth_replay_tear");
    model.component("comp1").variable("var_mixed_lub").set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)");
    model.save(OUT);
    System.out.println("GAP_FEEDBACK_SCALE=" + model.param().get("gap_feedback_scale"));
    System.out.println("SAVED_STAGE42_SETUP=" + OUT);
  }
}
