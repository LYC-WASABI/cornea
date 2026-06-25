import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;
import java.io.*;

public class diagnose_stage574j_gap_pressure_locations {
  private static final String BASE = "574j_stage574_fixed_structure_true_gap_jfo_checked.mph";
  private static final String OUT = "574j_diag_gap_pressure_locations.mph";
  private static final String REPORT = "574j_diag_gap_pressure_locations.md";
  private static final String SOL = "sol129";
  private static final List<String> lines = new ArrayList<>();

  private static void line(String s) {
    lines.add(s);
    System.out.println(s);
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double[] integrate(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] result = new double[raw.length];
    for (int i = 0; i < raw.length; i++) result[i] = raw[i][0];
    return result;
  }

  private static String sci(double value) {
    return String.format(Locale.US, "%.12g", value);
  }

  private static void addPlot(Model model, String tag, String label, String expr) {
    try {
      model.result().remove(tag);
    } catch (Exception ignored) {}
    try {
      model.result().create(tag, "PlotGroup2D");
      model.result(tag).label(label);
      model.result(tag).feature().create("surf1", "Surface");
      model.result(tag).feature("surf1").selection().named("sel_local_cornea_patch574");
      model.result(tag).feature("surf1").set("expr", expr);
    } catch (Exception error) {
      line("- plot skipped `" + label + "`: `" + error.getMessage().replace("`", "'") + "`");
    }
  }

  private static void nearMaxRegion(
      Model model, String data, String name, String expr, double max, String unit) {
    double threshold = 0.90 * max;
    if (max == 0) threshold = 0;
    String mask = "if((" + expr + ")>" + sci(threshold) + "[" + unit + "],1,0)";
    summarizeRegion(model, data, name, "near max >= 90%", mask);
  }

  private static void thresholdRegion(
      Model model, String data, String name, String expr, double threshold, String unit) {
    String mask = "if((" + expr + ")>" + sci(threshold) + "[" + unit + "],1,0)";
    summarizeRegion(model, data, name, "> " + sci(threshold) + " " + unit, mask);
  }

  private static void nearMinRegion(
      Model model, String data, String name, String expr, double min, double delta, String unit) {
    String mask = "if((" + expr + ")<" + sci(min + delta) + "[" + unit + "],1,0)";
    summarizeRegion(model, data, name, "near min", mask);
  }

