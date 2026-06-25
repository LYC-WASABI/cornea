import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;

public class probe_stage576p2_jfo_boundary_transport {
  private static final String INPUT =
      "build_stage576p2_moving_structure_sparse_jfo_output_Model.mph";
  private static final int[] BOUNDARIES = new int[] {6, 7, 10, 15, 16, 18};

  private static boolean run(Model model, String name, int[] boundaries,
      String[] velocity) {
    try {
      ModelNode comp = model.component("comp1");
      comp.physics("tff").selection().set(boundaries);
      comp.physics("tff").feature("ms_vent573").selection().set(boundaries);
      comp.physics("tff").feature("wc_open_anchor573").selection().set(boundaries);
      PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
      ffp.set("hw1", "3[um]");
      ffp.set("vw", velocity);
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression",
          "-kanchor576p*(pfilm-p_amb573)*test(pfilm)");
      model.sol("sol150").clearSolutionData();
      System.out.println("BOUNDARY_CASE_START=" + name);
      model.sol("sol150").runAll();
      System.out.println("BOUNDARY_CASE_PASS=" + name);
      return true;
    } catch (Exception error) {
      System.out.println("BOUNDARY_CASE_FAIL=" + name);
      System.out.println("BOUNDARY_ERROR=" + error.getMessage());
      return false;
    }
  }

  private static boolean runGauge(Model model, String name, String gauge) {
    try {
      ModelNode comp = model.component("comp1");
      comp.physics("tff").selection().set(BOUNDARIES);
      comp.physics("tff").feature("ms_vent573").selection().set(BOUNDARIES);
      comp.physics("tff").feature("wc_open_anchor573").selection().set(BOUNDARIES);
      PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
      ffp.set("hw1", "h_calc573");
      ffp.set("vw", new String[] {
        "0", "-omega_lid_rot572*Z", "omega_lid_rot572*Y"
      });
      model.param().set("kgauge576p2", gauge + "[kg/(m^2*s*Pa)]");
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression",
          "-(kanchor576p*(1-M_drain573)+kgauge576p2)"
              + "*(pfilm-p_amb573)*test(pfilm)");
      model.sol("sol150").clearSolutionData();
      System.out.println("GAUGE_CASE_START=" + name + " value=" + gauge);
      model.sol("sol150").runAll();
      System.out.println("GAUGE_CASE_PASS=" + name);
      return true;
    } catch (Exception error) {
      System.out.println("GAUGE_CASE_FAIL=" + name);
      System.out.println("GAUGE_ERROR=" + error.getMessage());
      return false;
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      model.param().set("omega_test576p2",
          "theta_slide_total*0.5*pi/T_slide572"
              + "*sin(pi*(t_position576p2-T_pre572)/T_slide572)");
      System.out.println("T_POSITION=" + model.param().evaluate("t_position576p2"));
      System.out.println("OMEGA_GLOBAL=" + model.param().evaluate("omega_test576p2"));
      String[] constantVelocity = new String[] {"0", "0.1[m/s]", "0"};
      run(model, "full_literal_velocity", BOUNDARIES, constantVelocity);
      run(model, "full_material_rotation", BOUNDARIES,
          new String[] {"0", "-20[1/s]*Z", "20[1/s]*Y"});
      run(model, "full_global_rotation", BOUNDARIES,
          new String[] {"0", "-omega_test576p2*Z", "omega_test576p2*Y"});
      run(model, "full_original_rotation", BOUNDARIES,
          new String[] {"0", "-omega_lid_rot572*Z", "omega_lid_rot572*Y"});
      run(model, "full_spatial_rotation", BOUNDARIES,
          new String[] {"0", "-20[1/s]*z", "20[1/s]*y"});
      runGauge(model, "gauge_1e_10", "1e-10");
      runGauge(model, "gauge_1e_8", "1e-8");
      runGauge(model, "gauge_1e_6", "1e-6");
      runGauge(model, "gauge_1e_4", "1e-4");
      for (int boundary : BOUNDARIES) {
        run(model, "boundary_" + boundary, new int[] {boundary},
            constantVelocity);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
