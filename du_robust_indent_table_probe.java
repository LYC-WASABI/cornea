import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_robust_indent_table_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_robust_contact_results.mph");
    for (String tag : model.result().table().tags()) {
      System.out.println("TABLE " + tag + " : " + model.result().table(tag).label());
      try { System.out.println(Arrays.deepToString(model.result().table(tag).getReal())); } catch (Exception ex) { System.out.println("getReal error: " + ex.getMessage()); }
      try { System.out.println(Arrays.deepToString(model.result().table(tag).getTableData(false))); } catch (Exception ex) { System.out.println("getTableData error: " + ex.getMessage()); }
    }
  }
}
