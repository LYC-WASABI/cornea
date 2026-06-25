import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage15_solver_properties_probe {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\55_lid8mm_stage15_strong_coupling_init_bridge_results.mph");
    model.study("std_local_pressure_strong").createAutoSequences("sol");
    String[] sols = model.sol().tags();
    String sol = sols[sols.length - 1];
    System.out.println("SOLUTION=" + sol);
    for (String p : model.sol(sol).feature("t1").feature("se1").properties()) {
      try { System.out.println(p + "=" + model.sol(sol).feature("t1").feature("se1").getString(p)); }
      catch (Exception ignored) {}
    }
  }
}
