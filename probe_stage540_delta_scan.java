import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage540_delta_scan {
  static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) if (!old.contains(tag)) return tag;
    throw new IllegalStateException("No solution created");
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "533_stage530_local_film_stationary_checked.mph");
    String comp = "comp1";
    model.param().set("h_sep_uniform540", "0[um]");
    model.component(comp).variable("var_mixed_lub").set(
        "h_jfo197", "max(h_residual189,h0_tear+h_sep_uniform540)");
    model.param().set("p_cav_transition540", "1[MPa]");
    model.component(comp).physics("tff").prop("EquationType").set(
        "EquationType", "ReynoldsEquationWithCavitation");
    model.component(comp).physics("tff").prop("EquationType").set(
        "sftransition", "p_cav_transition540");
    model.component(comp).physics("tff").feature("init1")
        .set("pfilm", "0[Pa]");
    String study = "std_probe540scan";
    model.study().create(study);
    model.study(study).create("param", "Parametric");
    model.study(study).feature("param").set(
        "pname", new String[] {"h_sep_uniform540"});
    model.study(study).feature("param").set(
        "plistarr", new String[] {"16 18 20 22 24 26 28 30"});
    model.study(study).feature("param").set(
        "punit", new String[] {"um"});
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set(
        "activate",
        new String[] {
          "solid", "off", "tff", "on", "ge_force_total111", "off"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", "sol50");
    model.study(study).feature("stat").set("initsoluse", "sol50");
    model.study(study).feature("stat").set("initsolusesolnum", "last");
    String step = study + "/stat";
    for (String tag : model.component(comp).physics("tff").feature().tags()) {
      try {
        model.component(comp).physics("tff").feature(tag)
            .set("StudyStep", step);
      } catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    model.sol(solution).runAll();
    model.result().dataset().create("dsetProbe540", "Solution");
    model.result().dataset("dsetProbe540").set("solution", solution);
    model.result().numerical().create("evalProbe540", "EvalGlobal");
    model.result().numerical("evalProbe540").set("data", "dsetProbe540");
    model.result().numerical("evalProbe540").set(
        "expr", new String[] {
          "h_sep_uniform540",
          "intop_film(max(tff.p,0))",
          "intop_film(h_jfo197)/intop_film(1)",
          "intop_film(tff.theta)/intop_film(1)"
        });
    model.result().numerical("evalProbe540").set(
        "unit", new String[] {"um", "N", "um", "1"});
    double[][] x = model.result().numerical("evalProbe540").getReal();
    for (int i = 0; i < x[0].length; i++) {
      System.out.printf(Locale.US,
          "SCAN540 delta=%.12g um W=%.12g N havg=%.12g um theta=%.12g%n",
          x[0][i], x[1][i], x[2][i], x[3][i]);
    }
    ModelUtil.disconnect();
  }
}
