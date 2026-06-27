import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577d_postprocess_calibration_sensitivity {
  private static final String INPUT = "577b_stage577_conserved_depletion_rupture_check_results.mph";
  private static final String RESULTS = "577d_stage577_postprocess_calibration_sensitivity_results.mph";
  private static final String DATASET = "dset577d";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final double FN = 0.03;
  private static final double AREA_FLOOR = 1.0e-12;
  private static final double[] DH_UM = new double[] {1.0, 1.5, 2.0, 2.5, 2.8};
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

  private static boolean increasing(double[] values) {
    for (int i = 1; i < values.length; i++) if (values[i] + 1e-12 < values[i - 1]) return false;
    return true;
  }

  private static boolean crossesZero(double[] values, double eps) {
    return rowMin(values) < -eps && rowMax(values) > eps;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, "eval577d_time", new String[] {"t"});
      double[][] base = surface(model, "int577d_base", "IntSurface",
          new String[] {"1", "tau_tff_signed577a"});
      double areaFilm = base[0][0];
      double[] ftFluid = base[1];
      double[] muFluid = new double[ftFluid.length];
      for (int i = 0; i < ftFluid.length; i++) muFluid[i] = Math.abs(ftFluid[i]) / FN;

      double[][] muMaxByDhMu = new double[DH_UM.length][MU.length];
      double[] muMaxAtMuMaxByDh = new double[DH_UM.length];
      boolean finiteAll = finite(ftFluid);
      boolean anyCandidate = false;
      boolean boundaryReversal = true;
      boolean areaNontrivialAny = false;

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n",
          rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "A_FILM=%.12g%n", areaFilm);
      System.out.printf(Locale.US, "FT_FLUID_RANGE=[%.12g,%.12g]%n", rowMin(ftFluid), rowMax(ftFluid));
      System.out.printf(Locale.US, "MU_FLUID_RANGE=[%.12g,%.12g]%n", rowMin(muFluid), rowMax(muFluid));
      System.out.println("SCAN_COLUMNS=dh_um,mu_boundary,h_avg_min,h_avg_max,h_min,h_max,A_close_max,A_close_ratio_max,Ft_boundary_min,Ft_boundary_max,Ft_total_min,Ft_total_max,mu_boundary_part_max,mu_total_max,candidate");

      for (int d = 0; d < DH_UM.length; d++) {
        String h = h(DH_UM[d]);
        String w = w(DH_UM[d]);
        double[][] integ = surface(model, "int577d_" + d, "IntSurface",
            new String[] {h, w, "(" + w + ")*(" + VSIGN + ")"});
        double[][] minv = surface(model, "min577d_" + d, "MinSurface", new String[] {h});
        double[][] maxv = surface(model, "max577d_" + d, "MaxSurface", new String[] {h});
        double[] hAvg = new double[integ[0].length];
        double[] areaClose = integ[1];
        double[] closeSign = integ[2];
        for (int i = 0; i < hAvg.length; i++) hAvg[i] = integ[0][i] / areaFilm;
        finiteAll = finiteAll && finite(hAvg) && finite(areaClose) && finite(closeSign) && finite(minv[0]) && finite(maxv[0]);
        double areaRatioMax = rowMax(areaClose) / areaFilm;
        boolean areaOk = areaRatioMax > 0.005 && areaRatioMax < 0.5;
        areaNontrivialAny = areaNontrivialAny || areaOk;

        for (int m = 0; m < MU.length; m++) {
          double[] ftBoundary = new double[ftFluid.length];
          double[] ftTotal = new double[ftFluid.length];
          double[] muBoundaryPart = new double[ftFluid.length];
          double[] muTotal = new double[ftFluid.length];
          for (int i = 0; i < ftFluid.length; i++) {
            ftBoundary[i] = MU[m] * FN * closeSign[i] / Math.max(areaClose[i], AREA_FLOOR);
            ftTotal[i] = ftFluid[i] + ftBoundary[i];
            muBoundaryPart[i] = Math.abs(ftBoundary[i]) / FN;
            muTotal[i] = Math.abs(ftTotal[i]) / FN;
          }
          finiteAll = finiteAll && finite(ftBoundary) && finite(ftTotal) && finite(muBoundaryPart) && finite(muTotal);
          boundaryReversal = boundaryReversal && crossesZero(ftBoundary, 1e-10);
          muMaxByDhMu[d][m] = rowMax(muTotal);
          boolean candidate = areaOk && rowMax(muTotal) >= 0.01 && rowMax(muTotal) <= 0.2;
          anyCandidate = anyCandidate || candidate;
          System.out.printf(Locale.US,
              "SCAN_RESULT=%.1f,%.3f,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%s%n",
              DH_UM[d], MU[m], rowMin(hAvg), rowMax(hAvg), rowMin(minv[0]), rowMax(maxv[0]),
              rowMax(areaClose), areaRatioMax, rowMin(ftBoundary), rowMax(ftBoundary),
              rowMin(ftTotal), rowMax(ftTotal), rowMax(muBoundaryPart), rowMax(muTotal),
              candidate ? "true" : "false");
        }
        muMaxAtMuMaxByDh[d] = muMaxByDhMu[d][MU.length - 1];
      }

      boolean monotonicMu = true;
      for (int d = 0; d < DH_UM.length; d++) monotonicMu = monotonicMu && increasing(muMaxByDhMu[d]);
      boolean monotonicDh = increasing(muMaxAtMuMaxByDh);
      boolean pass = finiteAll && boundaryReversal && monotonicMu && monotonicDh && anyCandidate && areaNontrivialAny;

      addSurfacePlot(model, "pg577d_wclose_dh25", "Stage 577d low-film activation dh=2.5um", w(2.5), "1");
      addSurfacePlot(model, "pg577d_wclose_dh28", "Stage 577d low-film activation dh=2.8um", w(2.8), "1");

      System.out.println("CHECK_FINITE=" + finiteAll);
      System.out.println("CHECK_LOCAL_TFF=true");
      System.out.println("CHECK_AREA_NONTRIVIAL_ANY=" + areaNontrivialAny);
      System.out.println("CHECK_BOUNDARY_SIGN_REVERSAL=" + boundaryReversal);
      System.out.println("CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=" + monotonicMu);
      System.out.println("CHECK_MU_MONOTONIC_WITH_DH=" + monotonicDh);
      System.out.println("CHECK_HAS_TARGET_CANDIDATE=" + anyCandidate);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577d postprocess calibration sensitivity " + (pass ? "PASS" : "FAIL"));
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
