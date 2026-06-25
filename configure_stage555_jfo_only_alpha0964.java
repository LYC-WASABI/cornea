import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class configure_stage555_jfo_only_alpha0964 {
  private static final String SOURCE =
      "557z_stage555_alpha0p964_before_JFO.mph";
  private static final String OUTPUT =
      "558a_stage555_JFO_only_study_alpha0p964_setup.mph";

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver sequence was created");
  }

  private static Set<Integer> entities(Model model, String feature) {
    int[] values = model.component("comp1").physics("tff")
        .feature(feature).selection().entities();
    Set<Integer> result = new TreeSet<>();
    for (int value : values) result.add(value);
    return result;
  }

  private static void requireDisjoint(
      Model model, String first, String second) {
    Set<Integer> overlap = entities(model, first);
    overlap.retainAll(entities(model, second));
    if (!overlap.isEmpty()) {
      throw new IllegalStateException(
          "Overlapping film border selections: "
              + first + " and " + second + " -> " + overlap);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", SOURCE);
      String comp = "comp1";
      String tff = "tff";
      String globalEq = "ge_force_total111";
      String study = "std_jfo_init555";

      String[] sol81Values =
          model.sol("sol81").feature("v1").getStringArray("clist");
      if (sol81Values.length == 0
          || !sol81Values[0].contains("0.964")) {
        throw new IllegalStateException(
            "sol81 solution number 3 is not alpha_gap555=0.964");
      }
      model.param().set(
          "alpha_gap555", "0.964",
          "Fixed Stage 555 JFO initialization gap-coupling value");
      model.param().set(
          "stage555_jfo_initial_solnum", "3",
          "sol81 parameter index for alpha_gap555=0.964");
      model.param().set(
          "p_cav_transition555", "1[MPa]",
          "Initial smooth Elrod-Adams cavitation transition width");
      model.param().set(
          "beta_tear555", "4.6e-10[1/Pa]",
          "Water compressibility used by the JFO density relation");

      model.component(comp).physics(tff).prop("EquationType").set(
          "EquationType", "ReynoldsEquationWithCavitation");
      model.component(comp).physics(tff).prop("EquationType").set(
          "sftransition", "p_cav_transition555");
      model.component(comp).physics(tff).feature("ffp1").set(
          "UseCompressibilityForDensity", "CompressibilityForm");
      model.component(comp).physics(tff).feature("ffp1").set(
          "rho_c", "rho_tear");
      model.component(comp).physics(tff).feature("ffp1").set(
          "beta", "beta_tear555");
      model.component(comp).physics(tff).feature("ffp1").set(
          "hw1", "h_geom555");

      model.component(comp).physics(tff).feature("init1").set(
          "pfilm", "withsol('sol81',pfilm,setind(alpha_gap555,3))");

      String[] drainage = {
        "bdr_inlet520", "bdr_outlet520",
        "bdr_left520", "bdr_right520"
      };
      for (int i = 0; i < drainage.length; i++) {
        if (entities(model, drainage[i]).isEmpty()) {
          throw new IllegalStateException(
              "Empty local-film drainage boundary: " + drainage[i]);
        }
        for (int j = i + 1; j < drainage.length; j++) {
          requireDisjoint(model, drainage[i], drainage[j]);
        }
      }

      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 555 JFO initialization at alpha 0.964 - film only");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", tff, "on", globalEq, "off"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol81");
      model.study(study).feature("stat").set("initsoluse", "sol81");

      String step = study + "/stat";
      for (String feature :
          model.component(comp).physics(tff).feature().tags()) {
        try {
          model.component(comp).physics(tff).feature(feature)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature variables = model.sol(solution).feature("v1");
      variables.set("initmethod", "sol");
      variables.set("initsol", "sol81");
      variables.set("solnum", "manual");
      variables.set("manualsolnum", "3");
      variables.set("notsolmethod", "sol");
      variables.set("notsol", "sol81");
      variables.set("notsolnum", "manual");
      variables.set("notmanualsolnum", "3");

      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 300);
      stationary.feature("fc1").set("damp", "0.1");

      model.label(
          "Stage 555 JFO film-only initialization setup at alpha 0.964");
      model.save(OUTPUT);
      System.out.println("OUTPUT=" + OUTPUT);
      System.out.println("STUDY=" + study);
      System.out.println("SOLUTION=" + solution);
      System.out.println("INITIAL_SOLUTION=sol81 parameter index 3");
      System.out.println("JFO_VARIABLES=tff.p and derived tff.theta");
      for (String feature : drainage) {
        System.out.println(feature + "=" + entities(model, feature));
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
