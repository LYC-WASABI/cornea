import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_apex_boundary_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_apex_spherical_rect_lid_wiper.mph");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    int[] src = model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2);
    int[] load = model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2);
    System.out.println("source=" + Arrays.toString(src));
    System.out.println("load=" + Arrays.toString(load));
    int[] check = {11, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    for (int b : check) {
      String tag = "intb" + b;
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      try {
        model.result().numerical().create(tag, "IntSurface");
        model.result().numerical(tag).selection().set(new int[]{b});
        model.result().numerical(tag).set("expr", new String[]{"1", "sqrt(x^2+y^2+z^2)", "z"});
        double[][] a = model.result().numerical(tag).getReal();
        System.out.println("b=" + b + " area=" + Arrays.deepToString(a));
      } catch (Exception e) {
        System.out.println("b=" + b + " error=" + e.getMessage());
      }
    }
  }
}
