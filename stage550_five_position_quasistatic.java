import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage550_five_position_quasistatic {
  static final String BASE =
      "543_stage540_jfo_joint_static_checked.mph";
  static final String INPUT =
      "550_stage550_five_position_input.mph";
  static final String SETUP =
      "551_stage550_five_position_setup.mph";
  static final String RESULTS =
      "552_stage550_five_position_results.mph";
  static final String CHECKED =
      "553_stage550_five_position_checked_9mm_track.mph";
  static final boolean ENDPOINT_REPAIR = false;

  static String lastFilmSolution;
  static String lastStructureSolution;

  static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) if (!old.contains(tag)) return tag;
    throw new IllegalStateException("No new solution created");
  }

  static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  static void configureFullyCoupled(Model model, String solution, int maxiter) {
    SolverFeature stationary = model.sol(solution).feature("s1");
    if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("maxiter", maxiter);
  }

  static double[] evaluateExistingMidpoint(Model model) {
    double[][] data = model.result().numerical("eval540").getReal();
    double[] result = new double[data.length];
    for (int i = 0; i < data.length; i++) result[i] = data[i][0];
    return result;
  }

  static String solveStructureBridge(
      Model model, String key, double angle, double replayTime,
      double phi, String pressureKey, String structureInitial) {
    String comp = "comp1";
    String ge = "ge_force_total111";
    String suffix = "550_" + pressureKey;
    model.param().set("t_replay",
        String.format(Locale.US, "%.12g[s]", replayTime));
    model.param().set("phi_qs142",
        String.format(Locale.US, "%.12g[deg]", phi));
    model.component(comp).physics("solid")
        .feature("load_partitioned_pfilm").set(
            "FperArea", new String[] {
              "-pfilm" + suffix + "*nx",
              "-pfilm" + suffix + "*ny",
              "-pfilm" + suffix + "*nz"
            });
    model.component(comp).physics(ge).feature("ge1")
        .set("equation", 1, 1, "Ferr" + suffix);

    String study = "std_struct550_" + key;
    try { model.study().remove(study); } catch (Exception ignored) {}
    model.study().create(study);
    model.study(study).label(
        "Stage 550 frozen-film structure bridge at " + angle + " deg");
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat")
        .set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set(
        "activate",
        new String[] {"solid", "on", "tff", "off", ge, "on"});
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", structureInitial);
    model.study(study).feature("stat").set("initsoluse", structureInitial);
    model.study(study).feature("stat").set("initsolusesolnum", "last");
    String step = study + "/stat";
    for (String tag : new String[] {
        "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
    }) {
      model.component(comp).physics("solid").feature(tag)
          .set("StudyStep", step);
    }
    model.component(comp).physics(ge).feature("ge1").set("StudyStep", step);
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    configureFullyCoupled(model, solution, 400);
    System.out.println("RUN_STRUCT_BRIDGE550 key=" + key + " sol="
        + solution + " angle=" + angle + " pressureKey=" + pressureKey);
    model.sol(solution).runAll();
    return solution;
  }

  static double[] solvePoint(
      Model model, String key, double angle, double replayTime,
      double phi, String filmInitial, String structureInitial) {
    String comp = "comp1";
    String ge = "ge_force_total111";
    String vars = "var_partitioned_local_pfilm";
    model.param().set("t_replay",
        String.format(Locale.US, "%.12g[s]", replayTime));
    model.param().set("phi_qs142",
        String.format(Locale.US, "%.12g[deg]", phi));
    model.param().set(
        "h_sep_uniform540",
        (angle < -10 && angle > -30) ? "25[um]" : "21[um]");

    String filmStudy = "std_film550_" + key;
    try { model.study().remove(filmStudy); } catch (Exception ignored) {}
    model.study().create(filmStudy);
    model.study(filmStudy).label(
        "Stage 550 local JFO " + key + " at " + angle + " deg");
    model.study(filmStudy).create("stat", "Stationary");
    model.study(filmStudy).feature("stat").set(
        "activate",
        new String[] {"solid", "off", "tff", "on", ge, "off"});
    model.study(filmStudy).feature("stat").set("useinitsol", "on");
    model.study(filmStudy).feature("stat").set("initmethod", "sol");
    model.study(filmStudy).feature("stat").set("initsol", filmInitial);
    model.study(filmStudy).feature("stat").set("initsoluse", filmInitial);
    model.study(filmStudy).feature("stat").set(
        "initsolusesolnum", "last");
    String filmStep = filmStudy + "/stat";
    for (String tag :
        model.component(comp).physics("tff").feature().tags()) {
      try {
        model.component(comp).physics("tff").feature(tag)
            .set("StudyStep", filmStep);
      } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(filmStudy).createAutoSequences("sol");
    String filmSolution = newest(model, before);
    configureFullyCoupled(model, filmSolution, 300);
    System.out.println("RUN_FILM550 key=" + key + " sol=" + filmSolution
        + " angle=" + angle);
    model.sol(filmSolution).runAll();

    String suffix = "550_" + key;
    model.component(comp).variable(vars).set(
        "pfilm" + suffix,
        "withsol('" + filmSolution + "',max(tff.p,0))");
    model.component(comp).variable(vars).set(
        "Wfilm" + suffix,
        "withsol('" + filmSolution
            + "',intop_film(max(tff.p,0)))");
    model.component(comp).variable(vars).set(
        "thetaAvg" + suffix,
        "withsol('" + filmSolution
            + "',intop_film(tff.theta)/intop_film(1))");
    model.component(comp).variable(vars).set(
        "Fshear" + suffix,
        "withsol('" + filmSolution
            + "',intop_film(tau_film_wall))");
    model.component(comp).variable(vars).set(
        "Ftotal" + suffix,
        "Fn_contact119+Wfilm" + suffix);
    model.component(comp).variable(vars).set(
        "Ferr" + suffix,
        "(Ftotal" + suffix + "-F_total_target)/F_total_target"
            + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
    model.component(comp).physics("solid")
        .feature("load_partitioned_pfilm").set(
            "FperArea", new String[] {
              "-pfilm" + suffix + "*nx",
              "-pfilm" + suffix + "*ny",
              "-pfilm" + suffix + "*nz"
            });
    model.component(comp).physics(ge).feature("ge1")
        .set("equation", 1, 1, "Ferr" + suffix);

    String structureStudy = "std_struct550_" + key;
    try { model.study().remove(structureStudy); } catch (Exception ignored) {}
    model.study().create(structureStudy);
    model.study(structureStudy).label(
        "Stage 550 joint load balance " + key + " at " + angle + " deg");
    model.study(structureStudy).create("stat", "Stationary");
    model.study(structureStudy).feature("stat")
        .set("geometricNonlinearity", "on");
    model.study(structureStudy).feature("stat").set(
        "activate",
        new String[] {"solid", "on", "tff", "off", ge, "on"});
    model.study(structureStudy).feature("stat").set("useinitsol", "on");
    model.study(structureStudy).feature("stat").set("initmethod", "sol");
    model.study(structureStudy).feature("stat")
        .set("initsol", structureInitial);
    model.study(structureStudy).feature("stat")
        .set("initsoluse", structureInitial);
    model.study(structureStudy).feature("stat").set(
        "initsolusesolnum", "last");
    String structureStep = structureStudy + "/stat";
    for (String tag : new String[] {
        "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
    }) {
      model.component(comp).physics("solid").feature(tag)
          .set("StudyStep", structureStep);
    }
    model.component(comp).physics(ge).feature("ge1")
        .set("StudyStep", structureStep);
    before = model.sol().tags();
    model.study(structureStudy).createAutoSequences("sol");
    String structureSolution = newest(model, before);
    configureFullyCoupled(model, structureSolution, 400);
    System.out.println("RUN_STRUCT550 key=" + key + " sol="
        + structureSolution + " angle=" + angle);
    model.sol(structureSolution).runAll();

    String dataset = "dset550_" + key;
    removeDataset(model, dataset);
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).set("solution", structureSolution);
    String eval = "eval550_" + key;
    removeNumerical(model, eval);
    model.result().numerical().create(eval, "EvalGlobal");
    model.result().numerical(eval).set("data", dataset);
    model.result().numerical(eval).set(
        "expr", new String[] {
          "Wfilm" + suffix,
          "Fn_contact119",
          "Ftotal" + suffix,
          "(Ftotal" + suffix + "-F_total_target)/F_total_target",
          "thetaAvg" + suffix,
          "Fshear" + suffix,
          "Fshear" + suffix + "/Ftotal" + suffix,
          "dr_indent119"
        });
    model.result().numerical(eval).set(
        "unit", new String[] {
          "N", "N", "N", "1", "1", "N", "1", "mm"
        });
    double[][] raw = model.result().numerical(eval).getReal();
    double[] result = new double[raw.length];
    for (int i = 0; i < raw.length; i++) result[i] = raw[i][0];
    lastFilmSolution = filmSolution;
    lastStructureSolution = structureSolution;
    return result;
  }

  static void printResult(
      String key, double angle, double replayTime, double[] values) {
    System.out.printf(Locale.US,
        "POINT550 key=%s angle=%.6g time=%.6g"
            + " Wfilm=%.12g Fcontact=%.12g Ftotal=%.12g"
            + " err=%.12g theta=%.12g Fshear=%.12g"
            + " muFilm=%.12g indent=%.12g%n",
        key, angle, replayTime,
        values[0], values[1], values[2],
        values[3], values[4], values[5],
        values[6], values[7]);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", ENDPOINT_REPAIR ? RESULTS : BASE);
      if (ENDPOINT_REPAIR) {
        model.component("comp1").variable("var_mixed_lub").set(
            "speed_factor550",
            "if(t_replay<T_structure_pre,0,"
                + "if(t_replay<T_structure_pre+T_speed_ramp,"
                + "0.5*(1-cos(pi*(t_replay-T_structure_pre)/T_speed_ramp)),"
                + "if(t_replay<T_structure_pre+T_structure_slide-T_speed_ramp,1,"
                + "if(t_replay<T_structure_pre+T_structure_slide,"
                + "0.5*(1-cos(pi*(T_structure_pre+T_structure_slide-t_replay)"
                + "/T_speed_ramp)),0))))");
        model.component("comp1").variable("var_mixed_lub").set(
            "vwall550", "v_blink_avg*speed_factor550");
        model.component("comp1").physics("tff").feature("ffp1").set(
            "vw", new String[] {
              "0",
              "-lid_mask*vwall550*Z/sqrt(Y^2+Z^2)",
              "lid_mask*vwall550*Y/sqrt(Y^2+Z^2)"
            });
        double[] plus35 = solvePoint(
            model, "plus35_endpoint", 35, 0.03, 0, "sol55", "sol56");
        double[] minus35 = solvePoint(
            model, "minus35_endpoint", -35, 0.53, -70, "sol59", "sol60");
        printResult("plus35_endpoint", 35, 0.03, plus35);
        printResult("minus35_endpoint", -35, 0.53, minus35);
        if (Math.abs(plus35[3]) > 0.02
            || Math.abs(minus35[3]) > 0.02) {
          throw new IllegalStateException(
              "Endpoint load balance exceeds 2 percent");
        }
        model.param().set("stage550_revision", "550");
        model.param().set("stage550_endpoint_repair", "1");
        model.label("Stage 550 five-position quasi-static results");
        model.save(RESULTS);
        model.label("Stage 550 five-position quasi-static checked");
        model.save(CHECKED);
        System.out.println("STAGE550_ENDPOINT_REPAIR_PASS");
        ModelUtil.disconnect();
        return;
      }
      if (Math.abs(model.param().evaluate("stage540_revision") - 540) > 0.1) {
        throw new IllegalStateException("Stage 540 dependency missing");
      }
      model.save(INPUT);
      model.param().set(
          "stage550_revision", "550",
          "Five-position quasi-static local JFO joint-load validation");
      model.component("comp1").variable("var_mixed_lub").set(
          "speed_factor550",
          "if(t_replay<T_structure_pre,0,"
              + "if(t_replay<T_structure_pre+T_speed_ramp,"
              + "0.5*(1-cos(pi*(t_replay-T_structure_pre)/T_speed_ramp)),"
              + "if(t_replay<T_structure_pre+T_structure_slide-T_speed_ramp,1,"
              + "if(t_replay<T_structure_pre+T_structure_slide,"
              + "0.5*(1-cos(pi*(T_structure_pre+T_structure_slide-t_replay)"
              + "/T_speed_ramp)),0))))");
      model.component("comp1").variable("var_mixed_lub").set(
          "vwall550", "v_blink_avg*speed_factor550");
      model.component("comp1").physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0",
            "-lid_mask*vwall550*Z/sqrt(Y^2+Z^2)",
            "lid_mask*vwall550*Y/sqrt(Y^2+Z^2)"
          });
      model.label("Stage 550 five-position quasi-static setup");
      model.save(SETUP);

      LinkedHashMap<String, double[]> results = new LinkedHashMap<>();
      LinkedHashMap<String, double[]> metadata = new LinkedHashMap<>();
      double[] midpoint = evaluateExistingMidpoint(model);
      results.put("mid", midpoint);
      metadata.put("mid", new double[] {0, 0.28});

      double[] plus17 = solvePoint(
          model, "plus17p5", 17.5, 0.155, -17.5,
          "sol51", "sol52");
      results.put("plus17p5", plus17);
      metadata.put("plus17p5", new double[] {17.5, 0.155});
      String plus17Film = lastFilmSolution;
      String plus17Structure = lastStructureSolution;
      double[] plus35 = solvePoint(
          model, "plus35", 35, 0.03, 0,
          plus17Film, plus17Structure);
      results.put("plus35", plus35);
      metadata.put("plus35", new double[] {35, 0.03});

      double[] minus17 = solvePoint(
          model, "minus17p5", -17.5, 0.405, -52.5,
          "sol51", "sol52");
      results.put("minus17p5", minus17);
      metadata.put("minus17p5", new double[] {-17.5, 0.405});
      String minus17Film = lastFilmSolution;
      String minus17Structure = lastStructureSolution;
      double[] minus35 = solvePoint(
          model, "minus35", -35, 0.53, -70,
          minus17Film, minus17Structure);
      results.put("minus35", minus35);
      metadata.put("minus35", new double[] {-35, 0.53});

      model.label("Stage 550 five-position quasi-static results");
      model.save(RESULTS);

      double maxError = 0;
      double minTotal = Double.POSITIVE_INFINITY;
      double maxTotal = Double.NEGATIVE_INFINITY;
      for (Map.Entry<String, double[]> entry : results.entrySet()) {
        String key = entry.getKey();
        double[] result = entry.getValue();
        double[] meta = metadata.get(key);
        printResult(key, meta[0], meta[1], result);
        for (double value : result) {
          if (!Double.isFinite(value)) {
            throw new IllegalStateException(
                "Non-finite result at " + key);
          }
        }
        maxError = Math.max(maxError, Math.abs(result[3]));
        minTotal = Math.min(minTotal, result[2]);
        maxTotal = Math.max(maxTotal, result[2]);
      }
      System.out.printf(Locale.US,
          "STAGE550 maxAbsError=%.12g minTotal=%.12g maxTotal=%.12g%n",
          maxError, minTotal, maxTotal);
      if (maxError > 0.02) {
        throw new IllegalStateException(
            "Stage 550 load error exceeds 2 percent");
      }
      model.label("Stage 550 five-position quasi-static checked");
      model.save(CHECKED);
      System.out.println("STAGE550 CHECK=PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
