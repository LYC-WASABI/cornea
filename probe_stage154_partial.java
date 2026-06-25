import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage154_partial {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "stage154_bounded_indent_physical_shear_output_Model.mph");
    m.result().dataset().create("dset154probe", "Solution");
    m.result().dataset("dset154probe").set("solution", "sol25");
    m.result().numerical().create("eval154probe2", "EvalGlobal");
    m.result().numerical("eval154probe2").set("data", "dset154probe");
    m.result().numerical("eval154probe2").set("expr", new String[]{
        "phi_qs142", "Fn_contact119", "Fn_film119", "Fn_total119",
        "Fn_error119", "dr_indent119",
        "withsol('sol21',F_film_shear,setval(t_replay,t_film_replay_grid))",
        "intop_film(tau_boundary_replay154)",
        "(withsol('sol21',F_film_shear,setval(t_replay,t_film_replay_grid))"
            + "+intop_film(tau_boundary_replay154))/max(Fn_total119,1e-9[N])"
    });
    double[][] a = m.result().numerical("eval154probe2").getReal();
    System.out.println("ROWS=" + a[0].length);
    for (int j = 0; j < a[0].length; j++)
      System.out.printf(Locale.US,
          "row=%d phi=%.8g Fc=%.8g Ft=%.8g err=%.8g d=%.8g Ffilm=%.8g Fb=%.8g mu=%.8g%n",
          j, a[0][j], a[1][j], a[3][j], a[4][j], a[5][j],
          a[6][j], a[7][j], a[8][j]);
    m.save("276_lid8mm_stage154_partial_verified_Model.mph");
    ModelUtil.disconnect();
  }
}
