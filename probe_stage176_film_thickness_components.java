import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage176_film_thickness_components {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "325_lid8mm_stage176_partitioned_friction_results_Model.mph");
      model.result().dataset().create("dset_hprobe", "Solution");
      model.result().dataset("dset_hprobe").set("solution", "sol41");
      model.result().numerical().create("eval_hprobe", "EvalGlobal");
      model.result().numerical("eval_hprobe").set("data", "dset_hprobe");
      model.result().numerical("eval_hprobe").set(
          "expr",
          new String[] {
            "h0_tear",
            "Rq_eq",
            "h_offset170",
            "intop_film(gap_pos169)/intop_film(1)",
            "intop_film(h_actual169)/intop_film(1)",
            "intop_film(h_prev173)/intop_film(1)",
            "intop_film(h_actual172)/intop_film(1)",
            "intop_film(h_relaxed173)/intop_film(1)",
            "intop_film(gap_pos175)/intop_film(1)",
            "intop_film(h_target175)/intop_film(1)",
            "intop_film(h_updated175)/intop_film(1)",
            "alpha_gap173",
            "beta_h175"
          });
      model.result().numerical("eval_hprobe").set(
          "unit",
          new String[] {
            "um", "um", "um", "um", "um", "um", "um", "um", "um", "um",
            "um", "1", "1"
          });
      double[][] values = model.result().numerical("eval_hprobe").getReal();
      String[] names = {
        "h0", "Rq_eq", "offset", "gap169_avg", "h169_avg", "hprev_avg",
        "h172_avg", "hrelaxed_avg", "gap175_avg", "htarget_avg",
        "hupdated_avg", "alpha", "beta"
      };
      for (int i = 0; i < names.length; i++) {
        System.out.printf(Locale.US, "%s=%.12g%n", names[i], values[i][0]);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
