import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_contact_pair_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\66_lid8mm_stage19_qs_local_pfilm_continuation_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("PAIRS=" + Arrays.toString(model.component("comp1").pair().tags()));
    for (String tag : model.component("comp1").pair().tags()) {
      System.out.println("PAIR " + tag + " label=" + model.component("comp1").pair(tag).label()
          );
      try { System.out.println("  source=" + Arrays.toString(model.component("comp1").pair(tag).source().entities())); }
      catch (Exception e) { System.out.println("  source=<unavailable>"); }
      try { System.out.println("  destination=" + Arrays.toString(model.component("comp1").pair(tag).destination().entities())); }
      catch (Exception e) { System.out.println("  destination=<unavailable>"); }
    }
    System.out.println("SELECTIONS");
    for (String tag : model.component("comp1").selection().tags()) {
      try {
        System.out.println("  " + tag + " label=" + model.component("comp1").selection(tag).label()
            + " bdr=" + Arrays.toString(model.component("comp1").selection(tag).entities(2)));
      } catch (Exception ignored) {}
    }
    System.out.println("DCNT_PROPERTIES");
    for (String prop : new String[]{"pair", "ContactMethodCtrl", "pairDisconnect", "useCutback",
        "PenaltyFactorCtrl", "PenaltyFactor", "SearchMethod", "SearchDist"}) {
      try {
        System.out.println("  " + prop + "="
            + model.component("comp1").physics("solid").feature("dcnt1").getString(prop));
      } catch (Exception e) {
        System.out.println("  " + prop + "=<unavailable>");
      }
    }
    System.out.println("DCNT_CHILDREN="
        + Arrays.toString(model.component("comp1").physics("solid").feature("dcnt1").feature().tags()));
  }
}
