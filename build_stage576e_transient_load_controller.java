import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576e_transient_load_controller {
  private static final String BASE = "576b_stage576_dynamic_load_verification_results.mph";
  private static final String SETUP = "576e_stage576_transient_load_controller_setup.mph";
  private static final String RESULTS = "576e_stage576_transient_load_controller_results.mph";
  private static final String CHECKED = "576e_stage576_transient_load_controller_checked.mph";
  private static final String INIT_SOL = "sol236";
  private static final String INIT_SOLNUM = "161";
  private static final String STUDY = "std576e_transient_load_controller";
  private static final String DATASET = "dset576e_transient_load_controller";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void configure(Model model, ModelNode comp) {
    model.param().set("F_target576e", "0.03[N]");
    model.param().set("kq_ctrl576e", "0.002[N]");
    model.param().set("tau_ctrl576e", "10[ms]");
    model.param().set("q_ctrl_init576e", "-10");
    model.param().set("alpha_pfb576e", "0");
    model.param().set("t_start576e", "T_pre572+0.80*T_slide572");
    model.param().set("t_end576e", "T_pre572+0.96*T_slide572");
    model.param().set("v_blink_avg", "0.15[m/s]");
    model.param().set("lambda_h574", "1");
    model.param().set("lambda_v574", "1");
    model.param().set("h_active_max573", "50[um]");
    model.param().set("dh_active573", "5[um]");
    model.param().set("dh_break573", "0.005[um]");
    model.param().set("kvent573", "1e-7[kg/(m^2*s*Pa)]");
    model.param().set("kanchor573", "1e-7[kg/(m^2*s*Pa)]");

    comp.variable("var_dynamic_motion572").set("tau572", "t");
    comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574");
    comp.variable("var_cornea_dynamic_regions573").set("M_core573", "M_lid572");
    comp.variable("var_cornea_dynamic_regions573").set("M_drain573", "M_lid_x572*M_drain_a573");
    comp.variable("var_cornea_dynamic_regions573").set("M_open573", "max(1-M_drain573,0)");
    comp.variable("var_cornea_dynamic_regions573").set("B_low573", "0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))");
    comp.variable("var_cornea_dynamic_regions573").set("B_high573", "0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))");
    comp.variable("var_cornea_dynamic_regions573").set("Bfilm573", "g_pair_valid573*B_low573*B_high573");
    comp.variable("var_cornea_dynamic_regions573").set("g_pair_physical573", "min(g_pair_safe573,h_active_max573)");
    comp.variable("var_cornea_dynamic_regions573").set(
        "h_wet573", "h_num573+0.5*((g_pair_physical573-h_num573)+sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))");
    comp.variable("var_cornea_dynamic_regions573").set(
        "Afilm573", "M_core573*Bfilm573+max(M_drain573-M_core573,0)*g_pair_valid573*B_high573");
    comp.variable("var_cornea_dynamic_regions573").set("h_calc573", "Afilm573*h_wet573+(1-Afilm573)*h_background573");
    comp.variable("var_cornea_dynamic_regions573").set("Qvent573", "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
    comp.variable("var_cornea_dynamic_regions573").set("p_load573", "M_core573*Bfilm573*(tff.p-p_amb573)");

    String vars = "var_transient_load_controller576e";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).label("Stage 576e transient total-load controller variables");
    comp.variable(vars).set("Fn_film576e", "intop_film(p_load573)");
    comp.variable(vars).set("Fn_total576e", "Fn_contact570+Fn_film576e");
    comp.variable(vars).set("Ferr576e", "(Fn_total576e-F_target576e)/kq_ctrl576e");

    PhysicsFeature ge = comp.physics("ge_force_total111").feature("ge1");
    ge.set("name", 1, 1, "q_ctrl576e");
    ge.set("equation", 1, 1, "tau_ctrl576e*d(q_ctrl576e,t)+Ferr576e");
    ge.set("initialValueU", 1, 1, "q_ctrl_init576e");
    ge.set("initialValueUt", 1, 1, "0");
    ge.set("description", 1, 1, "Transient normal displacement controller for 0.03 N total load");
    ge.set("DependentVariableQuantity", "dimensionless");
    ge.set("SourceTermQuantity", "dimensionless");
    comp.physics("ge_force_total111").active(true);

    comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
      "0",
      "-q_ctrl576e*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
      "-q_ctrl576e*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
    });
    try { comp.physics("solid").feature("load_pfilm576a").active(false); } catch (Exception ignored) {}
    try { comp.physics("solid").feature("load_pfilm576c").active(false); } catch (Exception ignored) {}
    String load = "load_pfilm576e";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label("Stage 576e current-time film pressure feedback");
    comp.physics("solid").feature(load).selection().named("sel_local_cornea_patch574");
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-alpha_pfb576e*p_load573*nx",
      "-alpha_pfb576e*p_load573*ny",
      "-alpha_pfb576e*p_load573*nz"
    });

    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    comp.physics("solid").prop("StructuralTransientBehavior")
        .set("StructuralTransientBehavior", "Quasistatic");
    contact.set("pairSelection", "list");
    contact.set("pairs", new String[] {"cp_lid_cornea"});
    try { contact.set("useCutback", "1"); } catch (Exception ignored) {}
    for (String childTag : contact.feature().tags()) {
      PhysicsFeature child = contact.feature(childTag);
      if ("Friction".equals(child.getType()) || child.label().toLowerCase(Locale.ROOT).contains("fric")) child.active(false);
    }

    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
    ffp.set("hb1", "0");
    ffp.set("vw", new String[] {"0", "-omega_lid_rot572*Z", "omega_lid_rot572*Y"});
    try { comp.physics("tff").feature("ms_vent573").set("QudR", "Qvent573"); } catch (Exception ignored) {}
    try {
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression", "-kanchor573*(1-M_drain573)*(pfilm-p_amb573)*test(pfilm)");
    } catch (Exception ignored) {}
  }

  private static String buildStudy(Model model, ModelNode comp) {
    removeStudy(model, STUDY);
    model.study().create(STUDY);
    model.study(STUDY).label("Stage 576e transient total-load controller window");
    model.study(STUDY).create("time", "Transient");
    model.study(STUDY).feature("time").set("tlist", "range(t_start576e,T_slide572/400,t_end576e)");
    model.study(STUDY).feature("time").set("geometricNonlinearity", "on");
    model.study(STUDY).feature("time").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "on", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(STUDY).feature("time").set("useinitsol", "on");
    model.study(STUDY).feature("time").set("initmethod", "sol");
    model.study(STUDY).feature("time").set("initsol", INIT_SOL);
    model.study(STUDY).feature("time").set("initsoluse", "current");
    model.study(STUDY).feature("time").set("initsolusesolnum", INIT_SOLNUM);
    String step = STUDY + "/time";
    for (String tag : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576e"}) {
      try { comp.physics("solid").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
    }
    comp.physics("ge_force_total111").feature("ge1").set("StudyStep", step);
    String[] before = model.sol().tags();
    model.study(STUDY).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dep = model.sol(sol).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", INIT_SOL);
    dep.set("solnum", INIT_SOLNUM);
    dep.set("notsolmethod", "sol");
    dep.set("notsol", INIT_SOL);
    dep.set("notsolnum", INIT_SOLNUM);
    SolverFeature time = model.sol(sol).feature("t1");
    time.set("tlist", "range(t_start576e,T_slide572/400,t_end576e)");
    time.set("consistent", "off");
    time.set("initialstepbdfactive", "on");
    time.set("initialstepbdf", "1e-7");
    time.set("maxstepconstraintbdf", "const");
    time.set("maxstepbdf", "T_slide572/400");
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    try { time.set("rtol", "0.002"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      time.create("fc1", "FullyCoupled");
    }
    try {
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.3");
      time.feature("fc1").set("maxiter", 100);
    } catch (Exception ignored) {}
    return sol;
  }

  private static double[][] global(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double min(double[] values) {
    double out = Double.POSITIVE_INFINITY;
    for (double value : values) out = Math.min(out, value);
    return out;
  }

  private static double max(double[] values) {
    double out = Double.NEGATIVE_INFINITY;
    for (double value : values) out = Math.max(out, value);
    return out;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      configure(model, comp);
      model.label("Stage 576e transient load-controller setup");
      model.save(SETUP);
      System.out.println("SAVED_SETUP=" + SETUP);
      String sol = buildStudy(model, comp);
      System.out.println("RUN_SOL=" + sol);
      model.sol(sol).runAll();
      model.save(RESULTS);
      System.out.println("SAVED_RESULTS=" + RESULTS);

      removeDataset(model, DATASET);
      model.result().dataset().create(DATASET, "Solution");
      model.result().dataset(DATASET).set("solution", sol);
      double[][] values = global(model, DATASET, "eval576e_controller", new String[] {
        "t", "q_ctrl576e", "Fn_contact570", "Fn_film576e", "Fn_total576e"
      });
      double[] minTheta = surface(model, DATASET, "min576e_theta", "MinSurface", "tff.theta")[0];
      double[] maxP = surface(model, DATASET, "max576e_p", "MaxSurface", "tff.p-p_amb573")[0];
      double[] minGap = surface(model, DATASET, "min576e_gap", "MinSurface", "geomgap_dst_cp_lid_cornea")[0];
      int outside = 0;
      for (double total : values[4]) if (total < 0.025 || total > 0.035) outside++;
      boolean stable = finite(values[1]) && finite(values[2]) && finite(values[3]) && finite(values[4])
          && finite(minTheta) && finite(maxP) && finite(minGap) && min(minTheta) >= -1e-8;
      boolean boundedQ = min(values[1]) >= -12 && max(values[1]) <= -6;
      boolean pass = stable && boundedQ && outside <= Math.max(2, values[4].length/10);
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", min(values[0]), max(values[0]), values[0].length);
      System.out.printf(Locale.US, "Q_RANGE=[%.12g,%.12g]%n", min(values[1]), max(values[1]));
      System.out.printf(Locale.US, "FCONTACT_RANGE=[%.12g,%.12g]%n", min(values[2]), max(values[2]));
      System.out.printf(Locale.US, "FFILM_RANGE=[%.12g,%.12g]%n", min(values[3]), max(values[3]));
      System.out.printf(Locale.US, "FTOTAL_RANGE=[%.12g,%.12g] OUTSIDE=%d%n", min(values[4]), max(values[4]), outside);
      System.out.printf(Locale.US, "MINTHETA_RANGE=[%.12g,%.12g] MAXP_RANGE=[%.12g,%.12g] MINGAP_RANGE=[%.12g,%.12g]%n",
          min(minTheta), max(minTheta), min(maxP), max(maxP), min(minGap), max(minGap));
      System.out.println("NUMERIC_STATUS=" + (stable ? "PASS" : "FAIL"));
      System.out.println("CONTROL_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) {
        model.label("Stage 576e transient load-controller checked");
        model.save(CHECKED);
        System.out.println("SAVED_CHECKED=" + CHECKED);
      }
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
