import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage161_tff_inventory {
  private static void dump(Model m, String feat) {
    System.out.println("FEATURE tff/" + feat + " label="
        + m.component("comp1").physics("tff").feature(feat).label());
    for (String p : m.component("comp1").physics("tff").feature(feat).properties()) {
      try {
        System.out.println(p + "=" + Arrays.toString(
            m.component("comp1").physics("tff").feature(feat).getStringArray(p)));
      } catch (Exception e1) {
        try { System.out.println(p + "="
            + m.component("comp1").physics("tff").feature(feat).getString(p)); }
        catch (Exception e2) {}
      }
    }
  }
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
    System.out.println("TFF FEATURES=" + Arrays.toString(
        m.component("comp1").physics("tff").feature().tags()));
    for (String f : m.component("comp1").physics("tff").feature().tags()) dump(m, f);
    System.out.println("STUDIES");
    for (String st : m.study().tags())
      System.out.println(st + " label=" + m.study(st).label()
          + " features=" + Arrays.toString(m.study(st).feature().tags()));
    for (String vt : m.component("comp1").variable().tags()) {
      for (String n : new String[]{"vwall_x", "vwall_y", "vwall_z",
          "lid_mask", "lid_mask_length", "lid_mask_width", "h_film_input",
          "omega_lid", "W_film"}) {
        try {
          String x = m.component("comp1").variable(vt).get(n);
          if (x != null && !x.isEmpty()) System.out.println(vt+" "+n+"="+x);
        } catch(Exception ignore) {}
      }
    }
    ModelUtil.disconnect();
  }
}
