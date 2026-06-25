import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage569_true_pair_gap {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "566c_stage569_true_pair_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("MODEL=" + model.label());
      System.out.println("PAIR_MAPPING="
          + comp.pair("cp_lid_cornea").mapping());
      System.out.println("PAIR_GAP="
          + comp.pair("cp_lid_cornea").gapName(true));
      System.out.println("VARIABLE_SELECTION="
          + comp.variable("var_true_pair_gap569").selection().named());
      for (String name : new String[] {
          "pair_gap_native569", "pair_map_valid569",
          "h_pair_raw569", "h_pair_regular569",
          "h_pair_penetration569", "pair_contact_state569",
          "h_proxy_error569"
      }) {
        System.out.println("VAR|" + name + "|"
            + comp.variable("var_true_pair_gap569").get(name));
      }
      System.out.println("TFF_HW1="
          + comp.physics("tff").feature("ffp1").getString("hw1"));
      System.out.println("FILM_LOAD="
          + Arrays.toString(comp.physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      System.out.println("DATASET="
          + model.result().dataset("dset569_pair_gap").getString("solution"));
      System.out.println("PLOTS="
          + Arrays.toString(model.result().tags()));

      try { model.result().numerical().remove("eval569_verify_load"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval569_verify_load", "EvalGlobal");
      model.result().numerical("eval569_verify_load")
          .set("data", "dset569_pair_gap");
      model.result().numerical("eval569_verify_load").set(
          "expr", new String[] {
            "Wfilm567", "Fn_contact119", "Ftotal567",
            "Ferr567", "dr_indent119", "q_force_total111"
          });
      System.out.println("LOAD_BALANCE=" + Arrays.deepToString(
          model.result().numerical("eval569_verify_load").getReal()));
      System.out.println("PAIR_DIAGNOSTICS=" + Arrays.deepToString(
          model.result().numerical("eval569_pair_gap").getReal()));
      System.out.println("MIN_RAW=" + Arrays.deepToString(
          model.result().numerical("min569_pair_gap").getReal()));
      System.out.println("MAX_RAW=" + Arrays.deepToString(
          model.result().numerical("max569_pair_gap").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
