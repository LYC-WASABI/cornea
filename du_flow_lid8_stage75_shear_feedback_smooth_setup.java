import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage75_shear_feedback_smooth_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\155_lid8mm_stage73_shear_feedback_setup.mph");
    m.label("157_lid8mm_stage75_shear_feedback_smooth_setup.mph");

    m.param().set("W_eps_shear73", "1e-5[N]",
        "Regularization load for pfilm-weighted tangential traction after smoothing");

    String v = "var_partitioned_local_pfilm";
    String win =
        "if(t<T_structure_pre,0,"
        + "if(t<T_structure_pre+T_speed_ramp,"
        + "0.5*(1-cos(pi*(t-T_structure_pre)/T_speed_ramp)),"
        + "if(t<T_structure_pre+T_structure_slide-T_speed_ramp,1,"
        + "if(t<T_structure_pre+T_structure_slide,"
        + "0.5*(1-cos(pi*(T_structure_pre+T_structure_slide-t)/T_speed_ramp)),0))))";
    m.component("comp1").variable(v).set("shear_speed_window73", win);
    m.component("comp1").variable(v).set("F_shear_feedback73",
        "shear_speed_window73*F_shear_sched73(t)");
    m.component("comp1").variable(v).set("tau_nominal_shear73",
        "scale_shear_feedback73*F_shear_feedback73/A_contact_nominal73");
    m.component("comp1").variable(v).set("tau_pfilm_shear73",
        "scale_shear_feedback73*F_shear_feedback73*pfilm_weight_shear73");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\157_lid8mm_stage75_shear_feedback_smooth_setup.mph");
    System.out.println("SAVED_STAGE75_SETUP=157_lid8mm_stage75_shear_feedback_smooth_setup.mph");
    System.out.println("SHEAR_SPEED_WINDOW73=" + win);
  }
}
