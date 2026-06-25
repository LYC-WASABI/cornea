import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_coupled_gap_indentation {
  static final String BASE =
      "553_stage550_five_position_checked_9mm_track.mph";
  static final String INPUT =
      "555_stage555_coupled_gap_input.mph";
  static final String GAP_SETUP =
      "556_stage555_gap_thickness_setup.mph";
  static final String SOLVER_SETUP =
      "557_stage555_joint_solver_setup.mph";
  static final String RESULTS =
      "558_stage555_midpoint_results.mph";
  static final String CHECKED =
      "559_stage555_midpoint_checked.mph";

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

  static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  static void surface(
      Model model, String tag, String label, String data,
      String expression, String unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", data);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expression);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String comp = "comp1";
      String ge = "ge_force_total111";
      String vars = "var_gap_coupled555";
      String loadVars = "var_load_coupled555";
      if (Math.abs(model.param().evaluate("stage550_revision") - 550) > 0.1) {
        throw new IllegalStateException("Stage 550 dependency missing");
      }
      model.save(INPUT);

      model.param().set(
          "stage555_revision", "555",
          "Monolithic midpoint gap-indentation coupling");
      model.param().set(
          "h_gap_smooth555", "0.02[um]",
          "Smooth positive-part width for geometric film thickness");
      model.param().set(
          "alpha_gap555", "0",
          "Continuation from Stage 540 thickness to geometric gap");
      model.param().set(
          "h_ref_reynolds555", "38[um]",
          "Uniform Reynolds reference thickness for continuation");
      model.param().set(
          "eps_q_regular555", "1e-4",
          "Weak Jacobian regularization for solved indentation");
      model.param().set(
          "q_ref555", "-0.043",
          "Initial global indentation unknown reference");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");

      try { model.component(comp).variable().remove(vars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars)
          .selection().named("sel_film_track");
      model.component(comp).variable(vars).set(
          "ulid_y555",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent119*(Y*cos(phi_lid_structure)"
              + "-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)");
      model.component(comp).variable(vars).set(
          "ulid_z555",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent119*(Y*sin(phi_lid_structure)"
              + "+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)");
      model.component(comp).variable(vars).set(
          "dgap_n555",
          "lid_mask*(dr_indent119-(u*nx+v*ny+w*nz))");
      model.component(comp).variable(vars).set(
          "h_gap_direct555", "h0_tear+dgap_n555");
      model.component(comp).variable(vars).set(
          "h_raw555",
          "(1-alpha_gap555)*h_ref_reynolds555"
              + "+alpha_gap555*h_gap_direct555");
      model.component(comp).variable(vars).set(
          "h_geom555",
          "h_residual189+0.5*((h_raw555-h_residual189)"
              + "+sqrt((h_raw555-h_residual189)^2+h_gap_smooth555^2))");
      model.component(comp).variable(vars).set(
          "h_liquid555", "h_geom555");
      try { model.component(comp).variable().remove(loadVars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(loadVars);
      model.component(comp).variable(loadVars).set(
          "Wfilm555", "intop_film(max(tff.p,0))");
      model.component(comp).variable(loadVars).set(
          "Ftotal555", "Wfilm555+Fn_contact119");
      model.component(comp).variable(loadVars).set(
          "Ferr555",
          "(Ftotal555-F_total_target)/F_total_target"
              + "+eps_q_regular555*(q_force_total111-q_ref555)");

      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_geom555");
      model.component(comp).physics("tff").prop("EquationType").set(
          "EquationType", "ReynoldsEquation");
      model.component(comp).physics("tff").feature("ffp1")
          .set("ub_src", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("ub", new String[] {"0", "0", "0"});
      model.component(comp).physics("tff").feature("ffp1")
          .set("uw_src", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("uw", new String[] {"0", "0", "0"});
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm", "withsol('sol51',pfilm)");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").selection()
          .named("sel_film_track");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-max(tff.p,0)*nx",
                "-max(tff.p,0)*ny",
                "-max(tff.p,0)*nz"
              });
      model.component(comp).physics(ge).feature("ge1")
          .set("equation", 1, 1, "Ferr555");
      model.label("Stage 555 coupled geometric gap setup");
      model.save(GAP_SETUP);

      String study = "std_coupled555";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 555 monolithic midpoint film-contact-load balance");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"alpha_gap555"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {
            "0.001 0.002 0.005 0.01 0.02 0.05 0.1 0.2 0.4 0.6 0.8 1"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"1"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", ge, "on"});
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol52");
      model.study(study).feature("stat").set("initsoluse", "sol52");
      model.study(study).feature("stat")
          .set("initsolusesolnum", "last");
      String step = study + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        model.component(comp).physics("solid").feature(tag)
            .set("StudyStep", step);
      }
      for (String tag : model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }
      model.component(comp).physics(ge).feature("ge1")
          .set("StudyStep", step);
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 500);
      model.label("Stage 555 monolithic joint solver setup");
      model.save(SOLVER_SETUP);

      System.out.println("RUN_STAGE555 SOLUTION=" + solution);
      model.sol(solution).runAll();
      model.label("Stage 555 midpoint coupled results");
      model.save(RESULTS);

      removeDataset(model, "dset555");
      model.result().dataset().create("dset555", "Solution");
      model.result().dataset("dset555").set("solution", solution);
      removeNumerical(model, "eval555");
      model.result().numerical().create("eval555", "EvalGlobal");
      model.result().numerical("eval555").set("data", "dset555");
      model.result().numerical("eval555").set(
          "expr", new String[] {
            "Wfilm555", "Fn_contact119", "Ftotal555",
            "(Ftotal555-F_total_target)/F_total_target",
            "dr_indent119",
            "intop_film(h_geom555)/intop_film(1)",
            "intop_film(h_geom555)/intop_film(1)",
            "1",
            "intop_film(tau_film_wall)"
          });
      model.result().numerical("eval555").set(
          "unit", new String[] {
            "N", "N", "N", "1", "mm",
            "um", "um", "1", "N"
          });
      double[][] values = model.result().numerical("eval555").getReal();
      for (int i = 0; i < values.length; i++) {
        System.out.printf(Locale.US,
            "STAGE555[%d]=%.12g%n", i, values[i][0]);
        if (!Double.isFinite(values[i][0])) {
          throw new IllegalStateException("Non-finite Stage 555 result");
        }
      }
      if (Math.abs(values[3][0]) > 0.02) {
        throw new IllegalStateException(
            "Stage 555 joint-load error exceeds 2 percent");
      }
      if (values[6][0] > values[5][0] + 1e-9) {
        throw new IllegalStateException(
            "Liquid-equivalent thickness exceeds geometric thickness");
      }

      surface(model, "pg555_hgeom",
          "Stage 555 geometric film thickness",
          "dset555", "h_geom555", "um");
      surface(model, "pg555_hliquid",
          "Stage 555 liquid-equivalent thickness",
          "dset555", "h_liquid555", "um");
      surface(model, "pg555_pfilm",
          "Stage 555 coupled film pressure",
          "dset555", "tff.p", "Pa");
      surface(model, "pg555_contact",
          "Stage 555 contact pressure",
          "dset555", "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      surface(model, "pg555_disp",
          "Stage 555 displacement",
          "dset555", "solid.disp", "mm");
      surface(model, "pg555_mises",
          "Stage 555 von Mises stress",
          "dset555", "solid.mises", "Pa");
      model.label("Stage 555 midpoint coupled checked");
      model.save(CHECKED);
      System.out.println("STAGE555_CHECK=PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
