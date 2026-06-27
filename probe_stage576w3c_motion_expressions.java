import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576w3c_motion_expressions {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph");
      ModelNode comp = model.component("comp1");
      for (String p : new String[] {
          "T_pre572", "T_slide572", "T_end572", "time_offset572",
          "theta_slide_total", "lambda_v574", "t_position576p2",
          "t0_576w3c", "t1_576w3c", "dt_576w3c"
      }) {
        try {
          System.out.println("PARAM " + p + " expr=" + model.param().get(p)
              + " value=" + model.param().evaluate(p));
        } catch (Exception error) {
          System.out.println("PARAM " + p + " ERROR=" + error.getMessage());
        }
      }
      for (String vtag : new String[] {
          "var_dynamic_motion572", "var_cornea_dynamic_regions573",
          "var_hrelease576w3c"
      }) {
        try {
          System.out.println("VARIABLE_TAG=" + vtag);
          for (String name : new String[] {
              "tau572", "slide_fraction572", "theta_lid_spatial572",
              "omega_lid_rot572", "M_lid572", "M_core573", "M_drain573",
              "h_calc573", "h_calc576w3c"
          }) {
            try {
              System.out.println("  " + name + "=" + comp.variable(vtag).get(name));
            } catch (Exception ignored) {}
          }
        } catch (Exception error) {
          System.out.println("VARIABLE_TAG=" + vtag + " ERROR=" + error.getMessage());
        }
      }
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
