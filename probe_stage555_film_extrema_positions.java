import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_film_extrema_positions {
  private static void evaluate(
      Model model, String tag, String type, String expression) {
    model.result().numerical().create(tag, type);
    var feature = model.result().numerical(tag);
    feature.set("data", "probe_extrema_dset");
    feature.selection().named("sel_film_track");
    feature.set("expr", expression);
    feature.set("unit", "um");
    feature.set("includepos", "on");
    double[][] values = feature.getReal();
    System.out.println(tag + " rows=" + values.length);
    for (int row = 0; row < values.length; row++) {
      System.out.println("  row" + row + "="
          + Arrays.toString(values[row]));
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558a_stage555_JFO_only_study_alpha0p964_setup.mph");
      model.result().dataset().create("probe_extrema_dset", "Solution");
      model.result().dataset("probe_extrema_dset").set("solution", "sol81");
      evaluate(model, "probe_hmax_pos", "MaxSurface", "h_geom555");
      evaluate(model, "probe_hmin_pos", "MinSurface", "h_geom555");
      evaluate(model, "probe_dgap_max_pos", "MaxSurface", "dgap_n555");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
