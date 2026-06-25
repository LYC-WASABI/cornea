import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class resume_stage576p2_after060 {
  private static final String INPUT =
      "build_stage576p2_moving_structure_sparse_jfo_output_Model.mph";
  private static final String CHECKPOINT =
      "576p2r_stage576_moving_structure_sparse_jfo_checkpoint.mph";
  private static final String RESULTS =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
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

  private static void fullyCoupled(Model model, String solution,
      double damping, int maxIterations) {
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

  private static String buildSolid(Model model, String previous, int index) {
    String study = "std576p2r_solid_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p2 resume moving contact " + index);
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
    fullyCoupled(model, solution, 0.03, 1200);
    return solution;
  }

  private static String buildJfo(Model model, String solid, int index) {
    String study = "std576p2r_jfo_" + index;
    model.study().create(study);
    model.study(study).label("Stage 576p2 resume sparse JFO " + index);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", "sol142");
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
    dependent.set("initsol", "sol142");
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", solid);
    dependent.set("notsolnum", "last");
    fullyCoupled(model, solution, 0.3, 400);
    return solution;
  }

  private static String data(Model model, String tag, String solution) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double global(Model model, String data, String tag,
      String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double[] integrate(Model model, String data, String tag,
      String[] expressions) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expressions);
    double[][] rows = model.result().numerical(tag).getReal();
    double[] result = new double[rows.length];
    for (int i = 0; i < rows.length; i++) {
      result[i] = rows[i][rows[i].length - 1];
    }
    return result;
  }

  private static double extreme(Model model, String data, String tag,
      String type, String expression) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double ratio(double a, double b) {
    return Math.abs(b) > 1e-30 ? a / b : Double.NaN;
  }

  private static void evaluate(Model model, String solid, String pressure,
      double fraction) {
    String ds = data(model, "dset576p2rSolid", solid);
    String dp = data(model, "dset576p2rPressure", pressure);
    double[] gap = integrate(model, ds, "int576p2rGap", new String[] {
      "M_core573", "M_core573*g_pair_valid573", "M_core573*Bfilm573",
      "M_core573*Y", "M_core573*Z"
    });
    double[] flow = integrate(model, dp, "int576p2rFlow", new String[] {
      "max(tff.p-p_amb573,0[Pa])",
      "M_drain573*max(tff.p-p_amb573,0[Pa])",
      "max(tff.p-p_amb573,0[Pa])*Y",
      "max(tff.p-p_amb573,0[Pa])*Z",
      "max(p_load573,0[Pa])"
    });
    double contact = global(model, ds, "eval576p2rContact", "Fn_contact570");
    double maxP = extreme(model, dp, "max576p2rP", "MaxSurface",
        "tff.p-p_amb573");
    double minTheta = extreme(model, dp, "min576p2rTheta", "MinSurface",
        "tff.theta");
    System.out.printf(Locale.US,
        "RESUME_POSITION fraction=%.4f Fcontact=%.12g gapValidCore=%.12g"
            + " activeCore=%.12g pressureInDrain=%.12g coreY=%.12g"
            + " pressureY=%.12g Ffilm=%.12g MaxP=%.12g MinTheta=%.12g"
            + " solid=%s pressure=%s%n",
        fraction, contact, ratio(gap[1], gap[0]), ratio(gap[2], gap[0]),
        ratio(flow[1], flow[0]), ratio(gap[3], gap[0]), ratio(flow[2], flow[0]),
        flow[4], maxP, minTheta, solid, pressure);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      String previous = "sol158";
      int index = 0;
      ArrayList<Double> positions = new ArrayList<Double>();
      for (double f = 0.6125; f <= 0.7500001; f += 0.0125) positions.add(f);
      for (double f = 0.775; f <= 1.0000001; f += 0.025) positions.add(f);
      for (double fraction : positions) {
        model.param().set("t_position576p2", String.format(Locale.US,
            "T_pre572+%.12g*T_slide572", fraction));
        String solid = buildSolid(model, previous, index);
        System.out.println("RESUME_SOLID index=" + index + " fraction="
            + fraction + " solution=" + solid);
        model.sol(solid).runAll();
        previous = solid;
        if (Math.abs(fraction - 0.75) < 1e-8
            || Math.abs(fraction - 1.0) < 1e-8) {
          String pressure = buildJfo(model, solid, index);
          System.out.println("RESUME_JFO fraction=" + fraction
              + " solution=" + pressure);
          model.sol(pressure).runAll();
          evaluate(model, solid, pressure, fraction);
          model.save(CHECKPOINT);
        }
        index++;
      }
      model.label("Stage 576p2 resumed moving structure sparse JFO results");
      model.save(RESULTS);
      System.out.println("RESUME_STATUS=COMPLETE");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
