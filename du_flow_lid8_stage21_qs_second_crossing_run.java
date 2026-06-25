import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage21_qs_second_crossing_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\69_lid8mm_stage20_pure_inner_contact_qs_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\72_lid8mm_stage21_qs_second_crossing_verified_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("72_lid8mm_stage21_qs_second_crossing_verified_results.mph");
    model.study("std_qs_local_pfilm19").feature("param").set("plistarr",
        new String[]{"range(0,0.002,0.58) range(0.5805,0.0005,0.62)"});
    model.study("std_qs_local_pfilm19").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).feature("s1").feature("fc1").set("maxiter", 200);
    System.out.println("RUN_STAGE21_QS_SECOND_CROSSING");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("SAVED_STAGE21_SECOND_CROSSING_RESULTS=" + OUT);
  }
}
