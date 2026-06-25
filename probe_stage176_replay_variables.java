import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage176_replay_variables {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      String[] features = {
        "var_partitioned_local_pfilm", "var_mixed_lub", "var_bounded_indent154"
      };
      String[] names = {
        "pfilm_replay53", "W_film_replay53", "h_replay154",
        "tau_film_replay154", "lambda_replay154", "Fn_film119",
        "Fn_error119", "scale_pfilm_effective120", "theta_slide_total",
        "slide_fraction_structure", "phi_lid_structure", "F_film_target147",
        "lid_mask", "A_lid_mask147", "A_mask_eps147", "pfilm_cap120"
      };
      for (String feature : features) {
        System.out.println("FEATURE " + feature);
        for (String name : names) {
          try {
            System.out.println(name + "="
                + model.component("comp1").variable(feature).get(name));
          } catch (Exception ignored) {
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
