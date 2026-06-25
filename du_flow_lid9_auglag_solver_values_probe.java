import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_auglag_solver_values_probe {
  private static void dump(SolverFeature f) {
    System.out.println("NODE=" + f.tag() + " TYPE=" + f.getType());
    for (String p : f.properties()) {
      try { System.out.println("  " + p + "=" + f.getString(p)); }
      catch (Exception ignored) {}
    }
  }
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\10_lid9mm_quasistatic_dynamic_sliding_auglag_setup.mph");
    model.study("std_dynamic_slide").createAutoSequences("sol");
    String[] sols = model.sol().tags();
    SolverFeature t1 = model.sol(sols[sols.length - 1]).feature("t1");
    dump(t1.feature("se1"));
    dump(t1.feature("se1").feature("ss1"));
  }
}
