import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage24_force_controlled_reconnect_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\78_lid8mm_stage24_force_controlled_reconnect_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\79_lid8mm_stage24_force_controlled_reconnect_short_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("79_lid8mm_stage24_force_controlled_reconnect_short_results.mph");
    try {
      model.component("comp1").physics("solid").feature("dcnt1").feature().remove("fric_partitioned_stabilizer");
    } catch (Exception ignored) {}
    model.component("comp1").physics("solid").feature("dcnt1").feature()
        .create("fric_partitioned_stabilizer", "Friction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .feature("fric_partitioned_stabilizer")
        .label("Numerical tangential stabilizer only - excluded from reported friction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .feature("fric_partitioned_stabilizer").set("mu_fric", "0.1");
    model.study("std_partitioned_local_pfilm").feature("time").set("tlist", "range(0,0.002,0.08)");
    model.study("std_partitioned_local_pfilm").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).feature("t1").set("consistent", "off");
    try { model.sol(solver).feature("t1").feature().remove("se1"); } catch (Exception ignored) {}
    try { model.sol(solver).feature("t1").feature().remove("fc1"); } catch (Exception ignored) {}
    model.sol(solver).feature("t1").create("fc1", "FullyCoupled");
    model.sol(solver).feature("t1").feature("fc1").set("linsolver", "d1");
    System.out.println("RUN_STAGE24_FORCE_CONTROL_SHORT");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("SAVED_STAGE24_FORCE_CONTROL_SHORT=" + OUT);
  }
}
