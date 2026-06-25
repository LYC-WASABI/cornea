import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage57_structure_feedback25_interp_run {
  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_structure_feedback57");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical(tag).getReal()[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
  }

  private static void support(Model m) {
    m.result().numerical().create("int57_support", "IntSurface");
    m.result().numerical("int57_support").selection().named("sel_lid_outer_support");
    m.result().numerical("int57_support").set("data", "dset_structure_feedback57");
    m.result().numerical("int57_support").set("expr", new String[] {"-(solid.RFy*Y+solid.RFz*Z)/sqrt(Y^2+Z^2)"});
    m.result().numerical("int57_support").set("unit", new String[] {"N"});
    m.result().numerical("int57_support").setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical("int57_support").getReal()[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "int57_support min=%.12g[N] max=%.12g[N]%n", min, max);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\140_lid8mm_stage57_structure_feedback25_interp_setup.mph");
    m.label("141_lid8mm_stage57_structure_feedback25_interp_results.mph");
    System.out.println("RUN_STAGE57_STUDY=std_partitioned_local_pfilm scale_partitioned_pfilm=0.25 interpolated");
    m.study("std_partitioned_local_pfilm").run();
    if (!Arrays.asList(m.result().dataset().tags()).contains("dset_structure_feedback57")) {
      m.result().dataset().create("dset_structure_feedback57", "Solution");
    }
    m.result().dataset("dset_structure_feedback57").set("solution", "sol20");
    support(m);
    global(m, "eval57_Wfilm", "W_film_replay53", "N");
    global(m, "eval57_Wcontact_budget", "W_contact_budget54", "N");
    global(m, "eval57_Ffriction", "F_friction_budget54", "N");
    global(m, "eval57_mu", "mu_app_budget54", "1");
    global(m, "eval57_dr", "dr_force_mixed54", "mm");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\141_lid8mm_stage57_structure_feedback25_interp_results.mph");
    System.out.println("SAVED_STAGE57_RESULTS=141_lid8mm_stage57_structure_feedback25_interp_results.mph");
  }
}
