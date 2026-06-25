import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_rot_model_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_rotating_lid_wiper_position_scan_results.mph");
    System.out.println("GEOM FEATURES");
    for (String f : model.component("comp1").geom("geom1").feature().tags()) {
      System.out.println("  " + f + " : " + model.component("comp1").geom("geom1").feature(f).label());
    }
    System.out.println("STUDIES");
    for (String s : model.study().tags()) System.out.println("  " + s + " : " + model.study(s).label());
    System.out.println("SOLUTIONS");
    for (String s : model.sol().tags()) System.out.println("  " + s + " : " + model.sol(s).label());
    System.out.println("PHYSICS");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println("  " + f + " : " + model.component("comp1").physics("solid").feature(f).label());
      try { System.out.println("    sel=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities())); } catch (Exception ignore) {}
    }
    System.out.println("SELECTIONS");
    for (String s : model.component("comp1").selection().tags()) {
      if (s.contains("lid") || s.contains("contact")) {
        try { System.out.println("  " + s + "=" + Arrays.toString(model.component("comp1").selection(s).entities(2))); } catch (Exception e) { System.out.println("  " + s + " err=" + e.getMessage()); }
      }
    }
  }
}
