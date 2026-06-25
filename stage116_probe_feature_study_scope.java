import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage116_probe_feature_study_scope {
  private static void show(Model m, String phys, String feat) {
    try {
      System.out.println("FEATURE " + feat + " label="
          + m.component("comp1").physics(phys).feature(feat).label());
      for (String p : new String[]{"StudyStep", "pairSelection", "pairs", "pairDisconnect"}) {
        try {
          System.out.println("  " + p + "="
              + m.component("comp1").physics(phys).feature(feat).getString(p));
        } catch (Exception e1) {
          try {
            String[] a = m.component("comp1").physics(phys).feature(feat).getStringArray(p);
            System.out.print("  " + p + "=[");
            for (int i = 0; i < a.length; i++) System.out.print((i == 0 ? "" : ",") + a[i]);
            System.out.println("]");
          } catch (Exception ignore) {}
        }
      }
    } catch (Exception e) {
      System.out.println("ERROR " + feat + ": " + e.getMessage());
    }
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      show(m, "solid", "dcnt1");
      show(m, "solid", "disp_lid_time");
      show(m, "solid", "load_partitioned_pfilm");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
