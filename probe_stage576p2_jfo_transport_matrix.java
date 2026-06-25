import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;

public class probe_stage576p2_jfo_transport_matrix {
  private static final String INPUT =
      "build_stage576p2_moving_structure_sparse_jfo_output_Model.mph";

  private static boolean runCase(Model model, String name, String thickness,
      String gate) {
    try {
      PhysicsFeature ffp = model.component("comp1").physics("tff").feature("ffp1");
      ffp.set("hw1", thickness);
      ffp.set("vw", new String[] {
        "0",
        "-lambda_v574*(" + gate + ")*omega_lid_rot572*Z",
        "lambda_v574*(" + gate + ")*omega_lid_rot572*Y"
      });
      model.sol("sol150").clearSolutionData();
      System.out.println("TRANSPORT_CASE_START=" + name);
      model.sol("sol150").runAll();
      System.out.println("TRANSPORT_CASE_PASS=" + name);
      return true;
    } catch (Exception error) {
      System.out.println("TRANSPORT_CASE_FAIL=" + name);
      System.out.println("TRANSPORT_ERROR=" + error.getMessage());
      return false;
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      model.param().set("gate_speed576p", "1");
      runCase(model, "constant_h_uniform_v", "3[um]", "1");
      runCase(model, "constant_h_mask_v", "3[um]", "M_drain573");
      runCase(model, "constant_h_film_v", "3[um]", "M_drain573*Bfilm573");
      runCase(model, "true_h_uniform_v", "h_calc573", "1");
      runCase(model, "true_h_mask_v", "h_calc573", "M_drain573");
      runCase(model, "true_h_film_v", "h_calc573", "M_drain573*Bfilm573");
      model.param().set("omega_test576p2",
          "theta_slide_total*0.5*pi/T_slide572"
              + "*sin(pi*(t_position576p2-T_pre572)/T_slide572)");
      model.component("comp1").variable("var_dynamic_motion572")
          .set("omega_lid_rot572", "omega_test576p2");
      runCase(model, "constant_h_global_omega", "3[um]", "1");
      runCase(model, "true_h_global_omega", "h_calc573", "1");
      runCase(model, "true_h_film_global_omega", "h_calc573",
          "M_drain573*Bfilm573");
      model.save("probe_stage576p2_jfo_transport_matrix_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
