import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_557z_jfo_setup {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557z_stage555_alpha0p964_before_JFO.mph");
      System.out.println("PARAM alpha_gap555="
          + model.param().get("alpha_gap555"));
      System.out.println("STUDIES");
      for (String study : model.study().tags()) {
        System.out.println("  " + study + " label="
            + model.study(study).label());
      }
      System.out.println("SOLUTIONS");
      for (String solution : model.sol().tags()) {
        System.out.println("  " + solution + " label="
            + model.sol(solution).label());
        try {
          SolverFeature variables = model.sol(solution).feature("v1");
          System.out.println("    cname="
              + Arrays.toString(variables.getStringArray("cname")));
          System.out.println("    clist="
              + Arrays.toString(variables.getStringArray("clist")));
        } catch (Exception ignored) {}
      }

      String comp = "comp1";
      System.out.println("PHYSICS");
      for (String physics : model.component(comp).physics().tags()) {
        System.out.println("  " + physics + " type="
            + model.component(comp).physics(physics).getType()
            + " label=" + model.component(comp).physics(physics).label());
      }

      String tff = "tff";
      System.out.println("TFF EQUATION TYPE");
      try {
        System.out.println("  EquationType="
            + Arrays.toString(model.component(comp).physics(tff)
                .prop("EquationType").getStringArray("EquationType")));
      } catch (Exception error) {
        System.out.println("  ERROR=" + error.getMessage());
      }
      System.out.println("TFF FEATURES");
      for (String tag :
          model.component(comp).physics(tff).feature().tags()) {
        var feature = model.component(comp).physics(tff).feature(tag);
        System.out.println("  " + tag + " type=" + feature.getType()
            + " label=" + feature.label());
        try {
          System.out.println("    selection="
              + Arrays.toString(feature.selection().entities()));
        } catch (Exception ignored) {}
        for (String property : feature.properties()) {
          String lower = property.toLowerCase(Locale.ROOT);
          if (lower.contains("pfilm") || lower.contains("theta")
              || lower.contains("cav") || lower.contains("equation")
              || lower.contains("pressure") || lower.contains("study")
              || lower.contains("boundary") || lower.contains("flow")) {
            System.out.println("    " + property + "="
                + Arrays.toString(feature.getStringArray(property)));
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
