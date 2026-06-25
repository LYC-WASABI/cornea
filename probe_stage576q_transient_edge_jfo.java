import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576q_transient_edge_jfo {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String OUTPUT =
      "576q3_stage576_ramped_transient_edge_jfo_025_results.mph";
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

  private static void configure(Model model) {
    ModelNode comp = model.component("comp1");
    model.param().set("t_position576p2", "T_pre572+0.25*T_slide572");
    model.param().set("t_ramp576q", "1e-3[s]");
    int[] allEdges = comp.physics("tff").feature("bdr1")
        .selection().entities();
    comp.physics("tff").feature("bdr_inlet520").active(true);
    comp.physics("tff").feature("bdr_inlet520").selection().set(allEdges);
    comp.physics("tff").feature("bdr_outlet520").active(false);
    comp.physics("tff").feature("bdr_left520").active(false);
    comp.physics("tff").feature("bdr_right520").active(false);
    comp.physics("tff").feature("wc_open_anchor573").active(false);
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc573");
    String ramp = "if(t<t_ramp576q,0.5-0.5*cos(pi*t/t_ramp576q),1)";
    ffp.set("vw", new String[] {
      "0",
      "-(" + ramp + ")*lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Z",
      "(" + ramp + ")*lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Y"
    });
  }

  private static String build(Model model) {
    String study = "std576q_transient_025";
    model.study().create(study);
    model.study(study).label("Stage 576q transient edge-pressure JFO at 25%");
    model.study(study).create("time", "Transient");
    model.study(study).feature("time").set(
        "tlist", "range(0[s],2e-5[s],3e-3[s])");
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", "sol142");
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", "last");
    String step = study + "/time";
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
    dependent.set("initsol", "sol142");
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", "sol149");
    dependent.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", "range(0[s],2e-5[s],3e-3[s])");
    try { time.set("maxorder", 2); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "1e-9[s]"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "1e-5[s]"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) time.create("fc1", "FullyCoupled");
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 100);
    return solution;
  }

  private static double lastSurface(Model model, String data, String tag,
      String type, String expression) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      configure(model);
      String solution = build(model);
      System.out.println("TRANSIENT_JFO_START=" + solution);
      model.sol(solution).runAll();
      String data = "dset576qTransient";
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", solution);
      double film = lastSurface(model, data, "int576qFilm", "IntSurface",
          "max(p_load573,0[Pa])");
      double maxP = lastSurface(model, data, "max576qP", "MaxSurface",
          "tff.p-p_amb573");
      double minTheta = lastSurface(model, data, "min576qTheta", "MinSurface",
          "tff.theta");
      System.out.printf(Locale.US,
          "TRANSIENT_JFO_RESULT Ffilm=%.12g MaxP=%.12g MinTheta=%.12g%n",
          film, maxP, minTheta);
      model.label("Stage 576q3 ramped transient edge-pressure JFO 25% results");
      model.save(OUTPUT);
      System.out.println("TRANSIENT_JFO_STATUS=COMPLETE");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
