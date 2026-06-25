import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage118_duplicated_contact_solver_variable_indent_run {
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
      m.param().set("tau_force118", "2e-3[s]");
      m.param().set("q_scale118", "0.01");
      m.component(c).variable(v).set("Fn_contact118",
          "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
      m.component(c).variable(v).set("Fn_film118",
          "scale_partitioned_pfilm*W_film_replay53");
      m.component(c).variable(v).set("Fn_total118", "Fn_contact118+Fn_film118");
      m.component(c).variable(v).set("Fn_error118",
          "(Fn_total118-F_total_target)/F_total_target");
      m.component(c).variable(v).set("dr_indent118", q + "*1[mm]");

      m.component(c).physics(ge).feature("ge1").set("equation", 1, 1,
          "Fn_error118-tau_force118*" + q + "t/q_scale118");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_indent118*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_indent118*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.component(c).physics("solid").feature("dcnt1").set("pairDisconnect", "1");
      m.component(c).physics("solid").feature("dcnt1").set("useCutback", "1");

      m.study(study).feature("time").set("tlist", "range(0.01,0.0005,0.03)");
      m.study(study).feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});

      try { m.sol().remove("sol23"); } catch (Exception ignore) {}
      m.sol().duplicate("sol23", "sol22");
      m.sol("sol23").label("Stage 118 duplicated contact solver continuation");
      m.sol("sol23").study(study);
      m.sol("sol23").feature("v1").set("initmethod", "sol");
      m.sol("sol23").feature("v1").set("initsol", "sol22");
      m.sol("sol23").feature("v1").set("initsoluse", "sol22");
      m.sol("sol23").feature("v1").set("initsolusesolnum", 11);
      m.sol("sol23").feature("v1").set("notsolmethod", "init");
      m.sol("sol23").feature("t1").set("tlist", "range(0.01,0.0005,0.03)");
      m.sol("sol23").feature("t1").set("consistent", "on");
      try { m.sol("sol23").feature("t1").feature().remove("se1"); } catch (Exception ignore) {}
      try { m.sol("sol23").feature("t1").feature().remove("fc1"); } catch (Exception ignore) {}
      m.sol("sol23").feature("t1").create("fc1", "FullyCoupled");
      m.sol("sol23").feature("t1").feature("fc1").set("linsolver", "dDef");

      m.save("200_lid8mm_stage118_duplicated_contact_solver_setup_Model.mph");
      System.out.println("Saved setup: 200_lid8mm_stage118_duplicated_contact_solver_setup_Model.mph");
      System.out.println("RUN_STAGE118 solver=sol23");
      m.sol("sol23").runAll();

      try { m.result().dataset().remove("dset118_dynamic"); } catch (Exception ignore) {}
      m.result().dataset().create("dset118_dynamic", "Solution");
      m.result().dataset("dset118_dynamic").set("solution", "sol23");
      m.result().dataset("dset118_dynamic").label(
          "Stage 118 duplicated contact solver variable-indentation result");
      String[] expr = {"Fn_contact118", "Fn_film118", "Fn_total118",
          "F_total_target", "Fn_error118", "dr_indent118", q + "t"};
      String[] unit = {"N", "N", "N", "N", "1", "mm", "1/s"};
      try { m.result().remove("pg118_total_load_indent"); } catch (Exception ignore) {}
      m.result().create("pg118_total_load_indent", "PlotGroup1D");
      m.result("pg118_total_load_indent").set("data", "dset118_dynamic");
      m.result("pg118_total_load_indent").feature().create("glob1", "Global");
      m.result("pg118_total_load_indent").feature("glob1").set("expr", expr);
      m.result("pg118_total_load_indent").feature("glob1").set("unit", unit);
      try { m.result().numerical().remove("eval118_total_load_indent"); } catch (Exception ignore) {}
      m.result().numerical().create("eval118_total_load_indent", "EvalGlobal");
      m.result().numerical("eval118_total_load_indent").set("data", "dset118_dynamic");
      m.result().numerical("eval118_total_load_indent").set("expr", expr);
      m.result().numerical("eval118_total_load_indent").set("unit", unit);
      printRange(m, "eval118_total_load_indent");

      m.save("201_lid8mm_stage118_duplicated_contact_solver_results_Model.mph");
      System.out.println("Saved result: 201_lid8mm_stage118_duplicated_contact_solver_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
