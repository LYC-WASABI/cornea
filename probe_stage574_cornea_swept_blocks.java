import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_cornea_swept_blocks {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double value(
      Model model, int boundary, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574_blocks");
    model.result().numerical(tag).selection().set(boundary);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_cornea_dynamic_regions_checked.mph");
      ModelNode comp = model.component("comp1");
      removeDataset(model, "dset574_blocks");
      model.result().dataset().create("dset574_blocks", "Solution");
      model.result().dataset("dset574_blocks").set("solution", "sol93");

      int index = 0;
      for (int boundary :
          comp.selection("sel_film_swept571").entities(2)) {
        String suffix = Integer.toString(++index);
        double area = value(
            model, boundary, "area574b" + suffix, "IntSurface", "1");
        double minX = value(
            model, boundary, "minx574b" + suffix, "MinSurface", "x");
        double maxX = value(
            model, boundary, "maxx574b" + suffix, "MaxSurface", "x");
        double minA = value(
            model, boundary, "mina574b" + suffix,
            "MinSurface", "atan2(y,z)");
        double maxA = value(
            model, boundary, "maxa574b" + suffix,
            "MaxSurface", "atan2(y,z)");
        System.out.printf(Locale.US,
            "BOUNDARY=%d AREA=%.12g X=[%.6g,%.6g]"
                + " ANGLE_DEG=[%.6g,%.6g]%n",
            boundary, area, minX, maxX,
            minA * 180 / Math.PI, maxA * 180 / Math.PI);
      }

      System.out.println("GEOMETRY_FEATURES="
          + Arrays.toString(comp.geom("geom1").feature().tags()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
