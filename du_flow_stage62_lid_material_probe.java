import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_stage62_lid_material_probe {
  private static void prop(Model m, String mat, String pg, String name) {
    try {
      System.out.println("PROP " + mat + "/" + pg + " " + name + "="
          + Arrays.toString(m.component("comp1").material(mat).propertyGroup(pg).getStringArray(name)));
    } catch (Exception ex) {
      try {
        System.out.println("PROP " + mat + "/" + pg + " " + name + "="
            + m.component("comp1").material(mat).propertyGroup(pg).getString(name));
      } catch (Exception ignored) {
        System.out.println("PROP " + mat + "/" + pg + " " + name + "=<missing>");
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    System.out.println("MATERIAL_TAGS=" + Arrays.toString(m.component("comp1").material().tags()));
    for (String mat : m.component("comp1").material().tags()) {
      System.out.println("MATERIAL " + mat + " label=" + m.component("comp1").material(mat).label()
          + " selection=" + Arrays.toString(m.component("comp1").material(mat).selection().entities()));
      System.out.println("PROPERTY_GROUPS=" + Arrays.toString(m.component("comp1").material(mat).propertyGroup().tags()));
      for (String pg : m.component("comp1").material(mat).propertyGroup().tags()) {
        prop(m, mat, pg, "youngsmodulus");
        prop(m, mat, pg, "poissonsratio");
        prop(m, mat, pg, "density");
      }
    }
    for (String p : new String[] {"Ecor", "nucor", "rhocor", "Elid", "nulid", "rholid"}) {
      try {
        System.out.println("PARAM " + p + "=" + m.param().get(p));
      } catch (Exception ex) {
        System.out.println("PARAM " + p + "=<missing>");
      }
    }
  }
}
