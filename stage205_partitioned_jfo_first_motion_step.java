import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage205_partitioned_jfo_first_motion_step {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!oldTags.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";

      model.param().set("phi_start201", "-35[deg]");
      model.param().set("theta_travel201", "70[deg]");
      model.param().set(
          "omega_slide_const",
          "theta_travel201/(T_structure_slide-T_speed_ramp)");
      model.param().set("phi_step205", "-34.5[deg]");
      model.param().set("p_cav_transition195", "50[kPa]");
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure", "phi_step205");
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure",
          "(phi_step205-phi_start201)/theta_travel201");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_step205");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "omega_lid", "omega_slide_const");
      model.component(comp).variable(mixedVars).set(
          "h_jfo205",
          "max(h_residual189,h0_tear+lid_mask*delta_h_jfo197)");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h_jfo205");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "withsol('sol48',pfilm)");

      String filmStudy = "std_film205";
      try { model.study().remove(filmStudy); } catch (Exception ignored) {}
      model.study().create(filmStudy);
      model.study(filmStudy).label(
          "Stage 205 JFO film at first moved position -34.5 deg");
      model.study(filmStudy).create("stat", "Stationary");
      model.study(filmStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String filmStep = filmStudy + "/stat";
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", filmStep);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(filmStudy).createAutoSequences("sol");
      String filmSolution = newest(model, before);
      SolverFeature filmStationary = model.sol(filmSolution).feature("s1");
      if (!Arrays.asList(filmStationary.feature().tags()).contains("fc1")) {
        filmStationary.create("fc1", "FullyCoupled");
      }
      filmStationary.feature("fc1").set("linsolver", "dDef");
      filmStationary.feature("fc1").set("maxiter", 200);
      model.save(
          "398_lid8mm_stage205_partitioned_first_step_film_setup_Model.mph");
      System.out.println("RUN_FILM205 " + filmSolution);
      model.sol(filmSolution).runAll();
      model.save(
          "399_lid8mm_stage205_partitioned_first_step_film_results_Model.mph");

      model.component(comp).variable(pressureVars).set(
          "pfilm205", "withsol('" + filmSolution + "',tff.p)");
      model.component(comp).variable(pressureVars).set(
          "Wfilm205",
          "withsol('" + filmSolution + "',intop_film(tff.p))");
      model.component(comp).variable(pressureVars).set(
          "Fshear205",
          "withsol('" + filmSolution
              + "',intop_film(tau_film_wall))");
      model.component(comp).variable(pressureVars).set(
          "Ftotal205", "Fn_contact119+Wfilm205");
      model.component(comp).variable(pressureVars).set(
          "Ferr205",
          "(Ftotal205-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea",
              new String[] {
                "-pfilm205*nx", "-pfilm205*ny", "-pfilm205*nz"
              });
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr205");

      String structureStudy = "std_struct205";
      try { model.study().remove(structureStudy); } catch (Exception ignored) {}
      model.study().create(structureStudy);
      model.study(structureStudy).label(
          "Stage 205 structure balance at first moved position");
      model.study(structureStudy).create("stat", "Stationary");
      model.study(structureStudy).feature("stat").set(
          "geometricNonlinearity", "on");
      model.study(structureStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(structureStudy).feature("stat").set("useinitsol", "on");
      model.study(structureStudy).feature("stat").set("initmethod", "sol");
      model.study(structureStudy).feature("stat").set("initsol", "sol49");
      model.study(structureStudy).feature("stat").set("initsoluse", "sol49");
      model.study(structureStudy).feature("stat").set(
          "initsolusesolnum", "last");
      String structureStep = structureStudy + "/stat";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set(
            "StudyStep", structureStep);
      }
      model.component(comp).physics(globalEq).feature("ge1").set(
          "StudyStep", structureStep);
      before = model.sol().tags();
      model.study(structureStudy).createAutoSequences("sol");
      String structureSolution = newest(model, before);
      SolverFeature structureStationary =
          model.sol(structureSolution).feature("s1");
      if (!Arrays.asList(structureStationary.feature().tags())
          .contains("fc1")) {
        structureStationary.create("fc1", "FullyCoupled");
      }
      structureStationary.feature("fc1").set("linsolver", "dDef");
      structureStationary.feature("fc1").set("maxiter", 300);
      model.save(
          "400_lid8mm_stage205_partitioned_first_step_balance_setup_Model.mph");
      System.out.println("RUN_STRUCT205 " + structureSolution);
      model.sol(structureSolution).runAll();

      try { model.result().dataset().remove("dset205"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset205", "Solution");
      model.result().dataset("dset205").set("solution", structureSolution);
      try { model.result().numerical().remove("eval205"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval205", "EvalGlobal");
      model.result().numerical("eval205").set("data", "dset205");
      model.result().numerical("eval205").set(
          "expr",
          new String[] {
            "phi_step205", "Fn_contact119", "Wfilm205", "Ftotal205",
            "dr_indent119", "Fshear205", "Fshear205/Ftotal205",
            "withsol('" + filmSolution
                + "',intop_film(h_jfo205)/intop_film(1))"
          });
      model.result().numerical("eval205").set(
          "unit",
          new String[] {"deg", "N", "N", "N", "mm", "N", "1", "um"});
      double[][] x = model.result().numerical("eval205").getReal();
      System.out.printf(
          Locale.US,
          "STAGE205 phi=%.12g Fc=%.12g Wf=%.12g Ft=%.12g"
              + " d=%.12g Fshear=%.12g muFilm=%.12g havg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0],
          x[4][0], x[5][0], x[6][0], x[7][0]);
      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")
            && !tag.equals(filmSolution) && !tag.equals(structureSolution)) {
          try { model.sol(tag).clearSolution(); } catch (Exception ignored) {}
        }
      }
      model.save(
          "401_lid8mm_stage205_partitioned_first_motion_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
