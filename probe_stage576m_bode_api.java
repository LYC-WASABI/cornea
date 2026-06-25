import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576m_bode_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.create("Probe");
      model.component().create("comp1", true);
      model.component("comp1").geom().create("geom1", 3);
      String[] candidates = new String[] {
        "BoundaryODE", "BoundaryODEsAndDAEs", "DistributedODE"
      };
      for (String type : candidates) {
        try {
          String tag = "p" + Math.abs(type.hashCode());
          model.component("comp1").physics().create(tag, type, "geom1");
          Physics physics = model.component("comp1").physics(tag);
          System.out.println("CREATED type=" + type + " tag=" + tag);
          System.out.println("FEATURES=" + Arrays.toString(physics.feature().tags()));
          System.out.println("FIELDS=" + Arrays.toString(physics.field().tags()));
          for (String feature : physics.feature().tags()) {
            System.out.println("FEATURE=" + feature
                + " PROPERTIES=" + Arrays.toString(physics.feature(feature).properties()));
          }
          for (String field : physics.field().tags()) {
            System.out.println("FIELD=" + field
                + " COMPONENTS=" + Arrays.toString(physics.field(field).component()));
          }
          break;
        } catch (Exception error) {
          System.out.println("FAILED type=" + type + " message=" + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
