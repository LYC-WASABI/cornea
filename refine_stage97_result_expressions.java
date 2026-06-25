import com.comsol.model.*;
import com.comsol.model.util.*;

public class refine_stage97_result_expressions {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "175_lid8mm_stage97_rq0p5um_break0p5to1_results_Model.mph");

      model.result("pg_normal_pressure_time").feature("glob1").set(
        "expr",
        new String[]{"W_film", "W_film/intop_film(1)"}
      );
      model.result("pg_normal_pressure_time").feature("glob1").set(
        "unit",
        new String[]{"N", "Pa"}
      );

      model.result("pg_hfilm_time").feature("glob1").set(
        "expr",
        new String[]{"intop_film(h_film_input)/intop_film(1)", "intop_film(f_break95)/intop_film(1)"}
      );
      model.result("pg_hfilm_time").feature("glob1").set(
        "unit",
        new String[]{"um", "1"}
      );
      model.result().numerical("eval_key_hfilm").set(
        "expr",
        new String[]{"intop_film(h_film_input)/intop_film(1)", "intop_film(f_break95)/intop_film(1)"}
      );
      model.result().numerical("eval_key_hfilm").set("unit", new String[]{"um", "1"});

      try { model.result().numerical().remove("max_pfilm"); } catch (Exception ignore) {}
      model.result().numerical().create("max_pfilm", "MaxSurface");
      model.result().numerical("max_pfilm").label("Maximum film pressure");
      model.result().numerical("max_pfilm").set("data", "dset_rq0p5_film92");
      model.result().numerical("max_pfilm").selection().named("sel_cornea_anterior_surface");
      model.result().numerical("max_pfilm").set("expr", "max(pfilm,0)");
      model.result().numerical("max_pfilm").set("unit", "Pa");

      model.save("175_lid8mm_stage97_rq0p5um_break0p5to1_results_Model.mph");
      System.out.println("Refined and saved local Stage 97 model.");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
