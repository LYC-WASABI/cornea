import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_arc_length_table_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rect_arc_length_coarse_calibration_results.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    String[][] data = model.result().table("tbl_arc_coarse_force").getTableData(false);
    for (String[] row : data) {
      System.out.println(String.join(" | ", row));
    }
  }
}
