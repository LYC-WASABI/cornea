import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_solver_param_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\16_quasistatic_stationary_scan_solver_tuned.mph");
    try {
      model.sol("sol1").feature("s1").feature().create("p1", "Parametric");
      System.out.println("created solver parametric");
      for (String p : model.sol("sol1").feature("s1").feature("p1").properties()) {
        System.out.println("  prop " + p);
      }
    } catch (Exception e) {
      System.out.println("failed: " + e.getMessage());
    }
  }
}
