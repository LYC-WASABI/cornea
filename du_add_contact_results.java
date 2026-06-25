import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_add_contact_results {
  private static final String FILE = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_center_directed_lid_load_scan_results.mph";

  private static boolean hasPlot(Model model, String tag) {
    for (String t : model.result().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasTable(Model model, String tag) {
    for (String t : model.result().table().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static boolean hasNumerical(Model model, String tag) {
    for (String t : model.result().numerical().tags()) if (t.equals(tag)) return true;
    return false;
  }

  private static void removePlot(Model model, String tag) {
    if (hasPlot(model, tag)) model.result().remove(tag);
  }

  private static void removeTable(Model model, String tag) {
    if (hasTable(model, tag)) model.result().table().remove(tag);
  }

  private static void removeNumerical(Model model, String tag) {
    if (hasNumerical(model, tag)) model.result().numerical().remove(tag);
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", FILE);
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");

    removePlot(model, "pg_contact_pressure");
    removePlot(model, "pg_gap_distance");
    removeNumerical(model, "int_contact_pressure_cornea");
    removeTable(model, "tbl_contact_pressure_int");

    model.result().create("pg_contact_pressure", "PlotGroup3D");
    model.result("pg_contact_pressure").label("Contact pressure: lid wiper to anterior cornea");
    model.result("pg_contact_pressure").set("data", "dset2");
    model.result("pg_contact_pressure").feature().create("surf1", "Surface");
    model.result("pg_contact_pressure").feature("surf1").set("expr", "solid.Tn");
    model.result("pg_contact_pressure").feature("surf1").set("unit", "Pa");
    model.result("pg_contact_pressure").feature("surf1").set("descr", "Contact pressure");

    model.result().create("pg_gap_distance", "PlotGroup3D");
    model.result("pg_gap_distance").label("Gap distance: lid wiper to anterior cornea");
    model.result("pg_gap_distance").set("data", "dset2");
    model.result("pg_gap_distance").feature().create("surf1", "Surface");
    model.result("pg_gap_distance").feature("surf1").set("expr", "solid.gap");
    model.result("pg_gap_distance").feature("surf1").set("unit", "m");
    model.result("pg_gap_distance").feature("surf1").set("descr", "Gap distance");

    model.result().table().create("tbl_contact_pressure_int", "Table");
    model.result().table("tbl_contact_pressure_int").label("Anterior cornea contact pressure surface integration");

    model.result().numerical().create("int_contact_pressure_cornea", "IntSurface");
    model.result().numerical("int_contact_pressure_cornea").label("Surface Integration: anterior cornea contact pressure");
    model.result().numerical("int_contact_pressure_cornea").set("data", "dset2");
    model.result().numerical("int_contact_pressure_cornea").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_contact_pressure_cornea").set("expr", "solid.Tn");
    model.result().numerical("int_contact_pressure_cornea").set("unit", "N");
    model.result().numerical("int_contact_pressure_cornea").set("descr", "Integral of contact pressure on anterior cornea");
    try { model.result().numerical("int_contact_pressure_cornea").set("outersolnum", "all"); } catch (Exception ignore) {}
    model.result().numerical("int_contact_pressure_cornea").set("table", "tbl_contact_pressure_int");
    model.result().numerical("int_contact_pressure_cornea").setResult();

    double[][] contactIntegral = model.result().numerical("int_contact_pressure_cornea").getReal();
    System.out.println("Anterior cornea integral of solid.Tn = " + java.util.Arrays.deepToString(contactIntegral));

    model.save(FILE);
  }
}
