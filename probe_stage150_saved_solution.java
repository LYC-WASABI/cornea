import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage150_saved_solution {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "stage150_clean_moving_film_normal_path_output_Model.mph");

    System.out.println("solutions=" + Arrays.toString(m.sol().tags()));
    String sol = "sol23";
    try { m.result().dataset().remove("dset150probe"); } catch (Exception ignore) {}
    m.result().dataset().create("dset150probe", "Solution");
    m.result().dataset("dset150probe").set("solution", sol);
    try { m.result().numerical().remove("eval150probe"); } catch (Exception ignore) {}
    m.result().numerical().create("eval150probe", "EvalGlobal");
    m.result().numerical("eval150probe").set("data", "dset150probe");
    m.result().numerical("eval150probe").set("expr", new String[]{
        "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
        "Fn_total119", "Fn_error119", "dr_indent119"
    });
    double[][] a = m.result().numerical("eval150probe").getReal();
    String[] n = {"phi", "treplay", "Fcontact", "Ffilm", "Ftotal", "err", "indent"};
    for (int j = 0; j < a[0].length; j++) {
      StringBuilder b = new StringBuilder("row=" + j);
      for (int i = 0; i < a.length; i++) {
        b.append(" ").append(n[i]).append("=")
            .append(String.format(Locale.US, "%.9g", a[i][j]));
      }
      System.out.println(b);
    }
    m.save("268_lid8mm_stage150_verified_normal_path_Model.mph");
    ModelUtil.disconnect();
  }
}
