import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage41_h3um_dynamic_gap_film_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\118_lid8mm_stage44_h3um_gap_temporal_continuation010_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\119_lid8mm_stage44_h3um_gap_temporal_continuation010_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("119_lid8mm_stage44_h3um_gap_temporal_continuation010_results.mph");
    model.study("std_tff_dynamic_gap41").createAutoSequences("sol");
    String solver = lastSolution(model);
    System.out.println("RUN_STAGE44_GAP_TEMPORAL_CONTINUATION010_SOLVER=" + solver);
    model.sol(solver).runAll();
    model.result().dataset().create("dset_tff_dynamic_gap44", "Solution");
    model.result().dataset("dset_tff_dynamic_gap44").label("Stage 44 h0=3 um temporally smoothed local dynamic-gap thin-film continuation 0.10");
    model.result().dataset("dset_tff_dynamic_gap44").set("solution", solver);
    model.save(OUT);
    System.out.println("STAGE44_SOLVER=" + solver);
    System.out.println("SAVED_STAGE44_RESULTS=" + OUT);
  }
}
