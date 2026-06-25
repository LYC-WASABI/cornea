import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_apex_contact_diagnostics {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_apex_spherical_rect_lid_position_scan_results.mph");
    String ds = "dset5";
    int[] paperAngles = {10, 20, 30, 40, 50, 60, 70};
    int[] theta = {30, 20, 10, 0, -10, -20, -30};
    String[] exprs = {"solid.Tn", "solid.dcnt1.pn", "solid.gap"};
    for (String expr : exprs) {
      System.out.println("EXPR " + expr);
      for (int i = 1; i <= 7; i++) {
        try {
          String maxTag = "max_" + expr.replace(".", "_").replace("1", "x") + "_" + i;
          try { model.result().numerical().remove(maxTag); } catch (Exception ignore) {}
          model.result().numerical().create(maxTag, "MaxSurface");
          model.result().numerical(maxTag).set("data", ds);
          model.result().numerical(maxTag).selection().named("sel_cornea_anterior_surface");
          model.result().numerical(maxTag).set("expr", expr);
          model.result().numerical(maxTag).set("outersolnum", i);
          double max = model.result().numerical(maxTag).getReal()[0][0];

          String intTag = "int_" + expr.replace(".", "_").replace("1", "x") + "_" + i;
          try { model.result().numerical().remove(intTag); } catch (Exception ignore) {}
          model.result().numerical().create(intTag, "IntSurface");
          model.result().numerical(intTag).set("data", ds);
          model.result().numerical(intTag).selection().named("sel_cornea_anterior_surface");
          model.result().numerical(intTag).set("expr", expr);
          model.result().numerical(intTag).set("outersolnum", i);
          double integ = model.result().numerical(intTag).getReal()[0][0];

          System.out.println("paper=" + paperAngles[i-1] + " theta=" + theta[i-1] +
              " max=" + max + " integral=" + integ);
        } catch (Exception e) {
          System.out.println("paper=" + paperAngles[i-1] + " theta=" + theta[i-1] + " failed: " + e.getMessage());
        }
      }
    }
  }
}
