import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_tff_selection_and_boundary_pde_field {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "368_lid8mm_stage194_cavitation_limited_joint_load_results_Model.mph");
    System.out.println("FFP_SELECTION="
        + Arrays.toString(model.component("comp1").physics("tff")
            .feature("ffp1").selection().entities()));
    System.out.println("TFF_SELECTION="
        + Arrays.toString(model.component("comp1").physics("tff")
            .selection().entities()));
    model.component("comp1").physics().create(
        "cavprobe", "CoefficientFormBoundaryPDE", "geom1");
    System.out.println("FIELD_TAGS="
        + Arrays.toString(model.component("comp1").physics("cavprobe")
            .field().tags()));
    for (String field :
        model.component("comp1").physics("cavprobe").field().tags()) {
      System.out.println("FIELD " + field);
    }
    model.component("comp1").physics().remove("cavprobe");
    ModelUtil.disconnect();
  }
}
