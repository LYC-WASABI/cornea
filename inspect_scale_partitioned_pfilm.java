import com.comsol.model.*;
import com.comsol.model.util.*;

public class inspect_scale_partitioned_pfilm {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\184_lid8mm_stage106_total_load_control_setup.mph");
      for (String p : new String[]{"scale_partitioned_pfilm","F_total_target","T_structure_pre","T_structure_slide","T_structure_hold","dt_structure_out"}) {
        try { System.out.println(p+"="+m.param().get(p)); } catch(Exception e) { System.out.println(p+" ERR "+e.getMessage()); }
      }
      try { System.out.println("forceReferenceArea="+java.util.Arrays.toString(m.component("comp1").physics("solid").feature("load_partitioned_pfilm").getStringArray("forceReferenceArea"))); } catch(Exception e) { System.out.println(e.getMessage()); }
      ModelUtil.disconnect();
    } catch(Exception e) { e.printStackTrace(); System.exit(1); }
  }
}
