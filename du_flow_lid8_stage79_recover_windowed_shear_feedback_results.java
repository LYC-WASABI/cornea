import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage79_recover_windowed_shear_feedback_results {
  private static void safePlot(Model m, String tag, String label, String expr, String unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_shear_feedback76");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void safeGlobalPlot(Model m, String tag, String label, String[] expr, String[] unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_shear_feedback76");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "C:\\Users\\l1363\\Documents\\复现\\du_flow_lid8_stage76_smooth_shear_feedback_run_results_Model.mph");
    m.label("160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");

    safePlot(m, "pg78_solid_disp_all",
        "Stage 78 displacement: cornea and lid with shear feedback",
        "solid.disp", "mm");
    safePlot(m, "pg78_solid_mises_all",
        "Stage 78 von Mises stress: cornea and lid with shear feedback",
        "solid.mises", "Pa");
    safePlot(m, "pg78_cornea_shear_traction_windowed",
        "Stage 78 applied cornea tangential shear traction window",
        "sqrt((tau_pfilm_shear73*ty_shear73)^2+(tau_pfilm_shear73*tz_shear73)^2)", "Pa");
    safeGlobalPlot(m, "pg78_shear_feedback_force_mu",
        "Stage 78 target shear feedback force and apparent friction coefficient",
        new String[] {"F_shear_feedback73", "mu_shear_feedback73", "tau_nominal_shear73"},
        new String[] {"N", "1", "Pa"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
    System.out.println("SAVED_STAGE78_RECOVERED=160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
  }
}
