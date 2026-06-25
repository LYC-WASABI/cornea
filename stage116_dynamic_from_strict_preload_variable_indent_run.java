import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage116_dynamic_from_strict_preload_variable_indent_run {
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
      for (double v : vals[i]) {
        if (!Double.isNaN(v)) {
          min = Math.min(min, v);
          max = Math.max(max, v);
        }
      }
      System.out.println("  expr" + i + " min=" + min + " max=" + max);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111";
      String q = "q_force_total111";

      m.param().set("scale_partitioned_pfilm", "1.0");
      m.param().set("tau_force116", "2e-3[s]",
          "Regularization time constant for dynamic total-load control");
      m.param().set("q_scale116", "0.01",
          "Dimensionless indentation scale for controller");

      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "1");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "1e-1");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "1");
      m.component(c).physics("solid").feature("dcnt1").set("useCutback", "1");
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "1");

      m.component(c).variable(v).set("Fn_contact116",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film116",
          "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total116", "Fn_contact116+Fn_film116");
      m.component(c).variable(v).set("Fn_error116",
          "(Fn_total116-F_total_target)/F_total_target");
      m.component(c).variable(v).set("dr_indent116", q + "*1[mm]");

      m.component(c).physics(ge).label(
          "Regularized variable indentation enforcing contact plus film equals 0.03 N");
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1,
          "Fn_error116-tau_force116*" + q + "t/q_scale116");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent116*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent116*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });

      try { m.study().remove("std_dynamic_total_load116"); } catch (Exception ignore) {}
      m.study().create("std_dynamic_total_load116");
      m.study("std_dynamic_total_load116").label(
          "Stage 116 dynamic scratch from strict 0.03 N preload");
      m.study("std_dynamic_total_load116").create("time", "Transient");
      m.study("std_dynamic_total_load116").feature("time").set(
          "tlist", "range(0.01,0.0005,0.03)");
      m.study("std_dynamic_total_load116").feature("time").set(
          "geometricNonlinearity", "on");
      m.study("std_dynamic_total_load116").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study("std_dynamic_total_load116").feature("time").set("useinitsol", "on");
      m.study("std_dynamic_total_load116").feature("time").set("initmethod", "sol");
      m.study("std_dynamic_total_load116").feature("time").set(
          "initstudy", "std_shear_feedback73");
      m.study("std_dynamic_total_load116").feature("time").set(
          "initstudystep", "time");
      m.study("std_dynamic_total_load116").feature("time").set("initsol", "sol22");
      m.study("std_dynamic_total_load116").feature("time").set("initsoluse", "sol22");

      m.component(c).physics("solid").feature("dcnt1").set(
          "StudyStep", "std_dynamic_total_load116/time");
      m.component(c).physics("solid").feature("disp_lid_time").set(
          "StudyStep", "std_dynamic_total_load116/time");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "StudyStep", "std_dynamic_total_load116/time");
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").set(
                "StudyStep", "std_dynamic_total_load116/time");
      } catch (Exception ignore) {}

      m.save("196_lid8mm_stage116_dynamic_variable_indent_from_preload_setup_Model.mph");
      System.out.println("Saved setup: 196_lid8mm_stage116_dynamic_variable_indent_from_preload_setup_Model.mph");

      String[] before = m.sol().tags();
      m.study("std_dynamic_total_load116").createAutoSequences("sol");
      String solTag = newestSolution(m, before);
      SolverFeature t1 = m.sol(solTag).feature("t1");
      t1.set("consistent", "on");
      try { t1.feature().remove("se1"); } catch (Exception ignore) {}
      try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
      t1.create("fc1", "FullyCoupled");
      t1.feature("fc1").label("Fully coupled regularized variable-indentation controller");
      t1.feature("fc1").set("linsolver", "dDef");

      System.out.println("RUN_STAGE116 solver=" + solTag);
      m.sol(solTag).runAll();

      try { m.result().dataset().remove("dset116_dynamic"); } catch (Exception ignore) {}
      m.result().dataset().create("dset116_dynamic", "Solution");
      m.result().dataset("dset116_dynamic").label(
          "Stage 116 dynamic scratch with solved indentation");
      m.result().dataset("dset116_dynamic").set("solution", solTag);

      String[] expr = new String[]{"Fn_contact116", "Fn_film116", "Fn_total116",
          "F_total_target", "Fn_error116", "dr_indent116", q + "t"};
      String[] unit = new String[]{"N", "N", "N", "N", "1", "mm", "1/s"};
      try { m.result().remove("pg116_total_load_indent"); } catch (Exception ignore) {}
      m.result().create("pg116_total_load_indent", "PlotGroup1D");
      m.result("pg116_total_load_indent").label(
          "Contact load, film load, total load, and solved indentation");
      m.result("pg116_total_load_indent").set("data", "dset116_dynamic");
      m.result("pg116_total_load_indent").feature().create("glob1", "Global");
      m.result("pg116_total_load_indent").feature("glob1").set("expr", expr);
      m.result("pg116_total_load_indent").feature("glob1").set("unit", unit);

      try { m.result().numerical().remove("eval116_total_load_indent"); } catch (Exception ignore) {}
      m.result().numerical().create("eval116_total_load_indent", "EvalGlobal");
      m.result().numerical("eval116_total_load_indent").set("data", "dset116_dynamic");
      m.result().numerical("eval116_total_load_indent").set("expr", expr);
      m.result().numerical("eval116_total_load_indent").set("unit", unit);
      printRange(m, "eval116_total_load_indent");

      m.save("197_lid8mm_stage116_dynamic_variable_indent_from_preload_results_Model.mph");
      System.out.println("Saved result: 197_lid8mm_stage116_dynamic_variable_indent_from_preload_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
