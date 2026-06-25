import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage32_reaction_iteration2_setup {
  private static final String BASE =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\90_lid8mm_stage31_extended_pair_mapping_setup.mph";
  private static final String REF =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\92_lid8mm_stage31_extended_pair_mapping_full_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\93_lid8mm_stage32_reaction_iteration2_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", BASE);
    Model ref = ModelUtil.load("Ref", REF);
    model.label("93_lid8mm_stage32_reaction_iteration2_setup.mph");
    model.param().set("K_contact_reaction32", "0.8051323301[N/mm]", "Radial correction stiffness");
    model.param().set("reaction_gain32", "1", "Second-pass external displacement correction gain");

    ref.result().numerical().create("tmp_rf32", "IntSurface");
    ref.result().numerical("tmp_rf32").selection().named("sel_lid_outer_support");
    ref.result().numerical("tmp_rf32").set("data", "dset5");
    ref.result().numerical("tmp_rf32").set("expr",
        new String[]{"-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)"});
    double[][] rf = ref.result().numerical("tmp_rf32").getReal();
    ref.result().numerical().create("tmp_s32", "EvalGlobal");
    ref.result().numerical("tmp_s32").set("data", "dset5");
    ref.result().numerical("tmp_s32").set("expr", new String[]{"comp1.slide_fraction_structure"});
    double[][] fraction = ref.result().numerical("tmp_s32").getReal();

    ArrayList<String[]> rows = new ArrayList<String[]>();
    double previous = -1;
    for (int i = 0; i < fraction[0].length; i++) {
      double s = fraction[0][i];
      if (s > previous + 1e-12 || i == fraction[0].length - 1) {
        rows.add(new String[]{Double.toString(s), Double.toString(rf[0][i])});
        previous = s;
      }
    }
    model.func().create("rf31_sched32", "Interpolation");
    model.func("rf31_sched32").label("Stage 31 total support reaction versus sliding fraction");
    model.func("rf31_sched32").set("funcname", "W_reaction31_sched");
    model.func("rf31_sched32").set("table", rows.toArray(new String[0][0]));
    model.func("rf31_sched32").set("argunit", new String[]{"1"});
    model.func("rf31_sched32").set("fununit", new String[]{"N"});

    model.component("comp1").variable("var_partitioned_local_pfilm").set("dr_force_reaction32",
        "dr_force_sched(slide_fraction_structure)"
            + "+reaction_gain32*(F_total_target-W_reaction31_sched(slide_fraction_structure))"
            + "/K_contact_reaction32");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_reaction32*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_reaction32*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.save(OUT);
    System.out.println("RF_TABLE_ROWS=" + rows.size());
    System.out.println("SAVED_STAGE32_SETUP=" + OUT);
    ModelUtil.remove("Ref");
  }
}
