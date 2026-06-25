import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_stage20_pure_inner_contact_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\66_lid8mm_stage19_qs_local_pfilm_continuation_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\69_lid8mm_stage20_pure_inner_contact_qs_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("69_lid8mm_stage20_pure_inner_contact_qs_setup.mph");
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.save(OUT);
    System.out.println("PURE_INNER_SOURCE="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("CORNEA_DESTINATION="
        + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
    System.out.println("SAVED_STAGE20_PURE_INNER_SETUP=" + OUT);
  }
}
