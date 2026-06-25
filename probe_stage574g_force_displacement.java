import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574g_force_displacement {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574g_stage574_local_contact_gap_results.mph");
      for (String sol : model.sol().tags()) {
        if (!sol.matches("sol(9[6-9]|10[0-9])")) continue;
        String data = "dset_fd_" + sol;
        removeDataset(model, data);
        model.result().dataset().create(data, "Solution");
        model.result().dataset(data).set("solution", sol);
        String eval = "eval_fd_" + sol;
        removeNumerical(model, eval);
        model.result().numerical().create(eval, "EvalGlobal");
        model.result().numerical(eval).set("data", data);
        model.result().numerical(eval).set("expr", new String[] {
          "q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"
        });
        double[][] v = model.result().numerical(eval).getReal();
        System.out.println(sol + " q=" + v[0][0] + " disp_m=" + v[1][0]
            + " Fn_N=" + v[2][0]);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
