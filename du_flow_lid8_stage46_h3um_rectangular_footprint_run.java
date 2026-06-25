import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage46_h3um_rectangular_footprint_run {
  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    model.label("123_lid8mm_stage46_h3um_gap_rectangular_footprint_results.mph");
    model.study("std_tff_gap_qs45").createAutoSequences("sol");
    String solver = lastSolution(model);
    System.out.println("RUN_STAGE46_RECTANGULAR_FOOTPRINT_SOLVER=" + solver);
    model.sol(solver).runAll();
    model.result().dataset().create("dset_tff_gap_qs46", "Solution");
    model.result().dataset("dset_tff_gap_qs46").label(
        "Stage 46 quasi-steady h0=3 um dynamic-gap tear-film replay with rectangular footprint");
    model.result().dataset("dset_tff_gap_qs46").set("solution", solver);
    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\123_lid8mm_stage46_h3um_gap_rectangular_footprint_results.mph");
    System.out.println("STAGE46_SOLVER=" + solver);
    System.out.println("SAVED_STAGE46_RESULTS=123_lid8mm_stage46_h3um_gap_rectangular_footprint_results.mph");
  }
}
