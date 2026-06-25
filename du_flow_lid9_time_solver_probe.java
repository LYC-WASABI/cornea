import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid9_time_solver_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    model.study("std_dynamic_slide").createAutoSequences("sol");
    String[] sols = model.sol().tags();
    String sol = sols[sols.length - 1];
    SolverFeature t1 = model.sol(sol).feature("t1");
    System.out.println("SOLUTION=" + sol);
    System.out.println("T1_PROPERTIES=" + Arrays.toString(t1.properties()));
    for (String p : t1.properties()) {
      try { System.out.println("T1 " + p + "=" + Arrays.toString(t1.getStringArray(p))); }
      catch (Exception ignored) {}
    }
    SolverFeature fc1 = model.sol(sol).feature("t1").feature("fc1");
    System.out.println("FC1_PROPERTIES=" + Arrays.toString(fc1.properties()));
    for (String p : fc1.properties()) {
      try { System.out.println("FC1 " + p + "=" + Arrays.toString(fc1.getStringArray(p))); }
      catch (Exception ignored) {}
    }
  }
}
