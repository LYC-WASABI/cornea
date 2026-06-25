import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574k_force_definition {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "574k_stage574_gap_regularized_release_scan_results.mph");
      ModelNode comp = model.component("comp1");
      for (String vt : comp.variable().tags()) {
        for (String name : new String[] {"Fn_contact570", "Fn_contact573", "Fn_total573"}) {
          try {
            System.out.println(vt + "." + name + "=" + comp.variable(vt).get(name));
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
