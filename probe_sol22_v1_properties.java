import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_sol22_v1_properties {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      SolverFeature v1 = m.sol("sol22").feature("v1");
      for (String p : v1.properties()) {
        try {
          System.out.println(p + "=" + v1.getString(p));
        } catch (Exception e1) {
          try { System.out.println(p + "=" + Arrays.toString(v1.getStringArray(p))); }
          catch (Exception ignore) {}
        }
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
