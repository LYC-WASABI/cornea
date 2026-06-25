import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage201_jfo_strong_coupled_micro_transient {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!oldTags.contains(tag)) return tag;
    }
    return model.sol().tags()[model.sol().tags().length - 1];
  }

  static void bind(Model model, String step) {
    for (String feature :
        new String[] {"dcnt1", "disp_lid_time", "load_partitioned_pfilm"}) {
      model.component("comp1").physics("solid").feature(feature).set(
          "StudyStep", step);
    }
    for (String feature :
        model.component("comp1").physics("tff").feature().tags()) {
      try {
        model.component("comp1").physics("tff").feature(feature).set(
            "StudyStep", step);
      } catch (Exception ignored) {}
    }
    model.component("comp1").physics("ge_force_total111").feature("ge1").set(
        "StudyStep", step);
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
      model.param().set("d_indent_ref201", "-0.0300501062555[mm]");
      model.param().set(
          "omega_slide_const",
          "theta_travel201/(T_structure_slide-T_speed_ramp)");

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
      model.component(comp).variable(mixedVars).set(
          "h_jfo201",
          "max(h_residual189,h0_tear+lid_mask*"
              + "(delta_h_jfo197+d_indent_ref201-dr_indent119))");
      model.component(comp).physics("tff").feature("ffp1").set(
          "hw1", "h_jfo201");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm", "withsol('sol48',pfilm)");

      model.component(comp).variable(pressureVars).set(
          "Wfilm201", "intop_film(tff.p)");
      model.component(comp).variable(pressureVars).set(
          "Ftotal201", "Fn_contact119+Wfilm201");
      model.component(comp).variable(pressureVars).set(
          "Ferr201",
          "(Ftotal201-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea",
              new String[] {
                "-tff.p*nx", "-tff.p*ny", "-tff.p*nz"
              });
      model.component(comp).physics(globalEq).feature("ge1").set(
          "equation", 1, 1, "Ferr201");
      model.component(comp).physics("solid").feature("disp_lid_time").set(
          "U0",
          new String[] {
            "0",
            "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
                + "-dr_indent119*(Y*cos(phi_lid_structure)"
                + "-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
                + "-dr_indent119*(Y*sin(phi_lid_structure)"
                + "+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
          });
      model.component(comp).physics("solid").feature("dcnt1").set(
          "useCutback", "1");

      String study = "std_jfomicro201";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 201 strongly coupled JFO onset micro transient");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set(
          "tlist", "range(0.0295,0.000025,0.031)");
      model.study(study).feature("time").set(
          "geometricNonlinearity", "on");
      model.study(study).feature("time").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", globalEq, "on"});
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", "sol49");
      model.study(study).feature("time").set("initsoluse", "sol49");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      bind(model, study + "/time");

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature timeSolver = model.sol(solution).feature("t1");
      timeSolver.set("consistent", "off");
      timeSolver.set("initialstepbdfactive", "on");
      timeSolver.set("initialstepbdf", "1e-7");
      timeSolver.set("maxstepconstraintbdf", "const");
      timeSolver.set("maxstepbdf", "2.5e-5");
      try { timeSolver.feature().remove("se1"); }
      catch (Exception ignored) {}
      try { timeSolver.feature().remove("fc1"); }
      catch (Exception ignored) {}
      timeSolver.create("fc1", "FullyCoupled");
      timeSolver.feature("fc1").set("linsolver", "dDef");
      timeSolver.feature("fc1").set("maxiter", 150);
      model.save(
          "386_lid8mm_stage201_jfo_strong_micro_setup_Model.mph");
      System.out.println("RUN_STAGE201 " + solution);
      model.sol(solution).runAll();
      model.save(
          "387_lid8mm_stage201_jfo_strong_micro_results_Model.mph");

      try { model.result().dataset().remove("dset201"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset201", "Solution");
      model.result().dataset("dset201").set("solution", solution);
      try { model.result().numerical().remove("eval201"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval201", "EvalGlobal");
      model.result().numerical("eval201").set("data", "dset201");
      model.result().numerical("eval201").set(
          "expr",
          new String[] {
            "t", "phi_lid_structure", "Fn_contact119", "Wfilm201",
            "Ftotal201", "dr_indent119",
            "intop_film(h_jfo201)/intop_film(1)",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval201").set(
          "unit",
          new String[] {"s", "deg", "N", "N", "N", "mm", "um", "N"});
      double[][] x = model.result().numerical("eval201").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(
            Locale.US,
            "t=%.8g phi=%.8g Fc=%.10g Wf=%.10g Ft=%.10g"
                + " d=%.10g havg=%.10g Fshear=%.10g%n",
            x[0][j], x[1][j], x[2][j], x[3][j],
            x[4][j], x[5][j], x[6][j], x[7][j]);
      }
      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")
            && !tag.equals(solution)) {
          try { model.sol(tag).clearSolution(); } catch (Exception ignored) {}
        }
      }
      model.save(
          "388_lid8mm_stage201_jfo_strong_micro_checked_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
