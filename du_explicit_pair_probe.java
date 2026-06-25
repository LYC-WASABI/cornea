import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_explicit_pair_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_explicit_pair_final.mph");
    System.out.println("pairs:");
    for (String p : model.component("comp1").pair().tags()) {
      System.out.println("  " + p + " : " + model.component("comp1").pair(p).label());
      try {
        System.out.println("    source=" + Arrays.toString(model.component("comp1").pair(p).source().entities()));
        System.out.println("    destination=" + Arrays.toString(model.component("comp1").pair(p).destination().entities()));
      } catch (Exception e) {
        System.out.println("    entities unavailable: " + e.getMessage());
      }
    }
    System.out.println("selections:");
    for (String s : new String[]{"sel_lid_wiper_inner_surface", "sel_cornea_anterior_surface"}) {
      System.out.println("  " + s + " = " + Arrays.toString(model.component("comp1").selection(s).entities()));
    }
    System.out.println("solid features:");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println("  " + f + " : " + model.component("comp1").physics("solid").feature(f).label());
      if (f.equals("dcnt1")) {
        try { System.out.println("    pairSelection=" + model.component("comp1").physics("solid").feature(f).getString("pairSelection")); } catch (Exception ignore) {}
        try { System.out.println("    pairs=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).getStringArray("pairs"))); } catch (Exception ignore) {}
        for (String child : model.component("comp1").physics("solid").feature(f).feature().tags()) {
          System.out.println("    child " + child + " : " + model.component("comp1").physics("solid").feature(f).feature(child).label());
          try {
            System.out.println("      mu_fric=" + model.component("comp1").physics("solid").feature(f).feature(child).getString("mu_fric"));
          } catch (Exception ignore) {}
        }
      }
    }
  }
}
