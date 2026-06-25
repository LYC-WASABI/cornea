import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574g_destination_contact_map {
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static double value(
      Model model, String tag, String type, String expr, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574g_probe");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static void explicit(ModelNode comp, String tag, int boundary) {
    removeSelection(comp, tag);
    comp.selection().create(tag, "Explicit");
    comp.selection(tag).geom("geom1", 2);
    comp.selection(tag).set(new int[] {boundary});
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574g_stage574_local_contact_gap_results.mph");
      ModelNode comp = model.component("comp1");
      model.result().dataset().create("dset574g_probe", "Solution");
      model.result().dataset("dset574g_probe").set("solution", "sol109");
      String gap = "geomgap_dst_cp_lid_cornea";
      System.out.println("LOCAL_PATCH="
          + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      System.out.println("PAIR_DESTINATION="
          + Arrays.toString(comp.pair("cp_lid_cornea").destination().entities()));
      for (int boundary : comp.pair("cp_lid_cornea").destination().entities()) {
        String sel = "tmp574g_b" + boundary;
        explicit(comp, sel, boundary);
        double area = value(model, "a574g_" + boundary, "IntSurface", "1", sel);
        double finite = value(model, "f574g_" + boundary, "IntSurface",
            "if(isdefined(" + gap + "),if(abs(" + gap + ")<0.1[mm],1,0),0)", sel);
        double minGap = value(model, "ming574g_" + boundary, "MinSurface",
            "if(isdefined(" + gap + "),if(abs(" + gap + ")<0.1[mm]," + gap + ",1[m]),1[m])", sel);
        double maxTn = value(model, "tn574g_" + boundary, "MaxSurface",
            "solid.Tn", sel);
        double xavg = value(model, "x574g_" + boundary, "IntSurface", "x", sel) / area;
        double aavg = value(model, "ang574g_" + boundary, "IntSurface",
            "atan2(y,z)", sel) / area * 180.0 / Math.PI;
        System.out.printf(Locale.US,
            "BOUNDARY=%d AREA=%.12g FINITE=%.12g COVER=%.12g MINGAP=%.12g MAXTN=%.12g XAVG=%.12g AAVG_DEG=%.12g%n",
            boundary, area, finite, finite / area, minGap, maxTn, xavg, aavg);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
