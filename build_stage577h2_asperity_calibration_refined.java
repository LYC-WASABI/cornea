import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577h2_asperity_calibration_refined {
  private static final String INPUT = "577g_stage577_contact_or_asperity_boundary_model_results.mph";
  private static final String RESULTS = "577h2_stage577_asperity_calibration_refined_results.mph";
  private static final String DATASET = "dset577h2";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final double FN = 0.03;
  private static final double[] DH_UM = new double[] {2.3, 2.5, 2.8};
  private static final double[] HCRIT_UM = new double[] {0.7, 0.8, 0.9, 1.0};
  private static final double[] K_KPA = new double[] {5.0, 7.5, 10.0, 12.5, 15.0};
  private static final double[] MU = new double[] {0.10, 0.15, 0.20};
  private static final String VSIGN =
      "tanh((lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2))/(1e-6[m/s]))";

  private static String h(double dh) {
    return "max(0.05[um],3[um]-" + dh + "[um]*M_core573)";
  }

  private static String w(double dh, double hcrit) {
    return "M_core573*flc2hs(" + hcrit + "[um]-(" + h(dh) + "),0.1[um])";
  }

  private static String pAsp(double dh, double hcrit, double kKpa) {
    String h = h(dh);
    String w = w(dh, hcrit);
    return "(" + w + ")*" + kKpa + "[kPa]*max((" + hcrit + "[um]-(" + h + "))/1[um],0)";
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

  private static double min(double[] values) {
    double result = Double.POSITIVE_INFINITY;
    for (double value : values) result = Math.min(result, value);
    return result;
  }

  private static double max(double[] values) {
    double result = Double.NEGATIVE_INFINITY;
    for (double value : values) result = Math.max(result, value);
    return result;
  }

  private static double maxAbs(double[] values) {
    double result = 0.0;
    for (double value : values) result = Math.max(result, Math.abs(value));
    return result;
  }

  private static double mean(double[] values) {
    double sum = 0.0;
    for (double value : values) sum += value;
    return sum / values.length;
  }

  private static double meanWhere(double[] values, double[] sign, boolean positive) {
    double sum = 0.0;
    int count = 0;
    for (int i = 0; i < values.length; i++) {
      if ((positive && sign[i] > 1e-6) || (!positive && sign[i] < -1e-6)) {
        sum += values[i];
        count++;
      }
    }
    return count == 0 ? 0.0 : sum / count;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  private static boolean crossesZero(double[] values, double eps) {
    return min(values) < -eps && max(values) > eps;
  }

  private static String csv(double dh, double hcrit, double k, double mu,
      double areaMax, double areaMean, double areaRatioMax, double areaRatioMean,
      double fnMax, double fnMean, double fnRatioMax, double fnRatioMean,
      double ftFluidMax, double ftAspMax, double ftTotalMax, double muFluidMax,
      double muTotalMax, double muTotalMean, double muForwardMean, double muReverseMean,
      double thetaMin, double pfilmMax, double score, String passLevel, String reason) {
    return String.format(Locale.US,
        "%.3f,%.3f,%.3f,%.3f,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%s,%s",
        dh, hcrit, k, mu, areaMax, areaMean, areaRatioMax, areaRatioMean,
        fnMax, fnMean, fnRatioMax, fnRatioMean, ftFluidMax, ftAspMax, ftTotalMax,
        muFluidMax, muTotalMax, muTotalMean, muForwardMean, muReverseMean,
        thetaMin, pfilmMax, score, passLevel, reason);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, "eval577h2_time", new String[] {"t"});
      double[][] base = surface(model, "int577h2_base", "IntSurface",
          new String[] {"1", "tau_tff_signed577a", VSIGN});
      double[][] baseMin = surface(model, "min577h2_base", "MinSurface", new String[] {"tff.theta"});
      double[][] baseMax = surface(model, "max577h2_base", "MaxSurface", new String[] {"tff.p-p_amb573"});
      double areaFilm = base[0][0];
      double[] ftFluid = base[1];
      double[] vSignInt = base[2];
      double[] muFluid = new double[ftFluid.length];
      for (int i = 0; i < ftFluid.length; i++) muFluid[i] = Math.abs(ftFluid[i]) / FN;

      String header = "dh_deplete,h_crit,K_asp_eff,mu_boundary,A_close_max,A_close_mean,A_close_over_Afilm_max,A_close_over_Afilm_mean,Fn_asp_max,Fn_asp_mean,Fn_asp_over_Fnref_max,Fn_asp_over_Fnref_mean,Ft_fluid_max,Ft_asp_max,Ft_total_max,mu_TFF_alt_max,mu_total_max,mu_total_mean,mu_total_forward_mean,mu_total_reverse_mean,theta_min,pfilm_max,PARAM_SCORE,PASS_LEVEL,FAIL_REASON";
      System.out.println("CSV_HEADER=" + header);

      int capacity = DH_UM.length * HCRIT_UM.length * K_KPA.length * MU.length;
      String[] rowLines = new String[capacity];
      String[] bestLines = new String[] {"", "", ""};
      double[] bestScores = new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
      int rowIndex = 0;
      int strongPassCount = 0;
      int candidatePassCount = 0;
      boolean finiteAll = finite(ftFluid) && finite(vSignInt) && finite(baseMin[0]) && finite(baseMax[0]);
      boolean signReversalAny = false;

      for (int idh = 0; idh < DH_UM.length; idh++) {
        double dh = DH_UM[idh];
        for (int ih = 0; ih < HCRIT_UM.length; ih++) {
          double hcrit = HCRIT_UM[ih];
          String w = w(dh, hcrit);
          double[][] winteg = surface(model, "int577h2_w_" + idh + "_" + ih, "IntSurface", new String[] {w});
          double[] areaClose = winteg[0];
          for (int ik = 0; ik < K_KPA.length; ik++) {
            double k = K_KPA[ik];
            String pAsp = pAsp(dh, hcrit, k);
            double[][] integ = surface(model, "int577h2_" + idh + "_" + ih + "_" + ik,
                "IntSurface", new String[] {pAsp, "(" + pAsp + ")*(" + VSIGN + ")"});
            double[] fnAsp = integ[0];
            double[] signedAsp = integ[1];
            for (int imu = 0; imu < MU.length; imu++) {
              double mu = MU[imu];
              double[] ftAsp = new double[ftFluid.length];
              double[] ftTotal = new double[ftFluid.length];
              double[] muTotal = new double[ftFluid.length];
              for (int i = 0; i < ftFluid.length; i++) {
                ftAsp[i] = mu * signedAsp[i];
                ftTotal[i] = ftFluid[i] + ftAsp[i];
                muTotal[i] = Math.abs(ftTotal[i]) / FN;
              }
              boolean finiteRow = finite(areaClose) && finite(fnAsp) && finite(signedAsp) && finite(ftAsp)
                  && finite(ftTotal) && finite(muTotal);
              double areaMax = max(areaClose);
              double areaMean = mean(areaClose);
              double areaRatioMax = areaMax / areaFilm;
              double areaRatioMean = areaMean / areaFilm;
              double fnMax = max(fnAsp);
              double fnMean = mean(fnAsp);
              double fnRatioMax = fnMax / FN;
              double fnRatioMean = fnMean / FN;
              double ftFluidMax = maxAbs(ftFluid);
              double ftAspMax = maxAbs(ftAsp);
              double ftTotalMax = maxAbs(ftTotal);
              double muFluidMax = max(muFluid);
              double muTotalMax = max(muTotal);
              double muTotalMean = mean(muTotal);
              double muForwardMean = meanWhere(muTotal, vSignInt, true);
              double muReverseMean = meanWhere(muTotal, vSignInt, false);
              double thetaMin = min(baseMin[0]);
              double pfilmMax = max(baseMax[0]);
              boolean signOk = crossesZero(ftAsp, 1e-10) && crossesZero(ftTotal, 1e-10);
              boolean strong = finiteRow && muTotalMax > 0.05 && muTotalMax < 0.15
                  && areaRatioMean > 0.001 && areaRatioMean < 0.30
                  && fnRatioMax > 0.0 && fnRatioMax < 1.5 && signOk;
              boolean candidate = finiteRow && muTotalMax > 0.07 && muTotalMax < 0.13
                  && areaRatioMean > 0.02 && areaRatioMean < 0.20
                  && fnRatioMax > 0.0 && fnRatioMax < 1.2
                  && Math.abs(muForwardMean - muReverseMean) < 0.03;
              String passLevel = strong ? "STRONG_PASS" : (candidate ? "CANDIDATE_PASS" : "FAIL");
              String reason = "OK";
              if (!strong && !candidate) {
                reason = "";
                if (!finiteRow) reason += "NUMERIC_FAIL;";
                if (!(muTotalMax > 0.05 && muTotalMax < 0.15)) reason += "COF_FAIL;";
                if (!(areaRatioMean > 0.001 && areaRatioMean < 0.30)) reason += "AREA_FAIL;";
                if (!(fnRatioMax > 0.0 && fnRatioMax < 1.5)) reason += "LOAD_FAIL;";
                if (!signOk) reason += "SIGN_FAIL;";
              }
              double score = Math.abs(muTotalMax - 0.10) / 0.10
                  + Math.abs(areaRatioMean - 0.10) / 0.10
                  + Math.max(fnRatioMax - 1.0, 0.0);
              String line = csv(dh, hcrit, k, mu, areaMax, areaMean, areaRatioMax, areaRatioMean,
                  fnMax, fnMean, fnRatioMax, fnRatioMean, ftFluidMax, ftAspMax, ftTotalMax,
                  muFluidMax, muTotalMax, muTotalMean, muForwardMean, muReverseMean,
                  thetaMin, pfilmMax, score, passLevel, reason);
              System.out.println("CSV_ROW=" + line);
              rowLines[rowIndex] = line;
              rowIndex++;
              finiteAll = finiteAll && finiteRow;
              signReversalAny = signReversalAny || signOk;
              if (strong) strongPassCount++;
              if (candidate) candidatePassCount++;
              if (strong || candidate) {
                for (int b = 0; b < bestScores.length; b++) {
                  if (score < bestScores[b]) {
                    for (int s = bestScores.length - 1; s > b; s--) {
                      bestScores[s] = bestScores[s - 1];
                      bestLines[s] = bestLines[s - 1];
                    }
                    bestScores[b] = score;
                    bestLines[b] = line;
                    break;
                  }
                }
              }
            }
          }
        }
      }

      boolean monotonicMu = true;
      for (int idh = 0; idh < DH_UM.length; idh++) for (int ih = 0; ih < HCRIT_UM.length; ih++) for (int ik = 0; ik < K_KPA.length; ik++) {
        double previous = -1.0;
        for (int imu = 0; imu < MU.length; imu++) {
          double current = findMuMax(rowLines, DH_UM[idh], HCRIT_UM[ih], K_KPA[ik], MU[imu]);
          if (previous >= 0.0 && current + 1e-12 < previous) monotonicMu = false;
          previous = current;
        }
      }
      boolean monotonicK = true;
      for (int idh = 0; idh < DH_UM.length; idh++) for (int ih = 0; ih < HCRIT_UM.length; ih++) for (int imu = 0; imu < MU.length; imu++) {
        double previous = -1.0;
        for (int ik = 0; ik < K_KPA.length; ik++) {
          double current = findMuMax(rowLines, DH_UM[idh], HCRIT_UM[ih], K_KPA[ik], MU[imu]);
          if (previous >= 0.0 && current + 1e-12 < previous) monotonicK = false;
          previous = current;
        }
      }

      boolean pass = finiteAll && (strongPassCount > 0 || candidatePassCount > 0) && signReversalAny && monotonicMu && monotonicK;
      System.out.println("BEST_HEADER=" + header);
      for (int i = 0; i < bestLines.length; i++) {
        if (bestLines[i] != null && bestLines[i].length() > 0) System.out.println("BEST_ROW=" + bestLines[i]);
      }
      addSurfacePlot(model, "pg577h2_pasp_focus", "Stage 577h2 refined asperity pressure candidate",
          pAsp(2.5, 1.0, 7.5), "Pa");

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", min(time[0]), max(time[0]), time[0].length);
      System.out.printf(Locale.US, "A_FILM=%.12g%n", areaFilm);
      System.out.printf(Locale.US, "MU_FLUID_MAX=%.12g%n", max(muFluid));
      System.out.printf(Locale.US, "THETA_MIN=%.12g%n", min(baseMin[0]));
      System.out.printf(Locale.US, "PFILM_MAX=%.12g%n", max(baseMax[0]));
      System.out.println("SCAN_COUNT=" + rowIndex);
      System.out.println("STRONG_PASS_COUNT=" + strongPassCount);
      System.out.println("CANDIDATE_PASS_COUNT=" + candidatePassCount);
      System.out.println("CHECK_FINITE=" + finiteAll);
      System.out.println("CHECK_SIGN_REVERSAL_ANY=" + signReversalAny);
      System.out.println("CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=" + monotonicMu);
      System.out.println("CHECK_MU_MONOTONIC_WITH_K_ASP=" + monotonicK);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577h2 refined asperity calibration " + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }

  private static double findMuMax(String[] lines, double dh, double hcrit, double k, double mu) {
    String prefix = String.format(Locale.US, "%.3f,%.3f,%.3f,%.3f,", dh, hcrit, k, mu);
    for (String line : lines) {
      if (line != null && line.startsWith(prefix)) {
        String[] parts = line.split(",");
        return Double.parseDouble(parts[16]);
      }
    }
    return Double.NaN;
  }
}
