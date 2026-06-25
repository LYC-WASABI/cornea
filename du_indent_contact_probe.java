import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_indent_contact_probe {
  private static double eval(Model model, String kind, String sel, String expr, String sol) {
    String dset = "tmp_dset_" + kind + "_" + sel;
    String num = "tmp_num_" + kind + "_" + sel;
    try { model.result().numerical().remove(num); } catch (Exception ignored) {}
    try { model.result().dataset().remove(dset); } catch (Exception ignored) {}
    model.result().dataset().create(dset, "Solution");
    model.result().dataset(dset).set("solution", sol);
    model.result().numerical().create(num, kind);
    model.result().numerical(num).selection().named(sel);
    model.result().numerical(num).set("data", dset);
    model.result().numerical(num).set("expr", new String[]{expr});
    double[][] v = model.result().numerical(num).getReal();
    return v[0][0];
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph");
    String[] sols = new String[]{"sol3","sol4","sol5","sol6","sol7","sol8","sol9","sol10","sol11"};
    String[] deltas = new String[]{"0.001","0.002","0.005","0.01","0.02","0.04","0.06","0.08","0.10"};
    String[] sels = new String[]{"sel_cornea_anterior_surface", "sel_lid_wiper_inner_surface_dyn"};
    for (int i = 0; i < sols.length; i++) {
      System.out.println("delta_indent_mm=" + deltas[i] + " sol=" + sols[i]);
      for (String sel : sels) {
        try {
          double intTn = eval(model, "IntSurface", sel, "solid.Tn", sols[i]);
          double intAbsTn = eval(model, "IntSurface", sel, "abs(solid.Tn)", sols[i]);
          double maxTn = eval(model, "MaxSurface", sel, "solid.Tn", sols[i]);
          double maxAbsTn = eval(model, "MaxSurface", sel, "abs(solid.Tn)", sols[i]);
          System.out.println("  " + sel + " intTn=" + intTn + " intAbsTn=" + intAbsTn + " maxTn=" + maxTn + " maxAbsTn=" + maxAbsTn);
        } catch (Exception ex) {
          System.out.println("  " + sel + " error=" + ex.getMessage());
        }
      }
    }
  }
}
