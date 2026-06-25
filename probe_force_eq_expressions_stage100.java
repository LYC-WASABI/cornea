import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class probe_force_eq_expressions_stage100 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      System.out.println("DATASETS=" + Arrays.toString(model.result().dataset().tags()));
      for (String ds : model.result().dataset().tags()) {
        try {
          System.out.println(ds + " label=" + model.result().dataset(ds).label()
              + " sol=" + model.result().dataset(ds).getString("solution"));
        } catch (Exception ignore) {}
      }
      String[] exprs = new String[]{
        "intop_contact(max(if(isdefined(solid.Tn),solid.Tn,0),0))",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))",
        "W_film_replay",
        "W_film_replay53",
        "dr_force_mixed54",
        "F_total_target",
        "(intop_contact(max(if(isdefined(solid.Tn),solid.Tn,0),0))+W_film_replay53-F_total_target)/F_total_target"
      };
      String tag = "eval_force_eq_probe";
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, "EvalGlobal");
      model.result().numerical(tag).set("data", "dset_shear_feedback76");
      model.result().numerical(tag).set("expr", exprs);
      model.result().numerical(tag).set("unit", new String[]{"N","N","N","N","mm","N","1"});
      double[][] vals = model.result().numerical(tag).getReal();
      for (int i = 0; i < exprs.length; i++) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        int imin = -1, imax = -1, n = 0;
        for (int j = 0; j < vals[i].length; j++) {
          double v = vals[i][j];
          if (!Double.isNaN(v)) {
            n++;
            if (v < min) { min = v; imin = j; }
            if (v > max) { max = v; imax = j; }
          }
        }
        System.out.println(exprs[i] + " n=" + n + " min=" + min + " @" + imin + " max=" + max + " @" + imax);
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
