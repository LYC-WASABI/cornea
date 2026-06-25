import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_pair_property_inventory {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\76_lid8mm_stage23_pure_inner_persistent_transient_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    Pair pair = model.component("comp1").pair("cp_lid_cornea");
    System.out.println("PAIR_LABEL=" + pair.label());
    System.out.println("TYPE=" + pair.type());
    System.out.println("PAIR_NAME=" + pair.pairName());
    System.out.println("MAPPING=" + pair.mapping());
    System.out.println("EXT_TOL=" + pair.extTol());
    System.out.println("SEARCH_METHOD=" + pair.searchMethod());
    System.out.println("MANUAL_DIST=" + pair.manualDist());
    System.out.println("SEARCH_DIST=" + pair.searchDist());
    System.out.println("SEARCH_TOL=" + pair.searchTol());
    System.out.println("GAP_DST=" + pair.gapName(true));
    System.out.println("GAP_SRC=" + pair.gapName(false));
    System.out.println("IN_CONTACT=" + pair.inContactName());
  }
}
