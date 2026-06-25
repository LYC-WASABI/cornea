import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_prescribed_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_rotating_lid_wiper_position_scan_results.mph");
    try { model.component("comp1").physics("solid").feature().remove("test_disp"); } catch (Exception ignore) {}
    model.component("comp1").physics("solid").feature().create("test_disp", "PrescribedDisplacement", 2);
    for (String p : model.component("comp1").physics("solid").feature("test_disp").properties()) {
      System.out.println(p);
    }
  }
}
