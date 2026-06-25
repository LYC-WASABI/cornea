import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_param_physics_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results-2.mph");
    System.out.println("Parameters:");
    for (String p : model.param().varnames()) {
      try { System.out.println(p + " = " + model.param().get(p)); } catch (Exception ignored) {}
    }
    System.out.println("Selections:");
    for (String s : model.component("comp1").selection().tags()) {
      try {
        System.out.println(s + " : " + model.component("comp1").selection(s).label()
            + " entities=" + Arrays.toString(model.component("comp1").selection(s).entities(2)));
      } catch (Exception ex) { System.out.println(s + " : " + ex.getMessage()); }
    }
    System.out.println("Solid features:");
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      System.out.println(f + " : " + model.component("comp1").physics("solid").feature(f).label()
          + " type=" + model.component("comp1").physics("solid").feature(f).getType());
      try { System.out.println("  sel=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities())); } catch (Exception ignored) {}
    }
    System.out.println("Pairs:");
    for (String p : model.component("comp1").pair().tags()) {
      System.out.println(p + " : " + model.component("comp1").pair(p).label());
    }
  }
}
