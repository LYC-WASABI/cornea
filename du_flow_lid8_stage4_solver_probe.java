import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage4_solver_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\36_lid8mm_mixed_lubrication_stage4_bidirectional_setup.mph";

  private static void walk(SolverFeature node, String indent) {
    for (String tag : node.feature().tags()) {
      SolverFeature child = node.feature(tag);
      System.out.printf("%s%s type=%s label=%s%n", indent, tag, child.getType(), child.label());
      walk(child, indent + "  ");
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.study("std_mixed_coupled").feature("time").set("tlist", "range(0,0.001,0.02)");
    model.study("std_mixed_coupled").createAutoSequences("sol");
    String[] tags = model.sol().tags();
    String sol = tags[tags.length - 1];
    System.out.println("SOLUTION=" + sol);
    for (String tag : model.sol(sol).feature().tags()) {
      SolverFeature child = model.sol(sol).feature(tag);
      System.out.printf("%s type=%s label=%s%n", tag, child.getType(), child.label());
      walk(child, "  ");
    }
  }
}
