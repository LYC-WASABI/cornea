import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage29_tabulated_reaction_calibrated_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\73_lid8mm_stage22_disconnect_reconnect_transient_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\85_lid8mm_stage29_tabulated_reaction_calibrated_setup.mph";

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("85_lid8mm_stage29_tabulated_reaction_calibrated_setup.mph");
    model.param().set("K_contact_reaction", "0.8051323301[N/mm]",
        "First-pass radial support-reaction stiffness");
    model.param().set("reaction_correction_gain", "0.5",
        "Conservative first external correction gain");

    try { model.result().numerical().remove("tmp_rf_dry29"); } catch (Exception ignored) {}
    model.result().numerical().create("tmp_rf_dry29", "IntSurface");
    model.result().numerical("tmp_rf_dry29").selection().named("sel_lid_outer_support");
    model.result().numerical("tmp_rf_dry29").set("data", "dset_dynamic_slide");
    model.result().numerical("tmp_rf_dry29").set("expr",
        new String[]{"-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)"});
    double[][] rf = model.result().numerical("tmp_rf_dry29").getReal();

    try { model.result().numerical().remove("tmp_s_dry29"); } catch (Exception ignored) {}
    model.result().numerical().create("tmp_s_dry29", "EvalGlobal");
    model.result().numerical("tmp_s_dry29").set("data", "dset_dynamic_slide");
    model.result().numerical("tmp_s_dry29").set("expr",
        new String[]{"if(t<0.01[s],0,if(t<0.51[s],0.5-0.5*cos(pi*(t-0.01[s])/0.50[s]),1))"});
    double[][] fraction = model.result().numerical("tmp_s_dry29").getReal();

    ArrayList<String[]> rows = new ArrayList<String[]>();
    double previous = -1;
    for (int i = 0; i < fraction[0].length; i++) {
      double s = fraction[0][i];
      if (s > previous + 1e-12 || i == fraction[0].length - 1) {
        rows.add(new String[]{Double.toString(s), Double.toString(rf[0][i])});
        previous = s;
      }
    }
    try { model.func().remove("rf_dry_sched29"); } catch (Exception ignored) {}
    model.func().create("rf_dry_sched29", "Interpolation");
    model.func("rf_dry_sched29").label("Validated dry support reaction versus sliding fraction");
    model.func("rf_dry_sched29").set("funcname", "W_reaction_dry_sched");
    model.func("rf_dry_sched29").set("table", rows.toArray(new String[0][0]));
    model.func("rf_dry_sched29").set("argunit", new String[]{"1"});
    model.func("rf_dry_sched29").set("fununit", new String[]{"N"});

    if (!hasCpl(model, "intop_lid_support")) {
      model.component("comp1").cpl().create("intop_lid_support", "Integration");
    }
    model.component("comp1").cpl("intop_lid_support").selection().named("sel_lid_outer_support");
    model.component("comp1").cpl("intop_lid_support").label("Lid outer-support reaction integration");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_reaction_dry_replay29",
        "W_reaction_dry_sched(slide_fraction_structure)");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("dr_force_reaction29",
        "dr_force_sched(slide_fraction_structure)"
            + "+reaction_correction_gain*(F_total_target-W_film_replay-W_reaction_dry_replay29)"
            + "/K_contact_reaction");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_reaction_current29",
        "intop_lid_support(-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2))");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_reaction29*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_reaction29*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("RF_TABLE_ROWS=" + rows.size());
    System.out.println("SAVED_STAGE29_SETUP=" + OUT);
  }
}