  private static void summarizeRegion(
      Model model, String data, String name, String criterion, String mask) {
    String tag = "int574j_diag_" + name.replaceAll("[^A-Za-z0-9]", "");
    double[] values = integrate(model, data, tag, new String[] {
      mask,
      "(" + mask + ")*X",
      "(" + mask + ")*Y",
      "(" + mask + ")*Z",
      "(" + mask + ")*theta_surface572",
      "(" + mask + ")*geomgap_dst_cp_lid_cornea",
      "(" + mask + ")*h_calc573",
      "(" + mask + ")*Bfilm573",
      "(" + mask + ")*Afilm573",
      "(" + mask + ")*(tff.p-p_amb573)",
      "(" + mask + ")*p_load573",
      "(" + mask + ")*tff.theta"
    });
    double area = values[0];
    line("");
    line("### " + name);
    line("");
    line("- criterion: `" + criterion + "`");
    line("- area: `" + sci(area) + " m^2`");
    if (Math.abs(area) < 1e-30 || !Double.isFinite(area)) {
      line("- status: `empty or nonfinite region`");
      return;
    }
    line("- centroid X,Y,Z: `(" + sci(values[1] / area) + ", "
        + sci(values[2] / area) + ", " + sci(values[3] / area) + ") m`");
    line("- mean theta_surface572: `" + sci(values[4] / area) + " rad`");
    line("- mean geomgap_dst_cp_lid_cornea: `" + sci(values[5] / area) + " m`");
    line("- mean h_calc573: `" + sci(values[6] / area) + " m`");
    line("- mean Bfilm573: `" + sci(values[7] / area) + "`");
    line("- mean Afilm573: `" + sci(values[8] / area) + "`");
    line("- mean pressure p-p_amb573: `" + sci(values[9] / area) + " Pa`");
    line("- mean p_load573: `" + sci(values[10] / area) + " Pa`");
    line("- mean theta: `" + sci(values[11] / area) + "`");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      String data = "dset574j_diag";
      removeDataset(model, data);
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", SOL);

      line("# Stage 574j gap and pressure location diagnostic");
      line("");
      line("## Source");
      line("");
      line("- Base model: `" + BASE + "`");
      line("- Solution: `" + SOL + "`");
      line("- Local patch: `" + Arrays.toString(
          comp.selection("sel_local_cornea_patch574").entities(2)) + "`");

      double area = integrate(model, data, "int574j_diag_global", new String[] {"1"})[0];
      double minX = surface(model, data, "min574j_diag_x", "MinSurface", "X");
      double maxX = surface(model, data, "max574j_diag_x", "MaxSurface", "X");
      double minY = surface(model, data, "min574j_diag_y", "MinSurface", "Y");
      double maxY = surface(model, data, "max574j_diag_y", "MaxSurface", "Y");
      double minZ = surface(model, data, "min574j_diag_z", "MinSurface", "Z");
      double maxZ = surface(model, data, "max574j_diag_z", "MaxSurface", "Z");
      line("");
      line("## Patch Bounds");
      line("");
      line("- area: `" + sci(area) + " m^2`");
      line("- X range: `" + sci(minX) + " .. " + sci(maxX) + "`");
      line("- Y range: `" + sci(minY) + " .. " + sci(maxY) + "`");
      line("- Z range: `" + sci(minZ) + " .. " + sci(maxZ) + "`");

      double minH = surface(model, data, "min574j_diag_h", "MinSurface", "h_calc573");
      double maxH = surface(model, data, "max574j_diag_h", "MaxSurface", "h_calc573");
      double minGap = surface(model, data, "min574j_diag_gap", "MinSurface", "geomgap_dst_cp_lid_cornea");
      double maxGapRaw = surface(model, data, "max574j_diag_gap_raw", "MaxSurface", "geomgap_dst_cp_lid_cornea");
      double maxGapFinite = surface(model, data, "max574j_diag_gap_finite", "MaxSurface",
          "if(abs(geomgap_dst_cp_lid_cornea)<1[m],geomgap_dst_cp_lid_cornea,-1[m])");
      double minP = surface(model, data, "min574j_diag_p", "MinSurface", "tff.p-p_amb573");
      double maxP = surface(model, data, "max574j_diag_p", "MaxSurface", "tff.p-p_amb573");
      double maxPload = surface(model, data, "max574j_diag_pload", "MaxSurface", "p_load573");
      double minTheta = surface(model, data, "min574j_diag_theta", "MinSurface", "tff.theta");
      double maxTheta = surface(model, data, "max574j_diag_theta", "MaxSurface", "tff.theta");
      double minB = surface(model, data, "min574j_diag_b", "MinSurface", "Bfilm573");
      double maxB = surface(model, data, "max574j_diag_b", "MaxSurface", "Bfilm573");

      line("");
      line("## Extrema");
      line("");
      line("- h_calc573: `" + sci(minH) + " .. " + sci(maxH) + " m`");
      line("- geomgap_dst_cp_lid_cornea raw: `" + sci(minGap) + " .. " + sci(maxGapRaw) + " m`");
      line("- geomgap_dst_cp_lid_cornea finite abs<1m max: `" + sci(maxGapFinite) + " m`");
      line("- pressure tff.p-p_amb573: `" + sci(minP) + " .. " + sci(maxP) + " Pa`");
      line("- max p_load573: `" + sci(maxPload) + " Pa`");
      line("- theta: `" + sci(minTheta) + " .. " + sci(maxTheta) + "`");
      line("- Bfilm573: `" + sci(minB) + " .. " + sci(maxB) + "`");

      line("");
      line("## Near-Extremum Regions");
      thresholdRegion(model, data, "h_calc573 larger than 1 mm", "h_calc573", 1e-3, "m");
      thresholdRegion(model, data, "h_calc573 larger than 50 um", "h_calc573", 50e-6, "m");
      nearMaxRegion(model, data, "finite max geomgap region",
          "if(abs(geomgap_dst_cp_lid_cornea)<1[m],geomgap_dst_cp_lid_cornea,-1[m])",
          maxGapFinite, "m");
      nearMaxRegion(model, data, "max pressure region", "tff.p-p_amb573", maxP, "Pa");
      nearMaxRegion(model, data, "max p_load573 region", "p_load573", maxPload, "Pa");
      nearMinRegion(model, data, "min theta region", "tff.theta", minTheta, 1e-6, "1");

      line("");
      line("## Interpretation");
      line("");
      line("- `max h_calc573` and `max geomgap_dst_cp_lid_cornea` should be compared by centroid and local Bfilm/Afilm values.");
      line("- If the large-gap region has low pressure and low p_load573, it is primarily a mapping/open-gap outlier rather than the active load source.");
      line("- If the max pressure region has low Bfilm573 or small area, pressure is being generated near a rupture/transition region and should be controlled by upper-gap gating and smoother masks before feedback.");

      addPlot(model, "pg574j_diag_h", "Stage 574j h_calc573", "h_calc573");
      addPlot(model, "pg574j_diag_gap", "Stage 574j pair gap", "geomgap_dst_cp_lid_cornea");
      addPlot(model, "pg574j_diag_p", "Stage 574j pressure", "tff.p-p_amb573");
      addPlot(model, "pg574j_diag_pload", "Stage 574j physical pressure load", "p_load573");
      addPlot(model, "pg574j_diag_b", "Stage 574j Bfilm573", "Bfilm573");
      addPlot(model, "pg574j_diag_theta", "Stage 574j theta", "tff.theta");

      try {
        PrintWriter writer = new PrintWriter(new FileWriter(REPORT));
        for (String s : lines) writer.println(s);
        writer.close();
      } catch (Exception error) {
        System.out.println("REPORT_WRITE_FAILED=" + error.getMessage());
      }
      model.label("Stage 574j gap pressure location diagnostic");
      model.save(OUT);
      System.out.println("SAVED_MODEL=" + OUT);
      System.out.println("SAVED_REPORT=" + REPORT);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
