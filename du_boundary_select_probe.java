import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_boundary_select_probe {
  private static void printSel(Model model, String tag) {
    try {
      System.out.println(tag + " entities=" + Arrays.toString(model.component("comp1").selection(tag).entities()));
    } catch (Exception e) {
      System.out.println(tag + " error=" + e.getMessage());
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");

    model.component("comp1").selection().create("probe_cor_inner_ball", "Ball");
    model.component("comp1").selection("probe_cor_inner_ball").set("entitydim", 2);
    model.component("comp1").selection("probe_cor_inner_ball").set("posx", "0");
    model.component("comp1").selection("probe_cor_inner_ball").set("posy", "0");
    model.component("comp1").selection("probe_cor_inner_ball").set("posz", "0");
    model.component("comp1").selection("probe_cor_inner_ball").set("r", "Rcor-0.03[mm]");
    model.component("comp1").selection("probe_cor_inner_ball").set("condition", "inside");

    model.component("comp1").selection().create("probe_cor_front_highz", "Box");
    model.component("comp1").selection("probe_cor_front_highz").set("entitydim", 2);
    model.component("comp1").selection("probe_cor_front_highz").set("xmin", "-6.2[mm]");
    model.component("comp1").selection("probe_cor_front_highz").set("xmax", "6.2[mm]");
    model.component("comp1").selection("probe_cor_front_highz").set("ymin", "-6.2[mm]");
    model.component("comp1").selection("probe_cor_front_highz").set("ymax", "6.2[mm]");
    model.component("comp1").selection("probe_cor_front_highz").set("zmin", "z_base+0.05[mm]");
    model.component("comp1").selection("probe_cor_front_highz").set("zmax", "Rcor+0.05[mm]");

    model.component("comp1").selection().create("probe_cor_ant_int", "Intersection");
    model.component("comp1").selection("probe_cor_ant_int").set("entitydim", 2);
    model.component("comp1").selection("probe_cor_ant_int").set("input", new String[]{"probe_cor_front_highz", "probe_cor_inner_ball"});

    model.component("comp1").selection().create("probe_lid_inner_in", "Ball");
    model.component("comp1").selection("probe_lid_inner_in").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_inner_in").set("posx", "0");
    model.component("comp1").selection("probe_lid_inner_in").set("posy", "0");
    model.component("comp1").selection("probe_lid_inner_in").set("posz", "0");
    model.component("comp1").selection("probe_lid_inner_in").set("r", "Rlid_in+0.04[mm]");
    model.component("comp1").selection("probe_lid_inner_in").set("condition", "inside");

    model.component("comp1").selection().create("probe_lid_box", "Box");
    model.component("comp1").selection("probe_lid_box").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_box").set("xmin", "-lid_length/2-0.1[mm]");
    model.component("comp1").selection("probe_lid_box").set("xmax", "lid_length/2+0.1[mm]");
    model.component("comp1").selection("probe_lid_box").set("ymin", "ylid-lid_width/2-0.1[mm]");
    model.component("comp1").selection("probe_lid_box").set("ymax", "ylid+lid_width/2+0.1[mm]");
    model.component("comp1").selection("probe_lid_box").set("zmin", "zlid-0.1[mm]");
    model.component("comp1").selection("probe_lid_box").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("probe_lid_inner_box_int", "Intersection");
    model.component("comp1").selection("probe_lid_inner_box_int").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_inner_box_int").set("input", new String[]{"probe_lid_inner_in", "probe_lid_box"});


    model.component("comp1").selection().create("probe_lid_outer_out", "Ball");
    model.component("comp1").selection("probe_lid_outer_out").set("entitydim", 2);
    model.component("comp1").selection("probe_lid_outer_out").set("posx", "0");
    model.component("comp1").selection("probe_lid_outer_out").set("posy", "0");
    model.component("comp1").selection("probe_lid_outer_out").set("posz", "0");
    model.component("comp1").selection("probe_lid_outer_out").set("r", "Rlid_out-0.04[mm]");
    model.component("comp1").selection("probe_lid_outer_out").set("condition", "inside");


    printSel(model, "probe_cor_inner_ball");
    printSel(model, "probe_cor_front_highz");
    printSel(model, "probe_cor_ant_int");
    printSel(model, "probe_lid_inner_in");
    printSel(model, "probe_lid_box");
    printSel(model, "probe_lid_inner_box_int");
    // printSel(model, "probe_lid_inner_int");
    printSel(model, "probe_lid_outer_out");
    // printSel(model, "probe_lid_outer_int");
    printSel(model, "sel_lid_outer_load");
  }
}
