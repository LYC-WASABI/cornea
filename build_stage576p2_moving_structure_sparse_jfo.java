import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576p2_moving_structure_sparse_jfo {
  private static final String BASE =
      "575d_stage575_dynamic_active_gap_regularized_checked.mph";
  private static final String SETUP =
      "576p2_stage576_moving_structure_sparse_jfo_setup.mph";
  private static final String CHECKPOINT =
      "576p2_stage576_moving_structure_sparse_jfo_checkpoint.mph";
  private static final String RESULTS =
      "576p2_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String CHECKED =
      "576p2_stage576_moving_structure_sparse_jfo_checked.mph";
  private static final String INITIAL_SOLID = "sol119";
  private static final String SWEPT = "sel_film_swept571";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

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

  private static double global(
      Model model, String data, String tag, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double[] integrate(
      Model model, String data, String tag, String[] expressions) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expressions);
    double[][] rows = model.result().numerical(tag).getReal();
    double[] result = new double[rows.length];
    for (int i = 0; i < rows.length; i++) result[i] = rows[i][rows[i].length - 1];
    return result;
  }

  private static double surface(
      Model model, String data, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double ratio(double numerator, double denominator) {
    return Math.abs(denominator) > 1e-30 ? numerator / denominator : Double.NaN;
  }

  private static void configure(Model model, ModelNode comp) {
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "1");
    model.param().set("t_position576p2", "T_pre572");
    model.param().set("slide_fraction_position576p2",
        "if(t_position576p2<T_pre572,0,"
            + "if(t_position576p2<T_pre572+T_slide572,"
            + "0.5-0.5*cos(pi*(t_position576p2-T_pre572)/T_slide572),1))");
    model.param().set("phi_position576p2",
        "theta_slide_total*slide_fraction_position576p2");
    model.param().set("gate_speed576p", "0");
    model.param().set("kvent576p", "1e-4[kg/(m^2*s*Pa)]");
    model.param().set("kanchor576p", "1e-4[kg/(m^2*s*Pa)]");

    comp.physics("tff").selection().named(SWEPT);
    comp.variable("var_dynamic_motion572").selection().named(SWEPT);
    comp.variable("var_dynamic_motion572").set("tau572", "t_position576p2");
    comp.variable("var_dynamic_motion572").set(
        "theta_lid_spatial572",
        "theta_lid_physical572+lid_mask_aoffset572");
    comp.variable("var_cornea_dynamic_regions573").selection().named(SWEPT);
    comp.cpl("intop_film").selection().named(SWEPT);
    comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
    comp.variable("var_cornea_dynamic_regions573").set(
        "M_drain573", "M_lid_x572*M_drain_a573");
    comp.variable("var_cornea_dynamic_regions573").set(
        "M_open573", "max(1-M_drain573,0)");
    comp.variable("var_cornea_dynamic_regions573").set(
        "Qvent573", "-kvent576p*(1-Afilm573)*(tff.p-p_amb573)");
    comp.variable("var_cornea_dynamic_regions573").set(
        "p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");

    String phi = "phi_position576p2";
    String indent = "q_scale574*q_fixed574*1[mm]";
    comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
      "0",
      "Y*(cos(" + phi + ")-1)-Z*sin(" + phi + ")"
          + "-(" + indent + ")*(Y*cos(" + phi + ")-Z*sin(" + phi + "))/sqrt(Y^2+Z^2)",
      "Y*sin(" + phi + ")+Z*(cos(" + phi + ")-1)"
          + "-(" + indent + ")*(Y*sin(" + phi + ")+Z*cos(" + phi + "))/sqrt(Y^2+Z^2)"
    });

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {
      "0",
      "-gate_speed576p*lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Z",
      "gate_speed576p*lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Y"
    });
    comp.physics("tff").feature("ms_vent573").selection().named(SWEPT);
    comp.physics("tff").feature("ms_vent573").set("QudR", "Qvent573");
    comp.physics("tff").feature("wc_open_anchor573").selection().named(SWEPT);
    comp.physics("tff").feature("wc_open_anchor573").set(
        "weakExpression",
        "-kanchor576p*(pfilm-p_amb573)*test(pfilm)");
    try { comp.physics("tff").feature("init1").set("pfilm", "p_amb573"); }
    catch (Exception ignored) {}
    try { comp.physics("ge_force_total111").active(false); } catch (Exception ignored) {}
  }

  private static void fullyCoupledStationary(
      Model model, String solution, double damping, int maxIterations) {
    SolverFeature stationary = model.sol(solution).feature("s1");
    for (String feature : stationary.feature().tags()) {
      if (feature.startsWith("se")) {
        try { stationary.feature().remove(feature); } catch (Exception ignored) {}
      }
    }
    if (!has(stationary.feature().tags(), "fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", damping);
    stationary.feature("fc1").set("maxiter", maxIterations);
  }

  private static String buildEnvironment(Model model, String solid) {
    String study = "std576p2_environment";
    model.study().create(study);
    model.study(study).label("Stage 576p2 full-swept environment pressure");
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "init");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", solid);
    dependent.set("notsolnum", "last");
    fullyCoupledStationary(model, solution, 0.5, 200);
    return solution;
  }

  private static String buildSolid(Model model, String previous, int index) {
    String study = "std576p2_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p2 moving contact position " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", previous);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String feature : new String[] {"dcnt1", "disp_lid_time"}) {
      try { comp.physics("solid").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", previous);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", previous);
    dependent.set("notsolnum", "last");
    fullyCoupledStationary(model, solution, 0.05, 800);
    return solution;
  }

  private static String buildJfo(
      Model model, String environment, String solid, int index) {
    String study = "std576p2_jfo_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p2 sparse JFO position " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", environment);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", environment);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", solid);
    dependent.set("notsolnum", "last");
    fullyCoupledStationary(model, solution, 0.3, 300);
    return solution;
  }

  private static boolean evaluate(
      Model model, String solid, String pressure, int index, double fraction) {
    String solidData = dataset(model, "dset576p2Solid", solid);
    String pressureData = dataset(model, "dset576p2Pressure", pressure);
    double[] gap = integrate(model, solidData, "int576p2Gap", new String[] {
      "1", "M_core573", "M_core573*g_pair_valid573",
      "M_core573*Bfilm573", "M_drain573", "M_drain573*g_pair_valid573"
    });
    double[] flow = integrate(model, pressureData, "int576p2Flow", new String[] {
      "max(tff.p-p_amb573,0[Pa])",
      "M_drain573*max(tff.p-p_amb573,0[Pa])",
      "max(tff.p-p_amb573,0[Pa])*Y",
      "max(tff.p-p_amb573,0[Pa])*Z",
      "max(p_load573,0[Pa])",
      "max(p_load573,0[Pa])*Y",
      "max(p_load573,0[Pa])*Z",
      "M_core573*Y", "M_core573*Z", "M_core573"
    });
    double contact = global(model, solidData, "eval576p2Contact", "Fn_contact570");
    double maxPressure = surface(model, pressureData, "max576p2Pressure",
        "MaxSurface", "tff.p-p_amb573");
    double minTheta = surface(model, pressureData, "min576p2Theta",
        "MinSurface", "tff.theta");
    double validCore = ratio(gap[2], gap[1]);
    double validDrain = ratio(gap[5], gap[4]);
    double activeCore = ratio(gap[3], gap[1]);
    double pressureInDrain = ratio(flow[1], flow[0]);
    double pressureY = ratio(flow[2], flow[0]);
    double pressureZ = ratio(flow[3], flow[0]);
    double loadY = ratio(flow[5], flow[4]);
    double loadZ = ratio(flow[6], flow[4]);
    double coreY = ratio(flow[7], flow[9]);
    double coreZ = ratio(flow[8], flow[9]);
    System.out.printf(Locale.US,
        "POSITION index=%d fraction=%.3f Fcontact=%.12g gapValidCore=%.12g gapValidDrain=%.12g activeCore=%.12g pressureInDrain=%.12g coreY=%.12g coreZ=%.12g pressureY=%.12g pressureZ=%.12g loadY=%.12g loadZ=%.12g Ffilm=%.12g MaxP=%.12g MinTheta=%.12g solid=%s pressure=%s%n",
        index, fraction, contact, validCore, validDrain, activeCore,
        pressureInDrain, coreY, coreZ, pressureY, pressureZ,
        loadY, loadZ, flow[4], maxPressure, minTheta, solid, pressure);
    return Double.isFinite(contact) && Double.isFinite(validCore)
        && validCore > 0.90 && Double.isFinite(maxPressure)
        && Double.isFinite(minTheta) && minTheta >= -1e-8;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      configure(model, comp);
      model.label("Stage 576p2 moving structure sparse JFO setup");
      model.save(SETUP);

      model.param().set("gate_speed576p", "0");
      String environment = buildEnvironment(model, INITIAL_SOLID);
      model.sol(environment).runAll();
      model.param().set("gate_speed576p", "1");
      System.out.println("ENVIRONMENT=" + environment);

      String previousSolid = INITIAL_SOLID;
      boolean pass = true;
      for (int index = 0; index <= 20; index++) {
        double fraction = index / 20.0;
        model.param().set("t_position576p2", String.format(Locale.US,
            "T_pre572+%.12g*T_slide572", fraction));
        String solid = buildSolid(model, previousSolid, index);
        System.out.println("RUN_SOLID index=" + index + " fraction=" + fraction
            + " solution=" + solid);
        model.sol(solid).runAll();
        previousSolid = solid;
        if (index % 5 == 0) {
          String pressure = buildJfo(model, environment, solid, index);
          System.out.println("RUN_JFO index=" + index + " solution=" + pressure);
          model.sol(pressure).runAll();
          pass = evaluate(model, solid, pressure, index, fraction) && pass;
          model.save(CHECKPOINT);
        }
      }
      model.label(pass
          ? "Stage 576p2 moving structure sparse JFO checked"
          : "Stage 576p2 moving structure sparse JFO failed");
      model.save(RESULTS);
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) model.save(CHECKED);
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
