import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage520_tff_api {
  static void dumpFeature(Model model, String tag) {
    System.out.println("FEATURE " + tag + " TYPE="
        + model.component("comp1").physics("tff").feature(tag).getType());
    try {
      System.out.println("  SEL=" + Arrays.toString(
          model.component("comp1").physics("tff").feature(tag)
              .selection().entities()));
    } catch (Exception ignored) {}
    System.out.println("  PROPS=" + Arrays.toString(
        model.component("comp1").physics("tff").feature(tag).properties()));
    for (String p :
        model.component("comp1").physics("tff").feature(tag).properties()) {
      try {
        System.out.println("  " + p + "=" + model.component("comp1")
            .physics("tff").feature(tag).getString(p));
      } catch (Exception ignored) {}
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    System.out.println("TFF ROOT SEL=" + Arrays.toString(
        model.component("comp1").physics("tff").selection().entities()));
    System.out.println("TFF FEATURES=" + Arrays.toString(
        model.component("comp1").physics("tff").feature().tags()));
    for (String tag :
        model.component("comp1").physics("tff").feature().tags()) {
      dumpFeature(model, tag);
    }
    String[] types = {
      "Pressure", "PressureBoundary", "BoundaryPressure",
      "OpenBoundary", "Outlet", "Inlet"
    };
    for (String type : types) {
      String tag = "probe_" + type.toLowerCase(Locale.ROOT);
      try {
        model.component("comp1").physics("tff").create(tag, type, 1);
        System.out.println("CREATED " + type + " PROPS=" + Arrays.toString(
            model.component("comp1").physics("tff").feature(tag)
                .properties()));
      } catch (Exception error) {
        System.out.println("FAILED " + type + ": " + error.getMessage());
      }
    }
    ModelUtil.disconnect();
  }
}
