import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage67_correct_fullslide_film_replay_run {
  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_fullslide_film67");
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

  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi; i++) sum += 0.5 * dt * (values[i - 1] + values[i]);
    return sum;
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_fullslide_film67");
    m.result(tag).create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    m.label("149_lid8mm_stage67_h3um_corrected_fullslide_film_replay_results.mph");

    String vars = "var_mixed_lub";
    m.component("comp1").variable(vars).set("slide_fraction_film_replay",
        "if(t_replay<T_structure_pre,0,if(t_replay<T_structure_pre+T_structure_slide,0.5-0.5*cos(pi*(t_replay-T_structure_pre)/T_structure_slide),1))");
    m.component("comp1").variable(vars).set("omega_lid",
        "theta_slide_total*if(t_replay<T_structure_pre,0,if(t_replay<T_structure_pre+T_structure_slide,0.5*pi/T_structure_slide*sin(pi*(t_replay-T_structure_pre)/T_structure_slide),0))");

    System.out.println("RUN_STAGE67_STUDY=std_tff_gap_qs45 corrected full structural slide replay");
    m.study("std_tff_gap_qs45").run();
    m.result().dataset().create("dset_fullslide_film67", "Solution");
    m.result().dataset("dset_fullslide_film67").set("solution", "sol21");
    global(m, "eval67_Wfilm", "W_film", "N");
    global(m, "eval67_Ffilm_shear", "F_film_shear", "N");
    global(m, "eval67_mu_film", "F_film_shear/F_total_target", "1");

    double[] force = m.result().numerical("eval67_Ffilm_shear").getReal()[0];
    double dt = .01;
    double avgAll = trapz(force, dt, 0, force.length - 1) / (dt * (force.length - 1));
    double avgSlide = trapz(force, dt, 1, 51) / .50;
    System.out.printf(Locale.US, "AVG67_Ffilm_all=%.12g[N] AVG67_tau_all=%.12g[Pa]%n", avgAll, avgAll / 8e-6);
    System.out.printf(Locale.US, "AVG67_Ffilm_slide=%.12g[N] AVG67_tau_slide=%.12g[Pa]%n", avgSlide, avgSlide / 8e-6);

    plot3(m, "pg67_hfilm", "Stage 67 corrected full-slide tear-film thickness", "h_film_input", "um");
    plot3(m, "pg67_gap", "Stage 67 corrected full-slide dynamic gap", "gap_replay_tear", "um");
    plot3(m, "pg67_pfilm", "Stage 67 corrected full-slide tear-film pressure", "max(pfilm,0)", "Pa");
    m.result().create("pg67_friction", "PlotGroup1D");
    m.result("pg67_friction").label("Stage 67 corrected full-slide film shear and coefficient");
    m.result("pg67_friction").set("data", "dset_fullslide_film67");
    m.result("pg67_friction").create("glob1", "Global");
    m.result("pg67_friction").feature("glob1").set("expr",
        new String[] {"F_film_shear", "F_film_shear/F_total_target"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\149_lid8mm_stage67_h3um_corrected_fullslide_film_replay_results.mph");
    System.out.println("SAVED_STAGE67=149_lid8mm_stage67_h3um_corrected_fullslide_film_replay_results.mph");
  }
}
