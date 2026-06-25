import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage117_original_study_regularized_indent_continuation_run {
  private static String newestSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String tag : m.sol().tags()) {
      last = tag;
      if (!old.contains(tag)) return tag;
    }
    return last;
  }

  private static void printRange(Model m, String tag) {
    double[][] vals = m.result().numerical(tag).getReal();
    System.out.println(tag + " rows=" + vals.length + " cols=" + vals[0].length);
    for (int i = 0; i < vals.length; i++) {
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double x : vals[i]) {
        if (!Double.isNaN(x)) {
          min = Math.min(min, x);
          max = Math.max(max, x);
        }
      }
      System.out.println("  expr" + i + " min=" + min + " max=" + max);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111";
      String q = "q_force_total111";
      String study = "std_shear_feedback73";

      m.param().set("scale_partitioned_pfilm", "1.0");
      m.param().set("tau_force117", "2e-3[s]");
      m.param().set("q_scale117", "0.01");
      m.component(c).variable(v).set("Fn_contact117",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film117",
          "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total117", "Fn_contact117+Fn_film117");
      m.component(c).variable(v).set("Fn_error117",
          "(Fn_total117-F_total_target)/F_total_target");
      m.component(c).variable(v).set("dr_indent117", q + "*1[mm]");

      m.component(c).physics(ge).label(
          "Stage 117 regularized solved indentation for total 0.03 N load");
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1,
          "Fn_error117-tau_force117*" + q + "t/q_scale117");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent117*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent117*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "1");
      m.component(c).physics("solid").feature("dcnt1").set("useCutback", "1");

      m.study(study).feature("time").set("tlist", "range(0.01,0.0005,0.03)");
      m.study(study).feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study(study).feature("time").set("useinitsol", "on");
      m.study(study).feature("time").set("initmethod", "sol");
      m.study(study).feature("time").set("initstudy", study);
      m.study(study).feature("time").set("initstudystep", "time");
      m.study(study).feature("time").set("initsol", "sol22");
      m.study(study).feature("time").set("initsoluse", "sol22");

      m.save("198_lid8mm_stage117_original_study_variable_indent_setup_Model.mph");
      System.out.println("Saved setup: 198_lid8mm_stage117_original_study_variable_indent_setup_Model.mph");

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String solTag = newestSolution(m, before);
      SolverFeature t1 = m.sol(solTag).feature("t1");
      t1.set("consistent", "on");
      try { t1.feature().remove("se1"); } catch (Exception ignore) {}
      try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
      t1.create("fc1", "FullyCoupled");
      t1.feature("fc1").set("linsolver", "dDef");
      System.out.println("RUN_STAGE117 solver=" + solTag);
      m.sol(solTag).runAll();

      try { m.result().dataset().remove("dset117_dynamic"); } catch (Exception ignore) {}
      m.result().dataset().create("dset117_dynamic", "Solution");
      m.result().dataset("dset117_dynamic").set("solution", solTag);
      m.result().dataset("dset117_dynamic").label(
          "Stage 117 original-study variable-indentation continuation");

      String[] expr = {"Fn_contact117", "Fn_film117", "Fn_total117",
          "F_total_target", "Fn_error117", "dr_indent117", q + "t"};
      String[] unit = {"N", "N", "N", "N", "1", "mm", "1/s"};
      try { m.result().remove("pg117_total_load_indent"); } catch (Exception ignore) {}
      m.result().create("pg117_total_load_indent", "PlotGroup1D");
      m.result("pg117_total_load_indent").set("data", "dset117_dynamic");
      m.result("pg117_total_load_indent").label(
          "Stage 117 total load and solved indentation");
      m.result("pg117_total_load_indent").feature().create("glob1", "Global");
      m.result("pg117_total_load_indent").feature("glob1").set("expr", expr);
      m.result("pg117_total_load_indent").feature("glob1").set("unit", unit);
      try { m.result().numerical().remove("eval117_total_load_indent"); } catch (Exception ignore) {}
      m.result().numerical().create("eval117_total_load_indent", "EvalGlobal");
      m.result().numerical("eval117_total_load_indent").set("data", "dset117_dynamic");
      m.result().numerical("eval117_total_load_indent").set("expr", expr);
      m.result().numerical("eval117_total_load_indent").set("unit", unit);
      printRange(m, "eval117_total_load_indent");

      m.save("199_lid8mm_stage117_original_study_variable_indent_results_Model.mph");
      System.out.println("Saved result: 199_lid8mm_stage117_original_study_variable_indent_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
