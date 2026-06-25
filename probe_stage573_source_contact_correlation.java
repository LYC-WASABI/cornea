import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_source_contact_correlation {
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
      String gap = pair.gapName(false);
      String contact = pair.inContactName();
      removeSelection(comp, "sel573corr");
      comp.selection().create("sel573corr", "Explicit");
      comp.selection("sel573corr").geom("geom1", 2);
      comp.selection("sel573corr").set(pair.source().entities());
      removeDataset(model, "dset573corr");
      model.result().dataset().create("dset573corr", "Solution");
      model.result().dataset("dset573corr").set("solution", "sol93");
      removeNumerical(model, "int573corr");
      model.result().numerical().create("int573corr", "IntSurface");
      model.result().numerical("int573corr").set("data", "dset573corr");
      model.result().numerical("int573corr")
          .selection().named("sel573corr");
      model.result().numerical("int573corr").set("expr", new String[] {
          "1",
          "if(" + gap + "<0.1[mm]," + contact + ",0)",
          "if(" + gap + "<0[m],1,0)",
          "if(" + gap + "<0.1[mm]&&" + contact + ">0.5," + gap + ",0[m])",
          "if(" + gap + "<0.1[mm]&&" + contact + "<=0.5," + gap + ",0[m])"
      });
      System.out.println("CORRELATION=" + Arrays.deepToString(
          model.result().numerical("int573corr").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
