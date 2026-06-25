import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_imprint {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    model.component("comp1").geom("geom1").feature("fin").set("imprint", "on");
    model.component("comp1").geom("geom1").run();
    System.out.println("GEOM=" + model.component("comp1").geom("geom1")
        .feature("fin").getString("buildmessage"));
    System.out.println("PAIRS=" + Arrays.toString(
        model.component("comp1").pair().tags()));
    for (String tag : model.component("comp1").pair().tags()) {
      System.out.println(tag + " " + model.component("comp1").pair(tag).label());
      System.out.println("  src=" + Arrays.toString(
          model.component("comp1").pair(tag).source().entities()));
      System.out.println("  dst=" + Arrays.toString(
          model.component("comp1").pair(tag).destination().entities()));
    }
    System.out.println("film=" + Arrays.toString(
        model.component("comp1").selection("sel_film_track").entities(2)));
    System.out.println("cornea=" + Arrays.toString(
        model.component("comp1").selection("sel_cornea_anterior_surface")
            .entities(2)));
    model.save("probe_stage510_imprint.mph");
    ModelUtil.disconnect();
  }
}
