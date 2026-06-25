import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p_gap_coverage {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576p_stage576_swept_domain_gated_jfo_results.mph");
      removeDataset(model, "dset576pCoverage");
      model.result().dataset().create("dset576pCoverage", "Solution");
      model.result().dataset("dset576pCoverage").set("solution", "sol145");
      removeNumerical(model, "int576pCoverage");
      model.result().numerical().create("int576pCoverage", "IntSurface");
      model.result().numerical("int576pCoverage").set("data", "dset576pCoverage");
      model.result().numerical("int576pCoverage").selection().named("sel_film_swept571");
      model.result().numerical("int576pCoverage").set("expr", new String[] {
        "1", "M_core573", "M_core573*g_pair_valid573", "M_core573*Bfilm573",
        "M_core573*B_low573", "M_core573*B_high573",
        "M_drain573", "M_drain573*g_pair_valid573", "M_drain573*Bfilm573",
        "max(tff.p-p_amb573,0[Pa])",
        "M_core573*max(tff.p-p_amb573,0[Pa])",
        "M_drain573*max(tff.p-p_amb573,0[Pa])"
      });
      double[][] rows = model.result().numerical("int576pCoverage").getReal();
      int count = rows[0].length;
      for (int i = 0; i < count; i++) {
        double area = rows[0][i];
        double core = rows[1][i];
        double drain = rows[6][i];
        double pressure = rows[9][i];
        System.out.printf(Locale.US,
            "COVERAGE fraction=%.3f coreAreaFrac=%.12g validInCore=%.12g BfilmInCore=%.12g BlowInCore=%.12g BhighInCore=%.12g drainAreaFrac=%.12g validInDrain=%.12g BfilmInDrain=%.12g pressureInCore=%.12g pressureInDrain=%.12g%n",
            i / Math.max(1.0, count - 1.0), core/area,
            core > 0 ? rows[2][i]/core : Double.NaN,
            core > 0 ? rows[3][i]/core : Double.NaN,
            core > 0 ? rows[4][i]/core : Double.NaN,
            core > 0 ? rows[5][i]/core : Double.NaN,
            drain/area,
            drain > 0 ? rows[7][i]/drain : Double.NaN,
            drain > 0 ? rows[8][i]/drain : Double.NaN,
            pressure > 0 ? rows[10][i]/pressure : Double.NaN,
            pressure > 0 ? rows[11][i]/pressure : Double.NaN);
      }
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
