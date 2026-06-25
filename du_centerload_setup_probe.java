import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_centerload_setup_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\18_center_directed_lid_load_setup_from_change.mph");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    for (String s : new String[]{"sel_lid_wiper_inner_surface_dyn", "sel_lid_load_surface_dyn", "sel_cornea_anterior_surface"}) {
      try {
        System.out.println(s + "=" + Arrays.toString(model.component("comp1").selection(s).entities(2)));
      } catch (Exception e) {
        System.out.println(s + " error: " + e.getMessage());
      }
    }
    for (String c : model.component("comp1").cpl().tags()) {
      if (c.equals("intop_lid_load_area")) {
        System.out.println("cpl=" + c + " sel=" + Arrays.toString(model.component("comp1").cpl(c).selection().entities()));
      }
    }
    for (String f : model.component("comp1").physics("solid").feature().tags()) {
      if (f.equals("load_lid")) {
        System.out.println("load_lid sel=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).selection().entities()));
        System.out.println("load_lid F=" + Arrays.toString(model.component("comp1").physics("solid").feature(f).getStringArray("forceReferenceArea")));
      }
    }
  }
}
