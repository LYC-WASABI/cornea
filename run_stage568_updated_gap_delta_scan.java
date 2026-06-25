import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class run_stage568_updated_gap_delta_scan {
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
          "Model", "558v_stage567_structure_balance_results.mph");
      String comp = "comp1";
      String study = "std_stage568_delta_scan";

      model.param().set(
          "delta_h568", "2[um]",
          "Stage 568 separation correction on updated structure");
      String vars = "var_stage568_delta_scan";
      try { model.component(comp).variable().remove(vars); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).label(
          "Stage 568 updated structural gap separation scan");
      model.component(comp).variable(vars)
          .selection().named("sel_film_track");
      model.component(comp).variable(vars).set(
          "h_film568_scan",
          "withsol('sol91',h_geom555)+delta_h568");

      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_film568_scan");
      model.component(comp).physics("tff").feature("init1").set(
          "pfilm",
          "withsol('sol90',pfilm,"
              + "setval(delta_h566,10.4[um]))");

      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 568 updated-gap JFO separation scan");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"delta_h568"});
      model.study(study).feature("param").set(
          "plistarr",
          new String[] {
            "2 2.5 3 3.5 4 4.5 5 5.5 6 6.5 7 7.5 8 8.5 9 9.5 10"
          });
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
      model.study(study).feature("stat").set("initsol", "sol90");
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
      dependent.set("initsol", "sol90");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol91");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 updated-gap delta scan setup");
      model.save("562a_stage568_updated_gap_delta_scan_setup.mph");
      System.out.println(
          "SETUP=562a_stage568_updated_gap_delta_scan_setup.mph");
      System.out.println("SOLUTION=" + solution);
      model.sol(solution).runAll();

      String dataset = "dset568_delta_scan";
      try { model.result().dataset().remove(dataset); }
      catch (Exception ignored) {}
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);
      String eval = "eval568_delta_scan";
      try { model.result().numerical().remove(eval); }
      catch (Exception ignored) {}
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
          "delta_h568",
          "intop_film(h_film568_scan)/intop_film(1)",
          "scale_pfilm555*intop_film(max(tff.p,0))",
          "intop_film(tff.theta)/intop_film(1)"
      });
      double[][] values = model.result().numerical(eval).getReal();
      for (int j = 0; j < values[0].length; j++) {
        System.out.printf(
            Locale.US,
            "SCAN delta_um=%.9g havg_um=%.9g Wscaled_N=%.12g theta=%.9g%n",
            values[0][j] * 1e6, values[1][j] * 1e6,
            values[2][j], values[3][j]);
      }
      model.label("Stage 568 updated-gap delta scan results");
      model.save("562b_stage568_updated_gap_delta_scan_results.mph");
      System.out.println(
          "RESULTS=562b_stage568_updated_gap_delta_scan_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
