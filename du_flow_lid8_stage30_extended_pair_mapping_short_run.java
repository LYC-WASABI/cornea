import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage30_extended_pair_mapping_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\109_lid8mm_stage39_reaction_iteration6_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\110_lid8mm_stage39_reaction_iteration6_full_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("110_lid8mm_stage39_reaction_iteration6_full_results.mph");
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
    model.study("std_partitioned_local_pfilm").feature("time").set("tlist",
        "range(0,dt_structure_out,T_structure_pre+T_structure_slide+T_structure_hold)");
    System.out.println("RUN_STAGE39_ITERATION6_FULL");
    model.study("std_partitioned_local_pfilm").run();
    model.save(OUT);
    System.out.println("SAVED_STAGE39_FULL=" + OUT);
  }
}
