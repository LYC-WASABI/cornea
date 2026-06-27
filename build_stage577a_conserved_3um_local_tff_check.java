import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage577a_conserved_3um_local_tff_check {
  private static final String INPUT = "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String RESULTS = "577a_stage577_conserved_3um_local_tff_check_results.mph";
  private static final String STUDY = "std577a";
  private static final String DATASET = "dset577a";
  private static final String SWEPT = "sel_film_swept571";
  private static final String INIT_PRESSURE = "sol271";
  private static final String INIT_SOLID = "sol273";
  private static final String V_SIGNED = "lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*sqrt(Y^2+Z^2)";
  private static final String TAU_SIGNED =
      "M_core573*Bfilm573*mu_lub577a*(" + V_SIGNED + ")/max(h_TFF577a,h_num573)";
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

  private static boolean rangeVaries(double[] values, double minSpan) {
    return rowMax(values) - rowMin(values) > minSpan;
  }

  private static void configure(Model model, ModelNode comp) {
    model.param().set("mu_lub577a", "1e-3[Pa*s]");
    model.param().set("time_offset572", "0[s]");
    String motion = "var_dynamic_motion572";
    comp.variable(motion).set("tau572", "t+time_offset572");
    comp.variable(motion).set("slide_fraction572",
        "if(tau572<T_pre572,0,"
        + "if(tau572<T_pre572+T_slide572,0.5-0.5*cos(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,0.5+0.5*cos(pi*(tau572-T_pre572-T_slide572)/T_slide572),0)))");
    comp.variable(motion).set("phi_lid_rot572", "theta_slide_total*slide_fraction572");
    comp.variable(motion).set("theta_lid_physical572", "-35[deg]+70[deg]*slide_fraction572");
    comp.variable(motion).set("theta_lid_spatial572", "theta_lid_physical572+lid_mask_aoffset572");
    comp.variable(motion).set("omega_lid_rot572",
        "if(tau572<T_pre572,0[rad/s],"
        + "if(tau572<T_pre572+T_slide572,theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572)/T_slide572),"
        + "if(tau572<T_pre572+2*T_slide572,-theta_slide_total*0.5*pi/T_slide572*sin(pi*(tau572-T_pre572-T_slide572)/T_slide572),0[rad/s])))");

    String vars = "var_conserved_film577a";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(SWEPT);
    comp.variable(vars).set("h_TFF577a", "3[um]");
    comp.variable(vars).set("vtheta_signed577a", V_SIGNED);
    comp.variable(vars).set("tau_tff_signed577a", TAU_SIGNED);
    comp.variable(vars).set("tau_tff_abs577a", TAU_ABS);
    comp.physics("tff").feature("ffp1").set("hw1", "h_TFF577a");
  }

  private static String runTff(Model model, ModelNode comp) {
    try { model.study().remove(STUDY); } catch (Exception ignored) {}
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 577a conserved 3um local TFF check");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set("tlist", "range(T_pre572,T_slide572/20,T_pre572+2*T_slide572)");
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
      try { comp.physics("tff").feature(feature).set("StudyStep", step); } catch (Exception ignored) {}
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

      double[][] time = evalGlobal(model, DATASET, "eval577a_time", new String[] {"t"});
      double[][] integ = surface(model, DATASET, "int577a", "IntSurface",
          new String[] {"1", "omega_lid_rot572", "h_TFF577a", "max(tff.p-p_amb573,0[Pa])",
              "max(p_load573,0[Pa])", "tau_tff_signed577a", "tau_tff_abs577a", "M_core573"});
      double[][] minv = surface(model, DATASET, "min577a", "MinSurface",
          new String[] {"h_TFF577a", "tff.theta", "tau_tff_signed577a"});
      double[][] maxv = surface(model, DATASET, "max577a", "MaxSurface",
          new String[] {"h_TFF577a", "tff.p-p_amb573", "tff.theta", "tau_tff_signed577a", "tau_tff_abs577a"});

      double area = integ[0][0];
      double[] omega = new double[integ[1].length];
      double[] hAvg = new double[integ[2].length];
      double[] pInt = integ[3], pLoad = integ[4], ft = integ[5], ftAbs = integ[6], coreArea = integ[7];
      double[] mu = new double[ft.length];
      for (int i = 0; i < ft.length; i++) {
        omega[i] = integ[1][i] / area;
        hAvg[i] = integ[2][i] / area;
        mu[i] = Math.abs(ft[i]) / 0.03;
      }

      addSurfacePlot(model, "pg577a_h", "Stage 577a conserved 3um film thickness", "h_TFF577a", "m");
      addSurfacePlot(model, "pg577a_p", "Stage 577a pressure", "tff.p-p_amb573", "Pa");
      addSurfacePlot(model, "pg577a_tau", "Stage 577a signed shear proxy", "tau_tff_signed577a", "Pa");

      boolean finite = finite(hAvg) && finite(pInt) && finite(pLoad) && finite(ft) && finite(mu) && finite(minv[1]) && finite(maxv[1]);
      boolean hConserved = Math.abs(rowMin(hAvg) - 3e-6) < 1e-10
          && Math.abs(rowMax(hAvg) - 3e-6) < 1e-10
          && Math.abs(rowMax(maxv[0]) - rowMin(minv[0])) < 1e-12;
      boolean pResponds = rowMax(maxv[1]) > 10.0 && rangeVaries(maxv[1], 100.0);
      boolean reversal = crossesZero(omega, 1e-3) && crossesZero(ft, 1e-10)
          && rowMin(minv[2]) < -1e-6 && rowMax(maxv[3]) > 1e-6;
      boolean local = rowMin(coreArea) >= 0.0 && rowMax(coreArea) < area;
      boolean pass = finite && hConserved && pResponds && reversal && local;

      model.label("Stage 577a conserved 3um local TFF check " + (pass ? "PASS" : "FAIL"));
      model.save(RESULTS);

      System.out.println("SOLUTION=" + sol);
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", rowMin(time[0]), rowMax(time[0]), time[0].length);
      System.out.printf(Locale.US, "H_AVG_RANGE=[%.12g,%.12g]%n", rowMin(hAvg), rowMax(hAvg));
      System.out.printf(Locale.US, "H_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[0]), rowMax(minv[0]));
      System.out.printf(Locale.US, "H_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[0]), rowMax(maxv[0]));
      System.out.printf(Locale.US, "P_MAX_RANGE=[%.12g,%.12g]%n", rowMin(maxv[1]), rowMax(maxv[1]));
      System.out.printf(Locale.US, "THETA_MIN_RANGE=[%.12g,%.12g]%n", rowMin(minv[1]), rowMax(minv[1]));
      System.out.printf(Locale.US, "FT_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(ft), rowMax(ft));
      System.out.printf(Locale.US, "TAU_SIGNED_RANGE=[%.12g,%.12g]%n", rowMin(minv[2]), rowMax(maxv[3]));
      System.out.printf(Locale.US, "MU_TFF_ALT_RANGE=[%.12g,%.12g]%n", rowMin(mu), rowMax(mu));
      System.out.println("CHECK_FINITE=" + finite);
      System.out.println("CHECK_LOCAL_TFF=" + local);
      System.out.println("CHECK_H_CONSERVATION=" + hConserved);
      System.out.println("CHECK_PRESSURE_DYNAMIC=" + pResponds);
      System.out.println("CHECK_TAU_SIGN_REVERSAL=" + reversal);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
