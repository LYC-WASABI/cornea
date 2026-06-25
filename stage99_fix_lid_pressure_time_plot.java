import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage99_fix_lid_pressure_time_plot {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "177_lid8mm_stage99_lid_pressure_time_results_Model.mph");
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String pContactPos = "max(" + pContact + ",0)";

      model.result("pg_lid_pressure_time").feature("glob1").set("expr", new String[]{
        "intop_film(" + pContactPos + ")",
        "intop_film(" + pContactPos + ")/intop_film(1)"
      });
      model.result("pg_lid_pressure_time").feature("glob1").set("unit", new String[]{"N", "Pa"});

      try { model.result().numerical().remove("eval_lid_pressure_plot_check"); } catch (Exception ignore) {}
      model.result().numerical().create("eval_lid_pressure_plot_check", "EvalGlobal");
      model.result().numerical("eval_lid_pressure_plot_check").label("Lid pressure time plot expression check");
      model.result().numerical("eval_lid_pressure_plot_check").set("data", "dset_shear_feedback76");
      model.result().numerical("eval_lid_pressure_plot_check").set("expr", new String[]{
        "intop_film(" + pContactPos + ")",
        "intop_film(" + pContactPos + ")/intop_film(1)"
      });
      model.result().numerical("eval_lid_pressure_plot_check").set("unit", new String[]{"N", "Pa"});

      double[][] vals = model.result().numerical("eval_lid_pressure_plot_check").getReal();
      System.out.println("plot check rows=" + vals.length + " cols=" + vals[0].length);
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
      System.out.println("Saved fixed local Stage 99 model.");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
