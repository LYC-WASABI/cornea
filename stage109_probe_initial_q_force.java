import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage109_probe_initial_q_force {
  private static void removeNumerical(Model m, String tag) { try { m.result().numerical().remove(tag); } catch (Exception ignore) {} }

  private static double evalFn(Model m, String tag, String dset) {
    removeNumerical(m, tag);
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", dset);
    m.result().numerical(tag).set("expr", "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    m.result().numerical(tag).set("unit", "N");
    return m.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      m.param().set("q_probe109", "0.002", "Probe radial indentation at t=0");
      m.param().set("scale_partitioned_pfilm", "0.1");
      try { m.component(c).physics().remove("ge_force_total108"); } catch (Exception ignore) {}
      try { m.component(c).physics().remove("ge_force_total107"); } catch (Exception ignore) {}
      m.component(c).variable(v).set("dr_force_probe109", "q_probe109*1[mm]");
      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_probe109*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_probe109*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate", new String[]{"solid", "on", "tff", "off"});
      m.study("std_shear_feedback73").feature("time").set("tlist", "range(0,0.001,0.001)");
      double[] qs = new double[]{0.0002,0.0005,0.001,0.0015,0.002,0.003,0.004,0.005,0.008,0.010};
      for (int i = 0; i < qs.length; i++) {
        m.param().set("q_probe109", Double.toString(qs[i]));
        try {
          m.study("std_shear_feedback73").run();
          String dset = "dset_probe109_" + i;
          try { m.result().dataset().remove(dset); } catch (Exception ignore) {}
          m.result().dataset().create(dset, "Solution");
          String[] sols = m.sol().tags();
          m.result().dataset(dset).set("solution", sols[sols.length-1]);
          double fn = evalFn(m, "eval_probe109_" + i, dset);
          System.out.println("Q_PROBE q=" + qs[i] + " Fn=" + fn + " N");
        } catch (Exception e) {
          System.out.println("Q_PROBE q=" + qs[i] + " FAILED " + e.getMessage());
        }
      }
      m.save("187_lid8mm_stage109_probe_initial_q_Model.mph");
      System.out.println("Saved local: 187_lid8mm_stage109_probe_initial_q_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
