import com.comsol.model.*;
import com.comsol.model.util.*;

public class validate_stage99_lid_pressure_plot {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "177_lid8mm_stage99_lid_pressure_time_results_Model.mph");
      try { model.result().numerical().remove("eval_lid_pressure_plot_check"); } catch (Exception ignore) {}
      model.result().numerical().create("eval_lid_pressure_plot_check", "EvalGlobal");
      model.result().numerical("eval_lid_pressure_plot_check").set("data", "dset_shear_feedback76");
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String pContactPos = "max(" + pContact + ",0)";
      model.result().numerical("eval_lid_pressure_plot_check").set("expr", new String[]{
        "intop_film(" + pContactPos + ")",
        "intop_film(" + pContactPos + ")/intop_film(1)",
        "maxop1(" + pContactPos + ")"
      });
      model.result().numerical("eval_lid_pressure_plot_check").set("unit", new String[]{"N","Pa","Pa"});
      double[][] vals = model.result().numerical("eval_lid_pressure_plot_check").getReal();
      System.out.println("eval_lid_pressure_plot_check rows=" + vals.length + " cols=" + vals[0].length);
      for (int i = 0; i < vals.length; i++) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (int j = 0; j < vals[i].length; j++) {
          double v = vals[i][j];
          if (!Double.isNaN(v)) {
            min = Math.min(min, v);
            max = Math.max(max, v);
          }
        }
        System.out.println("expr" + i + " min=" + min + " max=" + max);
      }
      model.save("177_lid8mm_stage99_lid_pressure_time_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
