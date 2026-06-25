import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_mesh_pairs {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
    System.out.println("MESH FEATURES=" + Arrays.toString(
        model.component("comp1").mesh("mesh1").feature().tags()));
    for (String tag : model.component("comp1").mesh("mesh1").feature().tags()) {
      System.out.print(tag + " TYPE="
          + model.component("comp1").mesh("mesh1").feature(tag).getType());
      try {
        System.out.print(" SEL=" + Arrays.toString(
            model.component("comp1").mesh("mesh1").feature(tag)
                .selection().entities()));
      } catch (Exception ignored) {}
      System.out.println();
    }
    System.out.println("PAIRS=" + Arrays.toString(
        model.component("comp1").pair().tags()));
    for (String tag : model.component("comp1").pair().tags()) {
      System.out.println(tag + " LABEL="
          + model.component("comp1").pair(tag).label());
    }
    ModelUtil.disconnect();
  }
}
