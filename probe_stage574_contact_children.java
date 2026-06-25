import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_contact_children {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574f_stage574_local_cornea_patch_structure_setup.mph");
      var contact =
          model.component("comp1").physics("solid").feature("dcnt1");
      System.out.println("CONTACT_PROPERTIES="
          + Arrays.toString(contact.properties()));
      for (String tag : contact.feature().tags()) {
        var child = contact.feature(tag);
        System.out.println("CHILD=" + tag + "|" + child.getType()
            + "|" + child.label());
        System.out.println("PROPS="
            + Arrays.toString(child.properties()));
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
