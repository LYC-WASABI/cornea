import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_auglag_solver_tree_probe {
  private static void print(SolverFeature feature, String indent) {
    System.out.println(indent + feature.tag() + " TYPE=" + feature.getType());
    for (String child : feature.feature().tags()) {
      print(feature.feature(child), indent + "  ");
    }
  }
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\10_lid9mm_quasistatic_dynamic_sliding_auglag_setup.mph");
    model.study("std_dynamic_slide").createAutoSequences("sol");
    String[] sols = model.sol().tags();
    String sol = sols[sols.length - 1];
    System.out.println("SOLUTION=" + sol);
    for (String tag : model.sol(sol).feature().tags()) print(model.sol(sol).feature(tag), "");
  }
}
