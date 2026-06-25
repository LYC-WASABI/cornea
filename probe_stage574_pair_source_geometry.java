import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_pair_source_geometry {
  private static double value(
      Model model, String tag, String type, String expr, int[] entities) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574src");
    model.result().numerical(tag).selection().set(entities);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static void inspect(String file, String solution) throws Exception {
    Model model = ModelUtil.load("SourceModel", file);
    try { model.result().dataset().remove("dset574src"); }
    catch (Exception ignored) {}
    model.result().dataset().create("dset574src", "Solution");
    model.result().dataset("dset574src").set("solution", solution);
    int[] source =
        model.component("comp1").pair("cp_lid_cornea").source().entities();
    System.out.println("FILE=" + file);
    System.out.println("SOURCE=" + Arrays.toString(source));
    System.out.printf(Locale.US,
        "AREA=%.12g X=[%.12g,%.12g] A=[%.12g,%.12g]%n",
        value(model, "asrc", "IntSurface", "1", source),
        value(model, "xminsrc", "MinSurface", "x", source),
        value(model, "xmaxsrc", "MaxSurface", "x", source),
        value(model, "aminSrc", "MinSurface", "atan2(y,z)", source)
            * 180 / Math.PI,
        value(model, "amaxSrc", "MaxSurface", "atan2(y,z)", source)
            * 180 / Math.PI);
    ModelUtil.remove("SourceModel");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      inspect("573_stage573_cornea_dynamic_regions_checked.mph", "sol93");
      inspect("scan_stage574_rebuilt_pair_indentation_output5_Model.mph",
          "sol95");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
