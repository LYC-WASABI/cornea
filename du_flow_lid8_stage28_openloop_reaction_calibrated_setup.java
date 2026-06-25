import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage28_openloop_reaction_calibrated_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\73_lid8mm_stage22_disconnect_reconnect_transient_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\83_lid8mm_stage28_openloop_reaction_calibrated_setup.mph";

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("83_lid8mm_stage28_openloop_reaction_calibrated_setup.mph");
    model.param().set("K_contact_reaction", "0.8051323301[N/mm]",
        "First-pass radial support-reaction stiffness");
    model.param().set("T_pre_dry_replay", "0.01[s]", "Validated dry structural replay hold");
    model.param().set("T_slide_dry_replay", "0.50[s]", "Validated dry structural replay slide duration");
    model.param().set("reaction_correction_gain", "1",
        "First external correction gain for total supported load");
    if (!hasCpl(model, "intop_lid_support")) {
      model.component("comp1").cpl().create("intop_lid_support", "Integration");
    }
    model.component("comp1").cpl("intop_lid_support").selection().named("sel_lid_outer_support");
    model.component("comp1").cpl("intop_lid_support").label("Lid outer-support reaction integration");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("t_dry_replay28",
        "T_pre_dry_replay+(T_slide_dry_replay/pi)"
            + "*acos(max(-1,min(1,1-2*slide_fraction_structure)))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_reaction_dry_replay28",
        "withsol('sol18',comp1.intop_lid_support(-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)),"
            + "setval(t,t_dry_replay28))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("dr_force_reaction28",
        "dr_force_sched(slide_fraction_structure)"
            + "+reaction_correction_gain*(F_total_target-W_film_replay-W_reaction_dry_replay28)"
            + "/K_contact_reaction");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_reaction_current28",
        "intop_lid_support(-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2))");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_reaction28*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_reaction28*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("SOURCE="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("DESTINATION="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
    System.out.println("SAVED_STAGE28_SETUP=" + OUT);
  }
}
