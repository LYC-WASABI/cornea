import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_nan_layers {
  private static final String MODEL =
      "build_stage576p2_moving_structure_sparse_jfo_output_Model.mph";
  private static final String DATA = "sol149";
  private static final String SWEPT = "sel_film_swept571";

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", MODEL);
      model.result().dataset().create("dsetNan576p2", "Solution");
      model.result().dataset("dsetNan576p2").set("solution", DATA);
      model.result().numerical().create("intNan576p2", "IntSurface");
      model.result().numerical("intNan576p2").set("data", "dsetNan576p2");
      model.result().numerical("intNan576p2").selection().named(SWEPT);
      String[] names = new String[] {
        "g_pair_native573", "g_pair_valid573", "g_pair_safe573",
        "B_low573", "B_high573", "Bfilm573", "g_pair_physical573",
        "h_wet573", "Afilm573", "h_calc573", "M_core573", "M_drain573",
        "omega_lid_rot572", "Qvent573"
      };
      String[] expr = new String[names.length + 1];
      expr[0] = "1";
      for (int i = 0; i < names.length; i++) {
        expr[i + 1] = "if(isdefined(" + names[i] + "),1,0)";
      }
      model.result().numerical("intNan576p2").set("expr", expr);
      double[][] rows = model.result().numerical("intNan576p2").getReal();
      double area = rows[0][rows[0].length - 1];
      System.out.printf(Locale.US, "AREA=%.12g%n", area);
      for (int i = 0; i < names.length; i++) {
        double value = rows[i + 1][rows[i + 1].length - 1];
        System.out.printf(Locale.US, "DEFINED name=%s fraction=%.12g%n",
            names[i], value / area);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
