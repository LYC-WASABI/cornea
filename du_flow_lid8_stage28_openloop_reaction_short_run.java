import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage28_openloop_reaction_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\85_lid8mm_stage29_tabulated_reaction_calibrated_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\86_lid8mm_stage29_tabulated_reaction_calibrated_short_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("86_lid8mm_stage29_tabulated_reaction_calibrated_short_results.mph");
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
    System.out.println("RUN_STAGE29_TABULATED_REACTION_SHORT");
    model.study("std_partitioned_local_pfilm").run();
    model.save(OUT);
    System.out.println("SAVED_STAGE29_REACTION_SHORT=" + OUT);
  }
}
