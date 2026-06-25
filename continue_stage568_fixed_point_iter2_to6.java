import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class continue_stage568_fixed_point_iter2_to6 {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static String resultName(int iteration) {
    String[] prefixes = {
        "", "", "", "", "", "", "",
        "560a", "560b", "560c", "560d", "560e", "560f"
    };
    return prefixes[iteration]
        + "_stage568_fixed_point_iter" + iteration + "_results.mph";
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "559f_stage568_fixed_point_iter6_results.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";
      String previousFilmSol = "sol102";
      String previousSolidSol = "sol103";
      String previousH = "h_relaxed_568i6";
      String previousP = "p_feedback_568i6";

      model.label("Stage 568 fixed-point iterations 7 to 12 setup");
      model.save("559g_stage568_fixed_point_iter7_to12_setup.mph");
      System.out.println(
          "SETUP=559g_stage568_fixed_point_iter7_to12_setup.mph");

      for (int iteration = 7; iteration <= 12; iteration++) {
        String suffix = "568i" + iteration;
        String hPrev = "h_prev_" + suffix;
        String hStruct = "h_struct_" + suffix;
        String hRelaxed = "h_relaxed_" + suffix;
        String pOld = "p_old_" + suffix;
        String pNew = "p_new_" + suffix;
        String pFeedback = "p_feedback_" + suffix;
        String wFilm = "Wfilm_" + suffix;
        String fTotal = "Ftotal_" + suffix;
        String fError = "Ferr_" + suffix;

        String gapVars = "var_gap_" + suffix;
        try { model.component(comp).variable().remove(gapVars); }
        catch (Exception ignored) {}
        model.component(comp).variable().create(gapVars);
        model.component(comp).variable(gapVars).label(
            "Stage 568 fixed-point gap iteration " + iteration);
        model.component(comp).variable(gapVars)
            .selection().named("sel_film_track");
        model.component(comp).variable(gapVars).set(
            hPrev,
            "withsol('" + previousFilmSol + "'," + previousH + ")");
        model.component(comp).variable(gapVars).set(
            hStruct,
            "withsol('" + previousSolidSol + "',h_geom555)");
        model.component(comp).variable(gapVars).set(
            hRelaxed,
            "(1-alpha_h568)*" + hPrev
                + "+alpha_h568*" + hStruct);

        model.component(comp).physics("tff").feature("ffp1")
            .set("hw1", hRelaxed);
        model.component(comp).physics("tff").feature("init1")
            .set("pfilm",
                "withsol('" + previousFilmSol + "',pfilm)");

        String filmStudy = "std_film_" + suffix;
        try { model.study().remove(filmStudy); }
        catch (Exception ignored) {}
        model.study().create(filmStudy);
        model.study(filmStudy).label(
            "Stage 568 JFO fixed-point iteration " + iteration);
        model.study(filmStudy).create("stat", "Stationary");
        model.study(filmStudy).feature("stat").set(
            "activate",
            new String[] {
              "solid", "off", ge, "off", "tff", "on",
              "frame:spatial1", "on", "frame:material1", "on",
              "comp1", "on"
            });
        model.study(filmStudy).feature("stat").set("useinitsol", "on");
        model.study(filmStudy).feature("stat").set("initmethod", "sol");
        model.study(filmStudy).feature("stat")
            .set("initsol", previousFilmSol);
        model.study(filmStudy).feature("stat")
            .set("initsoluse", "current");
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
        String filmSol = newest(model, before);
        SolverFeature filmDependent = model.sol(filmSol).feature("v1");
        filmDependent.set("initmethod", "sol");
        filmDependent.set("initsol", previousFilmSol);
        filmDependent.set("solnum", "last");
        filmDependent.set("notsolmethod", "sol");
        filmDependent.set("notsol", previousSolidSol);
        filmDependent.set("notsolnum", "last");
        SolverFeature filmStationary = model.sol(filmSol).feature("s1");
        if (!Arrays.asList(filmStationary.feature().tags())
            .contains("fc1")) {
          filmStationary.create("fc1", "FullyCoupled");
        }
        filmStationary.feature("fc1").set("linsolver", "dDef");
        filmStationary.feature("fc1").set("damp", "0.1");
        filmStationary.feature("fc1").set("maxiter", 300);

        System.out.println(
            "ITERATION=" + iteration + " FILM_SOL=" + filmSol);
        model.sol(filmSol).runAll();

        String feedbackVars = "var_feedback_" + suffix;
        try { model.component(comp).variable().remove(feedbackVars); }
        catch (Exception ignored) {}
        model.component(comp).variable().create(feedbackVars);
        model.component(comp).variable(feedbackVars).label(
            "Stage 568 relaxed pressure iteration " + iteration);
        model.component(comp).variable(feedbackVars)
            .selection().named("sel_film_track");
        model.component(comp).variable(feedbackVars)
            .set(pOld, previousP);
        model.component(comp).variable(feedbackVars)
            .set(pNew, "withsol('" + filmSol + "',max(tff.p,0))");
        model.component(comp).variable(feedbackVars).set(
            pFeedback,
            "(1-gamma_p568)*" + pOld + "+gamma_p568*" + pNew);

        String loadVars = "var_load_" + suffix;
        try { model.component(comp).variable().remove(loadVars); }
        catch (Exception ignored) {}
        model.component(comp).variable().create(loadVars);
        model.component(comp).variable(loadVars).set(
            wFilm, "scale_pfilm555*intop_film(" + pFeedback + ")");
        model.component(comp).variable(loadVars).set(
            fTotal, wFilm + "+Fn_contact119");
        model.component(comp).variable(loadVars).set(
            fError,
            "(" + fTotal + "-F_total_target)"
                + "/(F_total_target+1e-12[N])");

        model.component(comp).physics("solid")
            .feature("load_partitioned_pfilm").set(
                "FperArea", new String[] {
                  "-scale_pfilm555*" + pFeedback + "*nx",
                  "-scale_pfilm555*" + pFeedback + "*ny",
                  "-scale_pfilm555*" + pFeedback + "*nz"
                });
        model.component(comp).physics(ge).feature("ge1")
            .set("equation", new String[] {fError});

        String solidStudy = "std_solid_" + suffix;
        try { model.study().remove(solidStudy); }
        catch (Exception ignored) {}
        model.study().create(solidStudy);
        model.study(solidStudy).label(
            "Stage 568 structural balance iteration " + iteration);
        model.study(solidStudy).create("stat", "Stationary");
        model.study(solidStudy).feature("stat")
            .set("geometricNonlinearity", "on");
        model.study(solidStudy).feature("stat").set(
            "activate",
            new String[] {
              "solid", "on", ge, "on", "tff", "off",
              "frame:spatial1", "on", "frame:material1", "on",
              "comp1", "on"
            });
        model.study(solidStudy).feature("stat").set("useinitsol", "on");
        model.study(solidStudy).feature("stat")
            .set("initmethod", "sol");
        model.study(solidStudy).feature("stat")
            .set("initsol", previousSolidSol);
        model.study(solidStudy).feature("stat")
            .set("initsoluse", "current");
        String solidStep = solidStudy + "/stat";
        for (String tag : new String[] {
            "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
        }) {
          model.component(comp).physics("solid").feature(tag)
              .set("StudyStep", solidStep);
        }
        model.component(comp).physics(ge).feature("ge1")
            .set("StudyStep", solidStep);
        before = model.sol().tags();
        model.study(solidStudy).createAutoSequences("sol");
        String solidSol = newest(model, before);
        SolverFeature solidDependent = model.sol(solidSol).feature("v1");
        solidDependent.set("initmethod", "sol");
        solidDependent.set("initsol", previousSolidSol);
        solidDependent.set("solnum", "last");
        solidDependent.set("notsolmethod", "sol");
        solidDependent.set("notsol", previousSolidSol);
        solidDependent.set("notsolnum", "last");
        SolverFeature solidStationary = model.sol(solidSol).feature("s1");
        if (!Arrays.asList(solidStationary.feature().tags())
            .contains("fc1")) {
          solidStationary.create("fc1", "FullyCoupled");
        }
        solidStationary.feature("fc1").set("linsolver", "dDef");
        solidStationary.feature("fc1").set("damp", "0.1");
        solidStationary.feature("fc1").set("maxiter", 300);

        System.out.println(
            "ITERATION=" + iteration + " SOLID_SOL=" + solidSol);
        model.sol(solidSol).runAll();

        String filmDataset = "dset_film_" + suffix;
        removeDataset(model, filmDataset);
        model.result().dataset().create(filmDataset, "Solution");
        model.result().dataset(filmDataset).set("solution", filmSol);
        String filmEval = "eval_film_" + suffix;
        removeNumerical(model, filmEval);
        model.result().numerical().create(filmEval, "EvalGlobal");
        model.result().numerical(filmEval).set("data", filmDataset);
        model.result().numerical(filmEval).set("expr", new String[] {
            "intop_film(" + hPrev + ")/intop_film(1)",
            "intop_film(" + hStruct + ")/intop_film(1)",
            "intop_film(" + hRelaxed + ")/intop_film(1)",
            "scale_pfilm555*intop_film(max(tff.p,0))",
            "intop_film(tff.theta)/intop_film(1)"
        });
        double[][] filmValues =
            model.result().numerical(filmEval).getReal();

        String minEval = "min_h_" + suffix;
        removeNumerical(model, minEval);
        model.result().numerical().create(minEval, "MinSurface");
        model.result().numerical(minEval).set("data", filmDataset);
        model.result().numerical(minEval)
            .selection().named("sel_film_track");
        model.result().numerical(minEval).set("expr", hRelaxed);
        double[][] minValues =
            model.result().numerical(minEval).getReal();

        String solidDataset = "dset_solid_" + suffix;
        removeDataset(model, solidDataset);
        model.result().dataset().create(solidDataset, "Solution");
        model.result().dataset(solidDataset).set("solution", solidSol);
        String solidEval = "eval_solid_" + suffix;
        removeNumerical(model, solidEval);
        model.result().numerical().create(solidEval, "EvalGlobal");
        model.result().numerical(solidEval).set("data", solidDataset);
        model.result().numerical(solidEval).set("expr", new String[] {
            wFilm, "Fn_contact119", fTotal, fError,
            "dr_indent119", "q_force_total111"
        });
        double[][] solidValues =
            model.result().numerical(solidEval).getReal();

        System.out.println("ITERATION=" + iteration
            + " FILM_METRICS=" + Arrays.deepToString(filmValues));
        System.out.println("ITERATION=" + iteration
            + " HMIN=" + Arrays.deepToString(minValues));
        System.out.println("ITERATION=" + iteration
            + " SOLID_METRICS=" + Arrays.deepToString(solidValues));

        model.label(
            "Stage 568 fixed-point iteration " + iteration + " results");
        String file = resultName(iteration);
        model.save(file);
        System.out.println("ITERATION=" + iteration + " RESULTS=" + file);

        previousFilmSol = filmSol;
        previousSolidSol = solidSol;
        previousH = hRelaxed;
        previousP = pFeedback;
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
