import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574j_true_gap_vars {
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
          "Model", "574h_stage574_fixed_structure_constant_zero_jfo_checked.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("LOCAL_PATCH=" + Arrays.toString(
          comp.selection("sel_local_cornea_patch574").entities(2)));
      for (String var : new String[] {
          "g_pair573", "Bfilm573", "Afilm573", "h_calc573",
          "Qvent573", "p_load573", "M_core573", "M_drain573"
      }) {
        try {
          System.out.println(var + "="
              + comp.variable("var_cornea_dynamic_regions573").get(var));
        } catch (Exception error) {
          System.out.println(var + ".error=" + error.getMessage());
        }
      }
      String data = "dset574j_probe";
      removeDataset(model, data);
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", "sol110");
      removeNumerical(model, "int574j_probe");
      model.result().numerical().create("int574j_probe", "IntSurface");
      model.result().numerical("int574j_probe").set("data", data);
      model.result().numerical("int574j_probe")
          .selection().named("sel_local_cornea_patch574");
      model.result().numerical("int574j_probe").set("expr", new String[] {
        "1",
        "if(isdefined(geomgap_dst_cp_lid_cornea),if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)",
        "if(isdefined(h_calc573),if(abs(h_calc573)<1[m],1,0),0)",
        "if(isdefined(Bfilm573),Bfilm573,0)",
        "if(isdefined(Afilm573),Afilm573,0)",
        "if(isdefined(h_calc573),h_calc573,0[m])",
        "if(isdefined(M_core573),M_core573,0)",
        "if(isdefined(M_drain573),M_drain573,0)"
      });
      System.out.println("INTS=" + Arrays.deepToString(
          model.result().numerical("int574j_probe").getReal()));
      for (String expr : new String[] {
          "geomgap_dst_cp_lid_cornea", "h_calc573", "Bfilm573", "Afilm573"
      }) {
        for (String type : new String[] {"MinSurface", "MaxSurface"}) {
          String tag = (type.startsWith("Min") ? "min" : "max")
              + expr.replaceAll("[^A-Za-z0-9]", "") + "574jprobe";
          removeNumerical(model, tag);
          model.result().numerical().create(tag, type);
          model.result().numerical(tag).set("data", data);
          model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
          model.result().numerical(tag).set("expr", expr);
          System.out.println(tag + "="
              + model.result().numerical(tag).getReal()[0][0]);
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
