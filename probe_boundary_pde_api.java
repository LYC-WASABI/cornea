import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_boundary_pde_api {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "356_lid8mm_stage190_physical_h3um_diagnostics_results_Model.mph");
    String[] types = {
      "CoefficientFormBoundaryPDE", "GeneralFormBoundaryPDE",
      "CoefficientFormPDE", "GeneralFormPDE"
    };
    for (String type : types) {
      String tag = "probe" + type.hashCode();
      try {
        model.component("comp1").physics().create(tag, type, "geom1");
        System.out.println("CREATED " + type + " tag=" + tag);
        System.out.println("FEATURES=" + Arrays.toString(
            model.component("comp1").physics(tag).feature().tags()));
        for (String feature :
            model.component("comp1").physics(tag).feature().tags()) {
          System.out.println("  " + feature + " type="
              + model.component("comp1").physics(tag).feature(feature).getType()
              + " props=" + Arrays.toString(
                  model.component("comp1").physics(tag).feature(feature).properties()));
        }
        model.component("comp1").physics().remove(tag);
      } catch (Exception error) {
        System.out.println("FAILED " + type + " " + error.getMessage());
      }
    }
    ModelUtil.disconnect();
  }
}
