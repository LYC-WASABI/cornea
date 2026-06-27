import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576w3c_localfilm_recip_check {
  private static final String INPUT =
      "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String RESULTS =
      "576w3c_stage576_recursive_split005_localfilm_recip_check_results.mph";
  private static final String STUDY = "std576w3c_localfilm_recip";
  private static final String DATASET = "dset576w3c_localfilm_recip";
  private static final String SWEPT = "sel_film_swept571";
  private static final String INIT_PRESSURE = "sol271";
  private static final String INIT_SOLID = "sol273";

  private static final String V_SIGNED =
      "lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2)";
  private static final String TAU_SIGNED =
      "M_core573*Bfilm573*mu_lub576w3c*(" + V_SIGNED + ")/max(h_calc576w3c,h_num573)";
  private static final String TAU_ABS = "abs(" + TAU_SIGNED + ")";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
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

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static double[][] evalGlobal(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(
      Model model, String data, String tag, String selection, String type, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
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

  private static boolean rangeVaries(double[] values, double minSpan) {
    return rowMax(values) - rowMin(values) > minSpan;
  }

  private static void configureDiagnosticVariables(ModelNode comp, Model model) {
    model.param().set("mu_lub576w3c", "1e-3[Pa*s]",
        "Diagnostic lubricant viscosity for Stage 576w3c reciprocating local-film check");
    model.param().set("time_offset572", "0[s]",
        "Reciprocating local-film dynamic check uses transient clock");

    String motion = "var_dynamic_motion572";
    comp.variable(motion).set("tau572", "t+time_offset572");
    comp.variable(motion).set("slide_fraction572",
        "if(tau572<T_pre572,0,"
        + "if(tau572<T_pre572+T_slide572,"
        + "0.5-0.5*cos(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,"
        + "0.5+0.5*cos(pi*(tau572-T_pre572-T_slide572)/T_slide572),0)))");
    comp.variable(motion).set(
        "phi_lid_rot572", "theta_slide_total*slide_fraction572");
    comp.variable(motion).set(
        "theta_lid_physical572", "-35[deg]+70[deg]*slide_fraction572");
    comp.variable(motion).set(
        "theta_lid_spatial572", "theta_lid_physical572+lid_mask_aoffset572");
    comp.variable(motion).set("omega_lid_rot572",
        "if(tau572<T_pre572,0[rad/s],"
        + "if(tau572<T_pre572+T_slide572,"
        + "theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,"
        + "-theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572-T_slide572)/T_slide572),"
        + "0[rad/s])))");

    String vars = "var_localfilm_recip576w3c";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(SWEPT);
    comp.variable(vars).set("vtheta_signed576w3c_recip", V_SIGNED);
    comp.variable(vars).set("tau_tff_signed576w3c_recip", TAU_SIGNED);
    comp.variable(vars).set("tau_tff_abs576w3c_recip", TAU_ABS);
  }

  private static String runTffOnly(Model model, ModelNode comp) {
    try { model.study().remove(STUDY); } catch (Exception ignored) {}
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 576w3c local TFF reciprocating check");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set(
        "tlist", "range(T_pre572,T_slide572/20,T_pre572+2*T_slide572)");
    model.study(STUDY).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on", "bode576w3c", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(STUDY).feature("time").set("useinitsol", "on");
    model.study(STUDY).feature("time").set("initmethod", "sol");
    model.study(STUDY).feature("time").set("initsol", INIT_PRESSURE);
    model.study(STUDY).feature("time").set("initsoluse", "current");
    model.study(STUDY).feature("time").set("initsolusesolnum", "last");

    String step = STUDY + "/time";
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }

    String[] before = model.sol().tags();
    model.study(STUDY).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", INIT_PRESSURE);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", INIT_SOLID);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", "range(T_pre572,T_slide572/20,T_pre572+2*T_slide572)");
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "T_slide572/1000"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "T_slide572/100"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    model.sol(solution).runAll();
    return solution;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      ModelNode comp = model.component("comp1");
      configureDiagnosticVariables(comp, model);
      String solution = runTffOnly(model, comp);

      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", solution);

      double[][] time = evalGlobal(model, DATASET, "eval576w3c_recip_time", new String[] {"t"});
      double[][] sweptInt = surface(model, DATASET, "int576w3c_recip_swept", SWEPT, "IntSurface",
          new String[] {
            "1",
            "omega_lid_rot572",
            "h_calc576w3c",
            "max(tff.p-p_amb573,0[Pa])",
            "max(p_load573,0[Pa])",
            "tau_tff_signed576w3c_recip",
            "tau_tff_abs576w3c_recip",
            "M_core573",
            "M_core573*Bfilm573"
          });
      double[][] minVals = surface(model, DATASET, "min576w3c_recip", SWEPT, "MinSurface",
          new String[] {
            "h_calc576w3c",
            "tff.p-p_amb573",
            "tff.theta",
            "tau_tff_signed576w3c_recip"
          });
      double[][] maxVals = surface(model, DATASET, "max576w3c_recip", SWEPT, "MaxSurface",
          new String[] {
            "h_calc576w3c",
            "tff.p-p_amb573",
            "tff.theta",
            "tau_tff_signed576w3c_recip",
            "tau_tff_abs576w3c_recip",
            "M_core573"
          });

      double area = sweptInt[0][0];
      double[] omegaMean = new double[sweptInt[1].length];
      double[] hAvg = new double[sweptInt[2].length];
      double[] pInt = sweptInt[3];
      double[] pLoad = sweptInt[4];
      double[] ftSigned = sweptInt[5];
      double[] ftAbs = sweptInt[6];
      double[] coreArea = sweptInt[7];
      double[] wetLoadArea = sweptInt[8];
      double[] muAlt = new double[ftSigned.length];
      for (int i = 0; i < hAvg.length; i++) {
        omegaMean[i] = sweptInt[1][i] / area;
        hAvg[i] = sweptInt[2][i] / area;
        muAlt[i] = Math.abs(ftSigned[i]) / 0.03;
      }

      addSurfacePlot(model, "pg576w3c_recip_h", "Stage 576w3c reciprocating h_calc", "h_calc576w3c", "m");
      addSurfacePlot(model, "pg576w3c_recip_p", "Stage 576w3c reciprocating pressure", "tff.p-p_amb573", "Pa");
      addSurfacePlot(model, "pg576w3c_recip_tau", "Stage 576w3c reciprocating signed shear proxy",
          "tau_tff_signed576w3c_recip", "Pa");
      addSurfacePlot(model, "pg576w3c_recip_core", "Stage 576w3c reciprocating moving core mask", "M_core573", "1");

      boolean finite = finite(omegaMean) && finite(hAvg) && finite(pInt) && finite(pLoad)
          && finite(ftSigned) && finite(ftAbs) && finite(muAlt)
          && finite(minVals[0]) && finite(maxVals[1]) && finite(minVals[2]);
      boolean reversal = crossesZero(omegaMean, 1e-3)
          && crossesZero(ftSigned, 1e-10)
          && rowMin(minVals[3]) < -1e-6
          && rowMax(maxVals[3]) > 1e-6;
      boolean pressureResponds = anyPositive(maxVals[1], 10.0) && rangeVaries(maxVals[1], 100.0);
      boolean hResponds = rowMin(minVals[0]) > 0.0 && rangeVaries(hAvg, 1e-7);
      boolean muPass = rowMin(muAlt) >= -1e-12 && rowMax(muAlt) < 1.0 && rangeVaries(muAlt, 1e-10);
      boolean localPass = rowMin(coreArea) >= 0.0 && rowMax(coreArea) < area
          && rowMax(wetLoadArea) <= rowMax(coreArea) * 1.001;
      boolean pass = finite && reversal && pressureResponds && hResponds && muPass && localPass;

      System.out.println("LOCALFILM_RECIP_SOLUTION=" + solution);
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n",
          rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "OMEGA_MEAN_RANGE=[%.12g,%.12g]%n",
          rowMin(omegaMean), rowMax(omegaMean));
      System.out.printf(Locale.US, "AREA_SWEPT=%.12g%n", area);
      System.out.printf(Locale.US, "H_AVG_RANGE=[%.12g,%.12g]%n", rowMin(hAvg), rowMax(hAvg));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minVals[0]), rowMax(minVals[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[0]), rowMax(maxVals[0]));
      System.out.printf(Locale.US, "P_INT_RANGE=[%.12g,%.12g]%n", rowMin(pInt), rowMax(pInt));
      System.out.printf(Locale.US, "P_LOAD_RANGE=[%.12g,%.12g]%n", rowMin(pLoad), rowMax(pLoad));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[1]), rowMax(maxVals[1]));
      System.out.printf(Locale.US, "CORE_AREA_RANGE=[%.12g,%.12g]%n", rowMin(coreArea), rowMax(coreArea));
      System.out.printf(Locale.US, "WET_LOAD_AREA_RANGE=[%.12g,%.12g]%n", rowMin(wetLoadArea), rowMax(wetLoadArea));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minVals[2]), rowMax(minVals[2]));
      System.out.printf(Locale.US, "THETA_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[2]), rowMax(maxVals[2]));
      System.out.printf(Locale.US, "FT_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ftSigned), rowMax(ftSigned));
      System.out.printf(Locale.US, "FT_ABS_RANGE=[%.12g,%.12g]%n", rowMin(ftAbs), rowMax(ftAbs));
      System.out.printf(Locale.US, "TAU_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(minVals[3]), rowMax(maxVals[3]));
      System.out.printf(Locale.US, "TAU_ABS_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[4]), rowMax(maxVals[4]));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(muAlt), rowMax(muAlt));
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_REVERSAL_AVAILABLE=true");
      System.out.println("CHECK_TAU_SIGN_REVERSAL=" + reversal);
      System.out.println("CHECK_PRESSURE_RECIP_DYNAMIC=" + pressureResponds);
      System.out.println("CHECK_H_RECIP_DYNAMIC=" + hResponds);
      System.out.println("CHECK_MU_RECIP_DYNAMIC=" + muPass);
      System.out.println("CHECK_LOCAL_MASK_RECIP=" + localPass);
      System.out.println("LOCALFILM_RECIP_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 576w3c local-film reciprocating diagnostic check");
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }

  private static boolean anyPositive(double[] values, double threshold) {
    for (double value : values) if (value > threshold) return true;
    return false;
  }
}
