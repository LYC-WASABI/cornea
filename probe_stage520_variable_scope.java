import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage520_variable_scope {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    String[] wanted = {
      "h_jfo197", "vwall_x", "eta_tear", "rho_tear",
      "beta_tear195", "Rq_eq", "F_total_target"
    };
    for (String tag : model.component("comp1").variable().tags()) {
      String[] names = model.component("comp1").variable(tag).varnames();
      boolean hit = false;
      for (String name : names) {
        if (Arrays.asList(wanted).contains(name)) hit = true;
      }
      if (!hit) continue;
      System.out.println("VARIABLE " + tag + " LABEL="
          + model.component("comp1").variable(tag).label());
      try {
        System.out.println("  SEL=" + Arrays.toString(
            model.component("comp1").variable(tag).selection().entities()));
      } catch (Exception error) {
        System.out.println("  SEL=GLOBAL_OR_UNAVAILABLE");
      }
      for (String name : names) {
        if (Arrays.asList(wanted).contains(name)) {
          System.out.println("  " + name + "="
              + model.component("comp1").variable(tag).get(name));
        }
      }
    }
    System.out.println("GLOBAL PARAMETERS");
    for (String name : wanted) {
      try {
        System.out.println("  " + name + "=" + model.param().get(name));
      } catch (Exception ignored) {}
    }
    ModelUtil.disconnect();
  }
}
