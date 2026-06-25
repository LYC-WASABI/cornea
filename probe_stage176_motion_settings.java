import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage176_motion_settings {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      StudyFeature time = model.study("std_dynamic_slide").feature("time");
      System.out.println("DYNAMIC tlist=" + time.getString("tlist"));
      System.out.println("DYNAMIC activate="
          + Arrays.toString(time.getStringArray("activate")));
      System.out.println("DYNAMIC initsol=" + time.getString("initsol"));
      System.out.println("DYNAMIC initsoluse=" + time.getString("initsoluse"));
      System.out.println("DYNAMIC solnum=" + time.getString("initsolusesolnum"));

      System.out.println("DISP U0=" + Arrays.toString(
          model.component("comp1").physics("solid").feature("disp_lid_time")
              .getStringArray("U0")));
      System.out.println("DISP StudyStep="
          + model.component("comp1").physics("solid").feature("disp_lid_time")
              .getString("StudyStep"));

      System.out.println("LOAD FperArea=" + Arrays.toString(
          model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      System.out.println("LOAD StudyStep="
          + model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
              .getString("StudyStep"));

      System.out.println("GE equation=" + Arrays.deepToString(
          model.component("comp1").physics("ge_force_total111").feature("ge1")
              .getStringMatrix("equation")));
      System.out.println("GE StudyStep="
          + model.component("comp1").physics("ge_force_total111").feature("ge1")
              .getString("StudyStep"));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
