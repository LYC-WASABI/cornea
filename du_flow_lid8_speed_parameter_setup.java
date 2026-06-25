import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_speed_parameter_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\30_lid8mm_force_calibrated_iteration7_local_gain050_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\31_lid8mm_force_calibrated_speed_0p5mms_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("31_lid8mm_force_calibrated_speed_0p5mms_setup.mph");
    model.param().set("v_lid", "0.5[mm/s]",
        "Lid wiper scratching speed; paper does not specify a continuous speed");
    model.param().set("theta_start", "-35[deg]", "Initial lid position");
    model.param().set("theta_end", "35[deg]", "Final lid position");
    model.param().set("theta_span", "theta_end-theta_start", "Total scratching angle");
    model.param().set("s_slide", "R_cor*abs(theta_span)", "Arc length traveled along corneal surface");
    model.param().set("T_slide", "s_slide/v_lid", "Scratching duration derived from 0.5 mm/s");
    model.param().set("T_pre", "0.2[s]", "Initial stationary hold");
    model.param().set("T_hold", "0.2[s]", "Final stationary hold");
    model.param().set("dt_out", "T_slide/70", "Output interval: approximately one degree per output");
    model.component("comp1").variable("var_dynamic_lid_motion").set("slide_fraction",
        "if(t<T_pre,0,if(t<T_pre+T_slide,(t-T_pre)/T_slide,1))");
    model.component("comp1").variable("var_dynamic_lid_motion").set("phi_lid_dyn",
        "-theta_span*slide_fraction");
    model.component("comp1").variable("var_dynamic_lid_motion").set("theta_lid_physical",
        "theta_start+theta_span*slide_fraction");
    model.component("comp1").variable("var_dynamic_lid_motion").set("v_lid_actual",
        "if(t<T_pre,0,if(t<T_pre+T_slide,v_lid,0))");
    model.study("std_dynamic_slide").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.save(OUT);
    System.out.println("v_lid=" + model.param().get("v_lid"));
    System.out.println("s_slide=" + model.param().evaluate("s_slide") + " mm");
    System.out.println("T_slide=" + model.param().evaluate("T_slide") + " s");
    System.out.println("SAVED_SETUP=" + OUT);
  }
}
