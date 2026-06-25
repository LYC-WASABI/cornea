import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_indent_loop_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph");
    String numTag = "tmp_loop_contact_force";
    if (model.result().numerical().tags().length > 0) {
      try { model.result().numerical().remove(numTag); } catch (Exception ignored) {}
    }
    String dsetTag = "tmp_loop_dset";
    try { model.result().dataset().remove(dsetTag); } catch (Exception ignored) {}
    model.result().dataset().create(dsetTag, "Solution");

    model.result().numerical().create(numTag, "IntSurface");
    model.result().numerical(numTag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(numTag).set("data", dsetTag);
    model.result().numerical(numTag).set("expr", new String[]{"solid.Tn"});
    model.result().numerical(numTag).set("unit", new String[]{"N"});
    model.result().numerical(numTag).set("descr", new String[]{"integral solid.Tn"});

    String[] sols = new String[]{"sol3","sol4","sol5","sol6","sol7","sol8","sol9","sol10","sol11"};
    String[] deltas = new String[]{"0.001","0.002","0.005","0.01","0.02","0.04","0.06","0.08","0.10"};
    for (int i = 0; i < sols.length; i++) {
      try {
        model.result().dataset(dsetTag).set("solution", sols[i]);
        double[][] val = model.result().numerical(numTag).getReal();
        System.out.println("delta_indent_mm=" + deltas[i] + " solution=" + sols[i] + " force=" + val[0][0]);
      } catch (Exception ex) {
        System.out.println("delta_indent_mm=" + deltas[i] + " solution=" + sols[i] + " error=" + ex.getMessage());
      }
    }
  }
}
