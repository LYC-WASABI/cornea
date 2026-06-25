import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage181_exact_static {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "341_lid8mm_stage181_exact_static_results_Model.mph");
      model.result().dataset().create("dset181probe", "Solution");
      model.result().dataset("dset181probe").set("solution", "sol45");
      model.result().numerical().create("eval181probe", "EvalGlobal");
      model.result().numerical("eval181probe").set("data", "dset181probe");
      model.result().numerical("eval181probe").set(
          "expr",
          new String[] {
            "Fn_contact119", "Wfilm177", "Ftotal177", "Ferr177",
            "dr_indent119", "q_force_total111", "phi_lid_structure"
          });
      double[][] x = model.result().numerical("eval181probe").getReal();
      System.out.printf(
          Locale.US,
          "Fc=%.12g Wf=%.12g Ft=%.12g err=%.12g d=%.12g q=%.12g phi=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0], x[4][0], x[5][0], x[6][0]);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
