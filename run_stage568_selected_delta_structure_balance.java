import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class run_stage568_selected_delta_structure_balance {
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
          "Model", "564b_stage568_delta_scan_iter3_results.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";
      String study = "std_stage568_selected_balance3";

      model.param().set(
          "delta_selected568_i3", "5.8[um]",
          "Aitken-relaxed Stage 568 separation estimate");
      String vars = "var_stage568_selected_feedback3";
      model.component(comp).variable().create(vars);
      model.component(comp).variable(vars).label(
          "Stage 568 selected updated-gap pressure feedback iteration 3");
      model.component(comp).variable(vars)
          .selection().named("sel_film_track");
      model.component(comp).variable(vars).set(
          "p_feedback568_selected3",
          "withsol('sol98',max(tff.p,0),"
              + "setval(delta_h568_i3,5.8[um]))");

      String loads = "var_stage568_selected_load3";
      model.component(comp).variable().create(loads);
      model.component(comp).variable(loads).set(
          "Wfilm568_selected3",
          "scale_pfilm555*intop_film(p_feedback568_selected3)");
      model.component(comp).variable(loads).set(
          "Ftotal568_selected3",
          "Wfilm568_selected3+Fn_contact119");
      model.component(comp).variable(loads).set(
          "Ferr568_selected3",
          "(Ftotal568_selected3-F_total_target)"
              + "/(F_total_target+1e-12[N])");

      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-scale_pfilm555*p_feedback568_selected3*nx",
                "-scale_pfilm555*p_feedback568_selected3*ny",
                "-scale_pfilm555*p_feedback568_selected3*nz"
              });
      model.component(comp).physics(ge).feature("ge1")
          .set("equation", new String[] {"Ferr568_selected3"});

      model.study().create(study);
      model.study(study).label(
          "Stage 568 Aitken-relaxed structural load balance iteration 3");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "on", ge, "on", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol97");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        model.component(comp).physics("solid").feature(tag)
            .set("StudyStep", step);
      }
      model.component(comp).physics(ge).feature("ge1")
          .set("StudyStep", step);

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol97");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol97");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 568 relaxed delta structural balance 3 setup");
      model.save("564c_stage568_relaxed_delta_structure3_setup.mph");
      System.out.println(
          "SETUP=564c_stage568_relaxed_delta_structure3_setup.mph");
      System.out.println("SOLUTION=" + solution);
      model.sol(solution).runAll();

      String dataset = "dset568_selected_structure3";
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", solution);
      String eval = "eval568_selected_structure3";
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
          "delta_selected568_i3", "Wfilm568_selected3", "Fn_contact119",
          "Ftotal568_selected3", "Ferr568_selected3",
          "dr_indent119", "q_force_total111",
          "intop_film(h_geom555)/intop_film(1)"
      });
      System.out.println("BALANCE=" + Arrays.deepToString(
          model.result().numerical(eval).getReal()));

      String min = "min568_selected_hgeom3";
      model.result().numerical().create(min, "MinSurface");
      model.result().numerical(min).set("data", dataset);
      model.result().numerical(min)
          .selection().named("sel_film_track");
      model.result().numerical(min).set("expr", "h_geom555");
      System.out.println("HGEOM_MIN=" + Arrays.deepToString(
          model.result().numerical(min).getReal()));

      model.label("Stage 568 relaxed delta structural balance 3 results");
      model.save("564d_stage568_relaxed_delta_structure3_results.mph");
      System.out.println(
          "RESULTS=564d_stage568_relaxed_delta_structure3_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
