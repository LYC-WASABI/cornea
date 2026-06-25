import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_contact_integral_by_param {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_center_directed_lid_load_scan_results.mph");
    String tag = "int_tmp_by_param";
    try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset2");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("expr", "solid.Tn");
    model.result().numerical(tag).set("unit", "N");
    int[] paperAngles = {10, 20, 30, 40, 50, 60, 70};
    int[] thetaLid = {30, 20, 10, 0, -10, -20, -30};
    for (int i = 1; i <= 7; i++) {
      try {
        model.result().numerical(tag).set("outersolnum", i);
        double[][] val = model.result().numerical(tag).getReal();
        System.out.println("paper_angle=" + paperAngles[i-1] + "deg theta_lid=" + thetaLid[i-1] + "deg integral_N=" + val[0][0]);
      } catch (Exception e) {
        System.out.println("paper_angle=" + paperAngles[i-1] + "deg failed: " + e.getMessage());
      }
    }
  }
}
