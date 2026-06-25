import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_fillet_api_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_rotating_lid_wiper_position_scan_results.mph");
    try { model.component("comp1").geom("geom1").feature().remove("test_fillet"); } catch (Exception ignore) {}
    model.component("comp1").geom("geom1").create("test_fillet", "Fillet");
    System.out.println("FILLET PROPS");
    for (String p : model.component("comp1").geom("geom1").feature("test_fillet").properties()) {
      System.out.println("  " + p);
    }
    System.out.println("FILLET selection input exists? " + model.component("comp1").geom("geom1").feature("test_fillet").selection("input"));
  }
}
