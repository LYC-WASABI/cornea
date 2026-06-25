import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage15_strong_coupling_init_bridge_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\52_lid8mm_stage14_local_pressure_strong_coupled_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\54_lid8mm_stage15_strong_coupling_init_bridge_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("54_lid8mm_stage15_strong_coupling_init_bridge_setup.mph");
    try { model.study().remove("std_tff_init_bridge"); } catch (Exception ignored) {}
    model.study().create("std_tff_init_bridge");
    model.study("std_tff_init_bridge").label("Stage 15 bridge: stationary tear-film initial field");
    model.study("std_tff_init_bridge").create("stat", "Stationary");
    model.study("std_tff_init_bridge").feature("stat").set("activate",
        new String[]{"solid", "on", "tff", "on"});
    model.save(OUT);
    System.out.println("SAVED_STAGE15_BRIDGE_SETUP=" + OUT);
  }
}
