import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_rect_lid_boundary_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\19_rectangular_curved_lid_wiper_setup.mph");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").run();
    int[] source = model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2);
    int[] load = model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2);
    System.out.println("source candidates=" + Arrays.toString(source));
    System.out.println("load candidates=" + Arrays.toString(load));
    int[] all = new int[30];
    for (int i = 0; i < all.length; i++) all[i] = i + 1;
    for (int b : all) {
      try {
        String tag = "int_b_" + b;
        try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
        model.result().numerical().create(tag, "IntSurface");
        model.result().numerical(tag).selection().set(new int[]{b});
        model.result().numerical(tag).set("expr", new String[]{
          "1",
          "sqrt(y^2+z^2)",
          "x",
          "y",
          "z"
        });
        model.result().numerical(tag).set("unit", new String[]{"m^2", "m", "m^3", "m^3", "m^3"});
        double[][] v = model.result().numerical(tag).getReal();
        double area = v[0][0];
        if (area > 1e-10) {
          System.out.println("b=" + b + " area=" + area +
              " int_r=" + v[0][1] + " avg_r=" + (v[0][1] / area) +
              " avg_x=" + (v[0][2] / area) + " avg_y=" + (v[0][3] / area) + " avg_z=" + (v[0][4] / area));
        }
      } catch (Exception ignore) {}
    }
  }
}
