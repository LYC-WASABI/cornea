import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_tff_result_expression_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\32_lid8mm_mixed_lubrication_stage2_oneway_setup.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void probe(Model model, String expr) {
    String tag = "max_probe";
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    try {
      model.result().numerical().create(tag, "MaxSurface");
      model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
      model.result().numerical(tag).set("data", "dset_probe");
      model.result().numerical(tag).set("expr", new String[]{expr});
      double[][] value = model.result().numerical(tag).getReal();
      System.out.printf("EXPR_OK %s final=%.12g%n", expr, value[0][value[0].length - 1]);
    } catch (Exception ex) {
      System.out.println("EXPR_FAIL " + expr + " message=" + ex.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Probe", IN);
    model.study("std_tff_oneway").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).runAll();
    model.result().dataset().create("dset_probe", "Solution");
    model.result().dataset("dset_probe").set("solution", sol);
    String[] expressions = new String[]{
        "pfilm", "tff.pfilm", "tff.pf", "tff.ptot", "tff.h", "tff.hw", "tff.fwallx",
        "tff.fwally", "tff.fwallz", "tff.fbasex", "tff.fbasey", "tff.fbasez"
    };
    for (String expr : expressions) probe(model, expr);
  }
}
