import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558d_structural_film_load {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558d_stage555_scaled_JFO_structure_recovery.mph");
      var solid = model.component("comp1").physics("solid");
      System.out.println("SOLID_FEATURES");
      for (String tag : solid.feature().tags()) {
        var feature = solid.feature(tag);
        String type = "";
        try { type = feature.getType(); } catch (Exception ignored) {}
        System.out.println("TAG=" + tag
            + " TYPE=" + type
            + " LABEL=" + feature.label());
        try {
          System.out.println("  SELECTION="
              + Arrays.toString(feature.selection().entities()));
        } catch (Exception ignored) {}
        try {
          System.out.println("  forceType="
              + feature.getString("forceType"));
        } catch (Exception ignored) {}
        try {
          System.out.println("  FperArea="
              + Arrays.toString(feature.getStringArray("FperArea")));
        } catch (Exception ignored) {}
      }
      for (String parameter : new String[] {
          "scale_pfilm555", "beta_pfeedback558", "alpha_gap555"
      }) {
        try {
          System.out.println("PARAM " + parameter + "="
              + model.param().get(parameter));
        } catch (Exception error) {
          System.out.println("PARAM " + parameter + "=NOT_FOUND");
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
