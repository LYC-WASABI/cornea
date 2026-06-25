import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_source_edges {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }
  private static double integral(
      Model model, String tag, String expression, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntLine");
    model.result().numerical(tag).set("data", "dset573edge");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573a_stage573_source_gap_probe.mph");
      ModelNode comp = model.component("comp1");
      removeDataset(model, "dset573edge");
      model.result().dataset().create("dset573edge", "Solution");
      model.result().dataset("dset573edge").set("solution", "sol93");
      int[] edges =
          comp.selection("sel_lid_edges573_probe").entities(1);
      int index = 0;
      for (int edge : edges) {
        String selection = "sel573edge_" + edge;
        removeSelection(comp, selection);
        comp.selection().create(selection, "Explicit");
        comp.selection(selection).geom("geom1", 1);
        comp.selection(selection).set(new int[] {edge});
        String prefix = "e" + (++index) + "_";
        double length = integral(model, prefix + "l", "1", selection);
        double x = integral(model, prefix + "x", "x", selection) / length;
        double y = integral(model, prefix + "y", "y", selection) / length;
        double z = integral(model, prefix + "z", "z", selection) / length;
        double a = integral(
            model, prefix + "a", "atan2(y,z)", selection) / length;
        System.out.printf(Locale.US,
            "EDGE=%d LENGTH=%.12g XAVG=%.12g YAVG=%.12g"
                + " ZAVG=%.12g AAVG_DEG=%.12g%n",
            edge, length, x, y, z, a * 180.0 / Math.PI);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
