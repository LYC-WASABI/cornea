import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage45_h3um_gap_quasisteady_run {
  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\120_lid8mm_stage45_h3um_gap_quasisteady_setup.mph");
    model.label("121_lid8mm_stage45_h3um_gap_quasisteady_results.mph");
    model.study("std_tff_gap_qs45").createAutoSequences("sol");
    String solver = lastSolution(model);
    System.out.println("RUN_STAGE45_QS_GAP_SOLVER=" + solver);
    model.sol(solver).runAll();
    model.result().dataset().create("dset_tff_gap_qs45", "Solution");
    model.result().dataset("dset_tff_gap_qs45").label(
        "Stage 45 quasi-steady h0=3 um dynamic-gap tear-film replay");
    model.result().dataset("dset_tff_gap_qs45").set("solution", solver);
    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\121_lid8mm_stage45_h3um_gap_quasisteady_results.mph");
    System.out.println("STAGE45_SOLVER=" + solver);
    System.out.println("SAVED_STAGE45_RESULTS=121_lid8mm_stage45_h3um_gap_quasisteady_results.mph");
  }
}
