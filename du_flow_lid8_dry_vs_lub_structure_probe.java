import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_dry_vs_lub_structure_probe {
  private static void inspect(String name, String path) throws Exception {
    Model model = ModelUtil.load(name, path);
    System.out.println("MODEL=" + name);
    for (String param : new String[]{"theta_slide_total", "T_pre", "T_slide", "T_hold", "dt_out"}) {
      try { System.out.println("  PARAM " + param + "=" + model.param().get(param)); }
      catch (Exception e) { System.out.println("  PARAM " + param + "=<missing>"); }
    }
    try {
      System.out.println("  STRUCTURAL_TRANSIENT="
          + model.component("comp1").physics("solid").prop("StructuralTransientBehavior")
              .getString("StructuralTransientBehavior"));
    } catch (Exception e) { System.out.println("  STRUCTURAL_TRANSIENT=<missing>"); }
    System.out.println("  DCNT_CHILDREN="
        + Arrays.toString(model.component("comp1").physics("solid").feature("dcnt1").feature().tags()));
    for (String p : new String[]{"pairDisconnect", "useCutback"}) {
      try { System.out.println("  DCNT " + p + "="
          + model.component("comp1").physics("solid").feature("dcnt1").getString(p)); }
      catch (Exception e) { System.out.println("  DCNT " + p + "=<missing>"); }
    }
    System.out.println("  DISP_U0=" + Arrays.toString(
        model.component("comp1").physics("solid").feature("disp_lid_time").getStringArray("U0")));
    for (String tag : model.component("comp1").variable().tags()) {
      try {
        String phi = model.component("comp1").variable(tag).get("phi_lid_dyn");
        if (phi != null && !phi.isEmpty()) System.out.println("  " + tag + ".phi_lid_dyn=" + phi);
      } catch (Exception ignored) {}
      try {
        String dr = model.component("comp1").variable(tag).get("dr_force");
        if (dr != null && !dr.isEmpty()) System.out.println("  " + tag + ".dr_force=" + dr);
      } catch (Exception ignored) {}
    }
    ModelUtil.remove(name);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    inspect("Dry",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration7_local_gain050_results.mph");
    inspect("Lub",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\43_lid8mm_mixed_lubrication_stage8_pseudotime_setup.mph");
  }
}
