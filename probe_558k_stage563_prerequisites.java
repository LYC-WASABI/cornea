import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558k_stage563_prerequisites {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558k_stage562_JFO_separation_scan_results.mph");
      String comp = "comp1";
      System.out.println("SOL86_CLIST=" + Arrays.toString(
          model.sol("sol86").feature("v1").getStringArray("clist")));
      System.out.println("STUDY_STD7=" + model.study("std7").label());

      var global = model.component(comp).physics("ge_force_total111");
      System.out.println("GLOBAL_PHYSICS_FEATURES");
      for (String tag : global.feature().tags()) {
        var feature = global.feature(tag);
        System.out.println(tag + " type=" + feature.getType()
            + " label=" + feature.label());
        System.out.println("  properties="
            + Arrays.toString(feature.properties()));
        for (String property : feature.properties()) {
          try {
            String[] value = feature.getStringArray(property);
            if (value.length > 0) {
              System.out.println("  " + property + "="
                  + Arrays.toString(value));
            }
          } catch (Exception ignored) {}
        }
      }

      System.out.println("LOAD_VARIABLES");
      var variables = model.component(comp)
          .variable("var_load_coupled555");
      for (String name : new String[] {
          "Wfilm555", "Ftotal555", "Ferr555"
      }) {
        try {
          System.out.println(name + "=" + variables.get(name));
        } catch (Exception ignored) {}
      }
      System.out.println("BOUNDARY_LOAD=" + Arrays.toString(
          model.component(comp).physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      System.out.println("DR_INDENT=" + model.component(comp)
          .variable("var_total_force111").get("dr_indent119"));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
