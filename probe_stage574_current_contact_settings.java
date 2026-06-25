import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_current_contact_settings {
  private static void printProperty(PhysicsFeature entity, String property) {
    try {
      System.out.println(property + "=" + entity.getString(property));
      return;
    } catch (Exception ignored) {}
    try {
      System.out.println(property + "="
          + Arrays.toString(entity.getStringArray(property)));
    } catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574f_stage574_local_cornea_patch_structure_setup.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      PhysicsFeature contact = comp.physics("solid").feature("dcnt1");

      System.out.println("PAIR_LABEL=" + pair.label());
      System.out.println("PAIR_MAPPING=" + pair.mapping());
      System.out.println("PAIR_SOURCE="
          + Arrays.toString(pair.source().entities()));
      System.out.println("PAIR_DESTINATION="
          + Arrays.toString(pair.destination().entities()));
      System.out.println("PAIR_GAP_SOURCE=" + pair.gapName(false));
      System.out.println("PAIR_GAP_DESTINATION=" + pair.gapName(true));
      try { System.out.println("PAIR_EXTTOL=" + pair.extTol()); }
      catch (Exception ignored) {}
      try { System.out.println("PAIR_SEARCHTOL=" + pair.searchTol()); }
      catch (Exception ignored) {}

      System.out.println("CONTACT_LABEL=" + contact.label());
      for (String property : new String[] {
          "pairSelection", "pairs", "SolutionMethod",
          "ContactMethodCtrl", "PenaltyFactorCtrl", "PenaltyFactor",
          "zeroInitGap", "initgap_tol", "Tn_init",
          "destination_offset", "source_offset",
          "pressureOffsetCtrl", "T_0", "useCutback",
          "pairDisconnect", "SearchMethod", "SearchDist"
      }) {
        printProperty(contact, property);
      }

      System.out.println("CONTACT_CHILDREN="
          + Arrays.toString(contact.feature().tags()));
      for (String childTag : contact.feature().tags()) {
        PhysicsFeature child = contact.feature(childTag);
        System.out.println("CHILD=" + childTag
            + "|TYPE=" + child.getType()
            + "|LABEL=" + child.label()
            + "|ACTIVE=" + child.isActive());
        for (String property : new String[] {
            "mu_fric", "FrictionModel", "method",
            "PenaltyFactorCtrl", "PenaltyFactor"
        }) {
          printProperty(child, property);
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
