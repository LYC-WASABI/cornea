import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage96_high_break_threshold_sweep {
  private static double trapz(double[] v, double dt, int lo, int hi) {
    double s = 0;
    for (int i = lo + 1; i <= hi && i < v.length; i++) s += 0.5 * dt * (v[i - 1] + v[i]);
    return s;
  }

  private static double avg(double[] v) {
    if (v.length > 51) return trapz(v, .01, 1, 51) / .50;
    double s = 0;
    int n = 0;
    for (double x : v) if (Double.isFinite(x)) { s += x; n++; }
    return n > 0 ? s / n : Double.NaN;
  }

  private static double ge(Model m, String tag, String expr, String unit) {
    if (Arrays.asList(m.result().numerical().tags()).contains(tag)) {
      try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    return avg(m.result().numerical(tag).getReal()[0]);
  }

  private static String cfilm(double lo, double hi) {
    return String.format(Locale.US,
        "if(h_film_input<=%.6g[um],0,if(h_film_input>=%.6g[um],1,0.5-0.5*cos(pi*(h_film_input-%.6g[um])/(%.6g[um]))))",
        lo, hi, lo, hi - lo);
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
    m.label("174_lid8mm_stage96_rq0p5um_high_break_threshold_sweep_results.mph");

    double[][] windows = {{6, 7}, {7, 8}, {8, 9}, {9, 10}, {10, 12}, {12, 14}};
    String pnom = "F_total_target/A_contact_nominal73";
    double bestScore = Double.POSITIVE_INFINITY;
    double bestLo = 6, bestHi = 7, bestMu = 0, bestFb = 0, bestFbnd = 0, bestFtot = 0;
    String bestC = "", bestFbreak = "", bestFbndExpr = "", bestFtotExpr = "";

    for (int i = 0; i < windows.length; i++) {
      double lo = windows[i][0], hi = windows[i][1];
      String c = cfilm(lo, hi);
      String fbreak = "1-(" + c + ")";
      String fbnd = "intop_film(mu_boundary_break90*" + pnom + "*(" + fbreak + "))";
      String ftot = "F_film_shear+" + fbnd;
      double fbAvg = ge(m, "eval96_fbavg_" + i, "intop_film(" + fbreak + ")/A_contact_nominal73", "1");
      double fbndAvg = ge(m, "eval96_fbnd_" + i, fbnd, "N");
      double ftotAvg = ge(m, "eval96_ftot_" + i, ftot, "N");
      double muAvg = ge(m, "eval96_mu_" + i, "(" + ftot + ")/F_total_target", "1");
      System.out.printf(Locale.US,
          "HIGH_BREAK_WINDOW low=%.3f[um] high=%.3f[um] fbreak_avg=%.12g Fboundary_avg=%.12g[N] Ftotal_avg=%.12g[N] mu_avg=%.12g%n",
          lo, hi, fbAvg, fbndAvg, ftotAvg, muAvg);
      double score = Math.abs(muAvg - 0.1);
      if (score < bestScore) {
        bestScore = score;
        bestLo = lo; bestHi = hi; bestMu = muAvg; bestFb = fbAvg; bestFbnd = fbndAvg; bestFtot = ftotAvg;
        bestC = c; bestFbreak = fbreak; bestFbndExpr = fbnd; bestFtotExpr = ftot;
      }
    }

    m.param().set("h_break_low", String.format(Locale.US, "%.6g[um]", bestLo),
        "Calibrated effective film rupture lower threshold");
    m.param().set("h_break_high", String.format(Locale.US, "%.6g[um]", bestHi),
        "Calibrated effective film rupture upper threshold");

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("C_film_break96", bestC);
    m.component("comp1").variable(mv).set("f_break96", bestFbreak);
    m.component("comp1").variable(mv).set("F_boundary_break96", bestFbndExpr);
    m.component("comp1").variable(mv).set("F_total_break96", bestFtotExpr);
    m.component("comp1").variable(mv).set("mu_break96", "F_total_break96/F_total_target");

    plot3(m, "pg96_fbreak", "Stage 96 calibrated high-threshold film rupture weight", bestFbreak, "1");
    plot3(m, "pg96_hfilm", "Stage 96 h film", "h_film_input", "um");
    plot1(m, "pg96_break_force_mu",
        "Stage 96 calibrated high-threshold film + boundary shear force and friction coefficient",
        new String[] {"F_film_shear", bestFbndExpr, bestFtotExpr, "(" + bestFtotExpr + ")/F_total_target"},
        new String[] {"N", "N", "N", "1"});

    System.out.printf(Locale.US,
        "BEST_HIGH_BREAK_WINDOW low=%.3f[um] high=%.3f[um] fbreak_avg=%.12g Fboundary_avg=%.12g[N] Ftotal_avg=%.12g[N] mu_avg=%.12g%n",
        bestLo, bestHi, bestFb, bestFbnd, bestFtot, bestMu);

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\174_lid8mm_stage96_rq0p5um_high_break_threshold_sweep_results.mph");
    System.out.println("SAVED_STAGE96=174_lid8mm_stage96_rq0p5um_high_break_threshold_sweep_results.mph");
  }
}
