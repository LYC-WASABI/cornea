import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage95_break_threshold_sweep {
  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi && i < values.length; i++) {
      sum += 0.5 * dt * (values[i - 1] + values[i]);
    }
    return sum;
  }

  private static double slidingAvg(double[] values) {
    if (values.length > 51) return trapz(values, .01, 1, 51) / .50;
    double sum = 0;
    int n = 0;
    for (double v : values) {
      if (Double.isFinite(v)) {
        sum += v;
        n++;
      }
    }
    return n > 0 ? sum / n : Double.NaN;
  }

  private static double globalAvg(Model m, String tag, String expr, String unit) {
    if (Arrays.asList(m.result().numerical().tags()).contains(tag)) {
      try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    return slidingAvg(m.result().numerical(tag).getReal()[0]);
  }

  private static String cfilm(double lowUm, double highUm) {
    return String.format(Locale.US,
        "if(h_film_input<=%.6g[um],0,if(h_film_input>=%.6g[um],1,0.5-0.5*cos(pi*(h_film_input-%.6g[um])/(%.6g[um]))))",
        lowUm, highUm, lowUm, highUm - lowUm);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_rq0p5_film92");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void plot1(Model m, String tag, String label, String[] expr, String[] unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
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
        "D:\\COMSOL_Outputs\\models\\du\\flow\\170_lid8mm_stage92_rq0p5um_film_replay_results.mph");
    m.label("173_lid8mm_stage95_rq0p5um_break_threshold_sweep_results.mph");

    double[][] windows = new double[][] {
      {3.0, 4.0},
      {3.5, 4.5},
      {4.0, 5.0},
      {4.5, 5.5},
      {5.0, 6.0}
    };

    double bestScore = Double.POSITIVE_INFINITY;
    double bestLow = 3.0, bestHigh = 4.0, bestMu = 0, bestFb = 0, bestFtotal = 0, bestFboundary = 0;
    String bestC = "", bestFbreak = "", bestFboundaryExpr = "", bestFtotalExpr = "";
    String pnom = "F_total_target/A_contact_nominal73";

    for (int i = 0; i < windows.length; i++) {
      double low = windows[i][0];
      double high = windows[i][1];
      String c = cfilm(low, high);
      String fbreak = "1-(" + c + ")";
      String fboundary = "intop_film(mu_boundary_break90*" + pnom + "*(" + fbreak + "))";
      String ftotal = "F_film_shear+" + fboundary;
      double fbAvg = globalAvg(m, "eval95_fbavg_" + i, "intop_film(" + fbreak + ")/A_contact_nominal73", "1");
      double fbndAvg = globalAvg(m, "eval95_fbnd_" + i, fboundary, "N");
      double ftotAvg = globalAvg(m, "eval95_ftot_" + i, ftotal, "N");
      double muAvg = globalAvg(m, "eval95_mu_" + i, "(" + ftotal + ")/F_total_target", "1");
      double score = Math.abs(muAvg - 0.1);
      System.out.printf(Locale.US,
          "BREAK_WINDOW low=%.3f[um] high=%.3f[um] fbreak_avg=%.12g Fboundary_avg=%.12g[N] Ftotal_avg=%.12g[N] mu_avg=%.12g%n",
          low, high, fbAvg, fbndAvg, ftotAvg, muAvg);
      if (score < bestScore) {
        bestScore = score;
        bestLow = low;
        bestHigh = high;
        bestMu = muAvg;
        bestFb = fbAvg;
        bestFtotal = ftotAvg;
        bestFboundary = fbndAvg;
        bestC = c;
        bestFbreak = fbreak;
        bestFboundaryExpr = fboundary;
        bestFtotalExpr = ftotal;
      }
    }

    m.param().set("h_break_low", String.format(Locale.US, "%.6g[um]", bestLow),
        "Calibrated effective film rupture lower threshold");
    m.param().set("h_break_high", String.format(Locale.US, "%.6g[um]", bestHigh),
        "Calibrated effective film rupture upper threshold");

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("C_film_break95", bestC);
    m.component("comp1").variable(mv).set("f_break95", bestFbreak);
    m.component("comp1").variable(mv).set("F_boundary_break95", bestFboundaryExpr);
    m.component("comp1").variable(mv).set("F_total_break95", bestFtotalExpr);
    m.component("comp1").variable(mv).set("mu_break95", "F_total_break95/F_total_target");

    plot3(m, "pg95_fbreak", "Stage 95 calibrated film rupture weight", bestFbreak, "1");
    plot3(m, "pg95_hfilm", "Stage 95 h film", "h_film_input", "um");
    plot1(m, "pg95_break_force_mu",
        "Stage 95 calibrated film + boundary shear force and friction coefficient",
        new String[] {"F_film_shear", bestFboundaryExpr, bestFtotalExpr, "(" + bestFtotalExpr + ")/F_total_target"},
        new String[] {"N", "N", "N", "1"});

    System.out.printf(Locale.US,
        "BEST_BREAK_WINDOW low=%.3f[um] high=%.3f[um] fbreak_avg=%.12g Fboundary_avg=%.12g[N] Ftotal_avg=%.12g[N] mu_avg=%.12g%n",
        bestLow, bestHigh, bestFb, bestFboundary, bestFtotal, bestMu);

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\173_lid8mm_stage95_rq0p5um_break_threshold_sweep_results.mph");
    System.out.println("SAVED_STAGE95=173_lid8mm_stage95_rq0p5um_break_threshold_sweep_results.mph");
  }
}
