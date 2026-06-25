import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage563_frozen_pressure_and_std6 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558k_stage562_JFO_separation_scan_results.mph");
      model.result().numerical().create("probe563", "EvalGlobal");
      model.result().numerical("probe563").set("data", "dset562_scan");
      model.result().numerical("probe563").set("expr", new String[] {
        "withsol('sol86',intop_film(max(tff.p,0)),setval(delta_h562,3[um]))",
        "scale_pfilm555*withsol('sol86',intop_film(max(tff.p,0)),setval(delta_h562,3[um]))"
      });
      model.result().numerical("probe563").set(
          "unit", new String[] {"N", "N"});
      System.out.println("FROZEN=" + Arrays.deepToString(
          model.result().numerical("probe563").getReal()));
      System.out.println("STD6=" + model.study("std6").label());
      for (String tag : model.study("std6").feature().tags()) {
        var feature = model.study("std6").feature(tag);
        System.out.println("  " + tag + " type=" + feature.getType());
        try {
          System.out.println("    activate=" + Arrays.toString(
              feature.getStringArray("activate")));
        } catch (Exception ignored) {}
      }
      System.out.println("SOL85");
      for (String tag : model.sol("sol85").feature().tags()) {
        var feature = model.sol("sol85").feature(tag);
        System.out.println("  " + tag + " type=" + feature.getType());
        if ("Variables".equals(feature.getType())) {
          for (String property : new String[] {
              "initmethod", "initsol", "solnum", "manualsolnum",
              "notsolmethod", "notsol", "notsolnum", "notmanualsolnum"
          }) {
            try {
              System.out.println("    " + property + "="
                  + Arrays.toString(feature.getStringArray(property)));
            } catch (Exception ignored) {}
          }
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
