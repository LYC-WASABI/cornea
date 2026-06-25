import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_final_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    System.out.println("physics features:");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println("  " + f + " : " + model.component("comp1").physics("solid").feature(f).label());
      try {
        for (String child : model.component("comp1").physics("solid").feature(f).feature().tags()) {
          System.out.println("    child " + child + " : " + model.component("comp1").physics("solid").feature(f).feature(child).label());
          try {
            System.out.println("      mu_fric=" + model.component("comp1").physics("solid").feature(f).feature(child).getString("mu_fric"));
          } catch (Exception ignore) {}
        }
      } catch (Exception ignore) {}
    }
    System.out.println("study features:");
    for (String s : model.study().tags()) {
      System.out.println("  study " + s + " : " + model.study(s).label());
      for (String f : model.study(s).feature().tags()) {
        System.out.println("    " + f + " : " + model.study(s).feature(f).label());
      }
    }
    System.out.println("mesh features:");
    for (String f : model.component("comp1").mesh("mesh1").feature().tags()) {
      System.out.println("  " + f + " : " + model.component("comp1").mesh("mesh1").feature(f).label());
      try {
        for (String child : model.component("comp1").mesh("mesh1").feature(f).feature().tags()) {
          System.out.println("    child " + child + " : " + model.component("comp1").mesh("mesh1").feature(f).feature(child).label());
        }
      } catch (Exception ignore) {}
    }
  }
}
