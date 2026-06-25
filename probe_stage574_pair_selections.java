import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_pair_selections {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574b_stage574_inset_source_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      for (String tag : new String[] {
          "sel_lid_wiper_inner_surface_dyn",
          "sel_cornea_anterior_surface",
          "sel_lid_source_full574"}) {
        System.out.println(tag + "=" + Arrays.toString(
            comp.selection(tag).entities(2)));
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
