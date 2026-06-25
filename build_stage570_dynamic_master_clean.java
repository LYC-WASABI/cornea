import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage570_dynamic_master_clean {
  private static void removeResult(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); } catch (Exception ignored) {}
  }

  private static String newest(Model model, String[] before) {
    Set<String> previous = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!previous.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution was created");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "566c_stage569_true_pair_gap_checked.mph");
      ModelNode comp = model.component("comp1");

      model.label("Stage 570 raw dynamic master from checked Stage 569");
      model.save("570a_stage570_dynamic_master_raw.mph");

      model.param().set(
          "stage570_revision", "570",
          "Clean master for the fully dynamic scratch branch");
      model.param().set(
          "lambda_p570", "0",
          "Dynamic film-pressure feedback continuation factor");

      for (String tag : new String[] {
          "press_iop1", "load_partitioned_pfilm",
          "load_shear_cornea73", "load_shear_lid73"
      }) {
        try { comp.physics("solid").feature().remove(tag); }
        catch (Exception ignored) {}
      }

      comp.physics("tff").feature("ffp1").set("hw1", "h_residual569");
      comp.physics("tff").feature("ffp1").set("hb1", "0");

      for (String tag : new String[] {
          "var_stage40_final", "var_partition176", "var_jfo200",
          "var1", "var_stage562_gap", "var_stage563_pressure", "var2"
      }) {
        try { comp.variable().remove(tag); } catch (Exception ignored) {}
      }

      String cleanVars = "var_dynamic_clean570";
      try { comp.variable().remove(cleanVars); } catch (Exception ignored) {}
      comp.variable().create(cleanVars);
      comp.variable(cleanVars).label(
          "Stage 570 contact-only dynamic master variables");
      comp.variable(cleanVars).set(
          "Fn_contact570",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      comp.variable(cleanVars).set("Fn_film570", "0[N]");
      comp.variable(cleanVars).set("Fn_total570", "Fn_contact570");
      comp.variable(cleanVars).set(
          "Ferr570",
          "(Fn_total570-F_total_target)/(F_total_target+1e-12[N])"
              + "+eps_q_regular555*(q_force_total111-q_ref555)");
      comp.variable(cleanVars).set(
          "dr_indent570", "q_force_total111*1[mm]");

      comp.variable("var_partitioned_local_pfilm").set(
          "Fn_contact119", "Fn_contact570");
      comp.variable("var_partitioned_local_pfilm").set(
          "Fn_film119", "0[N]");
      comp.variable("var_partitioned_local_pfilm").set(
          "Fn_total119", "Fn_contact570");
      comp.variable("var_partitioned_local_pfilm").set(
          "Fn_error119",
          "(Fn_contact570-F_total_target)/(F_total_target+1e-12[N])");
      comp.variable("var_partitioned_local_pfilm").set(
          "dr_indent119", "dr_indent570");
      comp.physics("ge_force_total111").feature("ge1")
          .set("equation", new String[] {"Ferr570"});

      Set<String> keepDatasets = new HashSet<>(Arrays.asList(
          "dset570", "dset572", "dset569_pair_gap"));
      for (String tag : model.result().dataset().tags().clone()) {
        if (!keepDatasets.contains(tag)) removeDataset(model, tag);
      }
      Set<String> keepPlots = new HashSet<>(Arrays.asList(
          "pg569_true_gap", "pg569_pair_mask", "pg569_gap_error"));
      for (String tag : model.result().tags().clone()) {
        if (!keepPlots.contains(tag)) removeResult(model, tag);
      }
      Set<String> keepNumericals = new HashSet<>(Arrays.asList(
          "eval569_pair_gap", "min569_pair_gap", "max569_pair_gap",
          "min569_regular_gap", "max569_contact_pressure",
          "avg569_contact_gap"));
      for (String tag : model.result().numerical().tags().clone()) {
        if (!keepNumericals.contains(tag)) removeNumerical(model, tag);
      }
      for (String tag : model.result().table().tags().clone()) {
        if (!"tbl569_pair_gap".equals(tag)) removeTable(model, tag);
      }

      Set<String> keepSolutions = new HashSet<>(Arrays.asList("sol90", "sol92"));
      for (String tag : model.sol().tags().clone()) {
        if (!keepSolutions.contains(tag)) {
          try { model.sol().remove(tag); } catch (Exception ignored) {}
        }
      }
      Set<String> keepStudies = new HashSet<>(Arrays.asList(
          "std10", "std_stage569_gap_compile"));
      for (String tag : model.study().tags().clone()) {
        if (!keepStudies.contains(tag)) {
          try { model.study().remove(tag); } catch (Exception ignored) {}
        }
      }

      String study = "std570_clean_structure";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 570 clean contact-only structural balance");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "on", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol92");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }
      comp.physics("ge_force_total111").feature("ge1")
          .set("StudyStep", step);

      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol92");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol92");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      for (String tag : stationary.feature().tags()) {
        if (tag.startsWith("se")) {
          try { stationary.feature().remove(tag); }
          catch (Exception ignored) {}
        }
      }
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 570 clean dynamic master setup");
      model.save("570b_stage570_dynamic_master_clean_setup.mph");
      model.sol(solution).runAll();

      String dataset = "dset570_clean";
      removeDataset(model, dataset);
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).label(
          "Stage 570 clean contact-only structure");
      model.result().dataset(dataset).set("solution", solution);
      String eval = "eval570_clean";
      removeNumerical(model, eval);
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
          "Fn_contact570", "Fn_total570", "Ferr570",
          "dr_indent570", "q_force_total111"
      });
      double[][] values = model.result().numerical(eval).getReal();
      System.out.println("CLEAN_SOLUTION=" + solution);
      System.out.println("CLEAN_BALANCE=" + Arrays.deepToString(values));
      System.out.println("STUDIES=" + Arrays.toString(model.study().tags()));
      System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
      System.out.println("PHYSICS_FEATURES="
          + Arrays.toString(comp.physics("solid").feature().tags()));
      System.out.println("TFF_HW1="
          + comp.physics("tff").feature("ffp1").getString("hw1"));

      model.label("Stage 570 clean dynamic master checked");
      model.save("570b_stage570_dynamic_master_clean.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
