import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage62_closedloop100_film_replay_run {
  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_closedloop_film62");
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

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_closedloop_film62");
    m.result(tag).create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\147_lid8mm_stage61_structure_feedback100_interp_results.mph");
    m.label("148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    System.out.println("RUN_STAGE62_STUDY=std_tff_gap_qs45");
    m.study("std_tff_gap_qs45").run();
    m.result().dataset().create("dset_closedloop_film62", "Solution");
    m.result().dataset("dset_closedloop_film62").set("solution", "sol21");
    global(m, "eval62_Wfilm", "W_film", "N");
    global(m, "eval62_Wcontact_budget", "max(F_total_target-W_film,0)", "N");
    global(m, "eval62_Wtotal", "W_film+max(F_total_target-W_film,0)", "N");
    global(m, "eval62_Ffilm_shear", "F_film_shear", "N");
    global(m, "eval62_Ffriction", "F_film_shear+0.02*max(F_total_target-W_film,0)", "N");
    global(m, "eval62_mu", "(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target", "1");
    plot3(m, "pg62_hfilm", "Stage 62 full feedback tear-film thickness h0 plus gap plus roughness", "h_film_input", "um");
    plot3(m, "pg62_gap", "Stage 62 full feedback dynamic positive pair gap", "gap_replay_tear", "um");
    plot3(m, "pg62_pfilm", "Stage 62 full feedback tear-film pressure", "max(pfilm,0)", "Pa");
    m.result().create("pg62_loadshare", "PlotGroup1D");
    m.result("pg62_loadshare").label("Stage 62 full feedback 0.03 N load sharing");
    m.result("pg62_loadshare").set("data", "dset_closedloop_film62");
    m.result("pg62_loadshare").create("glob1", "Global");
    m.result("pg62_loadshare").feature("glob1").set("expr", new String[] {"W_film", "max(F_total_target-W_film,0)", "W_film+max(F_total_target-W_film,0)"});
    m.result().create("pg62_friction", "PlotGroup1D");
    m.result("pg62_friction").label("Stage 62 full feedback friction force and coefficient");
    m.result("pg62_friction").set("data", "dset_closedloop_film62");
    m.result("pg62_friction").create("glob1", "Global");
    m.result("pg62_friction").feature("glob1").set("expr", new String[] {"F_film_shear", "F_film_shear+0.02*max(F_total_target-W_film,0)", "(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    System.out.println("SAVED_STAGE62=148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
  }
}
