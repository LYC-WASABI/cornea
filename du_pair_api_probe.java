import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_pair_api_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    String[] sels = new String[]{"sel_posterior", "sel_limbus", "sel_lid_outer_load", "sel_contact_zone"};
    for (String s : sels) {
      try {
        System.out.println(s + " entities=" + Arrays.toString(model.component("comp1").selection(s).entities()));
      } catch (Exception e) {
        System.out.println(s + " error=" + e.getMessage());
      }
    }
    String[] types = new String[]{"ContactPair", "IdentityPair", "Pair", "Contact", "BoundaryPair"};
    for (String type : types) {
      try {
        model.component("comp1").pair().create("probe_" + type, type);
        System.out.println("CREATED component pair type=" + type);
      } catch (Exception e) {
        System.out.println("FAILED component pair type=" + type + " :: " + e.getMessage());
      }
    }
  }
}
