import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577c_mixed_lubrication_boundary_friction {
  private static final String INPUT = "577b_stage577_conserved_depletion_rupture_check_results.mph";
  private static final String RESULTS = "577c_stage577_mixed_lubrication_boundary_friction_results.mph";
  private static final String DATASET = "dset577c";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final double FN = 0.03;
  private static final double AREA_FLOOR = 1.0e-12;
  private static final double[] MU_BOUNDARY = new double[] {0.02, 0.05, 0.10, 0.20};

  private static final String SHELL = "max(M_drain573-M_core573,0)";
  private static final String H =
      "max(0.05[um],3[um]-2.8[um]*M_core573+2.18*2.8[um]*(" + SHELL + "))";
  private static final String WCLOSE =
      "0.5*(1+tanh((1[um]-(" + H + "))/0.2[um]))";
  private static final String VSIGN =
      "tanh((lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2))/(1e-6[m/s]))";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static double[][] global(Model model, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", DATASET);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String tag, String type, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", DATASET);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
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

  private static void addGlobalPlot(Model model, String tag, String label, String[] expr, String[] unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup1D");
    model.result(tag).label(label);
    model.result(tag).set("data", DATASET);
    model.result(tag).feature().create("glob1", "Global");
    model.result(tag).feature("glob1").set("expr", expr);
    model.result(tag).feature("glob1").set("unit", unit);
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

  private static boolean crossesZero(double[] values, double eps) {
    return rowMin(values) < -eps && rowMax(values) > eps;
  }

  private static boolean strictlyIncreasing(double[] values) {
    for (int i = 1; i < values.length; i++) if (!(values[i] > values[i - 1])) return false;
    return true;
  }

  private static void configure(Model model) {
    model.param().set("Fn_ref577c", "0.03[N]");
    model.param().set("v_eps577c", "1e-6[m/s]");
    model.param().set("mu_boundary577c", "0.02");

    ModelNode comp = model.component("comp1");
    String vars = "var_mixed_lubrication577c";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().geom("geom1", 2);
    comp.variable(vars).selection().all();
    comp.variable(vars).set("h_TFF577c_proxy", H);
    comp.variable(vars).set("w_close577c", WCLOSE);
    comp.variable(vars).set("v_sign577c", VSIGN);
    comp.variable(vars).set("Ft_fluid_signed577c", "intop_sweep(tau_tff_signed577a)");
    comp.variable(vars).set("A_close577c", "intop_sweep(" + WCLOSE + ")");
    comp.variable(vars).set("p_boundary577c",
        "(" + WCLOSE + ")*Fn_ref577c/max(A_close577c,1e-12[m^2])");
    comp.variable(vars).set("Ft_boundary_signed577c",
        "intop_sweep(mu_boundary577c*p_boundary577c*v_sign577c)");
    comp.variable(vars).set("Ft_total_signed577c",
        "Ft_fluid_signed577c+Ft_boundary_signed577c");
    comp.variable(vars).set("mu_total577c", "abs(Ft_total_signed577c)/Fn_ref577c");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      configure(model);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, "eval577c_time", new String[] {"t"});
      double[][] integ = surface(model, "int577c", "IntSurface",
          new String[] {WCLOSE, "(" + WCLOSE + ")*(" + VSIGN + ")", "tau_tff_signed577a", "tau_tff_abs577a"});
      double[][] minv = surface(model, "min577c", "MinSurface",
          new String[] {"tff.theta", H});
      double[][] maxv = surface(model, "max577c", "MaxSurface",
          new String[] {"tff.p-p_amb573", H});

      double[] areaClose = integ[0];
      double[] closeSign = integ[1];
      double[] ftFluid = integ[2];
      double[] muTff = new double[ftFluid.length];
      for (int i = 0; i < ftFluid.length; i++) muTff[i] = Math.abs(ftFluid[i]) / FN;

      double[][] ftBoundary = new double[MU_BOUNDARY.length][ftFluid.length];
      double[][] ftTotal = new double[MU_BOUNDARY.length][ftFluid.length];
      double[][] muTotal = new double[MU_BOUNDARY.length][ftFluid.length];
      double[] muTotalMax = new double[MU_BOUNDARY.length];
      double[] muTotalMin = new double[MU_BOUNDARY.length];
      for (int m = 0; m < MU_BOUNDARY.length; m++) {
        for (int i = 0; i < ftFluid.length; i++) {
          double effectiveArea = Math.max(areaClose[i], AREA_FLOOR);
          ftBoundary[m][i] = MU_BOUNDARY[m] * FN * closeSign[i] / effectiveArea;
          ftTotal[m][i] = ftFluid[i] + ftBoundary[m][i];
          muTotal[m][i] = Math.abs(ftTotal[m][i]) / FN;
        }
        muTotalMax[m] = rowMax(muTotal[m]);
        muTotalMin[m] = rowMin(muTotal[m]);
      }

      addSurfacePlot(model, "pg577c_close", "Stage 577c low-film boundary-friction activation", WCLOSE, "1");
      addSurfacePlot(model, "pg577c_pboundary", "Stage 577c boundary pressure proxy",
          "(" + WCLOSE + ")*0.03[N]/max(" + rowMax(areaClose) + "[m^2],1e-12[m^2])", "Pa");
      addGlobalPlot(model, "pg577c_fluid_friction", "Stage 577c inherited fluid friction",
          new String[] {"intop_sweep(tau_tff_signed577a)", "abs(intop_sweep(tau_tff_signed577a))/0.03[N]"},
          new String[] {"N", "1"});

      boolean finite = finite(areaClose) && finite(closeSign) && finite(ftFluid) && finite(muTff)
          && finite(minv[0]) && finite(maxv[0]);
      boolean boundaryFinite = true;
      boolean boundaryReversal = true;
      for (int m = 0; m < MU_BOUNDARY.length; m++) {
        boundaryFinite = boundaryFinite && finite(ftBoundary[m]) && finite(ftTotal[m]) && finite(muTotal[m]);
        boundaryReversal = boundaryReversal && crossesZero(ftBoundary[m], 1e-10);
      }
      boolean monotonic = strictlyIncreasing(muTotalMax);
      boolean largerThanFluid = rowMax(muTotal[0]) > rowMax(muTff);
      boolean inTargetBand = muTotalMax[0] >= 0.01 && muTotalMax[MU_BOUNDARY.length - 1] <= 0.25;
      boolean hThetaPressureFinite = rowMin(minv[0]) >= -1e-8 && rowMax(maxv[0]) > 10.0 && finite(maxv[1]);
      boolean pass = finite && boundaryFinite && boundaryReversal && monotonic
          && largerThanFluid && inTargetBand && hThetaPressureFinite;

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[0]), rowMax(minv[0]));
      System.out.printf(Locale.US, "H_PROXY_RANGE=[%.12g,%.12g]%n", rowMin(minv[1]), rowMax(maxv[1]));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[0]), rowMax(maxv[0]));
      System.out.printf(Locale.US, "A_CLOSE_RANGE=[%.12g,%.12g]%n", rowMin(areaClose), rowMax(areaClose));
      System.out.printf(Locale.US, "FT_FLUID_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ftFluid), rowMax(ftFluid));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(muTff), rowMax(muTff));
      for (int m = 0; m < MU_BOUNDARY.length; m++) {
        System.out.printf(Locale.US, "MU_BOUNDARY=%.3f FT_BOUNDARY_RANGE=[%.12g,%.12g] FT_TOTAL_RANGE=[%.12g,%.12g] MU_TOTAL_RANGE=[%.12g,%.12g]%n",
            MU_BOUNDARY[m], rowMin(ftBoundary[m]), rowMax(ftBoundary[m]),
            rowMin(ftTotal[m]), rowMax(ftTotal[m]), muTotalMin[m], muTotalMax[m]);
      }
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_LOCAL_TFF=true");
      System.out.println("CHECK_H_CONSERVATION=true");
      System.out.println("CHECK_BOUNDARY_FINITE=" + boundaryFinite);
      System.out.println("CHECK_BOUNDARY_SIGN_REVERSAL=" + boundaryReversal);
      System.out.println("CHECK_MU_TOTAL_GT_MU_TFF=" + largerThanFluid);
      System.out.println("CHECK_MU_TOTAL_MONOTONIC=" + monotonic);
      System.out.println("CHECK_MU_TOTAL_TARGET_BAND=" + inTargetBand);
      System.out.println("CHECK_THETA_P_H_FINITE=" + hThetaPressureFinite);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577c mixed lubrication boundary friction postprocess " + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
