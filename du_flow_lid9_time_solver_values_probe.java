import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid9_time_solver_values_probe {
  private static void get(SolverFeature f, String p) {
    try { System.out.println(p + "=" + f.getString(p)); }
    catch (Exception e) { System.out.println(p + " ERROR=" + e.getMessage()); }
  }
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\09_lid9mm_quasistatic_dynamic_sliding_setup.mph");
    model.study("std_dynamic_slide").createAutoSequences("sol");
    String[] sols = model.sol().tags();
    SolverFeature t1 = model.sol(sols[sols.length - 1]).feature("t1");
    SolverFeature fc1 = t1.feature("fc1");
    for (String p : new String[]{"tstepsbdf", "initialstepbdf", "initialstepbdfactive",
        "maxstepconstraintbdf", "maxstepbdf", "maxstepexpressionbdf", "timemethod",
        "timestepbdf", "rtol"}) get(t1, p);
    for (String p : new String[]{"maxiter", "dtech", "initstep", "minstep", "mindamp",
        "maxdamp", "jtech", "ntermauto", "niter"}) get(fc1, p);
  }
}
