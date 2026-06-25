import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_deform_lid_table_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_geometric_indent_deformable_lid_outer_support_results.mph");
    for (String tag : model.result().table().tags()) {
      System.out.println("TABLE " + tag + " : " + model.result().table(tag).label());
      System.out.println(Arrays.deepToString(model.result().table(tag).getReal()));
      System.out.println(Arrays.deepToString(model.result().table(tag).getTableData(false)));
    }
  }
}
