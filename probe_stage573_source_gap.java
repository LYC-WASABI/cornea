import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_source_gap {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double scalar(
      Model model, String tag, String type, String expr,
      String selection, int dimension) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset573_probe");
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
      String gap = pair.gapName(false);
      int[] source = pair.source().entities();

      removeSelection(comp, "sel_lid_film573_probe");
      comp.selection().create("sel_lid_film573_probe", "Explicit");
      comp.selection("sel_lid_film573_probe").geom("geom1", 2);
      comp.selection("sel_lid_film573_probe").set(source);

      removeSelection(comp, "sel_lid_edges573_probe");
      comp.selection().create("sel_lid_edges573_probe", "Adjacent");
      comp.selection("sel_lid_edges573_probe").set("entitydim", "2");
      comp.selection("sel_lid_edges573_probe").set("outputdim", "1");
      comp.selection("sel_lid_edges573_probe")
          .set("input", new String[] {"sel_lid_film573_probe"});
      comp.selection("sel_lid_edges573_probe").set("exterior", "on");
      comp.selection("sel_lid_edges573_probe").set("interior", "off");

      removeDataset(model, "dset573_probe");
      model.result().dataset().create("dset573_probe", "Solution");
      model.result().dataset("dset573_probe").set("solution", "sol93");

      double area = scalar(model, "int573_area", "IntSurface", "1",
          "sel_lid_film573_probe", 2);
      double validArea = scalar(model, "int573_valid", "IntSurface",
          "if(" + gap + "<0.1[mm],1,0)",
          "sel_lid_film573_probe", 2);
      double penetrationArea = scalar(
          model, "int573_pen", "IntSurface",
          "if(" + gap + "<0[m],1,0)",
          "sel_lid_film573_probe", 2);
      double minGap = scalar(model, "min573_gap", "MinSurface",
          gap, "sel_lid_film573_probe", 2);
      double maxGap = scalar(model, "max573_gap", "MaxSurface",
          gap, "sel_lid_film573_probe", 2);
      double avgGapIntegral = scalar(
          model, "int573_gap", "IntSurface",
          "if(" + gap + "<0.1[mm]," + gap + ",0[m])",
          "sel_lid_film573_probe", 2);
      double minTn = scalar(model, "min573_tn", "MinSurface",
          "if(isdefined(solid.Tn),solid.Tn,0[Pa])",
          "sel_lid_film573_probe", 2);
      double maxTn = scalar(model, "max573_tn", "MaxSurface",
          "if(isdefined(solid.Tn),solid.Tn,0[Pa])",
          "sel_lid_film573_probe", 2);
      double intTn = scalar(model, "int573_tn", "IntSurface",
          "if(isdefined(solid.Tn),solid.Tn,0[Pa])",
          "sel_lid_film573_probe", 2);

      double edgeXMin = scalar(model, "min573_ex", "MinLine",
          "x", "sel_lid_edges573_probe", 1);
      double edgeXMax = scalar(model, "max573_ex", "MaxLine",
          "x", "sel_lid_edges573_probe", 1);
      double edgeAMin = scalar(model, "min573_ea", "MinLine",
          "atan2(y,z)", "sel_lid_edges573_probe", 1);
      double edgeAMax = scalar(model, "max573_ea", "MaxLine",
          "atan2(y,z)", "sel_lid_edges573_probe", 1);

      System.out.println("GAP_SOURCE=" + gap);
      System.out.println("SOURCE_BOUNDARIES=" + Arrays.toString(source));
      System.out.println("SOURCE_EDGES=" + Arrays.toString(
          comp.selection("sel_lid_edges573_probe").entities(1)));
      System.out.printf(Locale.US,
          "AREA=%.12g%nVALID_AREA=%.12g%nVALID_FRACTION=%.12g%n"
              + "PENETRATION_AREA=%.12g%nMIN_GAP=%.12g%nMAX_GAP=%.12g%n"
              + "AVG_VALID_GAP=%.12g%nMIN_TN=%.12g%nMAX_TN=%.12g%n"
              + "INT_TN=%.12g%nEDGE_X_MIN=%.12g%nEDGE_X_MAX=%.12g%n"
              + "EDGE_A_MIN_DEG=%.12g%nEDGE_A_MAX_DEG=%.12g%n",
          area, validArea, validArea / area, penetrationArea,
          minGap, maxGap, avgGapIntegral / validArea,
          minTn, maxTn, intTn, edgeXMin, edgeXMax,
          edgeAMin * 180.0 / Math.PI, edgeAMax * 180.0 / Math.PI);

      model.label("Stage 573 source gap probe");
      model.save("573a_stage573_source_gap_probe.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
