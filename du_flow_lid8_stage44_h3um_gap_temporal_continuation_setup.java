import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage44_h3um_gap_temporal_continuation_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\112_lid8mm_stage41_h3um_dynamic_gap_film_setup.mph");

    model.param().set("gap_cap_tear", "3[um]",
        "Maximum pair-gap contribution retained in the local tear-film law");
    model.param().set("gap_feedback_scale", "0.10",
        "Continuation factor for dynamic pair-gap feedback");
    model.param().set("gap_smooth_tear", "0.50[um]",
        "Regularization length for the positive pair-gap part");

    String vars = "var_mixed_lub";
    model.component("comp1").variable(vars).set("gap_pair_tm2",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,max(0[s],t-0.002[s])))");
    model.component("comp1").variable(vars).set("gap_pair_tm1",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,max(0[s],t-0.001[s])))");
    model.component("comp1").variable(vars).set("gap_pair_t0",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,t))");
    model.component("comp1").variable(vars).set("gap_pair_tp1",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,min(0.53[s],t+0.001[s])))");
    model.component("comp1").variable(vars).set("gap_pair_tp2",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,min(0.53[s],t+0.002[s])))");
    model.component("comp1").variable(vars).set("gap_temporal_replay_tear",
        "(gap_pair_tm2+2*gap_pair_tm1+3*gap_pair_t0+2*gap_pair_tp1+gap_pair_tp2)/9");
    model.component("comp1").variable(vars).set("gap_raw_replay_tear",
        "min(max(gap_temporal_replay_tear,0),gap_cap_tear)");
    model.component("comp1").variable(vars).set("gap_smooth_replay_tear",
        "0.5*(gap_raw_replay_tear+sqrt(gap_raw_replay_tear^2+gap_smooth_tear^2))-0.5*gap_smooth_tear");
    model.component("comp1").variable(vars).set("gap_replay_tear",
        "gap_feedback_scale*lid_mask*gap_smooth_replay_tear");
    model.component("comp1").variable(vars).set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)");
    model.component("comp1").variable(vars).set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");

    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\118_lid8mm_stage44_h3um_gap_temporal_continuation010_setup.mph");
    ModelUtil.disconnect();
  }
}
