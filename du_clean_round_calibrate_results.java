import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_clean_round_calibrate_results {
  private static final String IN = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results-2.mph";
  private static final String SETUP = "D:\\COMSOL_Outputs\\models\\du\\26_robust_contact_rounded_lid_displacement_calibration_setup.mph";
  private static final String OUT = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_robust_contact_rounded_lid_disp_calibrated_0p03_results.mph";

  private static void clean(Model model) {
    for (String tag : model.study().tags()) { try { model.study().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.sol().tags()) { try { model.sol().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().numerical().tags()) { try { model.result().numerical().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().table().tags()) { try { model.result().table().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().tags()) { try { model.result().remove(tag); } catch (Exception ignored) {} }
    for (String tag : model.result().dataset().tags()) { try { model.result().dataset().remove(tag); } catch (Exception ignored) {} }
  }

  private static void ensureRoundedWindow(Model model) {
    model.param().set("lid_edge_round", "0.08[mm]", "Rounded/chamfer-like edge radius of lid contact footprint");
    model.param().set("round_core_L", "max(L_lid_chord-2*lid_edge_round, 0.1[mm])");
    model.param().set("round_core_W", "max(W_lid_chord-2*lid_edge_round, 0.1[mm])");
    model.param().set("round_cut_H", "t_lid+0.6[mm]");
    model.param().set("round_cut_Z", "R_lid_in+t_lid/2");

    String[] old = {
      "blk_round_core_x2", "blk_round_core_y2",
      "cyl_round_pxp2", "cyl_round_pxn2", "cyl_round_nxp2", "cyl_round_nxn2",
      "uni_round_cutter2"
    };
    for (String tag : old) {
      try { model.component("comp1").geom("geom1").feature().remove(tag); } catch (Exception ignored) {}
    }

    model.component("comp1").geom("geom1").create("blk_round_core_x2", "Block");
    model.component("comp1").geom("geom1").feature("blk_round_core_x2").label("Rounded lid cutter long core");
    model.component("comp1").geom("geom1").feature("blk_round_core_x2").set("size", new String[]{"round_core_L", "W_lid_chord", "round_cut_H"});
    model.component("comp1").geom("geom1").feature("blk_round_core_x2").set("pos", new String[]{"-round_core_L/2", "-W_lid_chord/2", "round_cut_Z-round_cut_H/2"});

    model.component("comp1").geom("geom1").create("blk_round_core_y2", "Block");
    model.component("comp1").geom("geom1").feature("blk_round_core_y2").label("Rounded lid cutter wide core");
    model.component("comp1").geom("geom1").feature("blk_round_core_y2").set("size", new String[]{"L_lid_chord", "round_core_W", "round_cut_H"});
    model.component("comp1").geom("geom1").feature("blk_round_core_y2").set("pos", new String[]{"-L_lid_chord/2", "-round_core_W/2", "round_cut_Z-round_cut_H/2"});

    String[][] cyls = {
      {"cyl_round_pxp2", "L_lid_chord/2-lid_edge_round", "W_lid_chord/2-lid_edge_round"},
      {"cyl_round_pxn2", "L_lid_chord/2-lid_edge_round", "-W_lid_chord/2+lid_edge_round"},
      {"cyl_round_nxp2", "-L_lid_chord/2+lid_edge_round", "W_lid_chord/2-lid_edge_round"},
      {"cyl_round_nxn2", "-L_lid_chord/2+lid_edge_round", "-W_lid_chord/2+lid_edge_round"}
    };
    int cornerIndex = 1;
    for (String[] c : cyls) {
      model.component("comp1").geom("geom1").create(c[0], "Cylinder");
      model.component("comp1").geom("geom1").feature(c[0]).label("Rounded lid cutter corner " + cornerIndex);
      model.component("comp1").geom("geom1").feature(c[0]).set("r", "lid_edge_round");
      model.component("comp1").geom("geom1").feature(c[0]).set("h", "round_cut_H");
      model.component("comp1").geom("geom1").feature(c[0]).set("axis", new String[]{"0", "0", "1"});
      model.component("comp1").geom("geom1").feature(c[0]).set("pos", new String[]{c[1], c[2], "round_cut_Z-round_cut_H/2"});
      cornerIndex++;
    }

    model.component("comp1").geom("geom1").create("uni_round_cutter2", "Union");
    model.component("comp1").geom("geom1").feature("uni_round_cutter2").label("Rounded rectangular lid cutter");
    model.component("comp1").geom("geom1").feature("uni_round_cutter2").selection("input").set(new String[]{
      "blk_round_core_x2", "blk_round_core_y2",
      "cyl_round_pxp2", "cyl_round_pxn2", "cyl_round_nxp2", "cyl_round_nxn2"
    });
    model.component("comp1").geom("geom1").feature("uni_round_cutter2").set("intbnd", false);
    String[] moveOrder = {
      "blk_round_core_x2", "blk_round_core_y2",
      "cyl_round_pxp2", "cyl_round_pxn2", "cyl_round_nxp2", "cyl_round_nxn2",
      "uni_round_cutter2"
    };
    int intLidIndex = 0;
    String[] tags = model.component("comp1").geom("geom1").feature().tags();
    for (int i = 0; i < tags.length; i++) {
      if ("int_lid".equals(tags[i])) {
        intLidIndex = i;
        break;
      }
    }
    for (String tag : moveOrder) {
      try { model.component("comp1").geom("geom1").feature().move(tag, intLidIndex); } catch (Exception ex) {
        System.out.println("move warning for " + tag + ": " + ex.getMessage());
      }
      intLidIndex++;
    }
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"uni_round_cutter2", "dif_lid_shell"});
  }

  private static void ensureRobustSelections(Model model) {
    String[] old = {
      "sel_lid_contact_near_inner_robust2", "sel_lid_contact_candidates_robust2",
      "sel_lid_contact_source_robust2", "sel_lid_hold_robust2"
    };
    for (String s : old) { try { model.component("comp1").selection().remove(s); } catch (Exception ignored) {} }

    model.component("comp1").selection().create("sel_lid_contact_near_inner_robust2", "Ball");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("posx", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("posy", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("posz", "0");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("r", "R_lid_in+0.05[mm]");
    model.component("comp1").selection("sel_lid_contact_near_inner_robust2").set("condition", "intersects");

    model.component("comp1").selection().create("sel_lid_contact_candidates_robust2", "Intersection");
    model.component("comp1").selection("sel_lid_contact_candidates_robust2").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_candidates_robust2").set("input", new String[]{"sel_lid_contact_near_inner_robust2", "sel_lid_box_rot"});

    model.component("comp1").selection().create("sel_lid_contact_source_robust2", "Difference");
    model.component("comp1").selection("sel_lid_contact_source_robust2").label("Robust rounded lid contact source");
    model.component("comp1").selection("sel_lid_contact_source_robust2").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_contact_source_robust2").set("add", new String[]{"sel_lid_contact_candidates_robust2"});
    model.component("comp1").selection("sel_lid_contact_source_robust2").set("subtract", new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").selection().create("sel_lid_hold_robust2", "Difference");
    model.component("comp1").selection("sel_lid_hold_robust2").label("Robust rounded lid held boundaries");
    model.component("comp1").selection("sel_lid_hold_robust2").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_hold_robust2").set("add", new String[]{"sel_lid_box_rot"});
    model.component("comp1").selection("sel_lid_hold_robust2").set("subtract", new String[]{"sel_cornea_anterior_surface"});

    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_contact_source_robust2");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});

    try { model.component("comp1").physics("solid").feature().remove("fix_lid_hold"); } catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("fix_lid_hold", "Fixed", 2);
    model.component("comp1").physics("solid").feature("fix_lid_hold").label("Displacement-control hold of rounded lid body");
    model.component("comp1").physics("solid").feature("fix_lid_hold").selection().named("sel_lid_hold_robust2");
  }

  private static void addStudyAndResults(Model model) {
    model.study().create("std_round_disp_cal");
    model.study("std_round_disp_cal").label("Rounded lid displacement-control calibration to 0.03 N");
    model.study("std_round_disp_cal").create("param", "Parametric");
    model.study("std_round_disp_cal").feature("param").set("pname", new String[]{"delta_indent"});
    model.study("std_round_disp_cal").feature("param").set("plistarr", new String[]{"0.076[mm] 0.077[mm] 0.078[mm] 0.079[mm]"});
    model.study("std_round_disp_cal").feature("param").set("punit", new String[]{"mm"});
    model.study("std_round_disp_cal").create("stat", "Stationary");
    model.study("std_round_disp_cal").feature("stat").set("geometricNonlinearity", "on");

    model.result().dataset().create("dset_round_scan", "Solution");
    model.result().dataset("dset_round_scan").label("Rounded lid displacement calibration solution");

    model.result().table().create("tbl_round_contact_force", "Table");
    model.result().table("tbl_round_contact_force").label("Rounded lid calibration: anterior cornea contact force");
    model.result().numerical().create("int_round_contact_force", "IntSurface");
    model.result().numerical("int_round_contact_force").selection().named("sel_cornea_anterior_surface");
    model.result().numerical("int_round_contact_force").set("data", "dset_round_scan");
    model.result().numerical("int_round_contact_force").set("expr", new String[]{"solid.Tn"});
    model.result().numerical("int_round_contact_force").set("unit", new String[]{"N"});
    model.result().numerical("int_round_contact_force").set("table", "tbl_round_contact_force");

    model.result().create("pg_cor_disp", "PlotGroup3D");
    model.result("pg_cor_disp").label("Cornea anterior displacement");
    model.result("pg_cor_disp").feature().create("surf1", "Surface");
    try { model.result("pg_cor_disp").feature("surf1").selection().named("sel_cornea_anterior_surface"); } catch (Exception ex) { System.out.println("plot selection warning cor disp: " + ex.getMessage()); }
    model.result("pg_cor_disp").feature("surf1").set("expr", "solid.disp");
    model.result("pg_cor_disp").feature("surf1").set("unit", "mm");

    model.result().create("pg_lid_disp", "PlotGroup3D");
    model.result("pg_lid_disp").label("Lid contact surface displacement");
    model.result("pg_lid_disp").feature().create("surf1", "Surface");
    try { model.result("pg_lid_disp").feature("surf1").selection().named("sel_lid_contact_source_robust2"); } catch (Exception ex) { System.out.println("plot selection warning lid disp: " + ex.getMessage()); }
    model.result("pg_lid_disp").feature("surf1").set("expr", "solid.disp");
    model.result("pg_lid_disp").feature("surf1").set("unit", "mm");

    model.result().create("pg_cor_mises", "PlotGroup3D");
    model.result("pg_cor_mises").label("Cornea anterior von Mises stress");
    model.result("pg_cor_mises").feature().create("surf1", "Surface");
    try { model.result("pg_cor_mises").feature("surf1").selection().named("sel_cornea_anterior_surface"); } catch (Exception ex) { System.out.println("plot selection warning cor mises: " + ex.getMessage()); }
    model.result("pg_cor_mises").feature("surf1").set("expr", "solid.mises");
    model.result("pg_cor_mises").feature("surf1").set("unit", "Pa");

    model.result().create("pg_lid_mises", "PlotGroup3D");
    model.result("pg_lid_mises").label("Lid contact surface von Mises stress");
    model.result("pg_lid_mises").feature().create("surf1", "Surface");
    try { model.result("pg_lid_mises").feature("surf1").selection().named("sel_lid_contact_source_robust2"); } catch (Exception ex) { System.out.println("plot selection warning lid mises: " + ex.getMessage()); }
    model.result("pg_lid_mises").feature("surf1").set("expr", "solid.mises");
    model.result("pg_lid_mises").feature("surf1").set("unit", "Pa");
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("du_cornea_lid_robust_contact_rounded_lid_disp_calibrated_0p03.mph");
    clean(model);
    model.param().set("theta_lid", "0[deg]", "Apex position; rot_lid moves the whole prebuilt lid body");
    model.param().set("delta_indent", "0.077[mm]", "Displacement-control indentation calibrated near 0.03 N");
    ensureRoundedWindow(model);
    ensureRobustSelections(model);
    model.component("comp1").geom("geom1").run();
    System.out.println("rot_lid input=" + Arrays.toString(model.component("comp1").geom("geom1").feature("rot_lid").getStringArray("input")));
    System.out.println("rot_lid angle=" + model.component("comp1").geom("geom1").feature("rot_lid").getString("rot"));
    System.out.println("contact source=" + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust2").entities(2)));
    model.component("comp1").mesh("mesh1").run();
    addStudyAndResults(model);
    model.save(SETUP);
    model.study("std_round_disp_cal").run();
    model.result().dataset("dset_round_scan").set("solution", "sol2");
    model.result().numerical("int_round_contact_force").setResult();
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
