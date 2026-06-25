import com.comsol.model.*;
import com.comsol.model.util.*;
import java.lang.reflect.*;
import java.util.*;

public class probe_stage574_mesh_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "probe_stage574_select_inset_patch.mph");
      Object mesh = model.component("comp1").mesh("mesh1");
      Object geometry = model.component("comp1").geom("geom1");
      Object selection = model.component("comp1")
          .selection("sel_lid_source_full574");
      for (Object target : new Object[] {mesh, geometry, selection}) {
        System.out.println("CLASS=" + target.getClass().getName());
        for (Method method : target.getClass().getMethods()) {
          String name = method.getName().toLowerCase(Locale.ROOT);
          if (name.contains("vert") || name.contains("coord")
              || name.contains("bound") || name.contains("entity")
              || name.contains("element") || name.contains("elem")) {
            System.out.println("  " + method.toString());
          }
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
