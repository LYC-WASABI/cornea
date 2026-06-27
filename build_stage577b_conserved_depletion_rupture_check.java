import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577b_conserved_depletion_rupture_check {
  private static final String INPUT = "577a_stage577_conserved_3um_local_tff_check_results.mph";
  private static final String RESULTS = "577b_stage577_conserved_depletion_rupture_check_results.mph";
  private static final String DATASET = "dset577b";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final String SHELL =
      "max(M_drain573-M_core573,0)";
  private static final String H =
      "max(0.05[um],3[um]-2.8[um]*M_core573+2.18*2.8[um]*(" + SHELL + "))";
  private static final String WCLOSE =
      "0.5*(1+tanh((1[um]-(" + H + "))/0.2[um]))";
  private static final double[] DH_SCAN_UM = new double[] {0.5, 1.0, 2.0, 2.8};

  private static String hExpr(double dhUm) {
    return "max(0.05[um],3[um]-" + dhUm + "[um]*M_core573+2.18*" + dhUm
        + "[um]*(" + SHELL + "))";
  }

  private static String wCloseExpr(double dhUm) {
    String h = hExpr(dhUm);
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

  private static double[][] global(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
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

  private static void configure(Model model) {
    ModelNode comp = model.component("comp1");
    model.param().set("h0_577b", "3[um]");
    model.param().set("h_min577b", "0.05[um]");
    model.param().set("h_cut577b", "1[um]");
    model.param().set("h_eps577b", "0.2[um]");
    model.param().set("dh_deplete577b", "2.8[um]");
    model.param().set("pileup_gain577b", "2.18");
    String vars = "var_conserved_depletion577b";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().geom("geom1", 2);
    comp.variable(vars).selection().all();
    comp.variable(vars).set("shell577b", "max(M_drain573-M_core573,0)");
    comp.variable(vars).set("h_TFF577b",
        "max(h_min577b,h0_577b-dh_deplete577b*M_core573+pileup_gain577b*dh_deplete577b*shell577b)");
    comp.variable(vars).set("w_close577b", "0.5*(1+tanh((h_cut577b-h_TFF577b)/h_eps577b))");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      configure(model);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, DATASET, "eval577b_time", new String[] {"t"});
      double[][] integ = surface(model, DATASET, "int577b", "IntSurface",
          new String[] {"1", H, WCLOSE, "max(tff.p-p_amb573,0[Pa])",
              "tau_tff_signed577a", "tau_tff_abs577a"});
      double[][] minv = surface(model, DATASET, "min577b", "MinSurface",
          new String[] {H, "tff.theta", "tau_tff_signed577a"});
      double[][] maxv = surface(model, DATASET, "max577b", "MaxSurface",
          new String[] {H, "tff.p-p_amb573", "tff.theta", WCLOSE, "tau_tff_signed577a"});

      double area = integ[0][0];
      double[] hAvg = new double[integ[1].length];
      double[] closeArea = integ[2];
      double[] pInt = integ[3];
      double[] ft = integ[4];
      double[] mu = new double[ft.length];
      for (int i = 0; i < ft.length; i++) {
        hAvg[i] = integ[1][i] / area;
        mu[i] = Math.abs(ft[i]) / 0.03;
      }

      double[][] scanHAvg = new double[DH_SCAN_UM.length][];
      double[][] scanClose = new double[DH_SCAN_UM.length][];
      double[][] scanHMin = new double[DH_SCAN_UM.length][];
      double[][] scanHMax = new double[DH_SCAN_UM.length][];
      for (int s = 0; s < DH_SCAN_UM.length; s++) {
        String hScan = hExpr(DH_SCAN_UM[s]);
        String wScan = wCloseExpr(DH_SCAN_UM[s]);
        double[][] scanInt = surface(model, DATASET, "int577b_scan_" + s, "IntSurface",
            new String[] {hScan, wScan});
        double[][] scanMin = surface(model, DATASET, "min577b_scan_" + s, "MinSurface",
            new String[] {hScan});
        double[][] scanMax = surface(model, DATASET, "max577b_scan_" + s, "MaxSurface",
            new String[] {hScan});
        scanHAvg[s] = new double[scanInt[0].length];
        for (int i = 0; i < scanInt[0].length; i++) scanHAvg[s][i] = scanInt[0][i] / area;
        scanClose[s] = scanInt[1];
        scanHMin[s] = scanMin[0];
        scanHMax[s] = scanMax[0];
      }

      addSurfacePlot(model, "pg577b_h", "Stage 577b conserved depleted film thickness", H, "m");
      addSurfacePlot(model, "pg577b_close", "Stage 577b low-film activation", WCLOSE, "1");
      addSurfacePlot(model, "pg577b_p", "Stage 577b inherited pressure", "tff.p-p_amb573", "Pa");

      boolean finite = finite(hAvg) && finite(closeArea) && finite(pInt) && finite(ft) && finite(mu) && finite(minv[1]);
      boolean hConserved = rowMin(hAvg) > 2.5e-6 && rowMax(hAvg) < 3.5e-6;
      boolean hFloor = rowMin(minv[0]) >= 0.05e-6 * 0.999;
      boolean closeNontrivial = rowMax(closeArea) > 0.0 && rowMax(closeArea) < area * 0.9;
      boolean thetaFinite = rowMin(minv[1]) >= -1e-8 && rowMax(maxv[2]) <= 1.000001;
      boolean pFinite = finite(maxv[1]) && rowMax(maxv[1]) > 10.0;
      boolean tauFinite = finite(minv[2]) && finite(maxv[4]) && crossesZero(ft, 1e-10);
      boolean pass = finite && hConserved && hFloor && closeNontrivial && thetaFinite && pFinite && tauFinite;

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "H_AVG_RANGE=[%.12g,%.12g]%n", rowMin(hAvg), rowMax(hAvg));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[0]), rowMax(minv[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[0]), rowMax(maxv[0]));
      System.out.printf(Locale.US, "A_CLOSE_RANGE=[%.12g,%.12g]%n", rowMin(closeArea), rowMax(closeArea));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[1]), rowMax(minv[1]));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[1]), rowMax(maxv[1]));
      System.out.printf(Locale.US, "FT_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ft), rowMax(ft));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(mu), rowMax(mu));
      for (int s = 0; s < DH_SCAN_UM.length; s++) {
        System.out.printf(Locale.US,
            "DH_DEPLETE_SCAN_UM=%.1f H_AVG_RANGE=[%.12g,%.12g] H_MIN_RANGE=[%.12g,%.12g] H_MAX_RANGE=[%.12g,%.12g] A_CLOSE_RANGE=[%.12g,%.12g]%n",
            DH_SCAN_UM[s], rowMin(scanHAvg[s]), rowMax(scanHAvg[s]),
            rowMin(scanHMin[s]), rowMax(scanHMin[s]), rowMin(scanHMax[s]), rowMax(scanHMax[s]),
            rowMin(scanClose[s]), rowMax(scanClose[s]));
      }
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_LOCAL_TFF=true");
      System.out.println("CHECK_H_CONSERVATION=" + hConserved);
      System.out.println("CHECK_H_FLOOR=" + hFloor);
      System.out.println("CHECK_CLOSE_NONTRIVIAL=" + closeNontrivial);
      System.out.println("CHECK_THETA_FINITE=" + thetaFinite);
      System.out.println("CHECK_PRESSURE_FINITE=" + pFinite);
      System.out.println("CHECK_TAU_FINITE=" + tauFinite);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577b conserved depletion rupture postprocess " + (pass ? "PASS" : "FAIL"));
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
