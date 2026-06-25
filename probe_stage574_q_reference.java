import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_q_reference {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_source_true_gap_checked.mph");
      try { model.result().dataset().remove("dset_q574"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_q574", "Solution");
      model.result().dataset("dset_q574").set("solution", "sol94");
      try { model.result().numerical().remove("gev_q574"); }
      catch (Exception ignored) {}
      model.result().numerical().create("gev_q574", "EvalGlobal");
      model.result().numerical("gev_q574").set("data", "dset_q574");
      model.result().numerical("gev_q574").set(
          "expr", new String[] {
            "q_force_total111", "dr_indent570",
            "Fn_contact570", "Ferr570"
          });
      System.out.println(Arrays.deepToString(
          model.result().numerical("gev_q574").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
