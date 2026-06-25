import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage46_h3um_rectangular_footprint_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\120_lid8mm_stage45_h3um_gap_quasisteady_setup.mph");
    model.label("122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    model.component("comp1").variable("var_mixed_lub").set("lid_mask",
        "lid_mask_length*lid_mask_width");
    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    ModelUtil.disconnect();
  }
}
