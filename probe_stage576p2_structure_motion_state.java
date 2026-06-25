import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_structure_motion_state {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "575d_stage575_dynamic_active_gap_regularized_checked.mph");
      ModelNode comp = model.component("comp1");
      for (String parameter : new String[] {"q_scale574", "q_fixed574",
          "dr_indent570", "T_pre572", "T_slide572", "v_blink_avg"}) {
        try {
          System.out.println("PARAM " + parameter + "=" + model.param().get(parameter)
              + " VALUE=" + model.param().evaluate(parameter));
        } catch (Exception error) {
          System.out.println("PARAM_ERROR " + parameter + "=" + error.getMessage());
        }
      }
      System.out.println("TAU572="
          + comp.variable("var_dynamic_motion572").get("tau572"));
      System.out.println("DISP_U0=" + Arrays.toString(
          comp.physics("solid").feature("disp_lid_time").getStringArray("U0")));
      System.out.println("PAIR_SOURCE="
          + Arrays.toString(comp.pair("cp_lid_cornea").source().entities()));
      System.out.println("PAIR_DESTINATION="
          + Arrays.toString(comp.pair("cp_lid_cornea").destination().entities()));
      System.out.println("CONTACT_PAIRS=" + Arrays.toString(
          comp.physics("solid").feature("dcnt1").getStringArray("pairs")));
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
