import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_dynamic_add_max_results {
  private static final String PATH =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_quasistatic_dynamic_sliding_minus35_to_plus35_results.mph";

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); } catch (Exception ignored) {}
  }

  private static void addMax(Model model, String tag, String table, String label,
      String selection, String expr, String unit) {
    removeNumerical(model, tag);
    removeTable(model, table);
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", "dset_dynamic_slide");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
    double[][] values = model.result().numerical(tag).getReal();
    double max = Double.NEGATIVE_INFINITY;
    for (double value : values[0]) max = Math.max(max, value);
    System.out.printf("%s overall_max=%.12g[%s]%n", tag, max, unit);
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", PATH);
    addMax(model, "max_dyn_cornea_disp", "tbl_max_dyn_cornea_disp",
        "Dynamic maximum cornea anterior displacement",
        "sel_cornea_anterior_surface", "solid.disp", "mm");
    addMax(model, "max_dyn_lid_disp", "tbl_max_dyn_lid_disp",
        "Dynamic maximum lid contact displacement",
        "sel_lid_contact_source_robust", "solid.disp", "mm");
    addMax(model, "max_dyn_cornea_mises", "tbl_max_dyn_cornea_mises",
        "Dynamic maximum cornea anterior von Mises stress",
        "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addMax(model, "max_dyn_lid_mises", "tbl_max_dyn_lid_mises",
        "Dynamic maximum lid contact von Mises stress",
        "sel_lid_contact_source_robust", "solid.mises", "Pa");
    model.save(PATH);
    System.out.println("Saved: " + PATH);
  }
}
