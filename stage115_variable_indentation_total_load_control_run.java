import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage115_variable_indentation_total_load_control_run {
  private static String newestSolution(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String tag : m.sol().tags()) {
      last = tag;
      if (!old.contains(tag)) return tag;
    }
    return last;
  }

  private static void removeOldLoadEquations(Model m) {
    for (String tag : m.component("comp1").physics().tags()) {
      if (tag.startsWith("ge_force_total") || tag.equals("ge_force_mixed")) {
        try { m.component("comp1").physics().remove(tag); } catch (Exception ignore) {}
      }
    }
  }

  private static void fullyCoupledStationary(Model m, String solTag) {
    SolverFeature s1 = m.sol(solTag).feature("s1");
    try { s1.feature().remove("se1"); } catch (Exception ignore) {}
    try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
    s1.create("fc1", "FullyCoupled");
    s1.feature("fc1").label("Fully coupled static total-load preload");
    s1.feature("fc1").set("linsolver", "dDef");
  }

  private static void fullyCoupledTransient(Model m, String solTag) {
    SolverFeature t1 = m.sol(solTag).feature("t1");
    t1.set("consistent", "off");
    try { t1.feature().remove("se1"); } catch (Exception ignore) {}
    try { t1.feature().remove("fc1"); } catch (Exception ignore) {}
    t1.create("fc1", "FullyCoupled");
    t1.feature("fc1").label("Fully coupled regularized total-load controller");
    t1.feature("fc1").set("linsolver", "dDef");
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
      Model m = ModelUtil.load("Model", "191_lid8mm_stage113_medium_restore_to_0020_results_Model.mph");
      String c = "comp1";
      String v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total115";
      String q = "q_indent115";

      removeOldLoadEquations(m);
      m.param().set("scale_partitioned_pfilm", "1.0",
          "Full applied film-pressure fraction in total-load control");
      m.param().set("q_indent115_init", "2e-4",
          "Initial dimensionless indentation estimate; indentation=q*1 mm");
      m.param().set("tau_force115", "2e-3[s]",
          "Regularization time constant for normal-load controller");
      m.param().set("q_scale115", "0.01",
          "Dimensionless indentation scale for controller regularization");

      m.component(c).physics("solid").feature("dcnt1").set("fp_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_init_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("fp_fin_penalty", "0.3");
      m.component(c).physics("solid").feature("dcnt1").set("useCutback", "1");
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "1");
      try {
        m.component(c).physics("solid").feature("dcnt1")
            .feature("fric_partitioned_stabilizer").set("ft_penalty", "0.3");
      } catch (Exception ignore) {}

      m.component(c).variable(v).set("dr_indent115", q + "*1[mm]");
      m.component(c).variable(v).set("Fn_contact115",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film115",
          "scale_partitioned_pfilm*W_film_sched54(0)");
      m.component(c).variable(v).set("Fn_total115", "Fn_contact115+Fn_film115");
      m.component(c).variable(v).set("Fn_error115",
          "(Fn_total115-F_total_target)/F_total_target");

      m.component(c).physics().create(ge, "GlobalEquations");
      m.component(c).physics(ge).label("Variable indentation enforcing total 0.03 N normal load");
      m.component(c).physics(ge).feature("ge1").set("name", 1, 1, q);
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1, "Fn_error115");
      m.component(c).physics(ge).feature("ge1").set("initialValueU", 1, 1, "q_indent115_init");
      m.component(c).physics(ge).feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics(ge).feature("ge1").set("description", 1, 1,
          "Solved radial indentation; static strict and transient regularized load control");

      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "-dr_indent115*Y/sqrt(Y^2+Z^2)",
          "-dr_indent115*Z/sqrt(Y^2+Z^2)"
      });

      try { m.study().remove("std_static_total_load115"); } catch (Exception ignore) {}
      m.study().create("std_static_total_load115");
      m.study("std_static_total_load115").label("Stage 115 static 0.03 N total-load preload");
      m.study("std_static_total_load115").create("stat", "Stationary");
      m.study("std_static_total_load115").feature("stat").set("geometricNonlinearity", "on");
      m.study("std_static_total_load115").feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study("std_static_total_load115").feature("stat").set("useinitsol", "off");

      m.save("193_lid8mm_stage115_variable_indentation_total_load_setup_Model.mph");
      System.out.println("Saved setup: 193_lid8mm_stage115_variable_indentation_total_load_setup_Model.mph");

      String[] beforeStatic = m.sol().tags();
      m.study("std_static_total_load115").createAutoSequences("sol");
      String solStatic = newestSolution(m, beforeStatic);
      fullyCoupledStationary(m, solStatic);
      System.out.println("RUN_STATIC_PRELOAD solver=" + solStatic);
      m.sol(solStatic).runAll();

      try { m.result().dataset().remove("dset115_static"); } catch (Exception ignore) {}
      m.result().dataset().create("dset115_static", "Solution");
      m.result().dataset("dset115_static").label("Stage 115 static total-load preload");
      m.result().dataset("dset115_static").set("solution", solStatic);
      try { m.result().numerical().remove("eval115_static"); } catch (Exception ignore) {}
      m.result().numerical().create("eval115_static", "EvalGlobal");
      m.result().numerical("eval115_static").set("data", "dset115_static");
      m.result().numerical("eval115_static").set("expr",
          new String[]{"Fn_contact115", "Fn_film115", "Fn_total115", "F_total_target",
              "Fn_error115", "dr_indent115"});
      m.result().numerical("eval115_static").set("unit",
          new String[]{"N", "N", "N", "N", "1", "mm"});
      printRange(m, "eval115_static");
      m.save("194_lid8mm_stage115_static_variable_indentation_results_Model.mph");
      System.out.println("Saved static result: 194_lid8mm_stage115_static_variable_indentation_results_Model.mph");

      m.component(c).variable(v).set("Fn_film115",
          "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1,
          "Fn_error115+tau_force115*" + q + "t/q_scale115");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent115*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent115*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });

      try { m.study().remove("std_dynamic_total_load115"); } catch (Exception ignore) {}
      m.study().create("std_dynamic_total_load115");
      m.study("std_dynamic_total_load115").label("Stage 115 dynamic variable-indentation total-load control");
      m.study("std_dynamic_total_load115").create("time", "Transient");
      m.study("std_dynamic_total_load115").feature("time").set("tlist", "range(0,0.0005,0.02)");
      m.study("std_dynamic_total_load115").feature("time").set("geometricNonlinearity", "on");
      m.study("std_dynamic_total_load115").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study("std_dynamic_total_load115").feature("time").set("useinitsol", "on");
      m.study("std_dynamic_total_load115").feature("time").set("initmethod", "sol");
      m.study("std_dynamic_total_load115").feature("time").set("initstudy", "std_static_total_load115");
      m.study("std_dynamic_total_load115").feature("time").set("initstudystep", "stat");
      m.study("std_dynamic_total_load115").feature("time").set("initsol", solStatic);
      m.study("std_dynamic_total_load115").feature("time").set("initsoluse", solStatic);

      String[] beforeDynamic = m.sol().tags();
      m.study("std_dynamic_total_load115").createAutoSequences("sol");
      String solDynamic = newestSolution(m, beforeDynamic);
      fullyCoupledTransient(m, solDynamic);
      System.out.println("RUN_DYNAMIC_REGULARIZED solver=" + solDynamic);
      m.sol(solDynamic).runAll();

      try { m.result().dataset().remove("dset115_dynamic"); } catch (Exception ignore) {}
      m.result().dataset().create("dset115_dynamic", "Solution");
      m.result().dataset("dset115_dynamic").label("Stage 115 dynamic regularized 0.03 N control");
      m.result().dataset("dset115_dynamic").set("solution", solDynamic);

      String[] expr = new String[]{"Fn_contact115", "Fn_film115", "Fn_total115",
          "F_total_target", "Fn_error115", "dr_indent115", q + "t"};
      String[] unit = new String[]{"N", "N", "N", "N", "1", "mm", "1/s"};
      try { m.result().remove("pg115_total_load"); } catch (Exception ignore) {}
      m.result().create("pg115_total_load", "PlotGroup1D");
      m.result("pg115_total_load").label("Total normal load and variable indentation");
      m.result("pg115_total_load").set("data", "dset115_dynamic");
      m.result("pg115_total_load").feature().create("glob1", "Global");
      m.result("pg115_total_load").feature("glob1").set("expr", expr);
      m.result("pg115_total_load").feature("glob1").set("unit", unit);

      try { m.result().numerical().remove("eval115_dynamic"); } catch (Exception ignore) {}
      m.result().numerical().create("eval115_dynamic", "EvalGlobal");
      m.result().numerical("eval115_dynamic").set("data", "dset115_dynamic");
      m.result().numerical("eval115_dynamic").set("expr", expr);
      m.result().numerical("eval115_dynamic").set("unit", unit);
      printRange(m, "eval115_dynamic");

      m.save("195_lid8mm_stage115_dynamic_variable_indentation_total_load_results_Model.mph");
      System.out.println("Saved dynamic result: 195_lid8mm_stage115_dynamic_variable_indentation_total_load_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
