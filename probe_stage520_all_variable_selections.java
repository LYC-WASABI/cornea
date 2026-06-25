import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage520_all_variable_selections {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    for (String tag : model.component("comp1").variable().tags()) {
      try {
        int[] ids = model.component("comp1").variable(tag)
            .selection().entities();
        System.out.println(tag + " " + Arrays.toString(ids)
            + " LABEL=" + model.component("comp1").variable(tag).label());
      } catch (Exception error) {
        System.out.println(tag + " GLOBAL LABEL="
            + model.component("comp1").variable(tag).label());
      }
    }
    ModelUtil.disconnect();
  }
}
