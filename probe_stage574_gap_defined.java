import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574_gap_defined {
  private static double value(
      Model model, String tag, String selection, String expression) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset574_gap");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double extremum(
      Model model, String tag, String type,
      String selection, String expression) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574_gap");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574b_stage574_inset_source_gap_checked.mph");
      String src = "geomgap_src_cp_lid_cornea";
      String dst = "geomgap_dst_cp_lid_cornea";
      for (String selection : new String[] {
          "sel_lid_source_full574", "sel_lid_film574"}) {
        System.out.println("SELECTION=" + selection);
        System.out.println("  AREA=" + value(
            model, "probe_area", selection, "1"));
        System.out.println("  SRC_DEFINED=" + value(
            model, "probe_src", selection, "isdefined(" + src + ")"));
        System.out.println("  SRC_INTEGRAL=" + value(
            model, "probe_src_int", selection, src));
        System.out.println("  SRC_MIN=" + extremum(
            model, "probe_src_min", "MinSurface", selection, src));
        System.out.println("  SRC_MAX=" + extremum(
            model, "probe_src_max", "MaxSurface", selection, src));
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
