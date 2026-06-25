import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_loadshare_dataset_combo {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\177_lid8mm_stage99_lid_pressure_time_results.mph");
      String p = "max(if(isdefined(solid.Tn),solid.Tn,0),0)";
      String[] datasets = new String[]{"dset_rq0p5_film92", "dset_shear_feedback76"};
      for (String ds : datasets) {
        String tag = "eval_combo_" + ds.replaceAll("[^A-Za-z0-9]", "_");
        try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
        model.result().numerical().create(tag, "EvalGlobal");
        model.result().numerical(tag).set("data", ds);
        model.result().numerical(tag).set("expr", new String[]{"W_film", "intop_film(" + p + ")", "W_film+intop_film(" + p + ")"});
        model.result().numerical(tag).set("unit", new String[]{"N","N","N"});
        try {
          double[][] vals = model.result().numerical(tag).getReal();
          System.out.println("DATASET " + ds + " ok rows=" + vals.length + " cols=" + vals[0].length);
          for (int i = 0; i < vals.length; i++) {
            double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < vals[i].length; j++) {
              double v = vals[i][j];
              if (!Double.isNaN(v)) { min = Math.min(min, v); max = Math.max(max, v); }
            }
            System.out.println("  expr" + i + " min=" + min + " max=" + max);
          }
        } catch (Exception e) {
          System.out.println("DATASET " + ds + " error " + e.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
