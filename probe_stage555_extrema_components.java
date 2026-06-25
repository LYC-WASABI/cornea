import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_extrema_components {
  private static void point(
      Model model, String tag, double x, double y, double z) {
    model.result().dataset().create(tag, "CutPoint3D");
    model.result().dataset(tag).set("data", "probe_component_sol");
    model.result().dataset(tag).set("pointx", x + "[mm]");
    model.result().dataset(tag).set("pointy", y + "[mm]");
    model.result().dataset(tag).set("pointz", z + "[mm]");
    String eval = tag + "_eval";
    model.result().numerical().create(eval, "EvalPoint");
    model.result().numerical(eval).set("data", tag);
    String[] expressions = {
      "lid_mask", "dr_indent119", "u*nx+v*ny+w*nz",
      "dgap_n555", "h_gap_direct555", "h_raw555", "h_geom555"
    };
    model.result().numerical(eval).set("expr", expressions);
    double[][] values = model.result().numerical(eval).getReal();
    System.out.println(tag);
    for (int i = 0; i < expressions.length; i++) {
      System.out.println("  " + expressions[i] + "=" + values[i][2]);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558a_stage555_JFO_only_study_alpha0p964_setup.mph");
      model.result().dataset().create("probe_component_sol", "Solution");
      model.result().dataset("probe_component_sol").set("solution", "sol81");
      point(model, "at_hmax", -3.5142235551796404,
          4.303682628338979E-16, 6.963489265735505);
      point(model, "at_hmin", -8.953398258744193E-16,
          -4.874002999601136, 6.089671153673172);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
