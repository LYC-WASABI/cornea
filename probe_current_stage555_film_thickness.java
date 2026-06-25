import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_current_stage555_film_thickness {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558a_stage555_JFO_only_study_alpha0p964_setup.mph");
      String dataset = "probe_h555";
      try { model.result().dataset().remove(dataset); } catch (Exception ignored) {}
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).set("solution", "sol81");

      String eval = "probe_h555_global";
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", dataset);
      model.result().numerical(eval).set("expr", new String[] {
        "intop_film(h_geom555)/intop_film(1)",
        "intop_film(h_liquid555)/intop_film(1)"
      });
      model.result().numerical(eval).set("unit", new String[] {"um", "um"});
      double[][] global = model.result().numerical(eval).getReal();

      String min = "probe_h555_min";
      model.result().numerical().create(min, "MinSurface");
      model.result().numerical(min).set("data", dataset);
      model.result().numerical(min).selection().named("sel_film_track");
      model.result().numerical(min).set("expr", "h_geom555");
      model.result().numerical(min).set("unit", "um");
      double[][] minimum = model.result().numerical(min).getReal();

      String max = "probe_h555_max";
      model.result().numerical().create(max, "MaxSurface");
      model.result().numerical(max).set("data", dataset);
      model.result().numerical(max).selection().named("sel_film_track");
      model.result().numerical(max).set("expr", "h_geom555");
      model.result().numerical(max).set("unit", "um");
      double[][] maximum = model.result().numerical(max).getReal();

      System.out.printf(Locale.US, "alpha=%.6f%n", 0.964);
      System.out.printf(Locale.US, "h_geom_avg_um=%.12g%n", global[0][2]);
      System.out.printf(Locale.US, "h_liquid_avg_um=%.12g%n", global[1][2]);
      System.out.printf(Locale.US, "h_geom_min_um=%.12g%n", minimum[0][2]);
      System.out.printf(Locale.US, "h_geom_max_um=%.12g%n", maximum[0][2]);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
