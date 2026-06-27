import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577g_contact_or_asperity_boundary_model {
  private static final String INPUT = "577f_stage577_load_sharing_boundary_pressure_results.mph";
  private static final String RESULTS = "577g_stage577_contact_or_asperity_boundary_model_results.mph";
  private static final String DATASET = "dset577g";
  private static final String SOLUTION = "sol274";
  private static final String SWEPT = "sel_film_swept571";
  private static final double FN = 0.03;
  private static final double MU = 0.10;
  private static final double K_ASP = 2.0e10;
  private static final double H_CRIT = 1.0e-6;
  private static final double DH_UM = 2.5;
  private static final String H = "max(0.05[um],3[um]-2.5[um]*M_core573)";
  private static final String WCLOSE = "0.5*(1+tanh((1[um]-(" + H + "))/0.2[um]))";
  private static final String VSIGN =
      "tanh((lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2))/(1e-6[m/s]))";
  private static final String P_SOLID =
      "if(isdefined(solid.Tn),max(solid.Tn,0[Pa]),0[Pa])";
  private static final String P_ASP =
      WCLOSE + "*(2e10[Pa/m])*max(1[um]-(" + H + "),0[m])";

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

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", SOLUTION);

      double[][] time = global(model, "eval577g_time", new String[] {"t"});
      double[][] integ = surface(model, "int577g", "IntSurface",
          new String[] {"tau_tff_signed577a", WCLOSE, P_SOLID, P_ASP,
              "(" + P_SOLID + ")*(" + VSIGN + ")",
              "(" + P_ASP + ")*(" + VSIGN + ")"});
      double[][] maxv = surface(model, "max577g", "MaxSurface",
          new String[] {P_SOLID, P_ASP, WCLOSE});

      double[] ftFluid = integ[0];
      double[] areaClose = integ[1];
      double[] fnSolid = integ[2];
      double[] fnAsp = integ[3];
      double[] signedSolidPressure = integ[4];
      double[] signedAspPressure = integ[5];
      double[] ftSolid = new double[ftFluid.length];
      double[] ftAsp = new double[ftFluid.length];
      double[] muSolid = new double[ftFluid.length];
      double[] muAsp = new double[ftFluid.length];
      for (int i = 0; i < ftFluid.length; i++) {
        ftSolid[i] = MU * signedSolidPressure[i];
        ftAsp[i] = MU * signedAspPressure[i];
        muSolid[i] = Math.abs(ftFluid[i] + ftSolid[i]) / FN;
        muAsp[i] = Math.abs(ftFluid[i] + ftAsp[i]) / FN;
      }

      boolean finite = finite(ftFluid) && finite(areaClose) && finite(fnSolid) && finite(fnAsp)
          && finite(ftSolid) && finite(ftAsp) && finite(muSolid) && finite(muAsp)
          && finite(maxv[0]) && finite(maxv[1]);
      boolean solidAvailable = rowMax(fnSolid) > 1e-12 && rowMax(maxv[0]) > 1.0;
      boolean asperityActive = rowMax(fnAsp) > 1e-9 && rowMax(maxv[1]) > 1.0;
      boolean solidOk = !solidAvailable || (crossesZero(ftSolid, 1e-10) && rowMax(muSolid) >= 0.01 && rowMax(muSolid) <= 0.2);
      boolean aspOk = asperityActive && crossesZero(ftAsp, 1e-10) && rowMax(muAsp) >= 0.01 && rowMax(muAsp) <= 0.2;
      boolean pass = finite && aspOk;

      addSurfacePlot(model, "pg577g_pasp", "Stage 577g asperity pressure proxy", P_ASP, "Pa");
      addSurfacePlot(model, "pg577g_psolid", "Stage 577g positive solid contact pressure probe", P_SOLID, "Pa");

      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "DH_UM=%.3f MU_BOUNDARY=%.3f K_ASP=%.12g H_CRIT=%.12g%n", DH_UM, MU, K_ASP, H_CRIT);
      System.out.printf(Locale.US, "A_CLOSE_RANGE=[%.12g,%.12g]%n", rowMin(areaClose), rowMax(areaClose));
      System.out.printf(Locale.US, "FN_SOLID_POS_RANGE=[%.12g,%.12g]%n", rowMin(fnSolid), rowMax(fnSolid));
      System.out.printf(Locale.US, "FN_ASP_RANGE=[%.12g,%.12g]%n", rowMin(fnAsp), rowMax(fnAsp));
      System.out.printf(Locale.US, "P_SOLID_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[0]), rowMax(maxv[0]));
      System.out.printf(Locale.US, "P_ASP_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[1]), rowMax(maxv[1]));
      System.out.printf(Locale.US, "FT_SOLID_RANGE=[%.12g,%.12g]%n", rowMin(ftSolid), rowMax(ftSolid));
      System.out.printf(Locale.US, "FT_ASP_RANGE=[%.12g,%.12g]%n", rowMin(ftAsp), rowMax(ftAsp));
      System.out.printf(Locale.US, "MU_SOLID_TOTAL_RANGE=[%.12g,%.12g]%n", rowMin(muSolid), rowMax(muSolid));
      System.out.printf(Locale.US, "MU_ASP_TOTAL_RANGE=[%.12g,%.12g]%n", rowMin(muAsp), rowMax(muAsp));
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_SOLID_TN_AVAILABLE=" + solidAvailable);
      System.out.println("CHECK_SOLID_PATH_OK_OR_SKIPPED=" + solidOk);
      System.out.println("CHECK_ASPERITY_ACTIVE=" + asperityActive);
      System.out.println("CHECK_ASPERITY_SIGN_REVERSAL_AND_TARGET=" + aspOk);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577g contact or asperity boundary model " + (pass ? "PASS" : "FAIL"));
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
