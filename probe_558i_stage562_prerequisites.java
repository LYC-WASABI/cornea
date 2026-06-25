import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558i_stage562_prerequisites {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      System.out.println("LATEST_STUDIES");
      for (String tag : model.study().tags()) {
        String label = model.study(tag).label();
        if (label.contains("Stage 560") || label.contains("Stage 561")) {
          System.out.println(tag + "=" + label);
        }
      }
      System.out.println("LATEST_SOLUTIONS");
      for (String tag : model.sol().tags()) {
        int number;
        try { number = Integer.parseInt(tag.substring(3)); }
        catch (Exception ignored) { continue; }
        if (number >= 83) {
          System.out.println(tag + "=" + model.sol(tag).label());
          try {
            System.out.println("  clist=" + Arrays.toString(
                model.sol(tag).feature("v1").getStringArray("clist")));
          } catch (Exception ignored) {}
        }
      }
      var tff = model.component("comp1").physics("tff");
      System.out.println("TFF_FEATURES");
      for (String tag : tff.feature().tags()) {
        var feature = tff.feature(tag);
        System.out.println(tag + " type=" + feature.getType()
            + " label=" + feature.label());
        try {
          System.out.println("  hw1=" + feature.getString("hw1"));
        } catch (Exception ignored) {}
      }
      System.out.println("FFP1_PROPERTIES="
          + Arrays.toString(tff.feature("ffp1").properties()));
      System.out.println("FFP1_HW1="
          + tff.feature("ffp1").getString("hw1"));
      System.out.println("TFF_SELECTION="
          + Arrays.toString(tff.selection().entities()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
