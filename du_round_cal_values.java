import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_round_cal_values {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_rounded_lid_displacement_calibration_results.mph");
    String tag = "int_check";
    try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset1");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("expr", "solid.Tn");
    model.result().numerical(tag).set("unit", "N");
    double[] dmm = {0.005, 0.01, 0.02, 0.04, 0.06, 0.08, 0.10};
    for (int i = 1; i <= dmm.length; i++) {
      model.result().numerical(tag).set("outersolnum", new int[]{i});
      double val = model.result().numerical(tag).getReal()[0][0];
      System.out.println("d_lid_press_mm=" + dmm[i-1] + " contact_integral_N=" + val);
    }
  }
}
