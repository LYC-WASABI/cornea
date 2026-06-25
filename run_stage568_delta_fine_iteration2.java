import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class run_stage568_delta_fine_iteration2 {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "563b_stage568_delta_scan_iter2_results.mph");
      String comp = "comp1";
      String study = "std_stage568_delta_fine2";
      model.param().set("delta_h568_i2fine", "6.15[um]");
      String vars = "var_stage568_delta_fine2";
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars)
          .selection().named("sel_film_track");
      model.component(comp).variable(vars).set(
          "h_film568_fine2",
          "withsol('sol94',h_geom555)+delta_h568_i2fine");
      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_film568_fine2");
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm", "withsol('sol95',pfilm,"
              + "setval(delta_h568_i2,6.25[um]))");

      model.study().create(study);
      model.study(study).label("Stage 568 fine separation iteration 2");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h568_i2fine"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {"6.15 6.175 6.2 6.225 6.25"});
      model.study(study).feature("param").set(
          "punit", new String[] {"um"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "off", "ge_force_total111", "off", "tff", "on",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol95");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol95");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol94");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 fine separation iteration 2 setup");
      model.save("563c_stage568_delta_fine_iter2_setup.mph");
      System.out.println("SETUP=563c_stage568_delta_fine_iter2_setup.mph");
      System.out.println("SOLUTION=" + solution);
      model.sol(solution).runAll();

      String dataset = "dset568_delta_fine2";
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);
      String eval = "eval568_delta_fine2";
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
          "delta_h568_i2fine",
          "scale_pfilm555*intop_film(max(tff.p,0))"
      });
      double[][] values = model.result().numerical(eval).getReal();
      for (int j = 0; j < values[0].length; j++) {
        System.out.printf(
            Locale.US, "FINE2 delta=%.9g W=%.12g%n",
            values[0][j], values[1][j]);
      }
      model.label("Stage 568 fine separation iteration 2 results");
      model.save("563d_stage568_delta_fine_iter2_results.mph");
      System.out.println("RESULTS=563d_stage568_delta_fine_iter2_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
