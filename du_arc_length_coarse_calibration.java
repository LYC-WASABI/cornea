import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_arc_length_coarse_calibration {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph";
  private static final String SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\28_rect_lid_arc_length_coarse_calibration_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rect_arc_length_coarse_calibration_results.mph";

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

  private static void removeRoundedFeaturesIfPresent(Model model) {
    String[] rounded = {
      "uni_round_cutter", "uni_round_cutter2",
      "blk_round_core_x", "blk_round_core_y", "blk_round_core_x2", "blk_round_core_y2",
      "cyl_round_pxp", "cyl_round_pxn", "cyl_round_nxp", "cyl_round_nxn",
      "cyl_round_pxp2", "cyl_round_pxn2", "cyl_round_nxp2", "cyl_round_nxn2"
    };
    for (String tag : rounded) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignored) {}
    }
  }

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void addIntegral(Model model, String dataset, String table) {
    model.result().table().create(table, "Table");
    model.result().table(table).label("Arc length calibration: anterior cornea intop(solid.Tn)");
    model.result().numerical().create("int_arc_contact_force", "IntSurface");
    model.result().numerical("int_arc_contact_force").label("intop(solid.Tn) on anterior cornea");
    model.result().numerical("int_arc_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_arc_contact_force").set("data", dataset);
    model.result().numerical("int_arc_contact_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_arc_contact_force").set("unit", new String[]{"N"});
    model.result().numerical("int_arc_contact_force").set("descr",
        new String[]{"Surface integral of contact pressure on anterior cornea"});
    model.result().numerical("int_arc_contact_force").set("table", table);
    model.result().numerical("int_arc_contact_force").setResult();
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_rect_arc_length_coarse_calibration.mph");
    clean(model);
    removeRoundedFeaturesIfPresent(model);

    model.param().set("theta_lid", "0[deg]", "Apex calibration position");
    model.param().set("s_lid", "1[mm]", "Lid wiper contact-surface arc length");
    model.param().set("lid_arc_length", "s_lid", "Alias for lid wiper contact-surface arc length");
    model.param().set("L_lid_chord", "2*R_cor*sin(s_lid/(2*R_cor))",
        "Rectangular cutter chord corresponding to spherical contact arc length");
    model.param().set("delta_indent", "0.10[mm]", "Geometric indentation calibration parameter");
    model.component("comp1").geom("geom1").feature("blk1").label("Straight rectangular cutter: variable arc length x 1 mm width");
    model.component("comp1").geom("geom1").feature("int_lid").label("Straight-edged spherical rectangular lid wiper");
    model.component("comp1").geom("geom1").feature("blk1").set("size",
        new String[]{"L_lid_chord", "W_lid_chord", "t_lid + 1[mm]"});
    model.component("comp1").geom("geom1").run();

    System.out.println("contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));

    model.component("comp1").mesh("mesh1").run();
    model.study().create("std_arc_coarse_cal");
    model.study("std_arc_coarse_cal").label("Straight rectangular lid: arc length and indentation coarse calibration");
    model.study("std_arc_coarse_cal").create("param", "Parametric");
    model.study("std_arc_coarse_cal").feature("param").set("pname",
        new String[]{"s_lid", "delta_indent"});
    model.study("std_arc_coarse_cal").feature("param").set("plistarr",
        new String[]{
          "1[mm] 1[mm] 1[mm] 1[mm] 1[mm] 1[mm] 1[mm] 1[mm] 1[mm] 1[mm] "
              + "1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] 1.5[mm] "
              + "2[mm] 2[mm] 2[mm] 2[mm] 2[mm] 2[mm] 2[mm] 2[mm] 2[mm] 2[mm]",
          "0.03[mm] 0.05[mm] 0.07[mm] 0.09[mm] 0.11[mm] 0.13[mm] 0.15[mm] 0.18[mm] 0.21[mm] 0.24[mm] "
              + "0.03[mm] 0.05[mm] 0.07[mm] 0.09[mm] 0.11[mm] 0.13[mm] 0.15[mm] 0.18[mm] 0.21[mm] 0.24[mm] "
              + "0.03[mm] 0.05[mm] 0.07[mm] 0.09[mm] 0.11[mm] 0.13[mm] 0.15[mm] 0.18[mm] 0.21[mm] 0.24[mm]"
        });
    model.study("std_arc_coarse_cal").feature("param").set("punit", new String[]{"mm", "mm"});
    model.study("std_arc_coarse_cal").create("stat", "Stationary");
    model.study("std_arc_coarse_cal").feature("stat").set("geometricNonlinearity", "on");

    model.save(SETUP);
    model.study("std_arc_coarse_cal").run();

    model.result().dataset().create("dset_arc_coarse", "Solution");
    model.result().dataset("dset_arc_coarse").label("Straight rectangular lid arc length coarse calibration solution");
    model.result().dataset("dset_arc_coarse").set("solution", lastSolution(model));
    addIntegral(model, "dset_arc_coarse", "tbl_arc_coarse_force");
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
