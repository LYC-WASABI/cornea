import com.comsol.model.*;
import com.comsol.model.util.*;
import java.lang.reflect.*;
import java.util.*;

public class probe_solver_collection_methods {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      Object coll = m.sol();
      Method[] methods = coll.getClass().getMethods();
      Arrays.sort(methods, Comparator.comparing(Method::getName));
      for (Method method : methods) {
        String name = method.getName().toLowerCase();
        if (name.contains("copy") || name.contains("duplicate") || name.contains("create")
            || name.contains("clone") || name.contains("attach")) {
          System.out.println(method.toString());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
