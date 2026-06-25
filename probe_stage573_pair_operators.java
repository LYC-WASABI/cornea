import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_pair_operators {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

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
          "Model", "572_stage572_dynamic_motion_mask_checked.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      removeSelection(comp, "sel573_src_all");
      comp.selection().create("sel573_src_all", "Explicit");
      comp.selection("sel573_src_all").geom("geom1", 2);
      comp.selection("sel573_src_all").set(pair.source().entities());
      removeDataset(model, "dset573op");
      model.result().dataset().create("dset573op", "Solution");
      model.result().dataset("dset573op").set("solution", "sol93");

      String dstGap = pair.gapName(true);
      String[] expressions = new String[] {
        "dst2src_cp_lid_cornea(" + dstGap + ")",
        "src2dst_cp_lid_cornea(" + dstGap + ")",
        "dst2src_cp_lid_cornea(solid.Tn)",
        "dst2src_cp_lid_cornea(incontact_cp_lid_cornea)",
        "-geomgap_src_cp_lid_cornea",
        "abs(geomgap_src_cp_lid_cornea)"
      };
      int index = 0;
      for (String expression : expressions) {
        String tag = "avg573op" + (++index);
        try {
          removeNumerical(model, tag);
          model.result().numerical().create(tag, "AvSurface");
          model.result().numerical(tag).set("data", "dset573op");
          model.result().numerical(tag)
              .selection().named("sel573_src_all");
          model.result().numerical(tag).set("expr", expression);
          System.out.println("OK|" + expression + "|"
              + Arrays.deepToString(
                  model.result().numerical(tag).getReal()));
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
