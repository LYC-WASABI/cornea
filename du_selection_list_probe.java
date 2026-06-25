import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_selection_list_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    for (String s : model.component("comp1").selection().tags()) {
      try {
        System.out.println(s + " dim? entities=" + Arrays.toString(model.component("comp1").selection(s).entities()));
      } catch (Exception e) {
        System.out.println(s + " error=" + e.getMessage());
      }
    }
  }
}
