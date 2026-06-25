import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage156_partial {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "stage156_resume_minus48_to_minus70_output_Model.mph");
    m.result().dataset().create("dset156probe", "Solution");
    m.result().dataset("dset156probe").set("solution", "sol27");
    m.result().numerical().create("eval156probe", "EvalGlobal");
    m.result().numerical("eval156probe").set("data", "dset156probe");
    m.result().numerical("eval156probe").set("expr", new String[]{
        "phi_qs142", "Fn_contact119", "Fn_total119", "Fn_error119",
        "dr_indent119", "F_friction_physical154", "mu_physical154"
    });
    double[][] a = m.result().numerical("eval156probe").getReal();
    System.out.println("ROWS=" + a[0].length);
    for (int j = 0; j < a[0].length; j++)
      System.out.printf(Locale.US,
          "row=%d phi=%.8g Fc=%.8g Ft=%.8g err=%.8g d=%.8g Ff=%.8g mu=%.8g%n",
          j, a[0][j], a[1][j], a[2][j], a[3][j],
          a[4][j], a[5][j], a[6][j]);
    m.save("281_lid8mm_stage156_partial_verified_Model.mph");
    ModelUtil.disconnect();
  }
}
