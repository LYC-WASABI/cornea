import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_deform_lid_add_postresults {
  private static final String PATH = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph";

  private static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); } catch (Exception ignored) {}
  }

  private static void addPlot(Model model, String tag, String label, String selection, String expr, String unit) {
    removePlot(model, tag);
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", "dset_deform_lid_indent");
    model.result(tag).selection().named(selection);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
    model.result(tag).feature("surf1").set("unit", unit);
  }

  private static void addMax(Model model, String tag, String table, String label, String selection, String expr, String unit) {
    removeNumerical(model, tag);
    removeTable(model, table);
    model.result().table().create(table, "Table");
    model.result().table(table).label(label);
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", "dset_deform_lid_indent");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).set("table", table);
    model.result().numerical(tag).setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", PATH);
    model.param().set("delta_indent", "0.073[mm]", "Calibrated geometric indentation: intop(solid.Tn) ~= 0.03 N");

    addPlot(model, "pg_cornea_disp", "Cornea anterior surface displacement", "sel_cornea_anterior_surface", "solid.disp", "mm");
    addPlot(model, "pg_lid_contact_disp", "Lid contact surface displacement", "sel_lid_contact_source_robust", "solid.disp", "mm");
    addPlot(model, "pg_cornea_mises", "Cornea anterior surface von Mises stress", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addPlot(model, "pg_lid_contact_mises", "Lid contact surface von Mises stress", "sel_lid_contact_source_robust", "solid.mises", "Pa");

    addMax(model, "max_cornea_disp", "tbl_max_cornea_disp", "Maximum cornea anterior displacement", "sel_cornea_anterior_surface", "solid.disp", "mm");
    addMax(model, "max_lid_contact_disp", "tbl_max_lid_contact_disp", "Maximum lid contact surface displacement", "sel_lid_contact_source_robust", "solid.disp", "mm");
    addMax(model, "max_cornea_mises", "tbl_max_cornea_mises", "Maximum cornea anterior von Mises stress", "sel_cornea_anterior_surface", "solid.mises", "Pa");
    addMax(model, "max_lid_contact_mises", "tbl_max_lid_contact_mises", "Maximum lid contact surface von Mises stress", "sel_lid_contact_source_robust", "solid.mises", "Pa");

    model.save(PATH);
    System.out.println("Saved postprocessed deformable lid model: " + PATH);
  }
}
