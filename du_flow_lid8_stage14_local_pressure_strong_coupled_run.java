import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage14_local_pressure_strong_coupled_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\55_lid8mm_stage15_strong_coupling_init_bridge_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\56_lid8mm_stage15_local_pressure_strong_coupled_short_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("56_lid8mm_stage15_local_pressure_strong_coupled_short_results.mph");
    model.study("std_local_pressure_strong").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).feature("t1").set("consistent", "off");
    model.sol(sol).feature("t1").feature().remove("se1");
    model.sol(sol).feature("t1").create("fc1", "FullyCoupled");
    model.sol(sol).feature("t1").feature("fc1").set("linsolver", "d1");
    model.sol(sol).runAll();
    model.result().dataset().create("dset_local_pressure_strong", "Solution");
    model.result().dataset("dset_local_pressure_strong").set("solution", sol);
    model.save(OUT);
    System.out.println("SAVED_STAGE14_STRONG_SHORT_RESULT=" + OUT);
  }
}
