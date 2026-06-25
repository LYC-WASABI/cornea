import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_run_stage568_fixed_point_iter1 {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";

      model.param().set(
          "alpha_h568", "0.1",
          "Stage 568 film-thickness under-relaxation");
      model.param().set(
          "gamma_p568", "0.15",
          "Stage 568 pressure under-relaxation");

      String gapVars = "var_stage568_gap";
      try { model.component(comp).variable().remove(gapVars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(gapVars);
      model.component(comp).variable(gapVars).label(
          "Stage 568 first fixed-point gap update");
      model.component(comp).variable(gapVars)
          .selection().named("sel_film_track");
      model.component(comp).variable(gapVars).set(
          "h_prev568",
          "withsol('sol90',h_film566,"
              + "setval(delta_h566,10.4[um]))");
      model.component(comp).variable(gapVars).set(
          "h_struct568", "withsol('sol91',h_geom555)");
      model.component(comp).variable(gapVars).set(
          "h_relaxed568",
          "(1-alpha_h568)*h_prev568+alpha_h568*h_struct568");
      model.component(comp).variable(gapVars).set(
          "dh_rel568", "(h_relaxed568-h_prev568)/h_prev568");

      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_relaxed568");
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm",
              "withsol('sol90',pfilm,"
                  + "setval(delta_h566,10.4[um]))");

      String filmStudy = "std_stage568_film1";
      try { model.study().remove(filmStudy); } catch (Exception ignored) {}
      model.study().create(filmStudy);
      model.study(filmStudy).label(
          "Stage 568 iteration 1 JFO with relaxed structural gap");
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
      model.study(filmStudy).feature("stat").set("initsol", "sol90");
      model.study(filmStudy).feature("stat").set("initsoluse", "current");
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
      filmDependent.set("initsol", "sol90");
      filmDependent.set("solnum", "last");
      filmDependent.set("notsolmethod", "sol");
      filmDependent.set("notsol", "sol91");
      filmDependent.set("notsolnum", "last");
      SolverFeature filmStationary = model.sol(filmSol).feature("s1");
      if (!Arrays.asList(filmStationary.feature().tags()).contains("fc1")) {
        filmStationary.create("fc1", "FullyCoupled");
      }
      filmStationary.feature("fc1").set("linsolver", "dDef");
      filmStationary.feature("fc1").set("damp", "0.1");
      filmStationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 fixed-point iteration 1 setup");
      model.save("558w_stage568_fixed_point_iter1_setup.mph");
      System.out.println("SETUP=558w_stage568_fixed_point_iter1_setup.mph");
      System.out.println("FILM_SOL=" + filmSol);

      model.sol(filmSol).runAll();
      model.label("Stage 568 iteration 1 film result");
      model.save("558x_stage568_iter1_film_results.mph");
      System.out.println("FILM_RESULTS=558x_stage568_iter1_film_results.mph");

      String feedbackVars = "var_stage568_feedback";
      try { model.component(comp).variable().remove(feedbackVars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(feedbackVars);
      model.component(comp).variable(feedbackVars).label(
          "Stage 568 iteration 1 relaxed pressure feedback");
      model.component(comp).variable(feedbackVars)
          .selection().named("sel_film_track");
      model.component(comp).variable(feedbackVars).set(
          "p_old568", "p_feedback567");
      model.component(comp).variable(feedbackVars).set(
          "p_new568", "withsol('" + filmSol + "',max(tff.p,0))");
      model.component(comp).variable(feedbackVars).set(
          "p_feedback568",
          "(1-gamma_p568)*p_old568+gamma_p568*p_new568");

      String loadVars = "var_stage568_load";
      try { model.component(comp).variable().remove(loadVars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(loadVars);
      model.component(comp).variable(loadVars).set(
          "Wfilm568",
          "scale_pfilm555*intop_film(p_feedback568)");
      model.component(comp).variable(loadVars).set(
          "Ftotal568", "Wfilm568+Fn_contact119");
      model.component(comp).variable(loadVars).set(
          "Ferr568",
          "(Ftotal568-F_total_target)/(F_total_target+1e-12[N])");

      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-scale_pfilm555*p_feedback568*nx",
                "-scale_pfilm555*p_feedback568*ny",
                "-scale_pfilm555*p_feedback568*nz"
              });
      model.component(comp).physics(ge).feature("ge1")
          .set("equation", new String[] {"Ferr568"});

      String solidStudy = "std_stage568_solid1";
      try { model.study().remove(solidStudy); } catch (Exception ignored) {}
      model.study().create(solidStudy);
      model.study(solidStudy).label(
          "Stage 568 iteration 1 structural load balance");
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
      model.study(solidStudy).feature("stat").set("initmethod", "sol");
      model.study(solidStudy).feature("stat").set("initsol", "sol91");
      model.study(solidStudy).feature("stat").set("initsoluse", "current");
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
      solidDependent.set("initsol", "sol91");
      solidDependent.set("solnum", "last");
      solidDependent.set("notsolmethod", "sol");
      solidDependent.set("notsol", "sol91");
      solidDependent.set("notsolnum", "last");
      SolverFeature solidStationary = model.sol(solidSol).feature("s1");
      if (!Arrays.asList(solidStationary.feature().tags()).contains("fc1")) {
        solidStationary.create("fc1", "FullyCoupled");
      }
      solidStationary.feature("fc1").set("linsolver", "dDef");
      solidStationary.feature("fc1").set("damp", "0.1");
      solidStationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 fixed-point iteration 1 complete setup");
      model.save("558y_stage568_iter1_structure_setup.mph");
      System.out.println("STRUCTURE_SETUP=558y_stage568_iter1_structure_setup.mph");
      System.out.println("SOLID_SOL=" + solidSol);

      model.sol(solidSol).runAll();
      model.label("Stage 568 fixed-point iteration 1 results");

      removeDataset(model, "dset568_film1");
      model.result().dataset().create("dset568_film1", "Solution");
      model.result().dataset("dset568_film1").set("solution", filmSol);
      removeNumerical(model, "eval568_film1");
      model.result().numerical().create("eval568_film1", "EvalGlobal");
      model.result().numerical("eval568_film1").set("data", "dset568_film1");
      model.result().numerical("eval568_film1").set("expr", new String[] {
          "intop_film(h_prev568)/intop_film(1)",
          "intop_film(h_struct568)/intop_film(1)",
          "intop_film(h_relaxed568)/intop_film(1)",
          "scale_pfilm555*intop_film(max(tff.p,0))",
          "intop_film(tff.theta)/intop_film(1)"
      });
      System.out.println("FILM_METRICS=" + Arrays.deepToString(
          model.result().numerical("eval568_film1").getReal()));

      removeNumerical(model, "min568_hfilm1");
      model.result().numerical().create("min568_hfilm1", "MinSurface");
      model.result().numerical("min568_hfilm1")
          .set("data", "dset568_film1");
      model.result().numerical("min568_hfilm1")
          .selection().named("sel_film_track");
      model.result().numerical("min568_hfilm1")
          .set("expr", "h_relaxed568");
      System.out.println("HFILM_MIN=" + Arrays.deepToString(
          model.result().numerical("min568_hfilm1").getReal()));

      removeDataset(model, "dset568_solid1");
      model.result().dataset().create("dset568_solid1", "Solution");
      model.result().dataset("dset568_solid1").set("solution", solidSol);
      removeNumerical(model, "eval568_solid1");
      model.result().numerical().create("eval568_solid1", "EvalGlobal");
      model.result().numerical("eval568_solid1")
          .set("data", "dset568_solid1");
      model.result().numerical("eval568_solid1").set("expr", new String[] {
          "Wfilm568", "Fn_contact119", "Ftotal568", "Ferr568",
          "dr_indent119", "q_force_total111"
      });
      System.out.println("SOLID_METRICS=" + Arrays.deepToString(
          model.result().numerical("eval568_solid1").getReal()));

      model.save("558z_stage568_fixed_point_iter1_results.mph");
      System.out.println(
          "RESULTS=558z_stage568_fixed_point_iter1_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
