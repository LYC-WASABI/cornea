import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage200_full_inventory {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
    String comp = "comp1";
    System.out.println("GEOM=" + Arrays.toString(
        model.component(comp).geom("geom1").feature().tags()));
    System.out.println("SELECTIONS=" + Arrays.toString(
        model.component(comp).selection().tags()));
    System.out.println("COUPLINGS=" + Arrays.toString(
        model.component(comp).cpl().tags()));
    for (String tag : model.component(comp).cpl().tags()) {
      try {
        System.out.println("CPL " + tag + " TYPE="
            + model.component(comp).cpl(tag).getType()
            + " SEL=" + Arrays.toString(
                model.component(comp).cpl(tag).selection().entities()));
      } catch (Exception error) {
        System.out.println("CPL " + tag + " ERROR=" + error.getMessage());
      }
    }
    System.out.println("PHYSICS=" + Arrays.toString(
        model.component(comp).physics().tags()));
    for (String ptag : model.component(comp).physics().tags()) {
      System.out.println("PHYS " + ptag + " FEATURES=" + Arrays.toString(
          model.component(comp).physics(ptag).feature().tags()));
      try {
        System.out.println("PHYS " + ptag + " SEL="
            + Arrays.toString(
                model.component(comp).physics(ptag).selection().entities()));
      } catch (Exception ignored) {}
    }
    System.out.println("STUDIES=" + Arrays.toString(model.study().tags()));
    System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
    System.out.println("DATASETS=" + Arrays.toString(
        model.result().dataset().tags()));
    System.out.println("NUMERICAL=" + Arrays.toString(
        model.result().numerical().tags()));
    ModelUtil.disconnect();
  }
}
