import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class run_stage568_pressure_relaxed_closure {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "564f_stage568_closed_loop_validation3_results.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";
      model.param().set(
          "gamma_p568_final", "0.1",
          "Final Stage 568 pressure under-relaxation");

      String previousSolid = "sol99";
      String previousFilm = "sol100";
      String previousFeedback = "p_feedback568_selected3";

      model.label("Stage 568 pressure-relaxed closure setup");
      model.save("565a_stage568_pressure_relaxed_closure_setup.mph");
      System.out.println(
          "SETUP=565a_stage568_pressure_relaxed_closure_setup.mph");

      for (int iteration = 1; iteration <= 5; iteration++) {
        String suffix = "568p" + iteration;
        String pNew = "p_new_" + suffix;
        String pFeedback = "p_feedback_" + suffix;
        String wFeedback = "Wfeedback_" + suffix;
        String total = "Ftotal_" + suffix;
        String error = "Ferr_" + suffix;

        String pvars = "var_pressure_" + suffix;
        model.component(comp).variable().create(pvars);
        model.component(comp).variable(pvars).label(
            "Stage 568 pressure relaxation iteration " + iteration);
        model.component(comp).variable(pvars)
            .selection().named("sel_film_track");
        model.component(comp).variable(pvars).set(
            pNew, "withsol('" + previousFilm + "',max(tff.p,0))");
        model.component(comp).variable(pvars).set(
            pFeedback,
            "(1-gamma_p568_final)*" + previousFeedback
                + "+gamma_p568_final*" + pNew);

        String lvars = "var_load_" + suffix;
        model.component(comp).variable().create(lvars);
        model.component(comp).variable(lvars).set(
            wFeedback,
            "scale_pfilm555*intop_film(" + pFeedback + ")");
        model.component(comp).variable(lvars)
            .set(total, wFeedback + "+Fn_contact119");
        model.component(comp).variable(lvars).set(
            error,
            "(" + total + "-F_total_target)"
                + "/(F_total_target+1e-12[N])");

        model.component(comp).physics("solid")
            .feature("load_partitioned_pfilm").set(
                "FperArea", new String[] {
                  "-scale_pfilm555*" + pFeedback + "*nx",
                  "-scale_pfilm555*" + pFeedback + "*ny",
                  "-scale_pfilm555*" + pFeedback + "*nz"
                });
        model.component(comp).physics(ge).feature("ge1")
            .set("equation", new String[] {error});

        String solidStudy = "std_solid_" + suffix;
        model.study().create(solidStudy);
        model.study(solidStudy).label(
            "Stage 568 relaxed structure iteration " + iteration);
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
            .set("initsol", previousSolid);
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
        String[] before = model.sol().tags();
        model.study(solidStudy).createAutoSequences("sol");
        String solidSol = newest(model, before);
        SolverFeature solidDependent = model.sol(solidSol).feature("v1");
        solidDependent.set("initmethod", "sol");
        solidDependent.set("initsol", previousSolid);
        solidDependent.set("solnum", "last");
        solidDependent.set("notsolmethod", "sol");
        solidDependent.set("notsol", previousSolid);
        solidDependent.set("notsolnum", "last");
        SolverFeature solidStationary = model.sol(solidSol).feature("s1");
        if (!Arrays.asList(solidStationary.feature().tags())
            .contains("fc1")) {
          solidStationary.create("fc1", "FullyCoupled");
        }
        solidStationary.feature("fc1").set("linsolver", "dDef");
        solidStationary.feature("fc1").set("damp", "0.1");
        solidStationary.feature("fc1").set("maxiter", 300);
        model.sol(solidSol).runAll();

        String hFilm = "h_film_" + suffix;
        String hvars = "var_gap_" + suffix;
        model.component(comp).variable().create(hvars);
        model.component(comp).variable(hvars)
            .selection().named("sel_film_track");
        model.component(comp).variable(hvars).set(
            hFilm,
            "withsol('" + solidSol + "',h_geom555)"
                + "+delta_selected568_i3");
        model.component(comp).physics("tff").feature("ffp1")
            .set("hw1", hFilm);
        model.component(comp).physics("tff").feature("init1")
            .set("pfilm", "withsol('" + previousFilm + "',pfilm)");

        String filmStudy = "std_film_" + suffix;
        model.study().create(filmStudy);
        model.study(filmStudy).label(
            "Stage 568 relaxed JFO iteration " + iteration);
        model.study(filmStudy).create("stat", "Stationary");
        model.study(filmStudy).feature("stat").set(
            "activate",
            new String[] {
              "solid", "off", ge, "off", "tff", "on",
              "frame:spatial1", "on", "frame:material1", "on",
              "comp1", "on"
            });
        model.study(filmStudy).feature("stat").set("useinitsol", "on");
        model.study(filmStudy).feature("stat")
            .set("initmethod", "sol");
        model.study(filmStudy).feature("stat")
            .set("initsol", previousFilm);
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
        before = model.sol().tags();
        model.study(filmStudy).createAutoSequences("sol");
        String filmSol = newest(model, before);
        SolverFeature filmDependent = model.sol(filmSol).feature("v1");
        filmDependent.set("initmethod", "sol");
        filmDependent.set("initsol", previousFilm);
        filmDependent.set("solnum", "last");
        filmDependent.set("notsolmethod", "sol");
        filmDependent.set("notsol", solidSol);
        filmDependent.set("notsolnum", "last");
        SolverFeature filmStationary = model.sol(filmSol).feature("s1");
        if (!Arrays.asList(filmStationary.feature().tags())
            .contains("fc1")) {
          filmStationary.create("fc1", "FullyCoupled");
        }
        filmStationary.feature("fc1").set("linsolver", "dDef");
        filmStationary.feature("fc1").set("damp", "0.1");
        filmStationary.feature("fc1").set("maxiter", 300);
        model.sol(filmSol).runAll();

        String ds = "dset_" + suffix;
        model.result().dataset().create(ds, "Solution");
        model.result().dataset(ds).set("solution", solidSol);
        String se = "eval_solid_" + suffix;
        model.result().numerical().create(se, "EvalGlobal");
        model.result().numerical(se).set("data", ds);
        model.result().numerical(se).set("expr", new String[] {
            wFeedback, "Fn_contact119", total, error,
            "dr_indent119", "q_force_total111"
        });
        double[][] sv = model.result().numerical(se).getReal();

        String fds = "dset_film_" + suffix;
        model.result().dataset().create(fds, "Solution");
        model.result().dataset(fds).set("solution", filmSol);
        String fe = "eval_film_" + suffix;
        model.result().numerical().create(fe, "EvalGlobal");
        model.result().numerical(fe).set("data", fds);
        model.result().numerical(fe).set("expr", new String[] {
            "scale_pfilm555*intop_film(max(tff.p,0))",
            "(scale_pfilm555*intop_film(max(tff.p,0))-"
                + wFeedback + ")/" + wFeedback,
            "intop_film(" + hFilm + ")/intop_film(1)"
        });
        double[][] fv = model.result().numerical(fe).getReal();

        System.out.println("ITERATION=" + iteration
            + " SOLID_SOL=" + solidSol
            + " FILM_SOL=" + filmSol);
        System.out.println("ITERATION=" + iteration
            + " SOLID=" + Arrays.deepToString(sv));
        System.out.println("ITERATION=" + iteration
            + " FILM=" + Arrays.deepToString(fv));

        model.label(
            "Stage 568 pressure-relaxed closure iteration "
                + iteration + " results");
        String file = "565" + (char)('a' + iteration)
            + "_stage568_pressure_relaxed_iter"
            + iteration + "_results.mph";
        model.save(file);
        System.out.println("ITERATION=" + iteration + " RESULTS=" + file);

        previousSolid = solidSol;
        previousFilm = filmSol;
        previousFeedback = pFeedback;
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
