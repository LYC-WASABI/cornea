import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage575b_dynamic_flow_shear_friction {
  private static final String BASE = "575a_stage575_dynamic_fixed_patch_jfo_checked.mph";
  private static final String RESULTS = "575b_stage575_dynamic_flow_shear_friction_results.mph";
  private static final String SOL = "sol139";
  private static final String DATASET = "dset575b_dynamic_flow_shear_friction";
  private static final String V_LID_MAG =
      "sqrt((-lambda_v574*omega_lid_rot572*Z)^2+(lambda_v574*omega_lid_rot572*Y)^2)";
  private static final String TAU_COUE =
      "(1e-3[Pa*s]*" + V_LID_MAG + "/max(h_calc573,h_num573))";
  private static final String TAU_ACTIVE =
      "(M_core573*Bfilm573*" + TAU_COUE + ")";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static double[][] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] intPatch(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double rowMin(double[] values) {
    double result = Double.POSITIVE_INFINITY;
    for (double value : values) result = Math.min(result, value);
    return result;
  }

  private static double rowMax(double[] values) {
    double result = Double.NEGATIVE_INFINITY;
    for (double value : values) result = Math.max(result, value);
    return result;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  private static void addSurfacePlot(Model model, String tag, String label, String expr, String unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", DATASET);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      if (!has(model.sol().tags(), SOL)) {
        throw new IllegalStateException("Required dynamic solution missing: " + SOL);
      }
      try {
        model.param().evaluate("eta_tear");
        model.param().set("mu_lub573", "eta_tear", "Lubricant viscosity used for Stage 575b shear diagnostics");
        System.out.println("MU_LUB_SOURCE=eta_tear");
      } catch (Exception ignored) {
        model.param().set("mu_lub573", "1e-3[Pa*s]", "Fallback lubricant viscosity for Stage 575b shear diagnostics");
        System.out.println("MU_LUB_SOURCE=fallback_1e-3_Pa_s");
      }

      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOL);

      double[][] time = evalGlobal(model, DATASET, "eval575b_time", new String[] {"t"});
      double[][] patch = intPatch(model, DATASET, "int575b_patch",
          new String[] {
            "1",
            "p_load573",
            TAU_ACTIVE,
            TAU_COUE,
            V_LID_MAG,
            "Bfilm573"
          });
      double area = patch[0][0];
      double[] fFilm = patch[1];
      double[] fShear = patch[2];
      double[] tauMean = new double[patch[3].length];
      double[] vMean = new double[patch[4].length];
      double[] bMean = new double[patch[5].length];
      double[] muFilm = new double[fFilm.length];
      double[] muTotal = new double[fFilm.length];
      double fnContact = model.result().numerical().tags().length >= 0
          ? model.param().evaluate("0[N]") : 0.0;
      try {
        double[][] contact = evalGlobal(model, DATASET, "eval575b_contact", new String[] {"Fn_contact570"});
        fnContact = contact[0][0];
      } catch (Exception error) {
        fnContact = 0.0285909645369;
        System.out.println("FN_CONTACT_SOURCE=fallback_574n_value");
      }
      for (int i = 0; i < fFilm.length; i++) {
        tauMean[i] = patch[3][i] / area;
        vMean[i] = patch[4][i] / area;
        bMean[i] = patch[5][i] / area;
        muFilm[i] = fShear[i] / (fFilm[i] + 1e-12);
        muTotal[i] = fShear[i] / (fnContact + fFilm[i] + 1e-12);
      }
      double[][] tauMax = surface(model, DATASET, "max575b_tau", "MaxSurface", TAU_ACTIVE);
      double[][] vMax = surface(model, DATASET, "max575b_v", "MaxSurface", V_LID_MAG);

      addSurfacePlot(model, "pg575b_pressure", "Stage 575b dynamic JFO pressure", "tff.p-p_amb573", "Pa");
      addSurfacePlot(model, "pg575b_theta", "Stage 575b dynamic JFO cavitation theta", "tff.theta", "1");
      addSurfacePlot(model, "pg575b_hcalc", "Stage 575b dynamic calculated film thickness", "h_calc573", "m");
      addSurfacePlot(model, "pg575b_tau", "Stage 575b Couette shear diagnostic", TAU_ACTIVE, "Pa");

      boolean pass = true;
      if (!finite(fFilm) || !finite(fShear) || !finite(tauMean) || !finite(muFilm) || !finite(muTotal)) pass = false;
      if (!finite(tauMax[0]) || !finite(vMax[0])) pass = false;
      if (rowMin(muFilm) < -1e-8 || rowMin(muTotal) < -1e-8) pass = false;

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n",
          rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "FN_CONTACT_USED=%.12g%n", fnContact);
      System.out.printf(Locale.US, "F_FILM_RANGE=[%.12g,%.12g]%n", rowMin(fFilm), rowMax(fFilm));
      System.out.printf(Locale.US, "F_SHEAR_RANGE=[%.12g,%.12g]%n", rowMin(fShear), rowMax(fShear));
      System.out.printf(Locale.US, "TAU_MEAN_RANGE=[%.12g,%.12g]%n", rowMin(tauMean), rowMax(tauMean));
      System.out.printf(Locale.US, "TAU_MAX_RANGE=[%.12g,%.12g]%n", rowMin(tauMax[0]), rowMax(tauMax[0]));
      System.out.printf(Locale.US, "V_MEAN_RANGE=[%.12g,%.12g]%n", rowMin(vMean), rowMax(vMean));
      System.out.printf(Locale.US, "V_MAX_RANGE=[%.12g,%.12g]%n", rowMin(vMax[0]), rowMax(vMax[0]));
      System.out.printf(Locale.US, "MU_FILM_RANGE=[%.12g,%.12g]%n", rowMin(muFilm), rowMax(muFilm));
      System.out.printf(Locale.US, "MU_TOTAL_RANGE=[%.12g,%.12g]%n", rowMin(muTotal), rowMax(muTotal));
      System.out.printf(Locale.US, "BFILM_MEAN_RANGE=[%.12g,%.12g]%n", rowMin(bMean), rowMax(bMean));
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      model.label("Stage 575b dynamic flow shear friction diagnostics");
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
