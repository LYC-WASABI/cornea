import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p_swept_domain_topology {
  private static void printSelection(ModelNode comp, String tag, int dim) {
    try {
      System.out.println("SELECTION " + tag + "="
          + Arrays.toString(comp.selection(tag).entities(dim)));
    } catch (Exception error) {
      System.out.println("SELECTION_ERROR " + tag + "=" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576p_stage576_swept_domain_gated_jfo_setup.mph");
      ModelNode comp = model.component("comp1");
      printSelection(comp, "sel_film_swept571", 2);
      printSelection(comp, "sel_local_cornea_patch574", 2);
      printSelection(comp, "sel_film_inlet571", 1);
      printSelection(comp, "sel_film_outlet571", 1);
      printSelection(comp, "sel_film_side_left571", 1);
      printSelection(comp, "sel_film_side_right571", 1);
      System.out.println("TFF_SELECTION="
          + Arrays.toString(comp.physics("tff").selection().entities()));
      System.out.println("MOTION_VARIABLE_SELECTION="
          + Arrays.toString(comp.variable("var_dynamic_motion572").selection().entities()));
      System.out.println("REGION_VARIABLE_SELECTION="
          + Arrays.toString(comp.variable("var_cornea_dynamic_regions573").selection().entities()));
      System.out.println("INTOP_FILM_SELECTION="
          + Arrays.toString(comp.cpl("intop_film").selection().entities()));
      for (String feature : new String[] {"ffp1", "ms_vent573", "wc_open_anchor573"}) {
        try {
          System.out.println("TFF_FEATURE_SELECTION " + feature + "="
              + Arrays.toString(comp.physics("tff").feature(feature).selection().entities()));
        } catch (Exception error) {
          System.out.println("TFF_FEATURE_SELECTION_ERROR " + feature + "=" + error.getMessage());
        }
      }
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
