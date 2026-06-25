import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_solver_probe {
  private static void dumpFeature(com.comsol.model.SolverFeature f, String indent) {
    System.out.println(indent + f.tag() + " : " + f.label());
    for (String p : f.properties()) {
      System.out.println(indent + "  prop " + p);
    }
    for (String c : f.feature().tags()) {
      dumpFeature(f.feature(c), indent + "  ");
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "C:\\Users\\l1363\\Documents\\复现\\du_flow_dynamic_sliding_run_Model.mph");
    for (String sol : model.sol().tags()) {
      System.out.println("SOL " + sol + " : " + model.sol(sol).label());
      for (String f : model.sol(sol).feature().tags()) {
        dumpFeature(model.sol(sol).feature(f), "  ");
      }
    }
  }
}
