import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage45_h3um_gap_quasisteady_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\112_lid8mm_stage41_h3um_dynamic_gap_film_setup.mph");
    model.label("120_lid8mm_stage45_h3um_gap_quasisteady_setup.mph");

    model.param().set("t_replay", "0[s]",
        "Quasi-steady tear-film replay time from the validated dynamic structural solution");
    model.param().set("gap_cap_tear", "5[um]",
        "Finite local cap that removes unmapped pair-gap infinity outside the lid neighborhood");
    model.param().set("gap_smooth_tear", "0.20[um]",
        "Positive-gap regularization length");

    String vars = "var_mixed_lub";
    model.component("comp1").variable(vars).set("slide_fraction_film_replay",
        "if(t_replay<T_pre,0,if(t_replay<T_pre+T_slide,0.5-0.5*cos(pi*(t_replay-T_pre)/T_slide),1))");
    model.component("comp1").variable(vars).set("phi_lid_film_replay",
        "theta_slide_total*slide_fraction_film_replay");
    model.component("comp1").variable(vars).set("theta_geom_dyn",
        "-theta_lid+phi_lid_film_replay");
    model.component("comp1").variable(vars).set("lid_width_coord",
        "Y*cos(theta_geom_dyn)+Z*sin(theta_geom_dyn)");
    model.component("comp1").variable(vars).set("omega_lid",
        "theta_slide_total*if(t_replay<T_pre,0,if(t_replay<T_pre+T_slide,0.5*pi/T_slide*sin(pi*(t_replay-T_pre)/T_slide),0))");
    model.component("comp1").variable(vars).set("gap_raw_replay_tear",
        "min(max(withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,t_replay)),0),gap_cap_tear)");
    model.component("comp1").variable(vars).set("gap_smooth_replay_tear",
        "0.5*(gap_raw_replay_tear+sqrt(gap_raw_replay_tear^2+gap_smooth_tear^2))-0.5*gap_smooth_tear");
    model.component("comp1").variable(vars).set("gap_replay_tear",
        "lid_mask*gap_smooth_replay_tear");
    model.component("comp1").variable(vars).set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)");
    model.component("comp1").variable(vars).set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");

    model.component("comp1").physics("tff").feature("ffp1").set("hw1", "h_film_input");
    model.component("comp1").physics("tff").feature("bdr1").set("BorderCondition", "ZeroPressure");
    model.component("comp1").physics("tff").feature("init1").set("pfilm", "0[Pa]");

    try { model.study().remove("std_tff_gap_qs45"); } catch (Exception ignored) {}
    model.study().create("std_tff_gap_qs45");
    model.study("std_tff_gap_qs45").label("Stage 45 quasi-steady tear-film replay with h0=3 um and dynamic pair gap");
    model.study("std_tff_gap_qs45").create("param", "Parametric");
    model.study("std_tff_gap_qs45").feature("param").set("pname", new String[]{"t_replay"});
    model.study("std_tff_gap_qs45").feature("param").set("plistarr",
        new String[]{"range(0,0.01,0.53)"});
    model.study("std_tff_gap_qs45").feature("param").set("punit", new String[]{"s"});
    model.study("std_tff_gap_qs45").create("stat", "Stationary");
    model.study("std_tff_gap_qs45").feature("stat").set("activate",
        new String[]{"solid", "off", "tff", "on"});

    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\120_lid8mm_stage45_h3um_gap_quasisteady_setup.mph");
    ModelUtil.disconnect();
  }
}
