import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage115_probe_motion_and_studies {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "191_lid8mm_stage113_medium_restore_to_0020_results_Model.mph");
      System.out.println("STUDIES=" + Arrays.toString(m.study().tags()));
      System.out.println("SOLUTIONS=" + Arrays.toString(m.sol().tags()));
      for (String vtag : m.component("comp1").variable().tags()) {
        for (String name : new String[]{"phi_lid_structure", "slide_fraction", "theta_lid_physical",
            "W_film_replay53", "pfilm_replay53", "dr_force_total113"}) {
          try {
            String val = m.component("comp1").variable(vtag).get(name);
            if (val != null && val.length() > 0) System.out.println(vtag + "." + name + "=" + val);
          } catch (Exception ignore) {}
        }
      }
      for (String p : new String[]{"T_structure_slide", "T_pre", "T_slide", "T_hold", "dt_out",
          "F_total_target", "scale_partitioned_pfilm"}) {
        try { System.out.println("PARAM " + p + "=" + m.param().get(p)); } catch (Exception ignore) {}
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
