import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage20_pure_inner_contact_qs_full_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\69_lid8mm_stage20_pure_inner_contact_qs_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\71_lid8mm_stage20_pure_inner_contact_qs_full_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("71_lid8mm_stage20_pure_inner_contact_qs_full_results.mph");
    model.study("std_qs_local_pfilm19").feature("param")
        .set("plistarr", new String[]{"range(0,0.002,1)"});
    model.study("std_qs_local_pfilm19").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).feature("s1").feature("fc1").set("maxiter", 100);
    System.out.println("RUN_STAGE20_QS_FULL=range(0,0.002,1)");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("SAVED_STAGE20_QS_FULL_RESULTS=" + OUT);
  }
}
