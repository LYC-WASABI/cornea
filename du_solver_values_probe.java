import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_solver_values_probe {
  private static void get(com.comsol.model.SolverFeature f, String p) {
    try { System.out.println(p + "=" + f.getString(p)); }
    catch (Exception e) { System.out.println(p + " err=" + e.getMessage()); }
  }
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_explicit_pair_quasistatic_scan_batch_output_Model.mph");
    com.comsol.model.SolverFeature fc = model.sol("sol1").feature("s1").feature("fc1");
    get(fc, "maxiter");
    get(fc, "dtech");
    get(fc, "initstep");
    get(fc, "minstep");
    get(fc, "maxdamp");
    get(fc, "mindamp");
    get(fc, "linsolver");
    get(model.sol("sol1").feature("s1"), "stol");
    get(model.sol("sol1").feature("s1"), "nonlin");
  }
}
