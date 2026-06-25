import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage91_set_lid_roughness_0p5um {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\168_lid8mm_stage90_film_break_thresholds_setup.mph");
    m.label("169_lid8mm_stage91_lid_roughness_0p5um_setup.mph");

    m.param().set("Rq_lid", "0.5[um]",
        "Effective lid wiper RMS roughness for local lambda and film rupture sensitivity");

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\169_lid8mm_stage91_lid_roughness_0p5um_setup.mph");
    System.out.println("SAVED_STAGE91=169_lid8mm_stage91_lid_roughness_0p5um_setup.mph");
    System.out.println("Rq_lid=" + m.param().get("Rq_lid"));
    System.out.println("Rq_eq=" + m.param().get("Rq_eq"));
  }
}
