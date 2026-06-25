import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage550_parameters {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "543_stage540_jfo_joint_static_checked.mph");
    for (String name : new String[] {
        "phi_qs142", "t_replay", "theta_lid", "delta_h_jfo197"
    }) {
      try { System.out.println(name + "=" + model.param().get(name)); }
      catch (Exception error) { System.out.println(name + "=MISSING"); }
    }
    ModelUtil.disconnect();
  }
}
