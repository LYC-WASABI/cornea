import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage568_closed_loop {
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
          "Model", "564d_stage568_relaxed_delta_structure3_results.mph");
      String comp = "comp1";
      String study = "std_stage568_closure_validation3";

      String vars = "var_stage568_closure_validation3";
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).label(
          "Stage 568 closed-loop validation gap");
      model.component(comp).variable(vars)
          .selection().named("sel_film_track");
      model.component(comp).variable(vars).set(
          "h_film568_validation3",
          "withsol('sol99',h_geom555)+delta_selected568_i3");
      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_film568_validation3");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm",
          "withsol('sol98',pfilm,"
              + "setval(delta_h568_i3,5.8[um]))");

      model.study().create(study);
      model.study(study).label(
          "Stage 568 closed-loop JFO validation");
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
      model.study(study).feature("stat").set("initsol", "sol98");
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
      dependent.set("initsol", "sol98");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol99");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 closed-loop validation 3 setup");
      model.save("564e_stage568_closed_loop_validation3_setup.mph");
      System.out.println(
          "SETUP=564e_stage568_closed_loop_validation3_setup.mph");
      System.out.println("SOLUTION=" + solution);
      model.sol(solution).runAll();

      String dataset = "dset568_closure_validation3";
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);
      String eval = "eval568_closure_validation3";
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
          "delta_selected568_i3",
          "intop_film(h_film568_validation3)/intop_film(1)",
          "scale_pfilm555*intop_film(max(tff.p,0))",
          "(scale_pfilm555*intop_film(max(tff.p,0))"
              + "-Wfilm568_selected3)/Wfilm568_selected3",
          "intop_film(tff.theta)/intop_film(1)"
      });
      System.out.println("VALIDATION=" + Arrays.deepToString(
          model.result().numerical(eval).getReal()));

      String min = "min568_validation_h3";
      model.result().numerical().create(min, "MinSurface");
      model.result().numerical(min).set("data", dataset);
      model.result().numerical(min)
          .selection().named("sel_film_track");
      model.result().numerical(min).set("expr", "h_film568_validation3");
      System.out.println("HFILM_MIN=" + Arrays.deepToString(
          model.result().numerical(min).getReal()));

      model.label("Stage 568 closed-loop validation 3 results");
      model.save("564f_stage568_closed_loop_validation3_results.mph");
      System.out.println(
          "RESULTS=564f_stage568_closed_loop_validation3_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
