import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574q_feedback_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "574o_stage574_fixed_structure_true_gap_from_003N_checked.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("SOL_TAGS=" + Arrays.toString(model.sol().tags()));
      System.out.println("SOLID_FEATURES=" + Arrays.toString(comp.physics("solid").feature().tags()));
      for (String tag : comp.physics("solid").feature().tags()) {
        PhysicsFeature f = comp.physics("solid").feature(tag);
        System.out.println("SOLID_FEATURE=" + tag + "|" + f.getType() + "|" + f.label());
        if (tag.toLowerCase(Locale.ROOT).contains("load")
            || f.label().toLowerCase(Locale.ROOT).contains("load")
            || f.label().toLowerCase(Locale.ROOT).contains("film")) {
          try { System.out.println("  SELECTION=" + Arrays.toString(f.selection().entities())); } catch (Exception ignored) {}
          for (String key : new String[] {"FperArea", "LoadType", "F", "Pressure", "p0"}) {
            try { System.out.println("  " + key + "=" + Arrays.toString(f.getStringArray(key))); }
            catch (Exception e1) {
              try { System.out.println("  " + key + "=" + f.getString(key)); }
              catch (Exception ignored) {}
            }
          }
        }
      }
      try {
        System.out.println("GE_ACTIVE=" + comp.physics("ge_force_total111").isActive());
        System.out.println("GE_FEATURES=" + Arrays.toString(comp.physics("ge_force_total111").feature().tags()));
        for (String tag : comp.physics("ge_force_total111").feature().tags()) {
          PhysicsFeature f = comp.physics("ge_force_total111").feature(tag);
          System.out.println("GE_FEATURE=" + tag + "|" + f.getType() + "|" + f.label());
          try { System.out.println("  equation=" + Arrays.toString(f.getStringArray("equation"))); }
          catch (Exception ignored) {}
        }
      } catch (Exception e) {
        System.out.println("GE_MISSING=" + e.getMessage());
      }
      for (String vt : comp.variable().tags()) {
        for (String name : new String[] {
            "q_force_total111", "F_total_target", "Fn_contact570",
            "Fn_film573", "Fn_total573", "p_load573", "lambda_load574"}) {
          try { System.out.println("VAR=" + vt + "." + name + "=" + comp.variable(vt).get(name)); }
          catch (Exception ignored) {}
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
