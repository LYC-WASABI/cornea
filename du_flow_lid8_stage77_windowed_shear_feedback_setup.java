import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage77_windowed_shear_feedback_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\157_lid8mm_stage75_shear_feedback_smooth_setup.mph");
    m.label("159_lid8mm_stage77_windowed_shear_feedback_setup.mph");

    m.param().set("pfilm_window_ref73", "500[Pa]",
        "Pressure footprint reference for bounded tangential shear window");

    String v = "var_partitioned_local_pfilm";
    m.component("comp1").variable(v).set("pfilm_window_shear73",
        "max(pfilm_replay53,0)/(max(pfilm_replay53,0)+pfilm_window_ref73)");
    m.component("comp1").variable(v).set("tau_pfilm_shear73",
        "tau_nominal_shear73*pfilm_window_shear73");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\159_lid8mm_stage77_windowed_shear_feedback_setup.mph");
    System.out.println("SAVED_STAGE77_SETUP=159_lid8mm_stage77_windowed_shear_feedback_setup.mph");
  }
}
