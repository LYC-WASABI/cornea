import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage161_film_force_definition {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
    for (String vt : m.component("comp1").variable().tags()) {
      for (String n : new String[]{"film_force_n_density", "W_film",
          "fwall_dot_n", "gap_replay_tear", "h_inside_lid",
          "h_available", "h_geom_limit", "lid_width_coord",
          "theta_geom_dyn", "phi_lid_film_replay"}) {
        try {
          String x = m.component("comp1").variable(vt).get(n);
          if (x != null && !x.isEmpty()) System.out.println(vt+" "+n+"="+x);
        } catch(Exception ignore) {}
      }
    }
    m.result().dataset().create("dsetforceprobe", "Solution");
    m.result().dataset("dsetforceprobe").set("solution", "sol21");
    String[] expr = {
        "t_replay", "intop_film(1)", "intop_film(max(pfilm,0))",
        "intop_film(pfilm)", "W_film",
        "intop_film(h_film_input)/intop_film(1)"
    };
    m.result().numerical().create("evalforceprobe", "EvalGlobal");
    m.result().numerical("evalforceprobe").set("data", "dsetforceprobe");
    m.result().numerical("evalforceprobe").set("expr", expr);
    double[][] a = m.result().numerical("evalforceprobe").getReal();
    for(int j=0;j<a[0].length;j++)
      System.out.printf(Locale.US,
          "row=%d t=%.6g A=%.6g IntPosP=%.6g IntP=%.6g W=%.6g Pmax=%.6g havg=%.6g%n",
          j,a[0][j],a[1][j],a[2][j],a[3][j],a[4][j],0.0,a[5][j]);
    ModelUtil.disconnect();
  }
}
