import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_midpoint_motion {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");
      System.out.println("phi_lid_structure expr="
          + model.component("comp1").variable("var_partitioned_local_pfilm")
              .get("phi_lid_structure"));
      for (String tag : model.component("comp1").variable().tags()) {
        try {
          String expr = model.component("comp1").variable(tag)
              .get("dr_indent119");
          if (expr != null && !expr.isEmpty()) {
            System.out.println(tag + " dr_indent119=" + expr);
          }
        } catch (Exception ignored) {}
      }
      model.result().numerical().create("evm555", "EvalGlobal");
      model.result().numerical("evm555").set("data", "dset540s");
      model.result().numerical("evm555").set("expr", new String[] {
        "phi_lid_structure", "theta_geom_dyn", "dr_indent119",
        "q_force_total111"
      });
      System.out.println(Arrays.deepToString(
          model.result().numerical("evm555").getReal()));
      System.out.println("U0=" + Arrays.toString(
          model.component("comp1").physics("solid")
              .feature("disp_lid_time").getStringArray("U0")));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
