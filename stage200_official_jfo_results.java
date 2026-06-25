import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage200_official_jfo_results {
  static void removeResult(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  static void surface(
      Model model, String tag, String label, String data,
      String expression, String unit) {
    removeResult(model, tag);
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
      Model model = ModelUtil.load(
          "Model", "384_lid8mm_stage199_jfo_joint_balance_results_slim_Model.mph");
      String comp = "comp1";
      String vars = "var_jfo200";
      try { model.component(comp).variable().remove(vars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).selection().geom("geom1", 2);
      model.component(comp).variable(vars).selection().all();
      model.component(comp).variable(vars).label(
          "Stage 200 official JFO diagnostic variables");
      model.component(comp).variable(vars).set(
          "lambda_jfo200", "withsol('sol48',h_jfo197)/Rq_eq");

      try { model.result().dataset().remove("dset200f"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset200f", "Solution");
      model.result().dataset("dset200f").set("solution", "sol48");
      model.result().dataset("dset200f").label(
          "Stage 200 official JFO film solution");
      try { model.result().dataset().remove("dset200s"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset200s", "Solution");
      model.result().dataset("dset200s").set("solution", "sol49");
      model.result().dataset("dset200s").label(
          "Stage 200 joint normal-load structural solution");

      surface(model, "pg200_hfilm",
          "Stage 200 effective film thickness",
          "dset200f", "h_jfo197", "um");
      surface(model, "pg200_pfilm",
          "Stage 200 official JFO physical film pressure",
          "dset200f", "tff.p", "Pa");
      surface(model, "pg200_theta",
          "Stage 200 JFO fractional film content",
          "dset200f", "tff.theta", "1");
      surface(model, "pg200_lambda",
          "Stage 200 lambda ratio",
          "dset200s", "lambda_jfo200", "1");
      surface(model, "pg200_contact",
          "Stage 200 cornea contact pressure",
          "dset200s", "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      surface(model, "pg200_disp",
          "Stage 200 cornea and lid displacement",
          "dset200s", "solid.disp", "mm");
      surface(model, "pg200_mises",
          "Stage 200 cornea and lid von Mises stress",
          "dset200s", "solid.mises", "Pa");

      removeResult(model, "pg200_loadshare");
      model.result().create("pg200_loadshare", "PlotGroup1D");
      model.result("pg200_loadshare").label(
          "Stage 200 film-contact normal-load sharing");
      model.result("pg200_loadshare").set("data", "dset200s");
      model.result("pg200_loadshare").feature().create("glob1", "Global");
      model.result("pg200_loadshare").feature("glob1").set(
          "expr",
          new String[] {
            "Fn_contact119", "Wfilm199", "Ftotal199", "F_total_target"
          });
      model.result("pg200_loadshare").feature("glob1").set(
          "unit", new String[] {"N", "N", "N", "N"});

      removeResult(model, "pg200_friction");
      model.result().create("pg200_friction", "PlotGroup1D");
      model.result("pg200_friction").label(
          "Stage 200 film shear and film-only friction coefficient");
      model.result("pg200_friction").set("data", "dset200s");
      model.result("pg200_friction").feature().create("glob1", "Global");
      model.result("pg200_friction").feature("glob1").set(
          "expr",
          new String[] {"FshearFilm199", "FshearFilm199/Ftotal199"});
      model.result("pg200_friction").feature("glob1").set(
          "unit", new String[] {"N", "1"});

      try { model.result().numerical().remove("eval200"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval200", "EvalGlobal");
      model.result().numerical("eval200").set("data", "dset200s");
      model.result().numerical("eval200").set(
          "expr",
          new String[] {
            "withsol('sol48',intop_film(h_jfo197)/intop_film(1))",
            "withsol('sol48',intop_film(tff.p))",
            "withsol('sol48',intop_film(tff.theta)/intop_film(1))",
            "Fn_contact119",
            "Ftotal199",
            "FshearFilm199",
            "FshearFilm199/Ftotal199",
            "withsol('sol48',intop_film(h_jfo197/Rq_eq)/intop_film(1))"
          });
      model.result().numerical("eval200").set(
          "unit",
          new String[] {"um", "N", "1", "N", "N", "N", "1", "1"});
      double[][] x = model.result().numerical("eval200").getReal();
      System.out.printf(
          Locale.US,
          "STAGE200 havg=%.12g Wfilm=%.12g thetaAvg=%.12g"
              + " Fc=%.12g Ft=%.12g Fshear=%.12g"
              + " muFilm=%.12g lambdaAvg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0],
          x[4][0], x[5][0], x[6][0], x[7][0]);
      model.save(
          "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
