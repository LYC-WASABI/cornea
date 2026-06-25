import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_motion_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    System.out.println("theta_lid=" + model.param().get("theta_lid"));
    System.out.println("rot_lid=" + model.component("comp1").geom("geom1").feature("rot_lid").getString("rot"));
    System.out.println("theta_slide_total=" + model.param().get("theta_slide_total"));
    System.out.println("phi_lid_dyn=" + model.component("comp1").variable("var_dynamic_lid_motion").get("phi_lid_dyn"));
    System.out.println("theta_lid_physical=" + model.component("comp1").variable("var_dynamic_lid_motion").get("theta_lid_physical"));
    System.out.println("U0=" + java.util.Arrays.toString(
        model.component("comp1").physics("solid").feature("disp_lid_time").getStringArray("U0")));
  }
}
