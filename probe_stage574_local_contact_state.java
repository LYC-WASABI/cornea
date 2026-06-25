import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_local_contact_state {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574e_stage574_local_cornea_patch_geometry.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("PAIR_SOURCE="
          + Arrays.toString(
              comp.pair("cp_lid_cornea").source().entities()));
      System.out.println("PAIR_DESTINATION="
          + Arrays.toString(
              comp.pair("cp_lid_cornea").destination().entities()));
      System.out.println("CONTACT_FEATURE="
          + Arrays.toString(
              comp.physics("solid").feature("dcnt1")
                  .selection().entities()));
      System.out.println("INTOP_CONTACT="
          + Arrays.toString(
              comp.cpl("intop_contact").selection().entities()));
      System.out.println("CORNEA_ANTERIOR="
          + Arrays.toString(
              comp.selection("sel_cornea_anterior_surface").entities(2)));

      try { model.result().dataset().remove("dset574_contact_probe"); }
      catch (Exception ignored) {}
      model.result().dataset().create(
          "dset574_contact_probe", "Solution");
      model.result().dataset("dset574_contact_probe")
          .set("solution", "sol93");
      try { model.result().numerical().remove("eval574_contact_probe"); }
      catch (Exception ignored) {}
      model.result().numerical().create(
          "eval574_contact_probe", "EvalGlobal");
      model.result().numerical("eval574_contact_probe")
          .set("data", "dset574_contact_probe");
      model.result().numerical("eval574_contact_probe").set(
          "expr", new String[] {
            "q_force_total111", "dr_indent570",
            "Fn_contact570", "Ferr570", "q_ref555"
          });
      System.out.println("SOL93="
          + Arrays.deepToString(
              model.result().numerical("eval574_contact_probe")
                  .getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
