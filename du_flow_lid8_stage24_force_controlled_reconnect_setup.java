import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage24_force_controlled_reconnect_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\73_lid8mm_stage22_disconnect_reconnect_transient_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\78_lid8mm_stage24_force_controlled_reconnect_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("78_lid8mm_stage24_force_controlled_reconnect_setup.mph");
    model.param().set("q_force_init", "dr_force_sched(0)/1[mm]",
        "Initial radial indentation from validated dry calibration");

    try { model.component("comp1").physics().remove("ge_force_mixed"); }
    catch (Exception ignored) {}
    model.component("comp1").physics().create("ge_force_mixed", "GlobalEquations");
    model.component("comp1").physics("ge_force_mixed")
        .label("Mixed-load closed-loop radial indentation");
    model.component("comp1").physics("ge_force_mixed").feature("ge1")
        .set("name", 1, 1, "q_force");
    model.component("comp1").physics("ge_force_mixed").feature("ge1")
        .set("equation", 1, 1,
            "(intop_contact(if(isdefined(solid.Tn),solid.Tn,0))+W_film_replay-F_total_target)/F_total_target");
    model.component("comp1").physics("ge_force_mixed").feature("ge1")
        .set("initialValueU", 1, 1, "q_force_init");
    model.component("comp1").physics("ge_force_mixed").feature("ge1")
        .set("initialValueUt", 1, 1, "0");
    model.component("comp1").physics("ge_force_mixed").feature("ge1")
        .set("description", 1, 1, "Radial indentation enforcing contact plus film support equals 0.03 N");

    model.component("comp1").variable("var_partitioned_local_pfilm").set("dr_force_closed_loop",
        "q_force*1[mm]");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_closed_loop*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_closed_loop*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.study("std_partitioned_local_pfilm").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "off", "ge_force_mixed", "on"});
    model.save(OUT);
    System.out.println("SOURCE="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("DESTINATION="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
    System.out.println("PAIR_DISCONNECT="
        + model.component("comp1").physics("solid").feature("dcnt1").getString("pairDisconnect"));
    System.out.println("SAVED_STAGE24_SETUP=" + OUT);
  }
}
