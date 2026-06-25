import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_genext_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.component("comp1").cpl().create("genext555probe",
          "GeneralExtrusion");
      var op = model.component("comp1").cpl("genext555probe");
      System.out.println("PROPS=" + Arrays.toString(op.properties()));
      for (String property : op.properties()) {
        try {
          System.out.println(property + "="
              + Arrays.toString(op.getStringArray(property)));
        } catch (Exception error) {
          try {
            System.out.println(property + "=" + op.getString(property));
          } catch (Exception ignored) {}
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
