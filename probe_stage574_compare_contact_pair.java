import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574_compare_contact_pair {
  private static void inspect(String file) throws Exception {
    Model model = ModelUtil.load("PairModel", file);
    Pair pair = model.component("comp1").pair("cp_lid_cornea");
    System.out.println("FILE=" + file);
    System.out.println("LABEL=" + pair.label());
    System.out.println("TYPE=" + pair.type());
    System.out.println("MAPPING=" + pair.mapping());
    System.out.println("EXT_TOL=" + pair.extTol());
    System.out.println("SEARCH_METHOD=" + pair.searchMethod());
    System.out.println("MANUAL_DIST=" + pair.manualDist());
    System.out.println("SEARCH_DIST=" + pair.searchDist());
    System.out.println("SEARCH_TOL=" + pair.searchTol());
    ModelUtil.remove("PairModel");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      inspect("573_stage573_cornea_dynamic_regions_checked.mph");
      inspect("build_stage574_midpoint_local_patch_structure_output4_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
