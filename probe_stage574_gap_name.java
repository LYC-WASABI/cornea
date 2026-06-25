import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_stage574_gap_name {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574b_stage574_inset_source_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      System.out.println("SOURCE_GAP=" + pair.gapName(false));
      System.out.println("DESTINATION_GAP=" + pair.gapName(true));
      System.out.println("STORED_GAP="
          + comp.variable("var_source_gap573").get("g_pair_raw573"));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
