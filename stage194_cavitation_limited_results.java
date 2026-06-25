import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage194_cavitation_limited_results {
  static void removeResult(Model model, String tag) {
    try {
      model.result().remove(tag);
    } catch (Exception ignored) {
    }
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
          "Model", "367_lid8mm_stage193_joint_load_balance_checked_Model.mph");
      String comp = "comp1";
      String vars = "var_cavitation194";
      try {
        model.component(comp).variable().remove(vars);
      } catch (Exception ignored) {
      }
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).selection().geom("geom1", 2);
      model.component(comp).variable(vars).selection().all();
      model.component(comp).variable(vars).label(
          "Stage 194 cavitation-limited diagnostic variables");
      model.param().set("p_cav_reg194", "50[Pa]",
          "Regularization pressure used only for cavitation-zone display");
      model.component(comp).variable(vars).set(
          "theta_cav194",
          "0.5*(1+tanh(withsol('sol46',pfilm)/p_cav_reg194))");
      model.component(comp).variable(vars).set(
          "p_effective194",
          "max(withsol('sol46',pfilm),0)");
      model.component(comp).variable(vars).set(
          "lambda194",
          "withsol('sol46',h_lift191)/Rq_eq");

      try {
        model.result().dataset().remove("dset194f");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset194f", "Solution");
      model.result().dataset("dset194f").set("solution", "sol46");
      model.result().dataset("dset194f").label(
          "Stage 194 physical film and cavitation-limited pressure");
      try {
        model.result().dataset().remove("dset194s");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset194s", "Solution");
      model.result().dataset("dset194s").set("solution", "sol47");
      model.result().dataset("dset194s").label(
          "Stage 194 joint-load structural solution");

      surface(model, "pg194_hfilm",
          "Stage 194 physical effective film thickness",
          "dset194f", "h_lift191", "um");
      surface(model, "pg194_psigned",
          "Stage 194 signed Reynolds pressure",
          "dset194f", "pfilm", "Pa");
      surface(model, "pg194_peffective",
          "Stage 194 cavitation-limited effective pressure",
          "dset194s", "p_effective194", "Pa");
      surface(model, "pg194_theta",
          "Stage 194 cavitation liquid-fraction indicator",
          "dset194s", "theta_cav194", "1");
      surface(model, "pg194_lambda",
          "Stage 194 lambda ratio",
          "dset194s", "lambda194", "1");
      surface(model, "pg194_contact",
          "Stage 194 cornea contact pressure",
          "dset194s", "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      surface(model, "pg194_disp",
          "Stage 194 cornea displacement",
          "dset194s", "solid.disp", "mm");
      surface(model, "pg194_mises",
          "Stage 194 cornea von Mises stress",
          "dset194s", "solid.mises", "Pa");

      removeResult(model, "pg194_loadshare");
      model.result().create("pg194_loadshare", "PlotGroup1D");
      model.result("pg194_loadshare").label(
          "Stage 194 film-contact normal-load sharing");
      model.result("pg194_loadshare").set("data", "dset194s");
      model.result("pg194_loadshare").feature().create("glob1", "Global");
      model.result("pg194_loadshare").feature("glob1").set(
          "expr",
          new String[] {
            "Fn_contact119", "Wfilm193", "Ftotal193", "F_total_target"
          });
      model.result("pg194_loadshare").feature("glob1").set(
          "unit", new String[] {"N", "N", "N", "N"});

      removeResult(model, "pg194_friction");
      model.result().create("pg194_friction", "PlotGroup1D");
      model.result("pg194_friction").label(
          "Stage 194 film friction prediction");
      model.result("pg194_friction").set("data", "dset194s");
      model.result("pg194_friction").feature().create("glob1", "Global");
      model.result("pg194_friction").feature("glob1").set(
          "expr",
          new String[] {
            "FshearFilm193", "FshearFilm193/Ftotal193"
          });
      model.result("pg194_friction").feature("glob1").set(
          "unit", new String[] {"N", "1"});

      try {
        model.result().numerical().remove("eval194");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval194", "EvalGlobal");
      model.result().numerical("eval194").set("data", "dset194s");
      model.result().numerical("eval194").set(
          "expr",
          new String[] {
            "withsol('sol46',intop_film(h_lift191)/intop_film(1))",
            "withsol('sol46',intop_film(max(pfilm,0)))",
            "withsol('sol46',-intop_film(min(pfilm,0)))",
            "Fn_contact119",
            "Ftotal193",
            "FshearFilm193",
            "FshearFilm193/Ftotal193",
            "withsol('sol46',intop_film(lambda191)/intop_film(1))"
          });
      model.result().numerical("eval194").set(
          "unit",
          new String[] {"um", "N", "N", "N", "N", "N", "1", "1"});
      double[][] x = model.result().numerical("eval194").getReal();
      System.out.printf(
          Locale.US,
          "STAGE194 havg=%.12g Wpos=%.12g Wneg=%.12g Fc=%.12g"
              + " Ft=%.12g Fshear=%.12g mu=%.12g lambdaAvg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0],
          x[4][0], x[5][0], x[6][0], x[7][0]);
      model.save(
          "368_lid8mm_stage194_cavitation_limited_joint_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
