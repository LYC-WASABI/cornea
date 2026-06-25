import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_change_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_explicit_pair_final change.mph");
    System.out.println("pairs:");
    for (String p : model.component("comp1").pair().tags()) {
      System.out.println("  " + p + " : " + model.component("comp1").pair(p).label());
      try { System.out.println("    source=" + Arrays.toString(model.component("comp1").pair(p).source().entities())); } catch (Exception e) { System.out.println("    source err=" + e.getMessage()); }
      try { System.out.println("    destination=" + Arrays.toString(model.component("comp1").pair(p).destination().entities())); } catch (Exception e) { System.out.println("    dest err=" + e.getMessage()); }
    }
    System.out.println("selections:");
    for (String s : model.component("comp1").selection().tags()) {
      try {
        System.out.println("  " + s + "=" + Arrays.toString(model.component("comp1").selection(s).entities()));
      } catch (Exception e) {
        System.out.println("  " + s + " err=" + e.getMessage());
      }
    }
    System.out.println("solid dcnt1:");
    try { System.out.println("  pairSelection=" + model.component("comp1").physics("solid").feature("dcnt1").getString("pairSelection")); } catch (Exception ignore) {}
    try { System.out.println("  pairs=" + Arrays.toString(model.component("comp1").physics("solid").feature("dcnt1").getStringArray("pairs"))); } catch (Exception ignore) {}
  }
}
