import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage30_extended_pair_mapping_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\76_lid8mm_stage23_pure_inner_persistent_transient_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\90_lid8mm_stage31_extended_pair_mapping_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("90_lid8mm_stage31_extended_pair_mapping_setup.mph");
    Pair pair = model.component("comp1").pair("cp_lid_cornea");
    pair.extTol("1.5");
    pair.searchTol("5e-3");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "0");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("EXT_TOL=" + pair.extTol());
    System.out.println("SEARCH_TOL=" + pair.searchTol());
    System.out.println("SAVED_STAGE31_SETUP=" + OUT);
  }
}
