import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage550_partial {
  static void eval(Model model, String data, String[] expr) {
    try { model.result().numerical().remove("tmp"); } catch (Exception ignored) {}
    model.result().numerical().create("tmp", "EvalGlobal");
    model.result().numerical("tmp").set("data", data);
    model.result().numerical("tmp").set("expr", expr);
    double[][] x = model.result().numerical("tmp").getReal();
    System.out.println(data);
    for (int i = 0; i < x.length; i++) {
      System.out.println("  " + expr[i] + "=" + x[i][0]);
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "stage550_five_position_quasistatic_output_Model.mph");
    System.out.println("SOLS=" + Arrays.toString(model.sol().tags()));
    eval(model, "dset550_plus17p5", new String[] {
      "Wfilm550_plus17p5", "Fn_contact119",
      "Ftotal550_plus17p5",
      "(Ftotal550_plus17p5-F_total_target)/F_total_target",
      "dr_indent119"
    });
    try {
      model.result().dataset().create("dtmp57", "Solution");
      model.result().dataset("dtmp57").set("solution", "sol57");
      eval(model, "dtmp57", new String[] {
        "intop_film(max(tff.p,0))",
        "intop_film(tff.theta)/intop_film(1)"
      });
    } catch (Exception error) {
      System.out.println("SOL59 ERROR=" + error.getMessage());
    }
    ModelUtil.disconnect();
  }
}
