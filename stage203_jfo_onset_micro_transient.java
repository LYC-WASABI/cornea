import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage203_jfo_onset_micro_transient {
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
          "Model", "391_lid8mm_stage202_jfo_rest_preload_checked_Model.mph");
      String comp = "comp1";
      String pressureVars = "var_partitioned_local_pfilm";
      String mixedVars = "var_mixed_lub";
      String globalEq = "ge_force_total111";
      model.param().set("tau_load203", "0.005[s]",
          "Dynamic indentation load-controller relaxation time");
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1,
          "Ferr201-tau_load203*q_force_total111t/q_indent_scale154");

      String slideFraction =
          "if(t<T_structure_pre,0,"
              + "if(t<T_structure_pre+T_speed_ramp,"
              + "(0.5*(t-T_structure_pre)-T_speed_ramp/(2*pi)"
              + "*sin(pi*(t-T_structure_pre)/T_speed_ramp))"
              + "/(T_structure_slide-T_speed_ramp),"
              + "if(t<T_structure_pre+T_structure_slide-T_speed_ramp,"
              + "((t-T_structure_pre)-0.5*T_speed_ramp)"
              + "/(T_structure_slide-T_speed_ramp),"
              + "if(t<T_structure_pre+T_structure_slide,"
              + "1-(0.5*(T_structure_pre+T_structure_slide-t)"
              + "-T_speed_ramp/(2*pi)*sin(pi*(T_structure_pre"
              + "+T_structure_slide-t)/T_speed_ramp))"
              + "/(T_structure_slide-T_speed_ramp),1))))";
      model.component(comp).variable(pressureVars).set(
          "slide_fraction_structure", slideFraction);
      model.component(comp).variable(pressureVars).set(
          "phi_lid_structure",
          "phi_start201+theta_travel201*slide_fraction_structure");
      model.component(comp).variable(pressureVars).set("t_replay", "t");
      model.component(comp).variable(mixedVars).set(
          "slide_fraction_film_replay", "slide_fraction_structure");
      model.component(comp).variable(mixedVars).set(
          "phi_lid_film_replay", "phi_lid_structure");
      model.component(comp).physics("solid")
          .prop("StructuralTransientBehavior").set(
              "StructuralTransientBehavior", "Quasistatic");

      String study = "std_onset203";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 203 strongly coupled JFO motion-onset micro transient");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set(
          "tlist", "range(0.03,0.000002,0.0302)");
      model.study(study).feature("time").set(
          "geometricNonlinearity", "on");
      model.study(study).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", globalEq, "on"});
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", "sol51");
      model.study(study).feature("time").set("initsoluse", "sol51");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      String step = study + "/time";
      for (String feature :
          new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
        model.component(comp).physics("solid").feature(feature).set(
            "StudyStep", step);
      }
      for (String feature :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set(
              "StudyStep", step);
        } catch (Exception ignored) {}
      }
      model.component(comp).physics(globalEq).feature("ge1").set(
          "StudyStep", step);
      model.component(comp).physics("solid").feature("dcnt1").set(
          "useCutback", "1");

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature timeSolver = model.sol(solution).feature("t1");
      timeSolver.set("consistent", "off");
      timeSolver.set("initialstepbdfactive", "on");
      timeSolver.set("initialstepbdf", "1e-8");
      timeSolver.set("maxstepconstraintbdf", "const");
      timeSolver.set("maxstepbdf", "2e-6");
      try { timeSolver.feature().remove("se1"); }
      catch (Exception ignored) {}
      try { timeSolver.feature().remove("fc1"); }
      catch (Exception ignored) {}
      timeSolver.create("fc1", "FullyCoupled");
      timeSolver.feature("fc1").set("linsolver", "dDef");
      timeSolver.feature("fc1").set("maxiter", 150);
      model.save(
          "392_lid8mm_stage203_jfo_onset_micro_setup_Model.mph");
      System.out.println("RUN_STAGE203 " + solution);
      model.sol(solution).runAll();
      model.save(
          "393_lid8mm_stage203_jfo_onset_micro_results_Model.mph");

      try { model.result().dataset().remove("dset203"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset203", "Solution");
      model.result().dataset("dset203").set("solution", solution);
      try { model.result().numerical().remove("eval203"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval203", "EvalGlobal");
      model.result().numerical("eval203").set("data", "dset203");
      model.result().numerical("eval203").set(
          "expr",
          new String[] {
            "t", "phi_lid_structure", "Fn_contact119", "Wfilm201",
            "Ftotal201", "dr_indent119",
            "intop_film(h_jfo201)/intop_film(1)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval203").set(
          "unit",
          new String[] {"s", "deg", "N", "N", "N", "mm", "um", "N"});
      double[][] x = model.result().numerical("eval203").getReal();
      for (int j = 0; j < x[0].length; j += Math.max(1, x[0].length / 10)) {
        System.out.printf(
            Locale.US,
            "t=%.9g phi=%.9g Fc=%.10g Wf=%.10g Ft=%.10g"
                + " d=%.10g havg=%.10g Fshear=%.10g%n",
            x[0][j], x[1][j], x[2][j], x[3][j],
            x[4][j], x[5][j], x[6][j], x[7][j]);
      }
      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")
            && !tag.equals("sol51") && !tag.equals(solution)) {
          try { model.sol(tag).clearSolution(); } catch (Exception ignored) {}
        }
      }
      model.save(
          "394_lid8mm_stage203_jfo_onset_micro_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
