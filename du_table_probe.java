import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_table_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_rounded_lid_displacement_calibration_results.mph");
    for (String t : model.result().table().tags()) {
      System.out.println("TABLE " + t + " : " + model.result().table(t).label());
      try { System.out.println(Arrays.deepToString(model.result().table(t).getReal())); } catch (Exception e) { System.out.println("getReal err " + e.getMessage()); }
      try { System.out.println(Arrays.deepToString(model.result().table(t).getTableData(false))); } catch (Exception e) { System.out.println("getTableData err " + e.getMessage()); }
    }
  }
}
