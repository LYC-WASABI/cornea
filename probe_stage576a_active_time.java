import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576a_active_time {
  private static final String BASE = "575d_stage575_dynamic_active_gap_regularized_checked.mph";
  private static final String SOL = "sol141";
  private static final String DATASET = "dset576a_active_time_probe";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static double[][] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
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

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static int maxIndex(double[] values) {
    int result = 0;
    for (int i = 1; i < values.length; i++) if (values[i] > values[result]) result = i;
    return result;
  }

  private static int bestCombinedIndex(double[] meanCore, double[] fFilm) {
    double maxCore = meanCore[maxIndex(meanCore)];
    double maxFilm = fFilm[maxIndex(fFilm)];
    int result = 0;
    double best = -Double.MAX_VALUE;
    for (int i = 0; i < meanCore.length; i++) {
      double coreScore = maxCore > 0 ? meanCore[i] / maxCore : 0;
      double filmScore = maxFilm > 0 ? fFilm[i] / maxFilm : 0;
      double score = 0.7 * coreScore + 0.3 * filmScore;
      if (score > best) {
        best = score;
        result = i;
      }
    }
    return result;
  }

  private static void printRow(String label, int i, double[] time, double[] meanCore, double[] meanDrain,
      double[] fFilm, double[] maxP, double[] minTheta, double[] maxH) {
    System.out.printf(Locale.US,
        "%s index=%d time=%.15g MeanCore=%.12g MeanDrain=%.12g Ffilm=%.12g MaxP=%.12g MinTheta=%.12g MaxH=%.12g%n",
        label, i, time[i], meanCore[i], meanDrain[i], fFilm[i], maxP[i], minTheta[i], maxH[i]);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      if (!has(model.sol().tags(), SOL)) throw new IllegalStateException("Missing dynamic solution " + SOL);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOL);

      double[][] timeRaw = evalGlobal(model, DATASET, "eval576a_probe_time", new String[] {"t"});
      double[][] patch = intPatch(model, DATASET, "int576a_probe_patch",
          new String[] {"1", "M_core573", "M_drain573", "p_load573"});
      double[][] maxPRaw = surface(model, DATASET, "max576a_probe_p", "MaxSurface", "tff.p-p_amb573");
      double[][] minThetaRaw = surface(model, DATASET, "min576a_probe_theta", "MinSurface", "tff.theta");
      double[][] maxHRaw = surface(model, DATASET, "max576a_probe_h", "MaxSurface", "h_calc573");

      double area = patch[0][0];
      double[] time = timeRaw[0];
      double[] meanCore = new double[patch[1].length];
      double[] meanDrain = new double[patch[2].length];
      double[] fFilm = patch[3];
      for (int i = 0; i < meanCore.length; i++) {
        meanCore[i] = patch[1][i] / area;
        meanDrain[i] = patch[2][i] / area;
      }
      int coreIndex = maxIndex(meanCore);
      int filmIndex = maxIndex(fFilm);
      int combinedIndex = bestCombinedIndex(meanCore, fFilm);
      System.out.println("BASE=" + BASE);
      System.out.println("SOLUTION=" + SOL);
      System.out.printf(Locale.US, "PATCH_AREA=%.15g%n", area);
      printRow("MAX_CORE", coreIndex, time, meanCore, meanDrain, fFilm, maxPRaw[0], minThetaRaw[0], maxHRaw[0]);
      printRow("MAX_FILM", filmIndex, time, meanCore, meanDrain, fFilm, maxPRaw[0], minThetaRaw[0], maxHRaw[0]);
      printRow("SELECTED", combinedIndex, time, meanCore, meanDrain, fFilm, maxPRaw[0], minThetaRaw[0], maxHRaw[0]);
      System.out.printf(Locale.US, "T_ACTIVE576A=%.15g[s]%n", time[combinedIndex]);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
