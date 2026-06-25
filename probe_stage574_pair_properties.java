import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_pair_properties {
  private static void print(String file) {
    Model model = ModelUtil.load("Model", file);
    Pair pair = model.component("comp1").pair("cp_lid_cornea");
    System.out.println("FILE=" + file);
    System.out.println("CLASS=" + pair.getClass().getName());
    try {
      for (String property : pair.properties()) {
        try {
          System.out.println(
              "  " + property + "=" + pair.getString(property));
        } catch (Exception ignored) {}
      }
    } catch (Exception error) {
      System.out.println("PROPERTIES_ERROR=" + error.getMessage());
    }
    ModelUtil.remove("Model");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      print("573_stage573_source_true_gap_checked.mph");
      print("574d_stage574_pair_rebound_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
