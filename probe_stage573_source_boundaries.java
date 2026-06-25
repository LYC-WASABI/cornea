import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_source_boundaries {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double value(
      Model model, String tag, String type, String expr, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset573b_probe");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "572_stage572_dynamic_motion_mask_checked.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      String sourceGap = pair.gapName(false);
      String destinationGap = pair.gapName(true);
      removeDataset(model, "dset573b_probe");
      model.result().dataset().create("dset573b_probe", "Solution");
      model.result().dataset("dset573b_probe").set("solution", "sol93");

      System.out.println("PAIR_NAME=" + pair.pairName());
      System.out.println("MAPPING=" + pair.mapping());
      System.out.println("SOURCE_GAP=" + sourceGap);
      System.out.println("DESTINATION_GAP=" + destinationGap);
      System.out.println("IN_CONTACT=" + pair.inContactName());

      int index = 0;
      for (int boundary : pair.source().entities()) {
        String selection = "sel573_src_" + boundary;
        removeSelection(comp, selection);
        comp.selection().create(selection, "Explicit");
        comp.selection(selection).geom("geom1", 2);
        comp.selection(selection).set(new int[] {boundary});
        String prefix = "b" + (++index) + "_";
        double area = value(
            model, prefix + "area", "IntSurface", "1", selection);
        double valid = value(
            model, prefix + "valid", "IntSurface",
            "if(" + sourceGap + "<0.1[mm],1,0)", selection);
        double negative = value(
            model, prefix + "neg", "IntSurface",
            "if(" + sourceGap + "<0[m],1,0)", selection);
        double integral = value(
            model, prefix + "int", "IntSurface",
            "if(" + sourceGap + "<0.1[mm]," + sourceGap + ",0[m])",
            selection);
        double minimum = value(
            model, prefix + "min", "MinSurface", sourceGap, selection);
        double maximum = value(
            model, prefix + "max", "MaxSurface", sourceGap, selection);
        double xavg = value(
            model, prefix + "x", "IntSurface", "x", selection) / area;
        double aavg = value(
            model, prefix + "a", "IntSurface",
            "atan2(y,z)", selection) / area;
        System.out.printf(Locale.US,
            "BOUNDARY=%d AREA=%.12g VALID=%.12g NEG=%.12g"
                + " AVG=%.12g MIN=%.12g MAX=%.12g"
                + " XAVG=%.12g AAVG_DEG=%.12g%n",
            boundary, area, valid / area, negative / area,
            valid > 0 ? integral / valid : Double.NaN,
            minimum, maximum, xavg, aavg * 180.0 / Math.PI);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
