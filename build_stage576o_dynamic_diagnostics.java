import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576o_dynamic_diagnostics {
  private static final String CSV = "576o_stage576_dynamic_diagnostics.csv";
  private static final String IMAGE_PREFIX = "576o_diag_";
  private static final String SHEAR =
      "M_core573*Bfilm573*(1e-3[Pa*s]*"
      + "sqrt((-lambda_v574*omega_lid_rot572*Z)^2"
      + "+(lambda_v574*omega_lid_rot572*Y)^2)"
      + "/max(h_calc573,h_num573))";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeExport(Model model, String tag) {
    try { model.result().export().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String solution) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double global(Model model, String data, String tag, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double surface(
      Model model, String data, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double optionalSurface(
      Model model, String data, String tag, String type, String expression) {
    try { return surface(model, data, tag, type, expression); }
    catch (Exception error) { return Double.NaN; }
  }

  private static void exportSurface(
      Model model, String data, String suffix, String expression, String unit) {
    String plot = "pg576o" + suffix;
    String export = "img576o" + suffix;
    removePlot(model, plot);
    removeExport(model, export);
    model.result().create(plot, "PlotGroup3D");
    model.result(plot).set("data", data);
    model.result(plot).feature().create("surf1", "Surface");
    model.result(plot).feature("surf1").set("expr", expression);
    model.result(plot).feature("surf1").set("unit", unit);
    model.result().export().create(export, "Image");
    model.result().export(export).set("plotgroup", plot);
    model.result().export(export).set("pngfilename", IMAGE_PREFIX + suffix + ".png");
    model.result().export(export).set("width", "1400");
    model.result().export(export).set("height", "900");
    model.result().export(export).run();
  }

  private static void exportStateImages(
      Model model, String pressureData, String solidData, String stateName) {
    try {
      exportSurface(model, pressureData, stateName + "_pressure",
          "tff.p-p_amb573", "Pa");
      exportSurface(model, pressureData, stateName + "_theta", "tff.theta", "1");
      exportSurface(model, pressureData, stateName + "_hcalc", "h_calc573", "m");
      exportSurface(model, solidData, stateName + "_gap",
          "geomgap_dst_cp_lid_cornea", "m");
      System.out.println("EXPORTED_IMAGES=" + stateName);
    } catch (Exception error) {
      System.out.println("IMAGE_EXPORT_WARNING=" + stateName + " " + error.getMessage());
    }
  }

  private static String stateName(double fraction) {
    return String.format(Locale.US, "f%04d", Math.round(fraction * 1000.0));
  }

  private static void evaluateFile(String file, String[][] states) throws Exception {
    Model model = ModelUtil.load("Model", file);
    double tPre = model.param().evaluate("T_pre572");
    double tSlide = model.param().evaluate("T_slide572");
    for (String[] state : states) {
      String pressure = state[0];
      String relaxed = state[1];
      String solid = state[2];
      boolean exportImages = Boolean.parseBoolean(state[3]);
      String pressureData = dataset(model, "dset576oPressure", pressure);
      String relaxedData = dataset(model, "dset576oRelaxed", relaxed);
      String solidData = dataset(model, "dset576oSolid", solid);
      double[] times = model.sol(pressure).getPVals();
      double time = times[times.length - 1];
      double fraction = (time - tPre) / tSlide;
      double film = surface(model, pressureData, "int576oFilm", "IntSurface", "p_load573");
      double theta = surface(model, pressureData, "min576oTheta", "MinSurface", "tff.theta");
      double maxPressure = surface(model, pressureData, "max576oPressure",
          "MaxSurface", "tff.p-p_amb573");
      double minH = surface(model, pressureData, "min576oH", "MinSurface", "h_calc573");
      double meanH = surface(model, pressureData, "mean576oH", "AvSurface", "h_calc573");
      double meanB = surface(model, pressureData, "mean576oB", "AvSurface", "Bfilm573");
      double shear = surface(model, pressureData, "int576oShear", "IntSurface", SHEAR);
      double feedback = surface(model, relaxedData, "int576oFeedback", "IntSurface",
          "p_scale576m*rrel576m");
      double residual = surface(model, relaxedData, "int576oResidual", "IntSurface",
          "abs(p_scale576m*rrel576m-alpha_pfb576m*withsol('"
          + pressure + "',p_load573))");
      double contact = global(model, solidData, "eval576oContact", "Fn_contact570");
      double minGap = surface(model, solidData, "min576oGap", "MinSurface",
          "geomgap_dst_cp_lid_cornea");
      double maxTn = optionalSurface(model, solidData, "max576oTn", "MaxSurface", "solid.Tn");
      double total = contact + film;
      double muTotal = shear / (total + 1e-12);
      System.out.printf(Locale.US,
          "CSVROW=%s,%.12g,%.12g,%s,%s,%s,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g,%.12g%n",
          file, fraction, time, pressure, relaxed, solid,
          contact, film, total, feedback, residual, theta, maxPressure,
          minH, meanH, meanB, minGap, maxTn, shear, muTotal);
      System.out.printf(Locale.US,
          "STATE fraction=%.6f Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g residual=%.12g MinTheta=%.12g MaxP=%.12g MinGap=%.12g Fshear=%.12g MuTotal=%.12g%n",
          fraction, contact, film, total, residual, theta, maxPressure,
          minGap, shear, muTotal);
      if (exportImages) {
        exportStateImages(model, pressureData, solidData, stateName(fraction));
      }
    }
    ModelUtil.remove("Model");
    System.gc();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      System.out.println("CSVHEADER=source,fraction,time_s,pressure_sol,relaxed_sol,solid_sol,Fcontact_N,Ffilm_N,Ftotal_N,Ffeedback_N,field_residual_N,min_theta,max_pressure_Pa,min_hcalc_m,mean_hcalc_m,mean_Bfilm,min_gap_m,max_solid_Tn_Pa,Fshear_N,mu_total");

      evaluateFile("576n12_stage576_full_dynamic_recursive_checked.mph",
          new String[][] {
            {"sol488", "sol489", "sol490", "true"},
            {"sol668", "sol669", "sol670", "false"},
            {"sol848", "sol849", "sol850", "false"},
            {"sol1028", "sol1029", "sol1030", "false"},
            {"sol1208", "sol1209", "sol1210", "true"},
            {"sol1388", "sol1389", "sol1390", "false"},
            {"sol1568", "sol1569", "sol1570", "true"},
            {"sol4984", "sol4985", "sol4986", "true"}
          });
      evaluateFile("576n9_stage576_full_dynamic_recursive_resume168_checkpoint.mph",
          new String[][] {{"sol2755", "sol2756", "sol2757", "true"}});
      evaluateFile("576n10_stage576_full_dynamic_recursive_resume174_checkpoint.mph",
          new String[][] {{"sol3412", "sol3413", "sol3414", "true"}});

      System.out.println("CSV_COMPLETE=" + CSV);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
