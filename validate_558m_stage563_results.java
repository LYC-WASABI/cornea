import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_558m_stage563_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558m_stage563_structure_balance_delta3um_results.mph");
      System.out.println("MODEL=" + model.label());
      System.out.println("STUDY="
          + model.study("std_stage563").label());
      System.out.println("PRESSURE_EXPR="
          + model.component("comp1").variable("var_stage563_pressure")
              .get("p_feedback563"));
      System.out.println("WFILM_EXPR="
          + model.component("comp1").variable("var_load_coupled555")
              .get("Wfilm563"));
      System.out.println("GLOBAL_EQ="
          + Arrays.toString(model.component("comp1")
              .physics("ge_force_total111").feature("ge1")
              .getStringArray("equation")));
      System.out.println("BOUNDARY_LOAD=" + Arrays.toString(
          model.component("comp1").physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      System.out.println("BALANCE=" + Arrays.deepToString(
          model.result().numerical("eval563_balance").getReal()));
      System.out.println("HMIN=" + Arrays.deepToString(
          model.result().numerical("min563_gap").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
