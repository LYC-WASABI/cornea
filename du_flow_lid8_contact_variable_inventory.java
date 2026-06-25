import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_lid8_contact_variable_inventory {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\82_lid8mm_stage27_force_controlled_qs_scan_short_results.mph";

  private static void printFeature(String prefix, PropFeature f) {
    System.out.println(prefix + " label=" + f.label());
    for (String p : f.properties()) {
      try { System.out.println("  " + p + "=" + Arrays.toString(f.getStringArray(p))); }
      catch (Exception ignored) {
        try { System.out.println("  " + p + "=" + f.getString(p)); }
        catch (Exception ignored2) {}
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    System.out.println("SOL21_ROOT=" + Arrays.toString(model.sol("sol21").feature().tags()));
    for (String root : model.sol("sol21").feature().tags()) {
      System.out.println("ROOT " + root + " label=" + model.sol("sol21").feature(root).label());
      for (String child : model.sol("sol21").feature(root).feature().tags()) {
        printFeature("  CHILD " + root + "/" + child, model.sol("sol21").feature(root).feature(child));
      }
    }
  }
}
