import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class inspect_stage100_force_control_context {
  private static void printFeature(Model model, String phys, String feat) {
    try {
      System.out.println("FEATURE " + phys + "/" + feat + " type=" +
          model.component("comp1").physics(phys).feature(feat).getType() +
          " label=" + model.component("comp1").physics(phys).feature(feat).label());
      for (String p : model.component("comp1").physics(phys).feature(feat).properties()) {
        try {
          System.out.println("  " + p + "=" + model.component("comp1").physics(phys).feature(feat).getString(p));
        } catch (Exception ignore) {
          try { System.out.println("  " + p + "=" + Arrays.toString(model.component("comp1").physics(phys).feature(feat).getStringArray(p))); }
          catch (Exception ignore2) {}
        }
      }
    } catch (Exception e) {
      System.out.println("FEATURE " + phys + "/" + feat + " missing: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      System.out.println("PHYSICS=" + Arrays.toString(model.component("comp1").physics().tags()));
      for (String ph : model.component("comp1").physics().tags()) {
        System.out.println("PHYS " + ph + " label=" + model.component("comp1").physics(ph).label());
        System.out.println("  FEATURES=" + Arrays.toString(model.component("comp1").physics(ph).feature().tags()));
      }
      System.out.println("VARIABLES=" + Arrays.toString(model.component("comp1").variable().tags()));
      for (String vtag : model.component("comp1").variable().tags()) {
        System.out.println("VAR " + vtag + " label=" + model.component("comp1").variable(vtag).label());
        for (String name : new String[]{"dr_force_closed_loop","dr_force_reaction28","dr_force_sched","phi_lid_structure","W_film_replay","W_film","F_total_target","q_force","gap_smooth_replay_tear"}) {
          try { System.out.println("  " + name + "=" + model.component("comp1").variable(vtag).get(name)); } catch (Exception ignore) {}
        }
      }
      printFeature(model, "solid", "disp_lid_time");
      printFeature(model, "solid", "load_partitioned_pfilm");
      printFeature(model, "solid", "dcnt1");
      try {
        System.out.println("PAIR cp_lid_cornea source=" + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
        System.out.println("PAIR cp_lid_cornea dest=" + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
      } catch (Exception e) { System.out.println("PAIR ERR " + e.getMessage()); }
      System.out.println("STUDIES=" + Arrays.toString(model.study().tags()));
      for (String st : model.study().tags()) {
        System.out.println("STUDY " + st + " label=" + model.study(st).label() + " features=" + Arrays.toString(model.study(st).feature().tags()));
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
