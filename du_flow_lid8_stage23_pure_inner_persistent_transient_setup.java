import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage23_pure_inner_persistent_transient_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\61_lid8mm_stage18_partitioned_local_pfilm_feedback_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\76_lid8mm_stage23_pure_inner_persistent_transient_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("76_lid8mm_stage23_pure_inner_persistent_transient_setup.mph");
    model.param().set("scale_partitioned_pfilm", "0.10", "Stable continuation scale for local pfilm replay");
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "0");
    model.component("comp1").physics("solid").feature("dcnt1").set("useCutback", "1");
    model.save(OUT);
    System.out.println("SOURCE="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("DESTINATION="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
    System.out.println("PAIR_DISCONNECT="
        + model.component("comp1").physics("solid").feature("dcnt1").getString("pairDisconnect"));
    System.out.println("SAVED_STAGE23_SETUP=" + OUT);
  }
}
