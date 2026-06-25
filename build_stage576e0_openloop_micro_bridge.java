import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576e0_openloop_micro_bridge {
  private static final String BASE = "576e_stage576_transient_load_controller_setup.mph";
  private static final String SETUP = "576e0_stage576_openloop_micro_bridge_setup.mph";
  private static final String RESULTS = "576e0_stage576_openloop_micro_bridge_results.mph";
  private static final String CHECKED = "576e0_stage576_openloop_micro_bridge_checked.mph";
  private static final String INIT_SOL = "sol236";
  private static final String INIT_SOLNUM = "161";

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

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      model.param().set("q_scale574", "-10");
      model.param().set("alpha_pfb576e", "0");
      model.param().set("t_bridge_end576e", "t_start576e+10[us]");
      comp.physics("ge_force_total111").active(false);
      comp.physics("solid").prop("StructuralTransientBehavior").set("StructuralTransientBehavior", "Quasistatic");
      comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
        "0",
        "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
        "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
      });
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      String study = "std576e0_openloop_micro_bridge";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label("Stage 576e0 open-loop structure-JFO micro bridge");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set("tlist", "range(t_start576e,1[us],t_bridge_end576e)");
      model.study(study).feature("time").set("geometricNonlinearity", "on");
      model.study(study).feature("time").set("activate", new String[] {
        "solid", "on", "ge_force_total111", "off", "tff", "on",
        "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
      });
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", INIT_SOL);
      model.study(study).feature("time").set("initsoluse", "current");
      model.study(study).feature("time").set("initsolusesolnum", INIT_SOLNUM);
      String step = study + "/time";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        try { comp.physics("solid").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
      }
      for (String tag : comp.physics("tff").feature().tags()) {
        try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String sol = newest(model, before);
      SolverFeature dep = model.sol(sol).feature("v1");
      dep.set("initmethod", "sol");
      dep.set("initsol", INIT_SOL);
      dep.set("solnum", INIT_SOLNUM);
      dep.set("notsolmethod", "sol");
      dep.set("notsol", INIT_SOL);
      dep.set("notsolnum", INIT_SOLNUM);
      SolverFeature time = model.sol(sol).feature("t1");
      time.set("tlist", "range(t_start576e,1[us],t_bridge_end576e)");
      time.set("consistent", "off");
      time.set("initialstepbdfactive", "on");
      time.set("initialstepbdf", "1e-9");
      time.set("maxstepconstraintbdf", "const");
      time.set("maxstepbdf", "1[us]");
      for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      if (!has(time.feature().tags(), "fc1")) time.create("fc1", "FullyCoupled");
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.3");
      time.feature("fc1").set("maxiter", 120);
      model.label("Stage 576e0 open-loop micro-bridge setup");
      model.save(SETUP);
      System.out.println("RUN_SOL=" + sol);
      model.sol(sol).runAll();
      model.save(RESULTS);
      String data = "dset576e0_bridge";
      try { model.result().dataset().remove(data); } catch (Exception ignored) {}
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", sol);
      String eval = "eval576e0_bridge";
      try { model.result().numerical().remove(eval); } catch (Exception ignored) {}
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", data);
      model.result().numerical(eval).set("expr", new String[] {"t", "Fn_contact570", "Fn_film576e", "Fn_total576e"});
      double[][] values = model.result().numerical(eval).getReal();
      boolean pass = values[0].length >= 2 && Double.isFinite(min(values[3])) && Double.isFinite(max(values[3]));
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", min(values[0]), max(values[0]), values[0].length);
      System.out.printf(Locale.US, "FCONTACT_RANGE=[%.12g,%.12g] FFILM_RANGE=[%.12g,%.12g] FTOTAL_RANGE=[%.12g,%.12g]%n",
          min(values[1]), max(values[1]), min(values[2]), max(values[2]), min(values[3]), max(values[3]));
      System.out.println("BRIDGE_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) {
        model.label("Stage 576e0 open-loop micro-bridge checked");
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
