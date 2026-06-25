import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage33_calibrated_extended_pair_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\93_lid8mm_stage32_reaction_iteration2_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\96_lid8mm_stage33_calibrated_extended_pair_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("96_lid8mm_stage33_calibrated_extended_pair_setup.mph");
    Pair pair = model.component("comp1").pair("cp_lid_cornea");
    pair.extTol("3.0");
    pair.searchTol("1e-2");
    model.save(OUT);
    System.out.println("EXT_TOL=" + pair.extTol());
    System.out.println("SEARCH_TOL=" + pair.searchTol());
    System.out.println("SAVED_STAGE33_SETUP=" + OUT);
  }
}
