import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_apex_scan_verify {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_apex_spherical_rect_lid_position_scan_results.mph");
    for (String sol : model.sol().tags()) {
      System.out.println("SOL " + sol + " : " + model.sol(sol).label());
      for (String f : model.sol(sol).feature().tags()) {
        System.out.println("  " + f + " : " + model.sol(sol).feature(f).label());
      }
    }
    for (String ds : model.result().dataset().tags()) {
      System.out.println("DATASET " + ds + " : " + model.result().dataset(ds).label());
    }
  }
}
