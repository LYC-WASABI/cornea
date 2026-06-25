import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage22_final_inventory {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("RESULTS=" + Arrays.toString(model.result().tags()));
    System.out.println("SOURCE="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("DESTINATION="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
    System.out.println("PAIR_DISCONNECT="
        + model.component("comp1").physics("solid").feature("dcnt1").getString("pairDisconnect"));
    System.out.println("EXT_TOL=" + model.component("comp1").pair("cp_lid_cornea").extTol());
    System.out.println("SEARCH_TOL=" + model.component("comp1").pair("cp_lid_cornea").searchTol());
  }
}
