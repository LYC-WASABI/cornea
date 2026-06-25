import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage550_saved_results {
  static void eval(Model model, String key) {
    String dset = key.equals("mid") ? "dset540_joint" : "dset550_" + key;
    String suffix = key.equals("mid") ? "540" : "550_" + key;
    String wfilm = key.equals("mid") ? "Wfilm540" : "Wfilm" + suffix;
    String total = key.equals("mid") ? "Ftotal540" : "Ftotal" + suffix;
    String tag = "tmp_" + key;
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", dset);
    model.result().numerical(tag).set("expr", new String[] {
      wfilm, "Fn_contact119", total,
      "(" + total + "-F_total_target)/F_total_target",
      "dr_indent119"
    });
    double[][] values = model.result().numerical(tag).getReal();
    System.out.print(key);
    for (double[] value : values) System.out.print(" " + value[0]);
    System.out.println();
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "552_stage550_five_position_results.mph");
    for (String key : new String[] {
        "plus35", "plus17p5", "mid", "minus17p5", "minus35"}) {
      try {
        eval(model, key);
        if (!key.equals("mid")) {
          System.out.println(key + " solution="
              + model.result().dataset("dset550_" + key).getString("solution"));
        }
      } catch (Exception error) {
        System.out.println(key + " ERROR " + error.getMessage());
      }
    }
    System.out.println("speed=" + model.component("comp1")
        .variable("var_mixed_lub").get("vwall550"));
    ModelUtil.disconnect();
  }
}
