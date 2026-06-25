import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage181_transient_behavior {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "341_lid8mm_stage181_exact_static_results_Model.mph");
    System.out.println(model.component("comp1").physics("solid")
        .prop("StructuralTransientBehavior")
        .getString("StructuralTransientBehavior"));
    ModelUtil.disconnect();
  }
}
