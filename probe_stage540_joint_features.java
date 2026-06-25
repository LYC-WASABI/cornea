import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage540_joint_features {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "533_stage530_local_film_stationary_checked.mph");
    for (String tag : new String[] {
        "load_partitioned_pfilm", "disp_lid_time", "dcnt1"
    }) {
      System.out.println(tag + " SEL=" + Arrays.toString(
          model.component("comp1").physics("solid").feature(tag)
              .selection().entities()));
      System.out.println("  PROPS=" + Arrays.toString(
          model.component("comp1").physics("solid").feature(tag)
              .properties()));
    }
    System.out.println("GE PROPS=" + Arrays.toString(
        model.component("comp1").physics("ge_force_total111")
            .feature("ge1").properties()));
    for (String p : model.component("comp1")
        .physics("ge_force_total111").feature("ge1").properties()) {
      try {
        System.out.println("  " + p + "=" + model.component("comp1")
            .physics("ge_force_total111").feature("ge1").getString(p));
      } catch (Exception ignored) {}
    }
    ModelUtil.disconnect();
  }
}
