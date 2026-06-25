import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage16_save_strong_coupled_solver_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\55_lid8mm_stage15_strong_coupling_init_bridge_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\57_lid8mm_stage16_local_pressure_strong_coupled_fullycoupled_setup.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("57_lid8mm_stage16_local_pressure_strong_coupled_fullycoupled_setup.mph");
    model.study("std_local_pressure_strong").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).feature("t1").set("consistent", "off");
    model.sol(sol).feature("t1").feature().remove("se1");
    model.sol(sol).feature("t1").create("fc1", "FullyCoupled");
    model.sol(sol).feature("t1").feature("fc1").set("linsolver", "d1");
    model.save(OUT);
    System.out.println("STRONG_SOLVER_SEQUENCE=" + sol);
    System.out.println("SAVED_STAGE16_STRONG_SETUP=" + OUT);
  }
}
