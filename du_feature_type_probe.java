import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_feature_type_probe {
  public static void main(String[] args) throws java.io.IOException {
    String[] types = {
      "Displacement", "PrescribedDisplacement", "PrescribedDisplacement2",
      "PrescribedDisplacementBoundary", "DisplacementBoundary", "PrescribedDisplacement1",
      "Displacement1", "SolidDisplacement", "PrescribedDisp", "PrescribedDisplacementBC",
      "Disp", "BoundaryDisplacement", "Fixed", "RigidConnector", "SpringFoundation"
    };
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph");
    for (String type : types) {
      String tag = "probe_" + type;
      try {
        try { model.component("comp1").physics("solid").feature().remove(tag); } catch (Exception ignore) {}
        model.component("comp1").physics("solid").feature().create(tag, type, 2);
        System.out.println("OK " + type);
        for (String p : model.component("comp1").physics("solid").feature(tag).properties()) System.out.println("  " + p);
      } catch (Exception e) {
        System.out.println("NO " + type + " : " + e.getMessage());
      }
    }
  }
}
