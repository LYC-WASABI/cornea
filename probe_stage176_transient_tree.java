import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage176_transient_tree {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      System.out.println("STUDIES");
      for (String tag : model.study().tags()) {
        System.out.println(tag + " label=" + model.study(tag).label()
            + " features=" + Arrays.toString(model.study(tag).feature().tags()));
      }
      System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
      System.out.println("FUNCTIONS=" + Arrays.toString(model.func().tags()));
      String[] parameters = {
        "T_structure_pre", "T_structure_slide", "T_structure_hold",
        "dt_structure_out", "phi_start", "phi_end", "F_total_target",
        "h0_tear", "Rq_lid", "Rq_eq"
      };
      for (String name : parameters) {
        try {
          System.out.println("PARAM " + name + "=" + model.param().get(name));
        } catch (Exception ignored) {
        }
      }
      String[] variableFeatures = {
        "var_motion", "var_mixed_lub", "var_partitioned_local_pfilm",
        "var_qs_rotation142", "var_bounded_indent154"
      };
      String[] variableNames = {
        "phi_lid_structure", "slide_fraction_structure", "lid_speed",
        "t_film_replay", "dr_indent119", "Fn_contact119", "Fn_film119",
        "Fn_total119"
      };
      for (String feature : variableFeatures) {
        try {
          System.out.println("VARIABLE_FEATURE " + feature);
          for (String name : variableNames) {
            try {
              System.out.println("  " + name + "="
                  + model.component("comp1").variable(feature).get(name));
            } catch (Exception ignored) {
            }
          }
        } catch (Exception ignored) {
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
