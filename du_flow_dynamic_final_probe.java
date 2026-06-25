import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_flow_dynamic_final_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_dynamic_sliding_minus35_to_plus35_results.mph";

  private static void summarize(Model model, String tag) {
    String[][] rows = model.result().table(tag).getTableData(false);
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (String[] row : rows) {
      double v = Double.parseDouble(row[row.length - 1]);
      min = Math.min(min, v);
      max = Math.max(max, v);
    }
    System.out.println(tag + " rows=" + rows.length + " min=" + min + " max=" + max
        + " first=" + Arrays.toString(rows[0])
        + " last=" + Arrays.toString(rows[rows.length - 1]));
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    System.out.println("PLOTS=" + Arrays.toString(model.result().tags()));
    System.out.println("TABLES=" + Arrays.toString(model.result().table().tags()));
    System.out.println("contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    for (String tag : new String[]{
        "tbl_dyn_contact_force", "tbl_dyn_theta", "tbl_dyn_slide_fraction",
        "tbl_max_dyn_cornea_disp", "tbl_max_dyn_lid_disp",
        "tbl_max_dyn_cornea_mises", "tbl_max_dyn_lid_mises"}) {
      summarize(model, tag);
    }
  }
}
