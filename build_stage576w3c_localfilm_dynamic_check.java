import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576w3c_localfilm_dynamic_check {
  private static final String INPUT =
      "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String RESULTS =
      "576w3c_stage576_recursive_split005_localfilm_dynamic_check_results.mph";
  private static final String STUDY = "std576w3c_localfilm_check";
  private static final String DATASET = "dset576w3c_localfilm_check";
  private static final String SWEPT = "sel_film_swept571";
  private static final String PATCH = "sel_local_cornea_patch574";
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

  private static boolean anyGreater(double[] values, double threshold) {
    for (double value : values) if (value > threshold) return true;
    return false;
  }

  private static boolean rangeVaries(double[] values, double minSpan) {
    return rowMax(values) - rowMin(values) > minSpan;
  }

  private static void configureDiagnosticVariables(ModelNode comp, Model model) {
    model.param().set("mu_lub576w3c", "1e-3[Pa*s]",
        "Diagnostic lubricant viscosity for Stage 576w3c local-film check");
    model.param().set("time_offset572", "0[s]",
        "Local-film dynamic check restores transient motion clock");
    comp.variable("var_dynamic_motion572").set("tau572", "t+time_offset572");
    String vars = "var_localfilm_check576w3c";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(SWEPT);
    comp.variable(vars).set("vtheta_signed576w3c", V_SIGNED);
    comp.variable(vars).set("tau_tff_signed576w3c", TAU_SIGNED);
    comp.variable(vars).set("tau_tff_abs576w3c", TAU_ABS);
    comp.variable(vars).set("Ft_TFF_signed576w3c", "intop_film(tau_tff_signed576w3c)");
    comp.variable(vars).set("Ft_TFF_abs576w3c", "intop_film(tau_tff_abs576w3c)");
  }

  private static String runTffOnly(Model model, ModelNode comp) {
    try { model.study().remove(STUDY); } catch (Exception ignored) {}
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 576w3c local TFF dynamic check");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set(
        "tlist", "range(T_pre572,T_slide572/20,T_pre572+T_slide572)");
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
    time.set("tlist", "range(T_pre572,T_slide572/20,T_pre572+T_slide572)");
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

      double[][] time = evalGlobal(model, DATASET, "eval576w3c_lf_time",
          new String[] {"t"});
      double[][] sweptInt = surface(model, DATASET, "int576w3c_lf_swept", SWEPT, "IntSurface",
          new String[] {
            "1",
            "h_calc576w3c",
            "max(tff.p-p_amb573,0[Pa])",
            "max(p_load573,0[Pa])",
            "tau_tff_signed576w3c",
            "tau_tff_abs576w3c",
            "M_core573",
            "M_core573*Bfilm573",
            "if(max(tff.p-p_amb573,0[Pa])>1[Pa],1,0)",
            "if(max(p_load573,0[Pa])>1[Pa],1,0)",
            "omega_lid_rot572"
          });
      double[][] minVals = surface(model, DATASET, "min576w3c_lf", SWEPT, "MinSurface",
          new String[] {
            "h_calc576w3c",
            "tff.p-p_amb573",
            "tff.theta",
            "tau_tff_signed576w3c"
          });
      double[][] maxVals = surface(model, DATASET, "max576w3c_lf", SWEPT, "MaxSurface",
          new String[] {
            "h_calc576w3c",
            "tff.p-p_amb573",
            "tff.theta",
            "tau_tff_signed576w3c",
            "tau_tff_abs576w3c",
            "M_core573"
          });

      double area = sweptInt[0][0];
      double[] hAvg = new double[sweptInt[1].length];
      double[] pInt = sweptInt[2];
      double[] pLoad = sweptInt[3];
      double[] ftSigned = sweptInt[4];
      double[] ftAbs = sweptInt[5];
      double[] coreArea = sweptInt[6];
      double[] wetLoadArea = sweptInt[7];
      double[] pArea = sweptInt[8];
      double[] pLoadArea = sweptInt[9];
      double[] omegaMean = new double[sweptInt[10].length];
      double[] muAlt = new double[ftSigned.length];
      for (int i = 0; i < hAvg.length; i++) {
        hAvg[i] = sweptInt[1][i] / area;
        omegaMean[i] = sweptInt[10][i] / area;
        muAlt[i] = Math.abs(ftSigned[i]) / 0.03;
      }

      addSurfacePlot(model, "pg576w3c_lf_h", "Stage 576w3c local-film h_calc", "h_calc576w3c", "m");
      addSurfacePlot(model, "pg576w3c_lf_p", "Stage 576w3c local-film pressure", "tff.p-p_amb573", "Pa");
      addSurfacePlot(model, "pg576w3c_lf_tau", "Stage 576w3c local-film signed shear proxy",
          "tau_tff_signed576w3c", "Pa");
      addSurfacePlot(model, "pg576w3c_lf_core", "Stage 576w3c moving core mask", "M_core573", "1");

      boolean hPass = finite(hAvg) && finite(minVals[0]) && finite(maxVals[0])
          && rowMin(minVals[0]) > 0.0 && rangeVaries(hAvg, 1e-7);
      boolean pPass = finite(pInt) && finite(maxVals[1])
          && anyGreater(maxVals[1], 10.0)
          && rangeVaries(maxVals[1], 100.0)
          && rowMax(pLoadArea) < area * 0.5;
      boolean tauPass = finite(ftSigned) && finite(ftAbs) && finite(maxVals[4])
          && anyGreater(maxVals[4], 1e-6)
          && Math.abs(ftSigned[0]) < Math.max(1e-12, 0.05 * rowMax(ftAbs))
          && Math.abs(ftSigned[ftSigned.length - 1]) < Math.max(1e-12, 0.05 * rowMax(ftAbs));
      boolean muPass = finite(muAlt) && rowMin(muAlt) >= -1e-12
          && rowMax(muAlt) < 1.0 && rangeVaries(muAlt, 1e-10);
      boolean localPass = rowMin(coreArea) > 0.0 && rowMax(coreArea) < area
          && rowMax(wetLoadArea) <= rowMax(coreArea) * 1.001
          && rowMax(pLoadArea) <= rowMax(coreArea) * 1.001;
      boolean oneWayMotion = rowMin(omegaMean) >= -1e-12 && rowMax(omegaMean) > 0.0;
      boolean pass = hPass && pPass && tauPass && muPass && localPass;

      System.out.println("LOCALFILM_DYNAMIC_SOLUTION=" + solution);
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n",
          rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "OMEGA_RANGE=[%.12g,%.12g] ONE_WAY=%s%n",
          rowMin(omegaMean), rowMax(omegaMean), Boolean.toString(oneWayMotion));
      System.out.printf(Locale.US, "AREA_SWEPT=%.12g%n", area);
      System.out.printf(Locale.US, "H_AVG_RANGE=[%.12g,%.12g]%n", rowMin(hAvg), rowMax(hAvg));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minVals[0]), rowMax(minVals[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[0]), rowMax(maxVals[0]));
      System.out.printf(Locale.US, "P_INT_RANGE=[%.12g,%.12g]%n", rowMin(pInt), rowMax(pInt));
      System.out.printf(Locale.US, "P_LOAD_RANGE=[%.12g,%.12g]%n", rowMin(pLoad), rowMax(pLoad));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[1]), rowMax(maxVals[1]));
      System.out.printf(Locale.US, "P_ACTIVE_AREA_RANGE=[%.12g,%.12g]%n", rowMin(pArea), rowMax(pArea));
      System.out.printf(Locale.US, "P_LOAD_ACTIVE_AREA_RANGE=[%.12g,%.12g]%n", rowMin(pLoadArea), rowMax(pLoadArea));
      System.out.printf(Locale.US, "CORE_AREA_RANGE=[%.12g,%.12g]%n", rowMin(coreArea), rowMax(coreArea));
      System.out.printf(Locale.US, "WET_LOAD_AREA_RANGE=[%.12g,%.12g]%n", rowMin(wetLoadArea), rowMax(wetLoadArea));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minVals[2]), rowMax(minVals[2]));
      System.out.printf(Locale.US, "THETA_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[2]), rowMax(maxVals[2]));
      System.out.printf(Locale.US, "FT_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ftSigned), rowMax(ftSigned));
      System.out.printf(Locale.US, "FT_ABS_RANGE=[%.12g,%.12g]%n", rowMin(ftAbs), rowMax(ftAbs));
      System.out.printf(Locale.US, "TAU_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(minVals[3]), rowMax(maxVals[3]));
      System.out.printf(Locale.US, "TAU_ABS_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxVals[4]), rowMax(maxVals[4]));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(muAlt), rowMax(muAlt));
      System.out.println("CHECK_H_LOCAL_DYNAMIC=" + hPass);
      System.out.println("CHECK_PRESSURE_LOCAL_DYNAMIC=" + pPass);
      System.out.println("CHECK_TAU_PROXY_DYNAMIC=" + tauPass);
      System.out.println("CHECK_MU_PROXY_DYNAMIC=" + muPass);
      System.out.println("CHECK_LOCAL_MASK_DYNAMIC=" + localPass);
      System.out.println("CHECK_REVERSAL_AVAILABLE=false");
      System.out.println("REVERSAL_NOTE=omega_lid_rot572 is one-way in Stage 572; sign reversal requires a reciprocating check.");
      System.out.println("LOCALFILM_DYNAMIC_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 576w3c local-film dynamic diagnostic check");
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
}
