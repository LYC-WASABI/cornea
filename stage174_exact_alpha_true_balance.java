import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage174_exact_alpha_true_balance {
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

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "314_lid8mm_stage173_gap_relax_scan_results_Model.mph");

      String comp = "comp1";
      String mixedVars = "var_mixed_lub";
      String pressureVars = "var_partitioned_local_pfilm";
      String globalEq = "ge_force_total111";

      // Step 1: calculate the selected under-relaxation point explicitly.
      model.param().set("alpha_gap173", "0.012");
      model.component(comp).physics("tff").feature("ffp1").set("hw1", "h_relaxed173");

      String filmStudy = "std_film174";
      try {
        model.study().remove(filmStudy);
      } catch (Exception ignored) {
      }
      model.study().create(filmStudy);
      model.study(filmStudy).label("Stage 174 exact alpha film pressure");
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
      model.save("315_lid8mm_stage174_exact_film_setup_Model.mph");
      System.out.println("RUN_FILM174 " + filmSol);
      model.sol(filmSol).runAll();

      removeDataset(model, "dset174f");
      model.result().dataset().create("dset174f", "Solution");
      model.result().dataset("dset174f").set("solution", filmSol);
      removeNumerical(model, "eval174f");
      model.result().numerical().create("eval174f", "EvalGlobal");
      model.result().numerical("eval174f").set("data", "dset174f");
      model.result().numerical("eval174f").set(
          "expr",
          new String[] {
            "intop_film(max(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)",
            "intop_film(h_relaxed173)/intop_film(1)"
          });
      double[][] filmValues = model.result().numerical("eval174f").getReal();
      System.out.printf(
          Locale.US,
          "FILM174 Wpos=%.10g Wnet=%.10g Fshear=%.10g havg=%.10g%n",
          filmValues[0][0],
          filmValues[1][0],
          filmValues[2][0],
          filmValues[3][0]);
      model.save("316_lid8mm_stage174_exact_film_results_Model.mph");

      // Step 2: freeze that film pressure and solve indentation from the total-load equation.
      model.component(comp).variable(pressureVars).set(
          "pfilm174", "withsol('" + filmSol + "',max(pfilm,0))");
      model.component(comp).variable(pressureVars).set(
          "Wfilm174", "withsol('" + filmSol + "',intop_film(max(pfilm,0)))");
      model.component(comp).variable(pressureVars).set(
          "FshearFilm174", "withsol('" + filmSol + "',intop_film(tau_film_wall))");
      model.component(comp).variable(pressureVars).set(
          "dr_indent119",
          "2*d_indent_bound154/pi*atan(pi*q_force_total111/(2*q_indent_scale154))");
      model.component(comp).variable(pressureVars).set(
          "Ftotal174", "Fn_contact119+Wfilm174");
      model.component(comp).variable(pressureVars).set(
          "Ferr174",
          "(Ftotal174-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      model.component(comp).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",
          new String[] {"-pfilm174*nx", "-pfilm174*ny", "-pfilm174*nz"});
      model.component(comp).physics(globalEq).feature("ge1").set("equation", 1, 1, "Ferr174");

      String structureStudy = "std_balance174";
      try {
        model.study().remove(structureStudy);
      } catch (Exception ignored) {
      }
      model.study().create(structureStudy);
      model.study(structureStudy).label("Stage 174 true 0.03 N midpoint load balance");
      model.study(structureStudy).create("stat", "Stationary");
      model.study(structureStudy).feature("stat").set("geometricNonlinearity", "on");
      model.study(structureStudy).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "off", globalEq, "on"});
      model.study(structureStudy).feature("stat").set("useinitsol", "on");
      model.study(structureStudy).feature("stat").set("initmethod", "sol");
      model.study(structureStudy).feature("stat").set("initsol", "sol35");
      model.study(structureStudy).feature("stat").set("initsoluse", "sol35");
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
      model.save("317_lid8mm_stage174_true_balance_setup_Model.mph");

      System.out.println("RUN_BALANCE174 " + structureSol);
      model.sol(structureSol).runAll();
      model.component(comp).variable(mixedVars).set(
          "gap_final174",
          "min(max(withsol('" + structureSol
              + "',geomgap_dst_cp_lid_cornea),0),gap_cap_tear)");

      removeDataset(model, "dset174s");
      model.result().dataset().create("dset174s", "Solution");
      model.result().dataset("dset174s").set("solution", structureSol);
      removeNumerical(model, "eval174s");
      model.result().numerical().create("eval174s", "EvalGlobal");
      model.result().numerical("eval174s").set("data", "dset174s");
      model.result().numerical("eval174s").set(
          "expr",
          new String[] {
            "Fn_contact119",
            "Wfilm174",
            "Ftotal174",
            "dr_indent119",
            "FshearFilm174",
            "FshearFilm174/Ftotal174"
          });
      double[][] values = model.result().numerical("eval174s").getReal();
      System.out.printf(
          Locale.US,
          "BALANCE174 Fc=%.10g Wfilm=%.10g Ftotal=%.10g d=%.10g"
              + " FshearFilm=%.10g muFilm=%.10g%n",
          values[0][0],
          values[1][0],
          values[2][0],
          values[3][0],
          values[4][0],
          values[5][0]);

      model.save("318_lid8mm_stage174_true_balance_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
