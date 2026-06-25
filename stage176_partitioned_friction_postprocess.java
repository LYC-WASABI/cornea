import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage176_partitioned_friction_postprocess {
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
          "Model", "324_lid8mm_stage176_updated_balance_results_Model.mph");
      String comp = "comp1";
      String vars = "var_partition176";
      try {
        model.component(comp).variable().remove(vars);
      } catch (Exception ignored) {
      }
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).label("Stage 176 local film-boundary partition");
      model.component(comp).variable(vars).selection().geom("geom1", 2);
      model.component(comp).variable(vars).selection().all();

      String filmFraction =
          "if(h_updated175<=h_break_low,0,"
              + "if(h_updated175>=h_break_high,1,"
              + "0.5-0.5*cos(pi*(h_updated175-h_break_low)"
              + "/(h_break_high-h_break_low))))";
      model.component(comp).variable(vars).set("Cfilm176", filmFraction);
      model.component(comp).variable(vars).set("Cboundary176", "1-Cfilm176");
      model.component(comp).variable(vars).set(
          "tauFilm176", "Cfilm176*withsol('sol41',tau_film_wall)");
      model.component(comp).variable(vars).set(
          "tauBoundary176", "Cboundary176*mu_boundary176*max(solid.Tn,0)");

      String cFilm = "(" + filmFraction + ")";
      String cBoundary = "(1-" + cFilm + ")";
      String filmForce =
          "intop_film(" + cFilm + "*withsol('sol41',tau_film_wall))";
      String boundaryForce =
          "intop_film(" + cBoundary + "*mu_boundary176*max(solid.Tn,0))";

      removeDataset(model, "dset176s");
      model.result().dataset().create("dset176s", "Solution");
      model.result().dataset("dset176s").set("solution", "sol42");
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
            "intop_film(" + cFilm + ")/intop_film(1)",
            "intop_film(" + cBoundary + ")/intop_film(1)",
            filmForce,
            boundaryForce,
            filmForce + "+" + boundaryForce,
            "(" + filmForce + "+" + boundaryForce + ")/Ftotal176"
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
      model.result("pg176_partition").selection().named(
          "sel_cornea_anterior_surface");
      model.result("pg176_partition").feature().create("surf1", "Surface");
      model.result("pg176_partition").feature("surf1").set("expr", cBoundary);
      model.result("pg176_partition").feature("surf1").set("unit", "1");

      removeResult(model, "pg176_tau");
      model.result().create("pg176_tau", "PlotGroup3D");
      model.result("pg176_tau").label("Stage 176 partitioned shear stress");
      model.result("pg176_tau").set("data", "dset176s");
      model.result("pg176_tau").selection().named("sel_cornea_anterior_surface");
      model.result("pg176_tau").feature().create("surf1", "Surface");
      model.result("pg176_tau").feature("surf1").set(
          "expr",
          cFilm + "*withsol('sol41',tau_film_wall)+"
              + cBoundary + "*mu_boundary176*max(solid.Tn,0)");
      model.result("pg176_tau").feature("surf1").set("unit", "Pa");

      removeResult(model, "pg176_friction");
      model.result().create("pg176_friction", "PlotGroup1D");
      model.result("pg176_friction").label("Stage 176 partitioned friction");
      model.result("pg176_friction").set("data", "dset176s");
      model.result("pg176_friction").feature().create("glob1", "Global");
      model.result("pg176_friction").feature("glob1").set(
          "expr",
          new String[] {
            filmForce,
            boundaryForce,
            filmForce + "+" + boundaryForce,
            "(" + filmForce + "+" + boundaryForce + ")/Ftotal176"
          });

      model.save("325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
