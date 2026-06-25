import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage34_reaction_iteration3_setup {
  private static final String BASE =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\96_lid8mm_stage33_calibrated_extended_pair_setup.mph";
  private static final String REF =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\98_lid8mm_stage33_calibrated_extended_pair_full_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\103_lid8mm_stage36_reaction_iteration3_gain100_ext8_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", BASE);
    Model ref = ModelUtil.load("Ref", REF);
    model.label("103_lid8mm_stage36_reaction_iteration3_gain100_ext8_setup.mph");
    model.param().set("reaction_gain34", "1", "Full third-pass cumulative displacement correction gain");
    model.component("comp1").pair("cp_lid_cornea").extTol("8.0");
    model.component("comp1").pair("cp_lid_cornea").searchTol("2e-2");

    ref.result().numerical().create("tmp_rf34", "IntSurface");
    ref.result().numerical("tmp_rf34").selection().named("sel_lid_outer_support");
    ref.result().numerical("tmp_rf34").set("data", "dset5");
    ref.result().numerical("tmp_rf34").set("expr",
        new String[]{"-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)"});
    double[][] rf = ref.result().numerical("tmp_rf34").getReal();
    ref.result().numerical().create("tmp_s34", "EvalGlobal");
    ref.result().numerical("tmp_s34").set("data", "dset5");
    ref.result().numerical("tmp_s34").set("expr", new String[]{"comp1.slide_fraction_structure"});
    double[][] fraction = ref.result().numerical("tmp_s34").getReal();

    ArrayList<String[]> rows = new ArrayList<String[]>();
    double previous = -1;
    for (int i = 0; i < fraction[0].length; i++) {
      double s = fraction[0][i];
      if (s > previous + 1e-12 || i == fraction[0].length - 1) {
        rows.add(new String[]{Double.toString(s), Double.toString(rf[0][i])});
        previous = s;
      }
    }
    model.func().create("rf33_sched34", "Interpolation");
    model.func("rf33_sched34").label("Stage 33 total support reaction versus sliding fraction");
    model.func("rf33_sched34").set("funcname", "W_reaction33_sched");
    model.func("rf33_sched34").set("table", rows.toArray(new String[0][0]));
    model.func("rf33_sched34").set("argunit", new String[]{"1"});
    model.func("rf33_sched34").set("fununit", new String[]{"N"});

    model.component("comp1").variable("var_partitioned_local_pfilm").set("dr_force_reaction34",
        "dr_force_reaction32"
            + "+reaction_gain34*(F_total_target-W_reaction33_sched(slide_fraction_structure))"
            + "/K_contact_reaction32");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_reaction34*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_reaction34*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.save(OUT);
    System.out.println("RF_TABLE_ROWS=" + rows.size());
    System.out.println("SAVED_STAGE36_SETUP=" + OUT);
    ModelUtil.remove("Ref");
  }
}
