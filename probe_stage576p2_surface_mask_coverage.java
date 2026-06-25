import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_surface_mask_coverage {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final int[] SURFACES = new int[] {6, 7, 10, 15, 16, 18};

  private static void evaluate(Model model, double fraction, String solution) {
    model.param().set("t_position576p2", String.format(Locale.US,
        "T_pre572+%.12g*T_slide572", fraction));
    String data = "dset576p2SurfaceMask";
    try { model.result().dataset().remove(data); } catch (Exception ignored) {}
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);
    for (int surface : SURFACES) {
      String tag = "int576p2SurfaceMask";
      try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
      model.result().numerical().create(tag, "IntSurface");
      model.result().numerical(tag).set("data", data);
      model.result().numerical(tag).selection().set(new int[] {surface});
      model.result().numerical(tag).set("expr", new String[] {
        "1", "M_core573", "M_core573*g_pair_valid573",
        "M_core573*Bfilm573", "M_drain573"
      });
      double[][] rows = model.result().numerical(tag).getReal();
      System.out.printf(Locale.US,
          "SURFACE_MASK fraction=%.3f surface=%d area=%.12g core=%.12g"
              + " validCore=%.12g activeCore=%.12g drain=%.12g%n",
          fraction, surface, rows[0][0], rows[1][0], rows[2][0],
          rows[3][0], rows[4][0]);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      evaluate(model, 0.0, "sol143");
      evaluate(model, 0.25, "sol149");
      evaluate(model, 0.5, "sol155");
      evaluate(model, 0.75, "sol171");
      evaluate(model, 1.0, "sol182");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
