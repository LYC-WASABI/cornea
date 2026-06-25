import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_contact {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557e_stage555_phase3c_alpha0p389.mph");
      System.out.println("TYPE=" + model.component("comp1").physics("solid")
          .feature("dcnt1").getType());
      for (String p : model.component("comp1").physics("solid")
          .feature("dcnt1").properties()) {
        try {
          System.out.println(p + "="
              + Arrays.toString(model.component("comp1").physics("solid")
                  .feature("dcnt1").getStringArray(p)));
        } catch (Exception ignored) {}
      }
      for (String tag : model.component("comp1").physics("solid")
          .feature("dcnt1").feature().tags()) {
        System.out.println("CHILD " + tag + " TYPE="
            + model.component("comp1").physics("solid").feature("dcnt1")
                .feature(tag).getType());
        for (String p : model.component("comp1").physics("solid")
            .feature("dcnt1").feature(tag).properties()) {
          try {
            System.out.println("  " + p + "="
                + Arrays.toString(model.component("comp1").physics("solid")
                    .feature("dcnt1").feature(tag).getStringArray(p)));
          } catch (Exception ignored) {}
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
