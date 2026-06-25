import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage15_strong_coupling_init_bridge_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\54_lid8mm_stage15_strong_coupling_init_bridge_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\55_lid8mm_stage15_strong_coupling_init_bridge_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("55_lid8mm_stage15_strong_coupling_init_bridge_results.mph");
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback").active(false);
    model.component("comp1").physics("tff").feature("ffp1").set("TangentialWallVelocity", "Off");
    String[] dynamicU0 =
        model.component("comp1").physics("solid").feature("disp_lid_time").getStringArray("U0");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{"0",
            "-dr_force_sched(0)*Y/sqrt(Y^2+Z^2)",
            "-dr_force_sched(0)*Z/sqrt(Y^2+Z^2)"});
    model.study("std_tff_init_bridge").createAutoSequences("sol");
    String bridgeSol = lastSolution(model);
    model.sol(bridgeSol).runAll();
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0", dynamicU0);
    model.component("comp1").physics("tff").feature("ffp1").set("TangentialWallVelocity", "userdef");
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback").active(true);
    model.study("std_local_pressure_strong").feature("time").set("useinitsol", "on");
    model.study("std_local_pressure_strong").feature("time").set("initmethod", "sol");
    model.study("std_local_pressure_strong").feature("time").set("initstudy", "std_tff_init_bridge");
    model.study("std_local_pressure_strong").feature("time").set("initstudystep", "stat");
    model.study("std_local_pressure_strong").feature("time").set("initsol", bridgeSol);
    model.study("std_local_pressure_strong").feature("time").set("initsoluse", bridgeSol);
    model.save(OUT);
    System.out.println("BRIDGE_SOLUTION=" + bridgeSol);
    System.out.println("SAVED_STAGE15_BRIDGE_RESULT=" + OUT);
  }
}
