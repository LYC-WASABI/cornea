import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage47_h3um_gap_postprocessing {
  private static final String DATA = "dset_tff_gap_qs46";

  private static void printRange(String label, double[][] values, String unit) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(java.util.Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n",
        label, min, unit, max, unit);
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", DATA);
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
    printRange(tag, model.result().numerical(tag).getReal(), unit);
  }

  private static void surface(Model model, String type, String tag, String expr, String unit) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", DATA);
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
    printRange(tag, model.result().numerical(tag).getReal(), unit);
  }

  private static void surfacePlot(Model model, String tag, String label, String expr, String unit) {
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", DATA);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\123_lid8mm_stage46_h3um_gap_rectangular_footprint_results.mph");
    model.label("124_lid8mm_stage47_h3um_dynamic_gap_quasisteady_final_results.mph");

    String vars = "var_mixed_lub";
    model.component("comp1").variable(vars).set("gap_pair_raw_requested",
        "withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,t_replay))");
    model.component("comp1").variable(vars).set("h_requested_raw",
        "h0_tear+max(gap_pair_raw_requested,0)+Rq_eq");

    surface(model, "MinSurface", "min47_hfilm", "h_film_input", "um");
    surface(model, "MaxSurface", "max47_hfilm", "h_film_input", "um");
    surface(model, "MinSurface", "min47_gap", "gap_replay_tear", "um");
    surface(model, "MaxSurface", "max47_gap", "gap_replay_tear", "um");
    surface(model, "MaxSurface", "max47_pfilm", "max(pfilm,0)", "Pa");
    global(model, "eval47_Wfilm", "W_film", "N");
    global(model, "eval47_Ffilm_shear", "F_film_shear", "N");
    global(model, "eval47_mu_film", "F_film_shear/F_total_target", "1");

    surfacePlot(model, "pg47_hfilm", "Stage 47 tear-film thickness: h0 + pair gap + roughness",
        "h_film_input", "um");
    surfacePlot(model, "pg47_gap", "Stage 47 regularized positive pair gap",
        "gap_replay_tear", "um");
    surfacePlot(model, "pg47_pfilm", "Stage 47 tear-film pressure",
        "max(pfilm,0)", "Pa");

    model.result().create("pg47_load_shear", "PlotGroup1D");
    model.result("pg47_load_shear").label("Stage 47 tear-film load and shear force");
    model.result("pg47_load_shear").set("data", DATA);
    model.result("pg47_load_shear").feature().create("glob1", "Global");
    model.result("pg47_load_shear").feature("glob1").set("expr",
        new String[]{"W_film", "F_film_shear"});
    model.result("pg47_load_shear").feature("glob1").set("unit", new String[]{"N", "N"});

    model.result().create("pg47_mu", "PlotGroup1D");
    model.result("pg47_mu").label("Stage 47 apparent friction coefficient from tear-film shear");
    model.result("pg47_mu").set("data", DATA);
    model.result("pg47_mu").feature().create("glob1", "Global");
    model.result("pg47_mu").feature("glob1").set("expr",
        new String[]{"F_film_shear/F_total_target"});
    model.result("pg47_mu").feature("glob1").set("unit", new String[]{"1"});

    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\124_lid8mm_stage47_h3um_dynamic_gap_quasisteady_final_results.mph");
    System.out.println("SAVED_STAGE47_FINAL=124_lid8mm_stage47_h3um_dynamic_gap_quasisteady_final_results.mph");
  }
}
