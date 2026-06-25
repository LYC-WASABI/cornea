import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_pair_operator_matrix {
  private static double value(
      Model model, String tag, String selection, String expr) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset574op");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model",
          "build_stage574_midpoint_local_patch_structure_output6_Model.mph");
      try { model.result().dataset().remove("dset574op"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574op", "Solution");
      model.result().dataset("dset574op").set("solution", "sol94");
      String selection = "sel_local_cornea_patch574";
      double area = value(model, "a574op", selection, "1");
      String src = "geomgap_src_cp_lid_cornea";
      String dst = "geomgap_dst_cp_lid_cornea";
      String[] expressions = {
        "src2dst_cp_lid_cornea(1)",
        "dst2src_cp_lid_cornea(1)",
        "src2dst_cp_lid_cornea(if(abs(" + src + ")<0.1[mm],1,0))",
        "dst2src_cp_lid_cornea(if(abs(" + src + ")<0.1[mm],1,0))",
        "src2dst_cp_lid_cornea(if(abs(" + dst + ")<0.1[mm],1,0))",
        "dst2src_cp_lid_cornea(if(abs(" + dst + ")<0.1[mm],1,0))"
      };
      int index = 0;
      for (String expression : expressions) {
        try {
          double integral =
              value(model, "v574op" + index++, selection, expression);
          System.out.printf(Locale.US, "OK|%s|AVG=%.12g%n",
              expression, integral / area);
        } catch (Exception error) {
          System.out.println("ERROR|" + expression + "|"
              + error.getMessage().replace('\n', ' '));
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
