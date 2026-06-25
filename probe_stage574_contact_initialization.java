import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_contact_initialization {
  private static void inspect(String file) throws Exception {
    Model model = ModelUtil.load("ContactModel", file);
    var contact =
        model.component("comp1").physics("solid").feature("dcnt1");
    System.out.println("FILE=" + file);
    for (String property : new String[] {
        "SolutionMethod", "ContactMethodCtrl", "zeroInitGap",
        "initgap_tol", "Tn_init", "destination_offset",
        "source_offset", "pressureOffsetCtrl", "T_0",
        "tolstart", "tolsize", "useCutback", "pairDisconnect"
    }) {
      try {
        System.out.println(property + "=" + contact.getString(property));
      } catch (Exception error) {
        try {
          System.out.println(property + "="
              + Arrays.toString(contact.getStringArray(property)));
        } catch (Exception ignored) {}
      }
    }
    ModelUtil.remove("ContactModel");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      inspect("573_stage573_cornea_dynamic_regions_checked.mph");
      inspect("574f_stage574_local_cornea_patch_structure_setup.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
