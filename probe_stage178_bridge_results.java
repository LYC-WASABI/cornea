import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage178_bridge_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "330_lid8mm_stage178_loadshare_bridge_results_Model.mph");
      try {
        model.result().dataset().remove("dset178probe");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset178probe", "Solution");
      model.result().dataset("dset178probe").set("solution", "sol44");
      try {
        model.result().numerical().remove("eval178probe");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval178probe", "EvalGlobal");
      model.result().numerical("eval178probe").set("data", "dset178probe");
      model.result().numerical("eval178probe").set(
          "expr",
          new String[] {
            "film_share177", "Fn_contact119", "Wfilm177", "Ftotal177",
            "Ferr177", "dr_indent119", "q_force_total111"
          });
      double[][] values = model.result().numerical("eval178probe").getReal();
      for (int index = 0; index < values[0].length; index++) {
        System.out.printf(
            Locale.US,
            "row=%d share=%.8g Fc=%.8g Wf=%.8g Ft=%.8g err=%.8g"
                + " d=%.8g q=%.8g%n",
            index,
            values[0][index],
            values[1][index],
            values[2][index],
            values[3][index],
            values[4][index],
            values[5][index],
            values[6][index]);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
