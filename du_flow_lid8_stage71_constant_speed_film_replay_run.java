import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage71_constant_speed_film_replay_run {
  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi; i++) sum += 0.5 * dt * (values[i - 1] + values[i]);
    return sum;
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_constant_speed_film71");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical(tag).getReal()[0]) {
      if (Double.isFinite(v)) { min = Math.min(min, v); max = Math.max(max, v); }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_constant_speed_film71");
    m.result(tag).create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\152_lid8mm_stage70_constant_speed_structure_results.mph");
    m.label("153_lid8mm_stage71_h3um_constant_speed_film_replay_results.mph");
    System.out.println("RUN_STAGE71=std_tff_gap_qs45 constant-speed smooth-start-stop film replay");
    m.study("std_tff_gap_qs45").run();
    m.result().dataset().create("dset_constant_speed_film71", "Solution");
    m.result().dataset("dset_constant_speed_film71").set("solution", "sol21");
    global(m, "eval71_Wfilm", "W_film", "N");
    global(m, "eval71_Ffilm", "F_film_shear", "N");
    global(m, "eval71_mu_film", "F_film_shear/F_total_target", "1");
    double[] f = m.result().numerical("eval71_Ffilm").getReal()[0];
    double avg = trapz(f, .01, 1, 51) / .50;
    System.out.printf(Locale.US,
        "AVG71_Ffilm_slide=%.12g[N] AVG71_tau_film_slide=%.12g[Pa] AVG71_mu_film_slide=%.12g%n",
        avg, avg / 8e-6, avg / .03);
    plot3(m, "pg71_hfilm", "Stage 71 constant-speed tear-film thickness", "h_film_input", "um");
    plot3(m, "pg71_gap", "Stage 71 constant-speed dynamic gap", "gap_replay_tear", "um");
    plot3(m, "pg71_pfilm", "Stage 71 constant-speed tear-film pressure", "max(pfilm,0)", "Pa");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\153_lid8mm_stage71_h3um_constant_speed_film_replay_results.mph");
    System.out.println("SAVED_STAGE71=153_lid8mm_stage71_h3um_constant_speed_film_replay_results.mph");
  }
}
