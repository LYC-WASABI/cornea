import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574f_structure_setup {
  private static void print(String key, Object value) {
    System.out.println(key + "=" + String.valueOf(value));
  }

  private static void printArray(String key, String[] values) {
    System.out.println(key + "=" + Arrays.toString(values));
  }

  private static void printIntArray(String key, int[] values) {
    System.out.println(key + "=" + Arrays.toString(values));
  }

  private static void trySelection(ModelNode comp, String tag, int dim) {
    try {
      print(tag + ".label", comp.selection(tag).label());
      printIntArray(tag + ".entities" + dim, comp.selection(tag).entities(dim));
    } catch (Exception error) {
      print(tag + ".error", error.getMessage());
    }
  }

  private static void tryParam(Model model, String name) {
    try {
      print("param." + name, model.param().get(name));
    } catch (Exception error) {
      print("param." + name + ".error", error.getMessage());
    }
  }

  private static void tryStringArray(String key, Object entity, String prop) {
    try {
      String[] values = (String[]) entity.getClass()
          .getMethod("getStringArray", String.class)
          .invoke(entity, prop);
      printArray(key, values);
    } catch (Exception error) {
      print(key + ".error", error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574f_stage574_local_cornea_patch_structure_setup.mph");
      ModelNode comp = model.component("comp1");
      print("model.label", model.label());
      tryParam(model, "q_fixed574");
      tryParam(model, "q_scale574");
      tryParam(model, "time_offset572");
      printArray("studies", model.study().tags());
      for (String study : model.study().tags()) {
        print("study." + study + ".label", model.study(study).label());
        printArray("study." + study + ".features", model.study(study).feature().tags());
      }
      printArray("solutions", model.sol().tags());
      printArray("datasets", model.result().dataset().tags());
      printArray("physics", comp.physics().tags());
      printArray("solid.features", comp.physics("solid").feature().tags());
      printArray("tff.features", comp.physics("tff").feature().tags());
      tryStringArray("solid.disp_lid_time.U0",
          comp.physics("solid").feature("disp_lid_time"), "U0");
      tryStringArray("solid.dcnt1.pairs",
          comp.physics("solid").feature("dcnt1"), "pairs");
      trySelection(comp, "sel_local_cornea_patch574", 2);
      trySelection(comp, "sel_local_edges_all574", 1);
      trySelection(comp, "sel_local_leading574", 1);
      trySelection(comp, "sel_local_trailing574", 1);
      trySelection(comp, "sel_local_left574", 1);
      trySelection(comp, "sel_local_right574", 1);
      trySelection(comp, "sel_lid_contact_source574", 2);
      try {
        print("tff.selection", comp.physics("tff").selection().named());
      } catch (Exception error) {
        print("tff.selection.error", error.getMessage());
      }
      try {
        print("intop_film.selection", comp.cpl("intop_film").selection().named());
      } catch (Exception error) {
        print("intop_film.selection.error", error.getMessage());
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
