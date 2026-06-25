import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_maxsurface_position_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558a_stage555_JFO_only_study_alpha0p964_setup.mph");
      model.result().numerical().create("probeMaxApi", "MaxSurface");
      var feature = model.result().numerical("probeMaxApi");
      System.out.println(Arrays.toString(feature.properties()));
      for (String property : feature.properties()) {
        try {
          System.out.println(property + "="
              + Arrays.toString(feature.getStringArray(property)));
        } catch (Exception ignored) {}
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
