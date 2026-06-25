import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_rigid_props_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph");
    try { model.component("comp1").physics("solid").feature().remove("probe_rc"); } catch (Exception ignore) {}
    model.component("comp1").physics("solid").feature().create("probe_rc", "RigidConnector", 2);
    for (String p : model.component("comp1").physics("solid").feature("probe_rc").properties()) {
      try { System.out.println(p + "=" + model.component("comp1").physics("solid").feature("probe_rc").getString(p)); } catch (Exception ignore) {}
      try { System.out.println(p + "=" + Arrays.toString(model.component("comp1").physics("solid").feature("probe_rc").getStringArray(p))); } catch (Exception ignore) {}
    }
  }
}
