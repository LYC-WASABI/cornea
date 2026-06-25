import com.comsol.model.*;
import com.comsol.model.util.*;
import java.lang.reflect.*;

public class probe_table_methods {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\177_lid8mm_stage99_lid_pressure_time_results.mph");
      try { model.result().table().remove("tbl_probe_methods"); } catch (Exception ignore) {}
      model.result().table().create("tbl_probe_methods", "Table");
      Object tbl = model.result().table("tbl_probe_methods");
      for (Method m : tbl.getClass().getMethods()) {
        String name = m.getName().toLowerCase();
        if (name.contains("table") || name.contains("row") || name.contains("data") || name.contains("header")) {
          System.out.println(m.toString());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
