import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage92_rq0p5_film_replay_run {
  private static String lastSolution(Model m, String[] before) {
    List<String> old = Arrays.asList(before);
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi && i < values.length; i++) {
      sum += 0.5 * dt * (values[i - 1] + values[i]);
    }
    return sum;
  }

  private static void printRange(String label, double[] values, String unit) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (double v : values) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", label, min, unit, max, unit);
    if (values.length > 51) {
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n",
          label, trapz(values, .01, 1, 51) / .50, unit);
    }
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void surface(Model m, String type, String tag, String expr, String unit) {
    m.result().numerical().create(tag, type);
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_rq0p5_film92");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void plot1(Model m, String tag, String label, String[] expr, String[] unit) {
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_rq0p5_film92");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\169_lid8mm_stage91_lid_roughness_0p5um_setup.mph");
    m.label("170_lid8mm_stage92_rq0p5um_film_replay_results.mph");
    try {
      m.component("comp1").variable("var_partitioned_local_pfilm").remove("p_boundary_nominal90");
    } catch (Exception ignored) {}

    String lambda = "h_film_input/Rq_eq";
    String fasp = "if((" + lambda + ")<=1,1,if((" + lambda + ")>=3,0,0.5*(1+cos(pi*((" + lambda + ")-1)/2))))";
    String cfilm = "if(h_film_input<=0.5[um],0,if(h_film_input>=1.0[um],1,0.5-0.5*cos(pi*(h_film_input-0.5[um])/(0.5[um]))))";
    String fbreak = "1-(" + cfilm + ")";

    String[] before = m.sol().tags();
    System.out.println("RUN_STAGE92=std_tff_gap_qs45 with Rq_lid=0.5um");
    m.study("std_tff_gap_qs45").run();
    String sol = "sol21";
    System.out.println("STAGE92_SOLUTION=" + sol);

    if (Arrays.asList(m.result().dataset().tags()).contains("dset_rq0p5_film92")) {
      m.result().dataset().remove("dset_rq0p5_film92");
    }
    m.result().dataset().create("dset_rq0p5_film92", "Solution");
    m.result().dataset("dset_rq0p5_film92").set("solution", sol);

    surface(m, "MinSurface", "min92_hfilm", "h_film_input", "um");
    surface(m, "MaxSurface", "max92_hfilm", "h_film_input", "um");
    surface(m, "MinSurface", "min92_lambda", lambda, "1");
    surface(m, "MaxSurface", "max92_fasp", fasp, "1");
    surface(m, "MaxSurface", "max92_fbreak", fbreak, "1");
    surface(m, "MaxSurface", "max92_pfilm", "max(pfilm,0)", "Pa");
    global(m, "eval92_Wfilm", "W_film", "N");
    global(m, "eval92_Ffilm", "F_film_shear", "N");
    global(m, "eval92_mu_film", "F_film_shear/F_total_target", "1");

    plot3(m, "pg92_hfilm", "Stage 92 h film with Rq_lid 0.5 um", "h_film_input", "um");
    plot3(m, "pg92_lambda", "Stage 92 local lambda with Rq_lid 0.5 um", lambda, "1");
    plot3(m, "pg92_fasp", "Stage 92 asperity weight with Rq_lid 0.5 um", fasp, "1");
    plot3(m, "pg92_fbreak", "Stage 92 film rupture weight with Rq_lid 0.5 um", fbreak, "1");
    plot3(m, "pg92_pfilm", "Stage 92 tear-film pressure with Rq_lid 0.5 um", "max(pfilm,0)", "Pa");
    plot1(m, "pg92_film_force_mu", "Stage 92 film load, shear force, and film-only friction",
        new String[] {"W_film", "F_film_shear", "F_film_shear/F_total_target"},
        new String[] {"N", "N", "1"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\170_lid8mm_stage92_rq0p5um_film_replay_results.mph");
    System.out.println("SAVED_STAGE92=170_lid8mm_stage92_rq0p5um_film_replay_results.mph");
  }
}
