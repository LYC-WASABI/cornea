import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage561_562_solver_config {
  private static void dumpStudy(Model model, String tag) {
    System.out.println("STUDY " + tag + " " + model.study(tag).label());
    for (String featureTag : model.study(tag).feature().tags()) {
      var feature = model.study(tag).feature(featureTag);
      System.out.println("  " + featureTag + " type=" + feature.getType());
      for (String property : new String[] {
          "activate", "useinitsol", "initmethod", "initsol",
          "initsoluse", "pname", "plistarr", "punit"
      }) {
        try {
          System.out.println("    " + property + "="
              + Arrays.toString(feature.getStringArray(property)));
        } catch (Exception ignored) {
          try {
            System.out.println("    " + property + "="
                + feature.getString(property));
          } catch (Exception ignoredAgain) {}
        }
      }
    }
  }

  private static void dumpSolution(Model model, String tag) {
    System.out.println("SOLUTION " + tag);
    for (String featureTag : model.sol(tag).feature().tags()) {
      var feature = model.sol(tag).feature(featureTag);
      System.out.println("  " + featureTag + " type=" + feature.getType());
      if ("Variables".equals(feature.getType())) {
        for (String property : new String[] {
            "initmethod", "initsol", "solnum", "manualsolnum",
            "notsolmethod", "notsol", "notsolnum", "notmanualsolnum"
        }) {
          try {
            System.out.println("    " + property + "="
                + Arrays.toString(feature.getStringArray(property)));
          } catch (Exception ignored) {
            try {
              System.out.println("    " + property + "="
                  + feature.getString(property));
            } catch (Exception ignoredAgain) {}
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558j_stage562_JFO_separation_scan_setup.mph");
      dumpStudy(model, "std7");
      dumpStudy(model, "std_stage562");
      dumpSolution(model, "sol86");
      dumpSolution(model, "sol87");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
