import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage116_print_time_series {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "197_lid8mm_stage116_dynamic_variable_indent_from_preload_results_Model.mph");
      double[][] v = m.result().numerical("eval116_total_load_indent").getReal();
      for (int j = 0; j < v[0].length; j++) {
        double t = 0.01 + 0.0005*j;
        System.out.println("t=" + t
            + " contact=" + v[0][j]
            + " film=" + v[1][j]
            + " total=" + v[2][j]
            + " err=" + v[4][j]
            + " indent_mm=" + v[5][j]);
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
