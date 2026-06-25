import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage520_pairs {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    for (String tag : model.component("comp1").pair().tags()) {
      System.out.println("PAIR " + tag + " LABEL="
          + model.component("comp1").pair(tag).label());
      try {
        System.out.println("  SOURCE=" + Arrays.toString(
            model.component("comp1").pair(tag).source().entities()));
        System.out.println("  DESTINATION=" + Arrays.toString(
            model.component("comp1").pair(tag).destination().entities()));
      } catch (Exception error) {
        System.out.println("  ENTITY ERROR=" + error.getMessage());
      }
    }
    ModelUtil.disconnect();
  }
}
