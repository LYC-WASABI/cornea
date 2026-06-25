import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_study_props_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\01_dynamic_sliding_minus35_to_plus35_setup.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    for (String f : model.study("std_dynamic_slide").feature().tags()) {
      System.out.println("STUDY " + f + " label=" + model.study("std_dynamic_slide").feature(f).label());
      for (String p : model.study("std_dynamic_slide").feature(f).properties()) {
        try { System.out.println("  " + p + "=" + Arrays.toString(
            model.study("std_dynamic_slide").feature(f).getStringArray(p))); }
        catch (Exception ex) {
          try { System.out.println("  " + p + "="
              + model.study("std_dynamic_slide").feature(f).getString(p)); }
          catch (Exception ignored) { System.out.println("  " + p); }
        }
      }
    }
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      if (f.contains("lid") || f.contains("rc")) {
        System.out.println("PHYSICS " + f + " label="
            + model.component("comp1").physics("solid").feature(f).label());
        for (String p : model.component("comp1").physics("solid").feature(f).properties()) {
          try { System.out.println("  " + p + "=" + Arrays.toString(
              model.component("comp1").physics("solid").feature(f).getStringArray(p))); }
          catch (Exception ex) {
            try { System.out.println("  " + p + "="
                + model.component("comp1").physics("solid").feature(f).getString(p)); }
            catch (Exception ignored) { System.out.println("  " + p); }
          }
        }
      }
    }
  }
}
