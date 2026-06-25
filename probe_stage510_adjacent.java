import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_adjacent {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "512_stage510_local_film_track_results.mph");
    model.component("comp1").selection().create("adjprobe", "Adjacent");
    System.out.println(Arrays.toString(
        model.component("comp1").selection("adjprobe").properties()));
    for (String p :
        model.component("comp1").selection("adjprobe").properties()) {
      try {
        System.out.println(p + "="
            + model.component("comp1").selection("adjprobe").getString(p));
      } catch (Exception ignored) {}
    }
    ModelUtil.disconnect();
  }
}
