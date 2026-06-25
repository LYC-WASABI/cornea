import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage73_feature_probe {
  private static void dumpFeature(Model m, String phys, String feat) {
    try {
      System.out.println("## FEATURE " + phys + "/" + feat + " label=" +
          m.component("comp1").physics(phys).feature(feat).label());
      for (String p : m.component("comp1").physics(phys).feature(feat).properties()) {
        try {
          String[] vals = m.component("comp1").physics(phys).feature(feat).getStringArray(p);
          System.out.println(p + "=" + Arrays.toString(vals));
        } catch (Exception e1) {
          try {
            System.out.println(p + "=" + m.component("comp1").physics(phys).feature(feat).getString(p));
          } catch (Exception e2) {
            System.out.println(p + "=<non-string>");
          }
        }
      }
    } catch (Exception e) {
      System.out.println("ERR feature " + phys + "/" + feat + ": " + e.getMessage());
    }
  }

  private static void dumpSelection(Model m, String tag) {
    try {
      System.out.println("## SELECTION " + tag + " label=" + m.component("comp1").selection(tag).label());
      for (int dim = 0; dim <= 3; dim++) {
        try {
          int[] ents = m.component("comp1").selection(tag).entities(dim);
          System.out.println("dim" + dim + "=" + Arrays.toString(ents));
        } catch (Exception e) {
          System.out.println("dim" + dim + "=<err>");
        }
      }
    } catch (Exception e) {
      System.out.println("ERR selection " + tag + ": " + e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
    dumpFeature(m, "solid", "press_iop");
    dumpFeature(m, "solid", "load_partitioned_pfilm");
    dumpFeature(m, "solid", "disp_lid_time");
    dumpFeature(m, "solid", "dcnt1");
    dumpSelection(m, "sel_cornea_anterior_surface");
    dumpSelection(m, "sel_lid_contact_source_robust");
    dumpSelection(m, "sel_lid_outer_support");
  }
}
