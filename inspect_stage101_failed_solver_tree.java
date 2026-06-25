import com.comsol.model.*;
import com.comsol.model.util.*;

public class inspect_stage101_failed_solver_tree {
  private static void walk(SolverFeature f, String pad) {
    try { System.out.println(pad + f.tag() + " type=" + f.getType() + " label=" + f.label()); } catch (Exception e) { return; }
    for (String p : f.properties()) {
      try {
        String s = f.getString(p);
        if (p.toLowerCase().contains("seg") || p.toLowerCase().contains("solve") || p.toLowerCase().contains("lin") || p.toLowerCase().contains("group")) {
          System.out.println(pad + "  " + p + "=" + s);
        }
      } catch (Exception ignore) {}
    }
    for (String ch : f.feature().tags()) {
      walk(f.feature(ch), pad + "  ");
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "stage101_force_control_total_load_run_output_Model.mph");
      for (String sol : m.sol().tags()) {
        System.out.println("SOL " + sol + " label=" + m.sol(sol).label());
      }
      String sol = "sol22";
      System.out.println("TREE " + sol);
      for (String f : m.sol(sol).feature().tags()) {
        walk(m.sol(sol).feature(f), "");
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
