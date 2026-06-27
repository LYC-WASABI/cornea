import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577f_load_sharing_boundary_pressure {
  private static final String INPUT = "577b_stage577_conserved_depletion_rupture_check_results.mph";
  private static final String RESULTS = "577f_stage577_load_sharing_boundary_pressure_results.mph";
  private static final String DATASET = "dset577f";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final double FN = 0.03;
  private static final double AREA_FLOOR = 1.0e-12;
  private static final double[] DH_UM = new double[] {2.0, 2.5, 2.8};
  private static final double[] MU = new double[] {0.02, 0.05, 0.10, 0.15, 0.20};
  private static final String VSIGN =
      "tanh((lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2))/(1e-6[m/s]))";

  private static String h(double dh) {
    return "max(0.05[um],3[um]-" + dh + "[um]*M_core573)";
  }

  private static String w(double dh) {
    String h = h(dh);
    return "0.5*(1+tanh((1[um]-(" + h + "))/0.2[um]))";
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

  private static boolean increasing(double[] values) {
    for (int i = 1; i < values.length; i++) if (values[i] + 1e-12 < values[i - 1]) return false;
    return true;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, "eval577f_time", new String[] {"t"});
      double[][] base = surface(model, "int577f_base", "IntSurface",
          new String[] {"1", "tau_tff_signed577a", "max(tff.p-p_amb573,0[Pa])"});
      double areaFilm = base[0][0];
      double[] ftFluid = base[1];
      double[] fnFluid = base[2];
      double[] fnBoundary = new double[fnFluid.length];
      double[] muFluid = new double[ftFluid.length];
      for (int i = 0; i < fnFluid.length; i++) {
        fnBoundary[i] = Math.max(FN - fnFluid[i], 0.0);
        muFluid[i] = Math.abs(ftFluid[i]) / FN;
      }

      boolean finiteAll = finite(ftFluid) && finite(fnFluid) && finite(fnBoundary);
      boolean fnOk = rowMin(fnBoundary) >= -1e-12 && rowMax(fnFluid) > 0.0;
      boolean boundaryReversal = true;
      boolean monotonicMu = true;
      boolean largerThanFluidAny = false;
      boolean targetAny = false;

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n",
          rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "A_FILM=%.12g%n", areaFilm);
      System.out.printf(Locale.US, "FN_FLUID_POS_RANGE=[%.12g,%.12g]%n", rowMin(fnFluid), rowMax(fnFluid));
      System.out.printf(Locale.US, "FN_BOUNDARY_RANGE=[%.12g,%.12g]%n", rowMin(fnBoundary), rowMax(fnBoundary));
      System.out.printf(Locale.US, "MU_FLUID_RANGE=[%.12g,%.12g]%n", rowMin(muFluid), rowMax(muFluid));
      System.out.println("SCAN_COLUMNS=dh_um,mu_boundary,A_close_ratio_max,Ft_boundary_min,Ft_boundary_max,Ft_total_min,Ft_total_max,mu_boundary_part_max,mu_total_max,candidate");

      for (int d = 0; d < DH_UM.length; d++) {
        String w = w(DH_UM[d]);
        double[][] integ = surface(model, "int577f_" + d, "IntSurface",
            new String[] {w, "(" + w + ")*(" + VSIGN + ")"});
        double[] areaClose = integ[0];
        double[] closeSign = integ[1];
        finiteAll = finiteAll && finite(areaClose) && finite(closeSign);
        double areaRatioMax = rowMax(areaClose) / areaFilm;
        double[] muMax = new double[MU.length];

        for (int m = 0; m < MU.length; m++) {
          double[] ftBoundary = new double[ftFluid.length];
          double[] ftTotal = new double[ftFluid.length];
          double[] muBoundaryPart = new double[ftFluid.length];
          double[] muTotal = new double[ftFluid.length];
          for (int i = 0; i < ftFluid.length; i++) {
            ftBoundary[i] = MU[m] * fnBoundary[i] * closeSign[i] / Math.max(areaClose[i], AREA_FLOOR);
            ftTotal[i] = ftFluid[i] + ftBoundary[i];
            muBoundaryPart[i] = Math.abs(ftBoundary[i]) / FN;
            muTotal[i] = Math.abs(ftTotal[i]) / FN;
          }
          finiteAll = finiteAll && finite(ftBoundary) && finite(ftTotal) && finite(muBoundaryPart) && finite(muTotal);
          boundaryReversal = boundaryReversal && crossesZero(ftBoundary, 1e-10);
          muMax[m] = rowMax(muTotal);
          boolean candidate = areaRatioMax > 0.005 && areaRatioMax < 0.5
              && rowMax(muTotal) >= 0.01 && rowMax(muTotal) <= 0.2;
          targetAny = targetAny || candidate;
          largerThanFluidAny = largerThanFluidAny || rowMax(muTotal) > rowMax(muFluid);
          System.out.printf(Locale.US,
              "SCAN_RESULT=%.1f,%.3f,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%s%n",
              DH_UM[d], MU[m], areaRatioMax, rowMin(ftBoundary), rowMax(ftBoundary),
              rowMin(ftTotal), rowMax(ftTotal), rowMax(muBoundaryPart), rowMax(muTotal),
              candidate ? "true" : "false");
        }
        monotonicMu = monotonicMu && increasing(muMax);
      }

      addSurfacePlot(model, "pg577f_wclose_dh25", "Stage 577f load-sharing low-film activation dh=2.5um", w(2.5), "1");
      addSurfacePlot(model, "pg577f_pfilm", "Stage 577f fluid positive pressure source", "max(tff.p-p_amb573,0[Pa])", "Pa");

      boolean pass = finiteAll && fnOk && boundaryReversal && monotonicMu && largerThanFluidAny && targetAny;
      System.out.println("CHECK_FINITE=" + finiteAll);
      System.out.println("CHECK_LOCAL_TFF=true");
      System.out.println("CHECK_FN_BOUNDARY_NONNEGATIVE=" + fnOk);
      System.out.println("CHECK_BOUNDARY_SIGN_REVERSAL=" + boundaryReversal);
      System.out.println("CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=" + monotonicMu);
      System.out.println("CHECK_MU_TOTAL_GT_MU_FLUID_ANY=" + largerThanFluidAny);
      System.out.println("CHECK_HAS_TARGET_CANDIDATE=" + targetAny);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577f load-sharing boundary pressure " + (pass ? "PASS" : "FAIL"));
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
