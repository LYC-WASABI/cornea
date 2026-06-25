import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage176_updated_balance_partitioned_friction {
  static String newest(Model model, String[] before) {
    Set<String> oldTags = new HashSet<>(Arrays.asList(before));
    String result = "";
    for (String tag : model.sol().tags()) {
      result = tag;
      if (!oldTags.contains(tag)) return tag;
    }
    return result;
  }

  static void removeDataset(Model model, String tag) {
    try {
      model.result().dataset().remove(tag);
    } catch (Exception ignored) {
    }
  }

  static void removeNumerical(Model model, String tag) {
    try {
      model.result().numerical().remove(tag);
    } catch (Exception ignored) {
    }
  }

  static void removeResult(Model model, String tag) {
    try {
      model.result().remove(tag);
    } catch (Exception ignored) {
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "320_lid8mm_stage175_updated_thickness_scan_results_Model.mph");
      String comp = "comp1";
      String mixedVars = "var_mixed_lub";
      String pressureVars = "var_partitioned_local_pfilm";
      String globalEq = "ge_force_total111";

      model.param().set("beta_h175", "0.0002");
      model.param().set("h_break_low", "0.5[um]");
      model.param().set("h_break_high", "1.0[um]");
      model.param().set("mu_boundary176", "0.1");
      model.component(comp).physics("tff").feature("ffp1").set("hw1", "h_updated175");

      String filmStudy = "std_film176";
      try {
        model.study().remove(filmStudy);
      } catch (Exception ignored) {
      }
      model.study().create(filmStudy);
      model.study(filmStudy).label("Stage 176 exact updated film thickness");
      model.study(filmStudy).create("stat", "Stationary");
      model.study(filmStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "off", "tff", "on", globalEq, "off"});
      String filmStep = filmStudy + "/stat";
      for (String feature : model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(feature).set("StudyStep", filmStep);
        } catch (Exception ignored) {
        }
      }
      String[] before = model.sol().tags();
      model.study(filmStudy).createAutoSequences("sol");
      String filmSol = newest(model, before);
      model.save("321_lid8mm_stage176_updated_film_setup_Model.mph");
      System.out.println("RUN_FILM176 " + filmSol);
      model.sol(filmSol).runAll();
      model.save("322_lid8mm_stage176_updated_film_results_Model.mph");

      model.component(comp).variable(pressureVars).set(
          "pfilm176", "withsol('" + filmSol + "',max(pfilm,0))");
      model.component(comp).variable(pressureVars).set(
          "Wfilm176", "withsol('" + filmSol + "',intop_film(max(pfilm,0)))");
      model.component(comp).variable(pressureVars).set(
          "dr_indent119",
          "2*d_indent_bound154/pi*atan(pi*q_force_total111/(2*q_indent_scale154))");
      model.component(comp).variable(pressureVars).set(
          "Ftotal176", "Fn_contact119+Wfilm176");
      model.component(comp).variable(pressureVars).set(
          "Ferr176",
          "(Ftotal176-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",
          new String[] {"-pfilm176*nx", "-pfilm176*ny", "-pfilm176*nz"});
      model.component(comp).physics(globalEq).feature("ge1").set("equation", 1, 1, "Ferr176");

      String structureStudy = "std_balance176";
      try {
        model.study().remove(structureStudy);
      } catch (Exception ignored) {
      }
      model.study().create(structureStudy);
      model.study(structureStudy).label("Stage 176 updated true-load balance");
      model.study(structureStudy).create("stat", "Stationary");
      model.study(structureStudy).feature("stat").set("geometricNonlinearity", "on");
      model.study(structureStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(structureStudy).feature("stat").set("useinitsol", "on");
      model.study(structureStudy).feature("stat").set("initmethod", "sol");
      model.study(structureStudy).feature("stat").set("initsol", "sol39");
      model.study(structureStudy).feature("stat").set("initsoluse", "sol39");
      model.study(structureStudy).feature("stat").set("initsolusesolnum", "last");
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
      String structureSol = newest(model, before);
      SolverFeature stationary = model.sol(structureSol).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      model.save("323_lid8mm_stage176_updated_balance_setup_Model.mph");
      System.out.println("RUN_BALANCE176 " + structureSol);
      model.sol(structureSol).runAll();
      model.save("324_lid8mm_stage176_updated_balance_results_Model.mph");

      String filmFraction =
          "if(h_updated175<=h_break_low,0,"
              + "if(h_updated175>=h_break_high,1,"
              + "0.5-0.5*cos(pi*(h_updated175-h_break_low)"
              + "/(h_break_high-h_break_low))))";
      model.component(comp).variable(pressureVars).set("Cfilm176", filmFraction);
      model.component(comp).variable(pressureVars).set("Cboundary176", "1-Cfilm176");
      model.component(comp).variable(pressureVars).set(
          "tauFilm176", "Cfilm176*withsol('" + filmSol + "',tau_film_wall)");
      model.component(comp).variable(pressureVars).set(
          "tauBoundary176", "Cboundary176*mu_boundary176*max(solid.Tn,0)");
      model.component(comp).variable(pressureVars).set(
          "FfilmFriction176", "intop_film(tauFilm176)");
      model.component(comp).variable(pressureVars).set(
          "FboundaryFriction176", "intop_film(tauBoundary176)");
      model.component(comp).variable(pressureVars).set(
          "Ffriction176", "FfilmFriction176+FboundaryFriction176");
      model.component(comp).variable(pressureVars).set(
          "muPredicted176", "Ffriction176/Ftotal176");
      model.component(comp).variable(pressureVars).set(
          "filmAreaFraction176", "intop_film(Cfilm176)/intop_film(1)");
      model.component(comp).variable(pressureVars).set(
          "boundaryAreaFraction176", "intop_film(Cboundary176)/intop_film(1)");

      removeDataset(model, "dset176s");
      model.result().dataset().create("dset176s", "Solution");
      model.result().dataset("dset176s").set("solution", structureSol);
      removeNumerical(model, "eval176");
      model.result().numerical().create("eval176", "EvalGlobal");
      model.result().numerical("eval176").set("data", "dset176s");
      model.result().numerical("eval176").set(
          "expr",
          new String[] {
            "Fn_contact119",
            "Wfilm176",
            "Ftotal176",
            "dr_indent119",
            "intop_film(Cfilm176)/intop_film(1)",
            "intop_film(Cboundary176)/intop_film(1)",
            "intop_film(tauFilm176)",
            "intop_film(tauBoundary176)",
            "intop_film(tauFilm176)+intop_film(tauBoundary176)",
            "(intop_film(tauFilm176)+intop_film(tauBoundary176))/Ftotal176"
          });
      double[][] values = model.result().numerical("eval176").getReal();
      System.out.printf(
          Locale.US,
          "STAGE176 Fc=%.10g Wfilm=%.10g Ftotal=%.10g d=%.10g"
              + " Afilm=%.10g Aboundary=%.10g Ffilm=%.10g Fboundary=%.10g"
              + " Ffriction=%.10g mu=%.10g%n",
          values[0][0],
          values[1][0],
          values[2][0],
          values[3][0],
          values[4][0],
          values[5][0],
          values[6][0],
          values[7][0],
          values[8][0],
          values[9][0]);

      removeResult(model, "pg176_partition");
      model.result().create("pg176_partition", "PlotGroup3D");
      model.result("pg176_partition").label("Stage 176 film-boundary partition");
      model.result("pg176_partition").set("data", "dset176s");
      model.result("pg176_partition").selection().named("sel_cornea_anterior_surface");
      model.result("pg176_partition").feature().create("surf1", "Surface");
      model.result("pg176_partition").feature("surf1").set("expr", "Cboundary176");
      model.result("pg176_partition").feature("surf1").set("unit", "1");

      removeResult(model, "pg176_friction");
      model.result().create("pg176_friction", "PlotGroup1D");
      model.result("pg176_friction").label("Stage 176 partitioned friction");
      model.result("pg176_friction").set("data", "dset176s");
      model.result("pg176_friction").feature().create("glob1", "Global");
      model.result("pg176_friction").feature("glob1").set(
          "expr",
          new String[] {
            "FfilmFriction176",
            "FboundaryFriction176",
            "Ffriction176",
            "muPredicted176"
          });

      model.save("324_lid8mm_stage176_updated_balance_partitioned_friction_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
