import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574_midpoint_local_patch_jfo {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static String solve(
      Model model, ModelNode comp, String suffix, String initial) {
    String study = "std574_local_" + suffix;
    try { model.study().remove(study); }
    catch (Exception ignored) {}
    model.study().create(study);
    model.study(study).label(
        "Stage 574 midpoint local-patch JFO " + suffix);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "off", "ge_force_total111", "off", "tff", "on",
          "frame:spatial1", "on", "frame:material1", "on",
          "comp1", "on"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initial);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String tag : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dependent = model.sol(solution).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", initial);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", "sol94");
    dependent.set("notsolnum", "last");
    SolverFeature stationary = model.sol(solution).feature("s1");
    for (String tag : stationary.feature().tags()) {
      if (tag.startsWith("se")) {
        try { stationary.feature().remove(tag); }
        catch (Exception ignored) {}
      }
    }
    if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", "0.5");
    stationary.feature("fc1").set("maxiter", 200);
    System.out.println("RUN " + suffix + " solution=" + solution);
    model.sol(solution).runAll();
    return solution;
  }

  private static double[] evaluate(
      Model model, String solution, String suffix) {
    String dataset = "dset574lp_" + suffix;
    String eval = "eval574lp_" + suffix;
    removeDataset(model, dataset);
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).set("solution", solution);
    removeNumerical(model, eval);
    model.result().numerical().create(eval, "EvalGlobal");
    model.result().numerical(eval).set("data", dataset);
    model.result().numerical(eval).set("expr", new String[] {
        "intop_film(tff.p-p_amb573)",
        "intop_film(max(tff.p-p_amb573,0[Pa]))",
        "intop_film(p_load573)",
        "intop_film(tff.theta)/intop_film(1)",
        "intop_film(M_core573)/intop_film(1)",
        "intop_film(Bfilm573*M_core573)"
            + "/(intop_film(M_core573)+1e-30[m^2])"
    });
    double[][] raw = model.result().numerical(eval).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = raw[i][0];
      if (!Double.isFinite(values[i])) {
        throw new IllegalStateException(suffix + " nonfinite result");
      }
    }
    for (String type : new String[] {"MinSurface", "MaxSurface"}) {
      String tag = (type.startsWith("Min") ? "min" : "max")
          + "574lp_" + suffix;
      removeNumerical(model, tag);
      model.result().numerical().create(tag, type);
      model.result().numerical(tag).set("data", dataset);
      model.result().numerical(tag)
          .selection().named("sel_local_cornea_patch574");
      model.result().numerical(tag).set("expr", "tff.p-p_amb573");
      double pressure =
          model.result().numerical(tag).getReal()[0][0];
      System.out.printf(Locale.US, "%s_%s=%.12g%n",
          type.startsWith("Min") ? "MINP" : "MAXP",
          suffix, pressure);
    }
    System.out.println("VALUES_" + suffix + "="
        + Arrays.toString(values));
    return values;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574g_stage574_local_cornea_patch_structure_checked.mph");
      ModelNode comp = model.component("comp1");
      comp.variable("var_dynamic_motion572")
          .set("tau572", "time_offset572");
      model.param().set(
          "time_offset572", "T_pre572+0.5*T_slide572");
      model.param().set(
          "omega_mid574",
          "theta_slide_total*0.5*pi/T_slide572");
      model.param().set("lambda_v574", "0");
      model.param().set("lambda_h574", "0");
      comp.variable("var_cornea_dynamic_regions573").set(
          "g_pair_native573",
          "withsol('sol94',geomgap_dst_cp_lid_cornea)");

      comp.physics("tff").feature("init1").set("pfilm", "p_amb573");
      for (String tag : new String[] {
          "bdr_inlet520", "bdr_outlet520",
          "bdr_left520", "bdr_right520"
      }) {
        comp.physics("tff").feature(tag).set("pf0", "p_amb573");
        comp.physics("tff").feature(tag).set("theta_0", "1");
      }
      comp.physics("tff").feature("ffp1").set("hb1", "0");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0", "-M_core573*lambda_v574*omega_mid574*Z",
            "M_core573*lambda_v574*omega_mid574*Y"
          });

      comp.physics("tff").feature("ffp1").set("hw1", "3[um]");
      comp.physics("tff").feature("ms_vent573").set("QudR", "0");
      comp.physics("tff").feature("wc_open_anchor573")
          .set("weakExpression",
              "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
      String baseline = solve(model, comp, "baseline", "sol94");
      double[] baselineValues = evaluate(model, baseline, "baseline");
      model.label("Stage 574 local-patch zero-speed baseline");
      model.save("574h_stage574_local_patch_baseline.mph");

      String velocity = baseline;
      double[] velocityFactors = new double[] {
          1e-5, 1e-4, 1e-3, 1e-2, 0.05,
          0.1, 0.2, 0.4, 0.7, 1.0
      };
      for (int i = 0; i < velocityFactors.length; i++) {
        model.param().set("lambda_v574",
            String.format(Locale.US, "%.12g", velocityFactors[i]));
        velocity = solve(
            model, comp, "velocity_" + (i + 1), velocity);
      }
      double[] velocityValues = evaluate(model, velocity, "velocity");
      model.label("Stage 574 local-patch constant-film velocity");
      model.save("574i_stage574_local_patch_velocity.mph");

      model.param().set("lambda_h574", "1e-4");
      comp.physics("tff").feature("ffp1").set(
          "hw1", "(1-lambda_h574)*3[um]+lambda_h574*h_calc573");
      comp.physics("tff").feature("ms_vent573").set("QudR", "Qvent573");
      comp.physics("tff").feature("wc_open_anchor573").set(
          "weakExpression",
          "-kanchor573*(pfilm-p_amb573)*test(pfilm)");
      String trueGap = velocity;
      double[] thicknessFactors = new double[] {
          1e-4, 1e-3, 1e-2, 0.05, 0.1,
          0.2, 0.4, 0.7, 1.0
      };
      for (int i = 0; i < thicknessFactors.length; i++) {
        model.param().set("lambda_h574",
            String.format(Locale.US, "%.12g", thicknessFactors[i]));
        trueGap = solve(
            model, comp, "gap_" + (i + 1), trueGap);
      }
      comp.physics("tff").feature("ffp1").set("hw1", "h_calc573");
      double[] trueGapValues = evaluate(model, trueGap, "true_gap");
      model.label("Stage 574 midpoint local-patch true-gap JFO checked");
      model.save("574j_stage574_midpoint_local_patch_jfo_checked.mph");

      if (Math.abs(baselineValues[0]) > 1e-8) {
        throw new IllegalStateException("Baseline pressure is not zero");
      }
      if (Math.abs(trueGapValues[2]) > 0.3) {
        throw new IllegalStateException("Core film load exceeds 0.3 N");
      }
      System.out.println("LOCAL_PATCH_MIDPOINT_GATE=PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
