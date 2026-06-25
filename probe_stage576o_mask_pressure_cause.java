import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576o_mask_pressure_cause {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String solution) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double[] integrate(Model model, String data, String[] expressions) {
    removeNumerical(model, "int576oCause");
    model.result().numerical().create("int576oCause", "IntSurface");
    model.result().numerical("int576oCause").set("data", data);
    model.result().numerical("int576oCause").selection().named("sel_local_cornea_patch574");
    model.result().numerical("int576oCause").set("expr", expressions);
    double[][] raw = model.result().numerical("int576oCause").getReal();
    double[] result = new double[raw.length];
    for (int i = 0; i < raw.length; i++) result[i] = raw[i][raw[i].length - 1];
    return result;
  }

  private static double divide(double numerator, double denominator) {
    return Math.abs(denominator) > 1e-30 ? numerator / denominator : Double.NaN;
  }

  private static void printDefinitions(Model model) {
    ModelNode comp = model.component("comp1");
    String[] motion = {"tau572", "phi_lid572", "Yc_lid572", "Zc_lid572", "M_lid572"};
    for (String name : motion) {
      try { System.out.println("DEFINITION var_dynamic_motion572." + name + "="
          + comp.variable("var_dynamic_motion572").get(name)); }
      catch (Exception error) { System.out.println("DEFINITION_MISSING=" + name); }
    }
    String[] region = {"M_core573", "M_drain573", "M_open573", "Afilm573",
        "h_calc573", "Qvent573", "p_load573"};
    for (String name : region) {
      try { System.out.println("DEFINITION var_cornea_dynamic_regions573." + name + "="
          + comp.variable("var_cornea_dynamic_regions573").get(name)); }
      catch (Exception error) { System.out.println("DEFINITION_MISSING=" + name); }
    }
    try { System.out.println("TFF_FFP_HW1=" + comp.physics("tff").feature("ffp1").getString("hw1")); }
    catch (Exception error) { System.out.println("TFF_FFP_HW1_MISSING"); }
    try { System.out.println("TFF_VENT=" + comp.physics("tff").feature("ms_vent573").getString("QudR")); }
    catch (Exception error) { System.out.println("TFF_VENT_MISSING"); }
    try { System.out.println("TFF_ANCHOR=" + comp.physics("tff").feature("wc_open_anchor573").getString("weakExpression")); }
    catch (Exception error) { System.out.println("TFF_ANCHOR_MISSING"); }
    for (String name : new String[] {"kvent573", "kanchor573", "h_background573",
        "h_active_max573", "dh_active573"}) {
      try { System.out.println("PARAM " + name + "=" + model.param().get(name)); }
      catch (Exception error) { System.out.println("PARAM_MISSING=" + name); }
    }
  }

  private static void evaluateFile(String file, String[][] states, boolean definitions) throws Exception {
    Model model = ModelUtil.load("Model", file);
    if (definitions) printDefinitions(model);
    double tPre = model.param().evaluate("T_pre572");
    double tSlide = model.param().evaluate("T_slide572");
    String[] expressions = {
      "1",
      "M_core573", "M_core573*Y", "M_core573*Z",
      "M_drain573", "M_drain573*Y", "M_drain573*Z",
      "max(tff.p-p_amb573,0[Pa])",
      "max(tff.p-p_amb573,0[Pa])*Y",
      "max(tff.p-p_amb573,0[Pa])*Z",
      "M_core573*max(tff.p-p_amb573,0[Pa])",
      "M_drain573*max(tff.p-p_amb573,0[Pa])",
      "max(p_load573,0[Pa])",
      "max(p_load573,0[Pa])*Y",
      "max(p_load573,0[Pa])*Z",
      "Afilm573", "Afilm573*Y", "Afilm573*Z"
    };
    for (String[] state : states) {
      String pressure = state[0];
      String data = dataset(model, "dset576oCause", pressure);
      double[] values = integrate(model, data, expressions);
      double[] times = model.sol(pressure).getPVals();
      double time = times[times.length - 1];
      double fraction = (time - tPre) / tSlide;
      System.out.printf(Locale.US,
          "CAUSE fraction=%.6f coreAreaFrac=%.12g coreY=%.12g coreZ=%.12g drainAreaFrac=%.12g drainY=%.12g drainZ=%.12g pressureY=%.12g pressureZ=%.12g pressureInCore=%.12g pressureInDrain=%.12g loadY=%.12g loadZ=%.12g afilmAreaFrac=%.12g afilmY=%.12g afilmZ=%.12g%n",
          fraction,
          divide(values[1], values[0]), divide(values[2], values[1]), divide(values[3], values[1]),
          divide(values[4], values[0]), divide(values[5], values[4]), divide(values[6], values[4]),
          divide(values[8], values[7]), divide(values[9], values[7]),
          divide(values[10], values[7]), divide(values[11], values[7]),
          divide(values[13], values[12]), divide(values[14], values[12]),
          divide(values[15], values[0]), divide(values[16], values[15]), divide(values[17], values[15]));
    }
    ModelUtil.remove("Model");
    System.gc();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      evaluateFile("576n12_stage576_full_dynamic_recursive_checked.mph",
          new String[][] {
            {"sol488"}, {"sol1208"}, {"sol1568"}, {"sol4984"}
          }, true);
      evaluateFile("576n9_stage576_full_dynamic_recursive_resume168_checkpoint.mph",
          new String[][] {{"sol2755"}}, false);
      evaluateFile("576n10_stage576_full_dynamic_recursive_resume174_checkpoint.mph",
          new String[][] {{"sol3412"}}, false);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
