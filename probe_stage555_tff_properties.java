import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_tff_properties {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      var ffp = model.component("comp1").physics("tff").feature("ffp1");
      System.out.println("PROPERTIES="
          + Arrays.toString(ffp.properties()));
      for (String property : new String[] {
          "ub_src", "ub", "uw_src", "uw", "WallNormal"
      }) {
        try {
          System.out.println("EXPLICIT " + property + "="
              + Arrays.toString(ffp.getStringArray(property)));
        } catch (Exception error) {
          try {
            System.out.println("EXPLICIT " + property + "="
                + ffp.getString(property));
          } catch (Exception ignored) {}
        }
      }
      for (String property : ffp.properties()) {
        String lower = property.toLowerCase(Locale.ROOT);
        if (lower.contains("disp") || lower.contains("wall")
            || lower.contains("base") || lower.contains("height")
            || lower.startsWith("h") || lower.contains("velocity")) {
          try {
            System.out.println(property + "="
                + Arrays.toString(ffp.getStringArray(property)));
          } catch (Exception error) {
            try {
              System.out.println(property + "=" + ffp.getString(property));
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
