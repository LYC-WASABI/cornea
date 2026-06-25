import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_domain_selection_materials {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "503_stage500_baseline_checked.mph");
    for (String tag : new String[] {"sel_cornea_dom"}) {
      System.out.println(tag + " TYPE="
          + model.component("comp1").selection(tag).getType()
          + " ENT=" + Arrays.toString(
              model.component("comp1").selection(tag).entities(3)));
      for (String p : model.component("comp1").selection(tag).properties()) {
        try { System.out.println("  " + p + "="
            + model.component("comp1").selection(tag).getString(p)); }
        catch (Exception ignored) {}
      }
    }
    System.out.println("MATERIALS=" + Arrays.toString(
        model.component("comp1").material().tags()));
    for (String tag : model.component("comp1").material().tags()) {
      System.out.println(tag + " SEL=" + Arrays.toString(
          model.component("comp1").material(tag).selection().entities()));
    }
    ModelUtil.disconnect();
  }
}
