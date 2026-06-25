import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage18_partitioned_local_pfilm_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\61_lid8mm_stage18_partitioned_local_pfilm_feedback_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\62_lid8mm_stage18_partitioned_local_pfilm_feedback_short_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("62_lid8mm_stage18_partitioned_local_pfilm_feedback_short_results.mph");

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
    System.out.println("RUN_STUDY=std_partitioned_local_pfilm");
    model.study("std_partitioned_local_pfilm").run();

    model.save(OUT);
    System.out.println("SAVED_STAGE18_SHORT_RESULTS=" + OUT);
  }
}
