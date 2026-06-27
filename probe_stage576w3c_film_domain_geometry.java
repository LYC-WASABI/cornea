import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576w3c_film_domain_geometry {
  private static final String MODEL =
      "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String SOL = "sol271";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", SOL);
    return tag;
  }

  private static double eval(Model model, String data, String tag, String sel, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(sel);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static void selectionSummary(Model model, ModelNode comp, String data, String sel) {
    try {
      int[] entities = comp.selection(sel).entities(2);
      System.out.println("SEL=" + sel + " ENTITIES2=" + Arrays.toString(entities));
      System.out.printf(Locale.US, "%s_AREA=%.12g%n", sel,
          eval(model, data, sel + "_area", sel, "IntSurface", "1"));
      System.out.printf(Locale.US, "%s_X_MIN=%.12g%n", sel,
          eval(model, data, sel + "_xmin", sel, "MinSurface", "x"));
      System.out.printf(Locale.US, "%s_X_MAX=%.12g%n", sel,
          eval(model, data, sel + "_xmax", sel, "MaxSurface", "x"));
      System.out.printf(Locale.US, "%s_THETA_MIN_DEG=%.12g%n", sel,
          eval(model, data, sel + "_amin", sel, "MinSurface", "atan2(y,z)") * 180.0 / Math.PI);
      System.out.printf(Locale.US, "%s_THETA_MAX_DEG=%.12g%n", sel,
          eval(model, data, sel + "_amax", sel, "MaxSurface", "atan2(y,z)") * 180.0 / Math.PI);
    } catch (Exception error) {
      System.out.println("SEL=" + sel + " ERROR=" + error.getMessage());
    }
  }

  private static void edgeSummary(ModelNode comp, String sel) {
    try {
      System.out.println("EDGE_SEL=" + sel + " ENTITIES1="
          + Arrays.toString(comp.selection(sel).entities(1)));
    } catch (Exception error) {
      System.out.println("EDGE_SEL=" + sel + " ERROR=" + error.getMessage());
    }
  }

  private static void featureSummary(ModelNode comp, String tag) {
    try {
      System.out.println("TFF_FEATURE=" + tag
          + " active=" + comp.physics("tff").feature(tag).isActive()
          + " entities=" + Arrays.toString(comp.physics("tff").feature(tag).selection().entities()));
    } catch (Exception error) {
      System.out.println("TFF_FEATURE=" + tag + " ERROR=" + error.getMessage());
    }
  }

  private static void param(Model model, String name) {
    try {
      System.out.printf(Locale.US, "PARAM_%s=%.12g%n", name, model.param().evaluate(name));
    } catch (Exception error) {
      System.out.println("PARAM_" + name + "_ERROR=" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", MODEL);
      ModelNode comp = model.component("comp1");
      String data = dataset(model, "probeW3cGeom");

      selectionSummary(model, comp, data, "sel_film_track");
      selectionSummary(model, comp, data, "sel_film_swept571");
      selectionSummary(model, comp, data, "sel_local_cornea_patch574");

      edgeSummary(comp, "sel_film_inlet571");
      edgeSummary(comp, "sel_film_outlet571");
      edgeSummary(comp, "sel_film_side_left571");
      edgeSummary(comp, "sel_film_side_right571");

      System.out.println("TFF_SELECTION_ENTITIES="
          + Arrays.toString(comp.physics("tff").selection().entities()));
      featureSummary(comp, "bdr_inlet520");
      featureSummary(comp, "bdr_outlet520");
      featureSummary(comp, "bdr_left520");
      featureSummary(comp, "bdr_right520");

      param(model, "film_track_half_width");
      param(model, "film_swept_half_width571");
      param(model, "film_track_theta_max");
      param(model, "film_swept_theta_max571");
      param(model, "drain_buffer573");
      param(model, "Rcor");

      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
