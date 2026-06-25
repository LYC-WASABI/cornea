import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_selection_props {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
    for (String tag : new String[] {
        "sel_cornea_anterior_surface", "sel_lid_box_dyn",
        "sel_lid_inner_ball_dyn", "sel_lid_inner_candidates_dyn"
    }) {
      System.out.println(tag + " TYPE="
          + model.component("comp1").selection(tag).getType());
      System.out.println("  PROPS=" + Arrays.toString(
          model.component("comp1").selection(tag).properties()));
      for (String p : model.component("comp1").selection(tag).properties()) {
        try {
          System.out.println("  " + p + "="
              + model.component("comp1").selection(tag).getString(p));
        } catch (Exception ignored) {}
      }
    }
    ModelUtil.disconnect();
  }
}
