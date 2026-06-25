import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_force_curve_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration4_gain030_results.mph");
    double[][] force = model.result().numerical("int_dyn_contact_force").getReal();
    double[][] theta = model.result().numerical("eval_dyn_theta").getReal();
    System.out.println("CURVE_BEGIN");
    for (int i = 0; i < force[0].length; i++) {
      System.out.printf(java.util.Locale.US, "CURVE %.12g %.12g%n", theta[0][i], force[0][i]);
    }
    System.out.println("CURVE_END");
  }
}
