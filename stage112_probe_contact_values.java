import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage112_probe_contact_values {
  private static void show(Model m, String f, String p) {
    try {
      System.out.println(f + "." + p + " = " + m.component("comp1").physics("solid").feature(f).getString(p));
    } catch (Exception e1) {
      try {
        String[] vals = m.component("comp1").physics("solid").feature(f).getStringArray(p);
        System.out.print(f + "." + p + " = [");
        for (int i = 0; i < vals.length; i++) System.out.print((i == 0 ? "" : ", ") + vals[i]);
        System.out.println("]");
      } catch (Exception e2) {
        System.out.println(f + "." + p + " = <unreadable>");
      }
    }
  }
  private static void showChild(Model m, String f, String ch, String p) {
    try {
      System.out.println(f + "/" + ch + "." + p + " = "
          + m.component("comp1").physics("solid").feature(f).feature(ch).getString(p));
    } catch (Exception e1) {
      try {
        String[] vals = m.component("comp1").physics("solid").feature(f).feature(ch).getStringArray(p);
        System.out.print(f + "/" + ch + "." + p + " = [");
        for (int i = 0; i < vals.length; i++) System.out.print((i == 0 ? "" : ", ") + vals[i]);
        System.out.println("]");
      } catch (Exception e2) {
        System.out.println(f + "/" + ch + "." + p + " = <unreadable>");
      }
    }
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "189_lid8mm_stage111_short_strict_total_load_results_Model.mph");
      String[] props = {"ContactMethodCtrl", "SolutionMethod", "penaltyCtrlPenalty", "fp_penalty",
          "fp_init_penalty", "fp_fin_penalty", "pn_penalty", "pfm", "useRelaxation", "tolcontact"};
      for (String p : props) show(m, "dcnt1", p);
      String[] fricProps = {"FrictionModel", "mu_fric", "penaltyCtrlFrictionPenalty", "ft_penalty", "pt_penalty"};
      for (String p : fricProps) showChild(m, "dcnt1", "fric_partitioned_stabilizer", p);
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
