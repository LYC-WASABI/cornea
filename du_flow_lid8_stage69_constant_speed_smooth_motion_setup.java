import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage69_constant_speed_smooth_motion_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\150_lid8mm_stage68_h3um_corrected_fullslide_calibrated_mixed_shear_results.mph");
    m.label("151_lid8mm_stage69_constant_speed_smooth_motion_setup.mph");
    m.param().set("T_speed_ramp", "0.05[s]",
        "Half-cosine acceleration and deceleration duration for nearly constant-speed scratching");
    m.param().set("omega_slide_const",
        "theta_slide_total/(T_structure_slide-T_speed_ramp)",
        "Signed constant angular speed in the central scratching interval");

    String sf =
        "if(t<T_structure_pre,0,"
        + "if(t<T_structure_pre+T_speed_ramp,"
        + "(0.5*(t-T_structure_pre)-T_speed_ramp/(2*pi)*sin(pi*(t-T_structure_pre)/T_speed_ramp))/(T_structure_slide-T_speed_ramp),"
        + "if(t<T_structure_pre+T_structure_slide-T_speed_ramp,"
        + "((t-T_structure_pre)-0.5*T_speed_ramp)/(T_structure_slide-T_speed_ramp),"
        + "if(t<T_structure_pre+T_structure_slide,"
        + "1-(0.5*(T_structure_pre+T_structure_slide-t)-T_speed_ramp/(2*pi)*sin(pi*(T_structure_pre+T_structure_slide-t)/T_speed_ramp))/(T_structure_slide-T_speed_ramp),1))))";
    String omega =
        "omega_slide_const*if(t_replay<T_structure_pre,0,"
        + "if(t_replay<T_structure_pre+T_speed_ramp,"
        + "0.5*(1-cos(pi*(t_replay-T_structure_pre)/T_speed_ramp)),"
        + "if(t_replay<T_structure_pre+T_structure_slide-T_speed_ramp,1,"
        + "if(t_replay<T_structure_pre+T_structure_slide,"
        + "0.5*(1-cos(pi*(T_structure_pre+T_structure_slide-t_replay)/T_speed_ramp)),0))))";
    String sfReplay =
        "if(t_replay<T_structure_pre,0,"
        + "if(t_replay<T_structure_pre+T_speed_ramp,"
        + "(0.5*(t_replay-T_structure_pre)-T_speed_ramp/(2*pi)*sin(pi*(t_replay-T_structure_pre)/T_speed_ramp))/(T_structure_slide-T_speed_ramp),"
        + "if(t_replay<T_structure_pre+T_structure_slide-T_speed_ramp,"
        + "((t_replay-T_structure_pre)-0.5*T_speed_ramp)/(T_structure_slide-T_speed_ramp),"
        + "if(t_replay<T_structure_pre+T_structure_slide,"
        + "1-(0.5*(T_structure_pre+T_structure_slide-t_replay)-T_speed_ramp/(2*pi)*sin(pi*(T_structure_pre+T_structure_slide-t_replay)/T_speed_ramp))/(T_structure_slide-T_speed_ramp),1))))";

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("slide_fraction_structure", sf);
    m.component("comp1").variable(pv).set("phi_lid_structure",
        "theta_slide_total*slide_fraction_structure");
    m.component("comp1").variable(pv).set("t_film_replay",
        "min(T_structure_pre+T_structure_slide+T_structure_hold,max(0[s],t))");

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("slide_fraction_film_replay", sfReplay);
    m.component("comp1").variable(mv).set("phi_lid_film_replay",
        "theta_slide_total*slide_fraction_film_replay");
    m.component("comp1").variable(mv).set("omega_lid", omega);

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\151_lid8mm_stage69_constant_speed_smooth_motion_setup.mph");
    System.out.println("SAVED_STAGE69_SETUP=151_lid8mm_stage69_constant_speed_smooth_motion_setup.mph");
    System.out.println("SLIDE_FRACTION_STRUCTURE=" + sf);
    System.out.println("OMEGA_LID=" + omega);
  }
}
