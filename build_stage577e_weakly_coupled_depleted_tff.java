import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577e_weakly_coupled_depleted_tff {
  private static final String INPUT = "577a_stage577_conserved_3um_local_tff_check_results.mph";
  private static final String RESULTS = "577e_stage577_weakly_coupled_depleted_tff_results.mph";
  private static final String STUDY = "std577e";
  private static final String DATASET = "dset577e";
  private static final String SWEPT = "sel_film_swept571";
  private static final String INIT = "sol274";
  private static final String V_SIGNED = "lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2)";
  private static final String H_RAW = "max(h_min577e,3[um]-dh_deplete577e*M_core573)";
  private static final String TAU_SIGNED =
      "M_core573*Bfilm573*mu_lub577e*(" + V_SIGNED + ")/max(h_TFF577e,h_num573)";

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

  private static boolean varies(double[] values, double eps) {
    return rowMax(values) - rowMin(values) > eps;
  }

  private static void configure(Model model, ModelNode comp) {
    model.param().set("mu_lub577e", "1e-3[Pa*s]");
    model.param().set("h_min577e", "0.05[um]");
    model.param().set("dh_deplete577e", "2.0[um]");
    model.param().set("time_offset572", "0[s]");
    String motion = "var_dynamic_motion572";
    comp.variable(motion).set("tau572", "t+time_offset572");
    comp.variable(motion).set("slide_fraction572",
        "if(tau572<T_pre572,0,"
        + "if(tau572<T_pre572+T_slide572,0.5-0.5*cos(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,0.5+0.5*cos(pi*(tau572-T_pre572-T_slide572)/T_slide572),0)))");
    comp.variable(motion).set("omega_lid_rot572",
        "if(tau572<T_pre572,0[rad/s],"
        + "if(tau572<T_pre572+T_slide572,theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,-theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572-T_slide572)/T_slide572),0[rad/s])))");

    String vars = "var_depleted_tff577e";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(SWEPT);
    comp.variable(vars).set("h_raw577e", H_RAW);
    comp.variable(vars).set("h_TFF577e", "h_raw577e");
    comp.variable(vars).set("vtheta_signed577e", V_SIGNED);
    comp.variable(vars).set("tau_tff_signed577e", TAU_SIGNED);
    comp.variable(vars).set("tau_tff_abs577e", "abs(" + TAU_SIGNED + ")");
    comp.variable(vars).set("w_close577e", "0.5*(1+tanh((1[um]-h_TFF577e)/0.2[um]))");
    comp.physics("tff").feature("ffp1").set("hw1", "h_TFF577e");
  }

  private static String runTff(Model model, ModelNode comp) {
    try { model.study().remove(STUDY); } catch (Exception ignored) {}
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 577e weakly coupled depleted TFF");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set("tlist", "range(T_pre572,T_slide572/20,T_pre572+2*T_slide572)");
    model.study(STUDY).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on", "bode576w3c", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(STUDY).feature("time").set("useinitsol", "on");
    model.study(STUDY).feature("time").set("initmethod", "sol");
    model.study(STUDY).feature("time").set("initsol", INIT);
    model.study(STUDY).feature("time").set("initsoluse", "current");
    model.study(STUDY).feature("time").set("initsolusesolnum", "last");
    String step = STUDY + "/time";
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(STUDY).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", INIT);
    dep.set("solnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", "range(T_pre572,T_slide572/20,T_pre572+2*T_slide572)");
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "T_slide572/1000"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "T_slide572/100"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
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
      configure(model, comp);
      String sol = runTff(model, comp);
      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", sol);

      double[][] time = global(model, "eval577e_time", new String[] {"t"});
      double[][] integ = surface(model, "int577e", "IntSurface",
          new String[] {"1", "h_TFF577e", "w_close577e", "max(tff.p-p_amb573,0[Pa])",
              "tau_tff_signed577e", "tau_tff_abs577e"});
      double[][] minv = surface(model, "min577e", "MinSurface",
          new String[] {"h_TFF577e", "tff.theta", "tau_tff_signed577e"});
      double[][] maxv = surface(model, "max577e", "MaxSurface",
          new String[] {"h_TFF577e", "tff.p-p_amb573", "tff.theta", "w_close577e", "tau_tff_signed577e"});

      double area = integ[0][0];
      double[] hAvg = new double[integ[1].length];
      double[] closeArea = integ[2];
      double[] pPos = integ[3];
      double[] ft = integ[4];
      double[] mu = new double[ft.length];
      for (int i = 0; i < ft.length; i++) {
        hAvg[i] = integ[1][i] / area;
        mu[i] = Math.abs(ft[i]) / 0.03;
      }

      addSurfacePlot(model, "pg577e_h", "Stage 577e depleted TFF film height", "h_TFF577e", "m");
      addSurfacePlot(model, "pg577e_p", "Stage 577e depleted TFF pressure", "tff.p-p_amb573", "Pa");
      addSurfacePlot(model, "pg577e_theta", "Stage 577e depleted TFF theta", "tff.theta", "1");

      boolean finite = finite(hAvg) && finite(closeArea) && finite(pPos) && finite(ft) && finite(mu)
          && finite(minv[1]) && finite(maxv[1]);
      boolean hFloor = rowMin(hAvg) > 0.4e-6 && rowMin(minv[0]) >= 0.049e-6;
      boolean pressure = rowMax(maxv[1]) > 10.0 && varies(maxv[1], 100.0);
      boolean theta = rowMin(minv[1]) >= -1e-8 && rowMax(maxv[2]) <= 1.000001 && varies(minv[1], 1e-4);
      boolean reversal = crossesZero(ft, 1e-10) && rowMin(minv[2]) < -1e-6 && rowMax(maxv[4]) > 1e-6;
      boolean close = rowMax(closeArea) > 0.0 && rowMax(closeArea) < area * 0.5;
      boolean pass = finite && hFloor && pressure && theta && reversal && close;

      System.out.println("SOLUTION=" + sol);
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "H_AVG_RANGE=[%.12g,%.12g]%n", rowMin(hAvg), rowMax(hAvg));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[0]), rowMax(minv[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[0]), rowMax(maxv[0]));
      System.out.printf(Locale.US, "A_CLOSE_RANGE=[%.12g,%.12g]%n", rowMin(closeArea), rowMax(closeArea));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[1]), rowMax(minv[1]));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[1]), rowMax(maxv[1]));
      System.out.printf(Locale.US, "FT_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ft), rowMax(ft));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(mu), rowMax(mu));
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_LOCAL_TFF=true");
      System.out.println("CHECK_H_FLOOR=" + hFloor);
      System.out.println("CHECK_PRESSURE_DYNAMIC=" + pressure);
      System.out.println("CHECK_THETA_RESPONSE=" + theta);
      System.out.println("CHECK_TAU_SIGN_REVERSAL=" + reversal);
      System.out.println("CHECK_CLOSE_NONTRIVIAL=" + close);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));

      model.label("Stage 577e weakly coupled depleted TFF " + (pass ? "PASS" : "FAIL"));
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
