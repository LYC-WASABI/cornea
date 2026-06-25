import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage90_lid_roughness_0p5um_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\167_lid8mm_stage89_light_lambda_film_shear_results.mph");
    m.label("168_lid8mm_stage90_lid_roughness_0p5um_setup.mph");
    m.param().set("Rq_lid", "0.5[um]", "Effective lid-wiper roughness for local lambda sensitivity");
    m.param().set("Rq_eq", "sqrt(Rq_cornea^2+Rq_lid^2)", "Updated equivalent RMS roughness");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\168_lid8mm_stage90_lid_roughness_0p5um_setup.mph");
    System.out.println("SAVED_STAGE90_SETUP=168_lid8mm_stage90_lid_roughness_0p5um_setup.mph");
    System.out.println("Rq_lid=" + m.param().get("Rq_lid"));
    System.out.println("Rq_eq=" + m.param().get("Rq_eq"));
  }
}
