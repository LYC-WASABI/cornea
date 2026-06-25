import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_run_stage563_structure_balance {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No Stage 563 solver created");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558k_stage562_JFO_separation_scan_results.mph");
      String comp = "comp1";
      String study = "std_stage563";

      model.param().set(
          "delta_h562", "3[um]",
          "Selected Stage 562 separation for Stage 563 balance");

      String variables = "var_stage563_pressure";
      try { model.component(comp).variable().remove(variables); }
      catch (Exception ignored) {}
      model.component(comp).variable().create(variables);
      model.component(comp).variable(variables).label(
          "Stage 563 frozen delta3um JFO pressure");
      model.component(comp).variable(variables)
          .selection().named("sel_film_track");
      model.component(comp).variable(variables).set(
          "p_feedback563",
          "withsol('sol86',max(tff.p,0),"
              + "setval(delta_h562,3[um]))");

      model.component(comp).variable("var_load_coupled555").set(
          "Wfilm563",
          "scale_pfilm555*intop_film(p_feedback563)");
      model.component(comp).variable("var_load_coupled555").set(
          "Ftotal563", "Wfilm563+Fn_contact119");
      model.component(comp).variable("var_load_coupled555").set(
          "Ferr563",
          "(Ftotal563-F_total_target)/F_total_target"
              + "+eps_q_regular555*(q_force_total111-q_ref555)");
      model.component(comp).variable("var_load_coupled555").set(
          "Wfilm555", "Wfilm563");
      model.component(comp).variable("var_load_coupled555").set(
          "Ftotal555", "Ftotal563");
      model.component(comp).variable("var_load_coupled555").set(
          "Ferr555", "Ferr563");

      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-scale_pfilm555*p_feedback563*nx",
                "-scale_pfilm555*p_feedback563*ny",
                "-scale_pfilm555*p_feedback563*nz"
              });

      model.component(comp).physics("ge_force_total111")
          .feature("ge1").set("equation", new String[] {"Ferr563"});

      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 563 structure balance with delta_h562 3 um JFO pressure");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "on", "ge_force_total111", "on",
            "tff", "off", "frame:spatial1", "on",
            "frame:material1", "on", "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol85");
      model.study(study).feature("stat").set("initsoluse", "current");

      String step = study + "/stat";
      model.component(comp).physics("ge_force_total111")
          .feature("ge1").set("StudyStep", step);

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol85");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol85");
      dependent.set("notsolnum", "last");

      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 563 delta3um JFO structure balance setup");
      model.save("558l_stage563_structure_balance_delta3um_setup.mph");
      System.out.println(
          "SETUP=558l_stage563_structure_balance_delta3um_setup.mph");
      System.out.println("SOLUTION=" + solution);

      model.sol(solution).runAll();
      model.label("Stage 563 delta3um JFO structure balance results");
      model.save("558m_stage563_structure_balance_delta3um_results.mph");
      System.out.println(
          "RESULTS=558m_stage563_structure_balance_delta3um_results.mph");

      String dataset = "dset563_balance";
      try { model.result().dataset().remove(dataset); }
      catch (Exception ignored) {}
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).label(
          "Stage 563 structure balance delta3um");
      model.result().dataset(dataset).set("solution", solution);

      String table = "tbl563_balance";
      try { model.result().table().remove(table); }
      catch (Exception ignored) {}
      model.result().table().create(table, "Table");
      model.result().table(table).label(
          "Stage 563 load balance validation");

      String eval = "eval563_balance";
      try { model.result().numerical().remove(eval); }
      catch (Exception ignored) {}
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).label(
          "Stage 563 film contact total load balance");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
        "delta_h562", "Wfilm563", "Fn_contact119",
        "Ftotal563", "Ferr563", "dr_indent119",
        "q_force_total111"
      });
      model.result().numerical(eval).set("unit", new String[] {
        "um", "N", "N", "N", "1", "mm", "1"
      });
      model.result().numerical(eval).set("table", table);
      model.result().numerical(eval).setResult();

      double[][] values =
          model.result().numerical(eval).getReal();
      System.out.println("BALANCE=" + Arrays.deepToString(values));

      String minGap = "min563_gap";
      try { model.result().numerical().remove(minGap); }
      catch (Exception ignored) {}
      model.result().numerical().create(minGap, "MinSurface");
      model.result().numerical(minGap).label(
          "Stage 563 minimum geometric film thickness");
      model.result().numerical(minGap).set("data", dataset);
      model.result().numerical(minGap)
          .selection().named("sel_film_track");
      model.result().numerical(minGap).set("expr", "h_geom555");
      model.result().numerical(minGap).set("unit", "um");
      System.out.println("HGEOM_MIN=" + Arrays.deepToString(
          model.result().numerical(minGap).getReal()));

      model.save("558m_stage563_structure_balance_delta3um_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
