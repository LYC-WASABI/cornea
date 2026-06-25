import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage200_dynamic_state {
  static void showParam(Model model, String name) {
    try {
      System.out.println("PARAM " + name + "=" + model.param().get(name));
    } catch (Exception error) {
      System.out.println("PARAM " + name + "=MISSING");
    }
  }

  static void showVar(Model model, String group, String name) {
    try {
      System.out.println("VAR " + group + "/" + name + "="
          + model.component("comp1").variable(group).get(name));
    } catch (Exception error) {
      System.out.println("VAR " + group + "/" + name + "=MISSING");
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
    for (String name : new String[] {
        "T_structure_pre", "T_structure_slide", "T_speed_ramp",
        "theta_slide_total", "F_total_target", "delta_h_jfo197"
    }) showParam(model, name);
    for (String name : new String[] {
        "slide_fraction_structure", "phi_lid_structure", "dr_indent119",
        "Fn_contact119", "Ftotal199", "Ferr199", "t_replay"
    }) showVar(model, "var_partitioned_local_pfilm", name);
    for (String name : new String[] {
        "slide_fraction_film_replay", "phi_lid_film_replay",
        "h_jfo197", "tau_film_wall", "vwall_x", "vwall_y", "vwall_z",
        "lid_mask", "omega_lid", "phi_lid"
    }) showVar(model, "var_mixed_lub", name);
    System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
    model.result().dataset().create("probeDset", "Solution");
    model.result().dataset("probeDset").set("solution", "sol49");
    model.result().numerical().create("probeEval", "EvalGlobal");
    model.result().numerical("probeEval").set("data", "probeDset");
    model.result().numerical("probeEval").set(
        "expr",
        new String[] {
          "phi_qs142", "phi_lid_structure", "q_force_total111",
          "dr_indent119", "Fn_contact119", "Wfilm199", "Ftotal199"
        });
    double[][] values = model.result().numerical("probeEval").getReal();
    for (int i = 0; i < values.length; i++) {
      System.out.println("VALUE[" + i + "]=" + values[i][0]);
    }
    ModelUtil.disconnect();
  }
}
