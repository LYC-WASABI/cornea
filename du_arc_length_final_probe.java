import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_arc_length_final_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rect_arc_length_14_22mm_sweep_calibrated_0p03N_results.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    System.out.println("PARAM s_lid=" + model.param().get("s_lid"));
    System.out.println("PARAM delta_indent=" + model.param().get("delta_indent"));
    System.out.println("contact source="
        + Arrays.toString(model.component("comp1").selection("sel_lid_contact_source_robust").entities(2)));
    System.out.println("outer support="
        + Arrays.toString(model.component("comp1").selection("sel_lid_outer_support").entities(2)));
    for (String tag : model.result().table().tags()) {
      System.out.println("TABLE " + tag + " : " + model.result().table(tag).label());
      String[][] rows = model.result().table(tag).getTableData(false);
      for (String[] row : rows) System.out.println("  " + String.join(" | ", row));
    }
  }
}
