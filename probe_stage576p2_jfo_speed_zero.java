import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage576p2_jfo_speed_zero {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "build_stage576p2_moving_structure_sparse_jfo_output_Model.mph");
      model.param().set("gate_speed576p", "0");
      System.out.println("RUN_ZERO_SPEED=sol150");
      model.sol("sol150").runAll();
      model.save("probe_stage576p2_jfo_speed_zero_checked.mph");
      System.out.println("ZERO_SPEED_STATUS=PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
