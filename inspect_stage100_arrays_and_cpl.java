import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class inspect_stage100_arrays_and_cpl {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      System.out.println("CPL=" + Arrays.toString(model.component("comp1").cpl().tags()));
      for (String c : model.component("comp1").cpl().tags()) {
        System.out.println("CPL " + c + " type=" + model.component("comp1").cpl(c).getType() +
            " label=" + model.component("comp1").cpl(c).label());
        try { System.out.println("  selection named=" + model.component("comp1").cpl(c).selection().named()); } catch (Exception ignore) {}
        try { System.out.println("  entities=" + Arrays.toString(model.component("comp1").cpl(c).selection().entities())); } catch (Exception ignore) {}
      }
      String[] u0 = model.component("comp1").physics("solid").feature("disp_lid_time").getStringArray("U0");
      System.out.println("disp_lid_time U0 array len=" + u0.length + " " + Arrays.toString(u0));
      String[] fpa = model.component("comp1").physics("solid").feature("load_partitioned_pfilm").getStringArray("FperArea");
      System.out.println("load_partitioned_pfilm FperArea len=" + fpa.length + " " + Arrays.toString(fpa));
      String[] fra = model.component("comp1").physics("solid").feature("load_partitioned_pfilm").getStringArray("forceReferenceArea");
      System.out.println("load_partitioned_pfilm forceReferenceArea len=" + fra.length + " " + Arrays.toString(fra));
      for (String vtag : model.component("comp1").variable().tags()) {
        for (String name : new String[]{"pfilm_replay53","scale_partitioned_pfilm","slide_fraction_structure","theta_slide_total","t_film_replay","T_pre","T_slide"}) {
          try { System.out.println(vtag + " " + name + "=" + model.component("comp1").variable(vtag).get(name)); } catch (Exception ignore) {}
        }
      }
      for (String name : new String[]{"F_total_target","T_pre","T_slide","dr_force_init","q_force_init"}) {
        try { System.out.println("PARAM " + name + "=" + model.param().get(name)); } catch (Exception ignore) {}
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
