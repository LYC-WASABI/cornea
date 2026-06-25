import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_pair_method_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    model.component("comp1").pair().create("cp1", "Contact");
    model.component("comp1").pair("cp1").label("probe contact pair");
    model.component("comp1").pair("cp1").source().set(new int[]{1});
    model.component("comp1").pair("cp1").destination().set(new int[]{11});
    model.save("D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\pair_method_probe.mph");
  }
}
