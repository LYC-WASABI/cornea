import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_load_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_explicit_pair_final change.mph");
    System.out.println("PARAMETERS");
    for (String p : model.param().varnames()) {
      try {
        System.out.println("  " + p + " = " + model.param().get(p) + " ; " + model.param().descr(p));
      } catch (Exception e) {
        System.out.println("  " + p);
      }
    }
    System.out.println("SOLID FEATURES");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println("FEATURE " + f + " : " + model.component("comp1").physics("solid").feature(f).label());
      try { System.out.println("  selection=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities())); } catch (Exception ignore) {}
      try {
        for (String prop : model.component("comp1").physics("solid").feature(f).properties()) {
          if (prop.toLowerCase().contains("f") || prop.toLowerCase().contains("load") ||
              prop.toLowerCase().contains("pair") || prop.toLowerCase().contains("press") ||
              prop.toLowerCase().contains("foundation") || prop.toLowerCase().contains("u0") ||
              prop.toLowerCase().contains("disp")) {
            try { System.out.println("  " + prop + "=" + model.component("comp1").physics("solid").feature(f).getString(prop)); } catch (Exception ignore) {}
            try { System.out.println("  " + prop + "=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).getStringArray(prop))); } catch (Exception ignore) {}
          }
        }
      } catch (Exception ignore) {}
      for (String c : model.component("comp1").physics("solid").feature(f).feature().tags()) {
        System.out.println("  CHILD " + c + " : " + model.component("comp1").physics("solid").feature(f).feature(c).label());
        try {
          for (String prop : model.component("comp1").physics("solid").feature(f).feature(c).properties()) {
            if (prop.toLowerCase().contains("mu") || prop.toLowerCase().contains("f") ||
                prop.toLowerCase().contains("press") || prop.toLowerCase().contains("gap")) {
              try { System.out.println("    " + prop + "=" + model.component("comp1").physics("solid").feature(f).feature(c).getString(prop)); } catch (Exception ignore) {}
              try { System.out.println("    " + prop + "=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).feature(c).getStringArray(prop))); } catch (Exception ignore) {}
            }
          }
        } catch (Exception ignore) {}
      }
    }
    System.out.println("COUPLINGS");
    for (String c : model.component("comp1").cpl().tags()) {
      System.out.println("CPL " + c + " : " + model.component("comp1").cpl(c).label());
      try { System.out.println("  selection=" + Arrays.toString(model.component("comp1").cpl(c).selection().entities())); } catch (Exception ignore) {}
    }
  }
}
