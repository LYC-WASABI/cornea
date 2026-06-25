import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_tff_continuity_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557_stage555_joint_solver_setup.mph");
      System.out.println("FEATURES="
          + Arrays.toString(
              model.component("comp1").physics("tff").feature().tags()));
      for (String type : new String[] {
          "Continuity", "ThinFilmContinuity", "InteriorWall"
      }) {
        String tag = "probe_" + type;
        try {
          model.component("comp1").physics("tff")
              .create(tag, type, 1);
          System.out.println("CREATED " + type + " PROPS="
              + Arrays.toString(model.component("comp1").physics("tff")
                  .feature(tag).properties()));
        } catch (Exception error) {
          System.out.println("FAILED " + type + " " + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
