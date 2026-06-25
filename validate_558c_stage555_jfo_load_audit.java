import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_558c_stage555_jfo_load_audit {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558c_stage555_JFO_load_audit.mph");
      System.out.println("INTOP=" + Arrays.toString(
          model.component("comp1").cpl("intop_film")
              .selection().entities()));
      System.out.println("TRACK=" + Arrays.toString(
          model.component("comp1").selection("sel_film_track")
              .entities(2)));
      System.out.println("WFILM=" + model.component("comp1")
          .variable("var_load_coupled555").get("Wfilm555"));
      System.out.println("LOAD=" + Arrays.toString(
          model.component("comp1").physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      System.out.println("AUDIT=" + Arrays.deepToString(
          model.result().numerical("eval558c_load_audit").getReal()));
      System.out.println("TABLE="
          + model.result().table("tbl558c_load_audit").label());
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
