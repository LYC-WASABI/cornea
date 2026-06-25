import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage175_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "stage175_updated_film_thickness_scan_output_Model.mph");
      try {
        model.result().dataset().remove("dset175probe");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset175probe", "Solution");
      model.result().dataset("dset175probe").set("solution", "sol40");
      try {
        model.result().numerical().remove("eval175probe");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval175probe", "EvalGlobal");
      model.result().numerical("eval175probe").set("data", "dset175probe");
      model.result().numerical("eval175probe").set(
          "expr",
          new String[] {
            "beta_h175",
            "intop_film(max(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)",
            "intop_film(h_updated175)/intop_film(1)"
          });
      double[][] values = model.result().numerical("eval175probe").getReal();
      for (int index = 0; index < values[0].length; index++) {
        System.out.printf(
            Locale.US,
            "beta=%.7g Wpos=%.10g Wnet=%.10g Fshear=%.10g havg=%.10g%n",
            values[0][index],
            values[1][index],
            values[2][index],
            values[3][index],
            values[4][index]);
      }
      model.save("320_lid8mm_stage175_updated_thickness_scan_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
