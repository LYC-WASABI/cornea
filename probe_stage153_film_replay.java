import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage153_film_replay {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "273_lid8mm_stage153_complete_qs_mixed_shear_results_Model.mph");
    String v = "var_partitioned_local_pfilm";
    m.component("comp1").variable(v).set("h_replay154",
        "withsol('sol21',h_film_input,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(v).set("tau_film_replay154",
        "withsol('sol21',tau_film_wall,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(v).set("F_film_replay154",
        "withsol('sol21',F_film_shear,setval(t_replay,t_film_replay_grid))");
    m.result().numerical().create("eval154probe", "EvalGlobal");
    m.result().numerical("eval154probe").set("data", "dset152");
    m.result().numerical("eval154probe").set("expr", new String[]{
        "phi_qs142", "t_film_replay",
        "withsol('sol21',F_film_shear,setval(t_replay,t_film_replay_grid))",
        "withsol('sol21',W_film,setval(t_replay,t_film_replay_grid))"
    });
    double[][] a = m.result().numerical("eval154probe").getReal();
    for (int j = 0; j < a[0].length; j++)
      System.out.printf(Locale.US, "row=%d phi=%.8g t=%.8g Fshear=%.8g Wfilm=%.8g%n",
          j, a[0][j], a[1][j], a[2][j], a[3][j]);
    ModelUtil.disconnect();
  }
}
