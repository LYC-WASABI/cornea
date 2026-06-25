import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_solution_count_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_explicit_pair_study_parametric_scan_results.mph");
    for (String sol : model.sol().tags()) {
      System.out.println("SOL " + sol + " : " + model.sol(sol).label());
      for (String f : model.sol(sol).feature().tags()) {
        System.out.println("  feature " + f + " : " + model.sol(sol).feature(f).label());
      }
    }
    for (String ds : model.result().dataset().tags()) {
      System.out.println("DATASET " + ds + " : " + model.result().dataset(ds).label());
      for (String p : model.result().dataset(ds).properties()) {
        if (p.equals("solnum") || p.equals("outersolnum") || p.equals("solution")) {
          try { System.out.println("  " + p + "=" + model.result().dataset(ds).getString(p)); } catch (Exception ignore) {}
          try { System.out.println("  " + p + "=" + Arrays.toString(model.result().dataset(ds).getStringArray(p))); } catch (Exception ignore) {}
        }
      }
    }
  }
}
