import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage128_contact_tracking {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "220_lid8mm_stage128_algebraic_exact_load_30pct_setup_Model.mph");
    System.out.println("## CONTACT");
    for (String p : m.component("comp1").physics("solid").feature("dcnt1").properties()) {
      String value = "";
      try { value = m.component("comp1").physics("solid").feature("dcnt1").getString(p); }
      catch (Exception ignore) {}
      if (value.isEmpty()) {
        try { value = Arrays.toString(m.component("comp1").physics("solid")
            .feature("dcnt1").getStringArray(p)); } catch (Exception ignore) {}
      }
      System.out.println(p + "=" + value);
      try {
        String[] allowed = m.component("comp1").physics("solid").feature("dcnt1")
            .getAllowedPropertyValues(p);
        if (allowed != null && allowed.length > 0)
          System.out.println("  allowed=" + Arrays.toString(allowed));
      } catch (Exception ignore) {}
    }
    System.out.println("## PAIRS");
    for (String tag : m.component("comp1").pair().tags())
      System.out.println("PAIR " + tag + " label=" + m.component("comp1").pair(tag).label());
    ModelUtil.disconnect();
  }
}
