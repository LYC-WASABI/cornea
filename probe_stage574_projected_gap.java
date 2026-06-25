import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574_projected_gap {
  private static double value(
      Model model, String tag, String type, String expression) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574_projected");
    model.result().numerical(tag).selection().named("sel_lid_film574");
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574a_stage574_inset_source_film_setup.mph");
      try { model.result().dataset().remove("dset574_projected"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574_projected", "Solution");
      model.result().dataset("dset574_projected")
          .set("solution", "sol94");
      String gap = model.component("comp1").pair("cp_lid_cornea")
          .gapName(false);
      double area = value(model, "prj_area", "IntSurface", "1");
      double defined = value(
          model, "prj_defined", "IntSurface",
          "isdefined(" + gap + ")");
      double minimum = value(
          model, "prj_min", "MinSurface", gap);
      double maximum = value(
          model, "prj_max", "MaxSurface", gap);
      System.out.println("DEFINED_FRACTION=" + defined / area);
      System.out.println("MIN_GAP=" + minimum);
      System.out.println("MAX_GAP=" + maximum);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
