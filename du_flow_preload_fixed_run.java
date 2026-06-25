import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_preload_fixed_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results-change.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\02_dynamic_preload_minus35_fixed_results.mph";

  private static void clean(Model model) {
    for (String tag : model.result().numerical().tags()) {
      try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().table().tags()) {
      try { model.result().table().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().tags()) {
      try { model.result().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.result().dataset().tags()) {
      try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.study().tags()) {
      try { model.study().remove(tag); } catch (Exception ignored) {}
    }
    for (String tag : model.sol().tags()) {
      try { model.sol().remove(tag); } catch (Exception ignored) {}
    }
  }

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void fixMovingContactSource(Model model) {
    try { model.component("comp1").selection().remove("sel_cornea_all_boundaries_dyn"); }
    catch (Exception ignored) {}
    model.component("comp1").selection().create("sel_cornea_all_boundaries_dyn", "Explicit");
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn")
        .label("All cornea boundaries excluded from moving lid contact source");
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn").geom("geom1", 2);
    model.component("comp1").selection("sel_cornea_all_boundaries_dyn")
        .set(new int[]{16, 17, 18, 19, 20, 21, 22, 23, 24});
    model.component("comp1").selection("sel_lid_contact_source_robust").set("subtract",
        new String[]{"sel_cornea_all_boundaries_dyn"});
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_contact_source_robust");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("02_dynamic_preload_minus35_fixed_results.mph");
    clean(model);
    model.param().set("theta_lid", "0[deg]", "Stationary continuation starts at apex and ends at -35 deg");
    model.component("comp1").geom("geom1").feature("rot_lid").set("rot", "-theta_lid");
    model.component("comp1").geom("geom1").run();
    fixMovingContactSource(model);
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std_preload");
    model.study("std_preload").label("Stationary continuation preload from apex to -35 deg");
    model.study("std_preload").create("param", "Parametric");
    model.study("std_preload").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std_preload").feature("param").set("plistarr",
        new String[]{"0[deg] -2.5[deg] -5[deg] -7.5[deg] -10[deg] -12.5[deg] -15[deg] "
            + "-17.5[deg] -20[deg] -22.5[deg] -25[deg] -27.5[deg] -30[deg] -32.5[deg] -35[deg]"});
    model.study("std_preload").feature("param").set("punit", new String[]{"deg"});
    model.study("std_preload").create("stat", "Stationary");
    model.study("std_preload").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_preload").run();

    model.result().dataset().create("dset_preload", "Solution");
    model.result().dataset("dset_preload").label("Stationary preload solution at -35 deg");
    model.result().dataset("dset_preload").set("solution", lastSolution(model));
    model.result().table().create("tbl_preload_force", "Table");
    model.result().numerical().create("int_preload_force", "IntSurface");
    model.result().numerical("int_preload_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_preload_force").set("data", "dset_preload");
    model.result().numerical("int_preload_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_preload_force").set("unit", new String[]{"N"});
    model.result().numerical("int_preload_force").set("table", "tbl_preload_force");
    model.result().numerical("int_preload_force").setResult();
    String[][] rows = model.result().table("tbl_preload_force").getTableData(false);
    System.out.println("PRELOAD force=" + rows[rows.length - 1][rows[0].length - 1] + " N");
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
