import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576w3c_local_tff_region {
  private static final String MODEL =
      "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String PRESSURE_SOL = "sol271";
  private static final String SWEPT = "sel_film_swept571";
  private static final String PATCH = "sel_local_cornea_patch574";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String solution) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double surface(
      Model model, String data, String tag, String selection, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static boolean sameEntities(int[] a, int[] b) {
    int[] aa = a.clone();
    int[] bb = b.clone();
    Arrays.sort(aa);
    Arrays.sort(bb);
    return Arrays.equals(aa, bb);
  }

  private static int[] entities(Selection selection) {
    try { return selection.entities(); } catch (Exception ignored) { return new int[0]; }
  }

  private static String array(int[] values) {
    return Arrays.toString(values);
  }

  private static void printFeature(Physics physics, String tag) {
    try {
      PhysicsFeature feature = physics.feature(tag);
      System.out.println("TFF_FEATURE tag=" + tag
          + " type=" + feature.getType()
          + " active=" + feature.isActive()
          + " entities=" + array(entities(feature.selection()))
          + " label=" + feature.label());
    } catch (Exception error) {
      System.out.println("TFF_FEATURE_MISSING tag=" + tag + " message=" + error.getMessage());
    }
  }

  private static void printVariableSelection(ModelNode comp, String tag) {
    try {
      System.out.println("VARIABLE_SELECTION tag=" + tag
          + " entities=" + array(entities(comp.variable(tag).selection())));
    } catch (Exception error) {
      System.out.println("VARIABLE_SELECTION_MISSING tag=" + tag
          + " message=" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", MODEL);
      ModelNode comp = model.component("comp1");
      Physics tff = comp.physics("tff");
      String data = dataset(model, "probeW3cLocalTff", PRESSURE_SOL);

      int[] sweptEntities = comp.selection(SWEPT).entities(2);
      int[] patchEntities = comp.selection(PATCH).entities(2);
      int[] tffEntities = tff.selection().entities();
      boolean tffOnSwept = sameEntities(tffEntities, sweptEntities);

      System.out.println("PROBE_MODEL=" + MODEL);
      System.out.println("PROBE_PRESSURE_SOL=" + PRESSURE_SOL);
      System.out.println("SWEPT_SELECTION=" + SWEPT);
      System.out.println("SWEPT_ENTITIES=" + array(sweptEntities));
      System.out.println("PATCH_SELECTION=" + PATCH);
      System.out.println("PATCH_ENTITIES=" + array(patchEntities));
      System.out.println("TFF_SELECTION_ENTITIES=" + array(tffEntities));
      System.out.println("TFF_SELECTION_EQUALS_SWEPT=" + tffOnSwept);

      printFeature(tff, "ffp1");
      printFeature(tff, "ms_vent573");
      printFeature(tff, "wc_open_anchor573");
      printFeature(tff, "bdr1");
      printFeature(tff, "bdr_inlet520");
      printFeature(tff, "bdr_outlet520");
      printFeature(tff, "bdr_left520");
      printFeature(tff, "bdr_right520");

      try {
        System.out.println("FFP1_HW1=" + tff.feature("ffp1").getString("hw1"));
      } catch (Exception error) {
        System.out.println("FFP1_HW1_ERROR=" + error.getMessage());
      }
      try {
        System.out.println("FFP1_VW=" + Arrays.toString(tff.feature("ffp1").getStringArray("vw")));
      } catch (Exception error) {
        System.out.println("FFP1_VW_ERROR=" + error.getMessage());
      }
      try {
        System.out.println("INTOP_FILM_ENTITIES=" + array(entities(comp.cpl("intop_film").selection())));
      } catch (Exception error) {
        System.out.println("INTOP_FILM_ERROR=" + error.getMessage());
      }
      printVariableSelection(comp, "var_cornea_dynamic_regions573");
      printVariableSelection(comp, "var_hrelease576w3c");

      double areaSwept = surface(model, data, "probeW3cAreaSwept", SWEPT, "IntSurface", "1");
      double areaPatch = surface(model, data, "probeW3cAreaPatch", PATCH, "IntSurface", "1");
      double areaCore = surface(model, data, "probeW3cAreaCore", SWEPT, "IntSurface", "M_core573");
      double areaDrain = surface(model, data, "probeW3cAreaDrain", SWEPT, "IntSurface", "M_drain573");
      double areaActive = surface(model, data, "probeW3cAreaActive", SWEPT, "IntSurface", "Afilm573");
      double areaWetLoad = surface(
          model, data, "probeW3cAreaWetLoad", SWEPT, "IntSurface", "M_core573*Bfilm573");
      double intPositivePressure = surface(
          model, data, "probeW3cPositivePressure", SWEPT, "IntSurface", "max(tff.p-p_amb573,0[Pa])");
      double intLoadPressure = surface(
          model, data, "probeW3cLoadPressure", SWEPT, "IntSurface", "max(p_load573,0[Pa])");
      double intOpenDiagnostic = surface(
          model, data, "probeW3cOpenPressure", SWEPT, "IntSurface", "abs(p_open_diagnostic573)");
      double intDrainDiagnostic = surface(
          model, data, "probeW3cDrainPressure", SWEPT, "IntSurface", "abs(p_drain_diagnostic573)");
      double intRuptureDiagnostic = surface(
          model, data, "probeW3cRupturePressure", SWEPT, "IntSurface", "abs(p_ruptured_diagnostic573)");
      double minPressure = surface(
          model, data, "probeW3cMinPressure", SWEPT, "MinSurface", "tff.p-p_amb573");
      double maxPressure = surface(
          model, data, "probeW3cMaxPressure", SWEPT, "MaxSurface", "tff.p-p_amb573");
      double minH = surface(model, data, "probeW3cMinH", SWEPT, "MinSurface", "h_calc576w3c");
      double maxH = surface(model, data, "probeW3cMaxH", SWEPT, "MaxSurface", "h_calc576w3c");
      double avgH = surface(model, data, "probeW3cIntH", SWEPT, "IntSurface", "h_calc576w3c")
          / areaSwept;
      double minTheta = surface(model, data, "probeW3cMinTheta", SWEPT, "MinSurface", "tff.theta");
      double maxTheta = surface(model, data, "probeW3cMaxTheta", SWEPT, "MaxSurface", "tff.theta");
      double minMcore = surface(model, data, "probeW3cMinMcore", SWEPT, "MinSurface", "M_core573");
      double maxMcore = surface(model, data, "probeW3cMaxMcore", SWEPT, "MaxSurface", "M_core573");

      boolean finite =
          Double.isFinite(areaSwept) && Double.isFinite(areaPatch) &&
          Double.isFinite(areaCore) && Double.isFinite(areaDrain) &&
          Double.isFinite(areaActive) && Double.isFinite(areaWetLoad) &&
          Double.isFinite(intPositivePressure) && Double.isFinite(intLoadPressure) &&
          Double.isFinite(minPressure) && Double.isFinite(maxPressure) &&
          Double.isFinite(minH) && Double.isFinite(maxH) &&
          Double.isFinite(avgH) && Double.isFinite(minTheta) && Double.isFinite(maxTheta);
      boolean localMasksNontrivial = areaSwept > 0.0
          && areaCore > 0.0 && areaCore < areaSwept
          && areaDrain >= areaCore && areaDrain <= areaSwept * 1.001
          && areaWetLoad > 0.0 && areaWetLoad <= areaCore * 1.001
          && maxMcore > 0.5 && minMcore < 0.5;
      boolean loadIsMasked = intLoadPressure >= 0.0
          && intPositivePressure >= intLoadPressure
          && intLoadPressure < intPositivePressure * 0.95;
      boolean hUsesRelease = false;
      try { hUsesRelease = "h_calc576w3c".equals(tff.feature("ffp1").getString("hw1")); }
      catch (Exception ignored) {}

      System.out.printf(Locale.US, "AREA_SWEPT=%.12g%n", areaSwept);
      System.out.printf(Locale.US, "AREA_PATCH=%.12g%n", areaPatch);
      System.out.printf(Locale.US, "AREA_CORE_INT_MCORE=%.12g%n", areaCore);
      System.out.printf(Locale.US, "AREA_DRAIN_INT_MDRAIN=%.12g%n", areaDrain);
      System.out.printf(Locale.US, "AREA_ACTIVE_INT_AFILM=%.12g%n", areaActive);
      System.out.printf(Locale.US, "AREA_WET_LOAD_INT_MCORE_BFILM=%.12g%n", areaWetLoad);
      System.out.printf(Locale.US, "MCORE_MIN=%.12g%n", minMcore);
      System.out.printf(Locale.US, "MCORE_MAX=%.12g%n", maxMcore);
      System.out.printf(Locale.US, "INT_POSITIVE_TFF_PRESSURE=%.12g%n", intPositivePressure);
      System.out.printf(Locale.US, "INT_LOAD_PRESSURE_P_LOAD573=%.12g%n", intLoadPressure);
      System.out.printf(Locale.US, "LOAD_TO_POSITIVE_PRESSURE_RATIO=%.12g%n",
          intPositivePressure > 0.0 ? intLoadPressure / intPositivePressure : Double.NaN);
      System.out.printf(Locale.US, "INT_ABS_OPEN_PRESSURE_DIAG=%.12g%n", intOpenDiagnostic);
      System.out.printf(Locale.US, "INT_ABS_DRAIN_PRESSURE_DIAG=%.12g%n", intDrainDiagnostic);
      System.out.printf(Locale.US, "INT_ABS_RUPTURE_PRESSURE_DIAG=%.12g%n", intRuptureDiagnostic);
      System.out.printf(Locale.US, "MIN_PRESSURE=%.12g%n", minPressure);
      System.out.printf(Locale.US, "MAX_PRESSURE=%.12g%n", maxPressure);
      System.out.printf(Locale.US, "MIN_H_CALC576W3C=%.12g%n", minH);
      System.out.printf(Locale.US, "MAX_H_CALC576W3C=%.12g%n", maxH);
      System.out.printf(Locale.US, "AVG_H_CALC576W3C=%.12g%n", avgH);
      System.out.printf(Locale.US, "MIN_THETA=%.12g%n", minTheta);
      System.out.printf(Locale.US, "MAX_THETA=%.12g%n", maxTheta);
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_TFF_SELECTION_LOCAL=" + tffOnSwept);
      System.out.println("CHECK_H_USES_LOCAL_RELEASE=" + hUsesRelease);
      System.out.println("CHECK_LOCAL_MASKS_NONTRIVIAL=" + localMasksNontrivial);
      System.out.println("CHECK_LOAD_PRESSURE_MASKED_TO_CORE=" + loadIsMasked);
      System.out.println("LOCAL_TFF_REGION_STATUS="
          + (finite && tffOnSwept && hUsesRelease && localMasksNontrivial && loadIsMasked
              ? "PASS" : "FAIL"));

      ModelUtil.remove("Model");
      ModelUtil.disconnect();
      if (!(finite && tffOnSwept && hUsesRelease && localMasksNontrivial && loadIsMasked)) {
        System.exit(2);
      }
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
