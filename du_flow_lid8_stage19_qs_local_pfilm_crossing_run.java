import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage19_qs_local_pfilm_crossing_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\69_lid8mm_stage20_pure_inner_contact_qs_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\70_lid8mm_stage20_pure_inner_contact_qs_crossing_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("70_lid8mm_stage20_pure_inner_contact_qs_crossing_results.mph");
    model.study("std_qs_local_pfilm19").feature("param")
        .set("plistarr", new String[]{"range(0,0.002,0.15)"});
    model.study("std_qs_local_pfilm19").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).feature("s1").feature("fc1").set("maxiter", 100);
    System.out.println("RUN_STAGE19_QS_FINE_CROSSING=range(0,0.002,0.15)");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("SAVED_STAGE19_QS_CROSSING_RESULTS=" + OUT);
  }
}
