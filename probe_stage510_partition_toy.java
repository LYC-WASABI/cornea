import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_partition_toy {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.create("Model");
    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    model.component("comp1").geom("geom1").lengthUnit("mm");
    model.component("comp1").geom("geom1").create("sph1", "Sphere");
    model.component("comp1").geom("geom1").feature("sph1").set("r", "7.8");

    model.component("comp1").geom("geom1").create("wpxp", "WorkPlane");
    model.component("comp1").geom("geom1").feature("wpxp")
        .set("planetype", "quick");
    model.component("comp1").geom("geom1").feature("wpxp")
        .set("quickplane", "yz");
    model.component("comp1").geom("geom1").feature("wpxp")
        .set("quickx", "1");

    model.component("comp1").geom("geom1").create("pdxp", "PartitionDomains");
    model.component("comp1").geom("geom1").feature("pdxp")
        .selection("domain").set("sph1", 1);
    model.component("comp1").geom("geom1").feature("pdxp")
        .set("partitionwith", "workplane");
    model.component("comp1").geom("geom1").feature("pdxp")
        .set("workplane", "wpxp");
    model.component("comp1").geom("geom1").run();

    for (String tag : new String[] {"wpxp", "pdxp"}) {
      System.out.println(tag + " props=" + Arrays.toString(
          model.component("comp1").geom("geom1").feature(tag).properties()));
      for (String p : model.component("comp1").geom("geom1")
          .feature(tag).properties()) {
        try {
          System.out.println("  " + p + "=" + model.component("comp1")
              .geom("geom1").feature(tag).getString(p));
        } catch (Exception ignored) {}
      }
    }
    model.save("probe_stage510_partition_toy.mph");
    ModelUtil.disconnect();
  }
}
