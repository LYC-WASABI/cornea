import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_four_partition_toy {
  static void wp(Model model, String tag, String plane, String offset) {
    model.component("comp1").geom("geom1").create(tag, "WorkPlane");
    model.component("comp1").geom("geom1").feature(tag)
        .set("planetype", "quick");
    model.component("comp1").geom("geom1").feature(tag)
        .set("quickplane", plane);
    if ("yz".equals(plane)) {
      model.component("comp1").geom("geom1").feature(tag)
          .set("quickx", offset);
    }
  }

  static void generalWp(
      Model model, String tag, String ny, String nz) {
    model.component("comp1").geom("geom1").create(tag, "WorkPlane");
    model.component("comp1").geom("geom1").feature(tag)
        .set("planetype", "general");
    model.component("comp1").geom("geom1").feature(tag)
        .set("normalvector", new String[] {"0", ny, nz});
    model.component("comp1").geom("geom1").feature(tag)
        .set("normalpoint", "coord");
    model.component("comp1").geom("geom1").feature(tag)
        .set("normalcoord", new String[] {"0", "0", "0"});
  }

  static void partition(
      Model model, String tag, String input, int count, String wp) {
    model.component("comp1").geom("geom1").create(tag, "PartitionDomains");
    int[] ids = new int[count];
    for (int i = 0; i < count; i++) ids[i] = i + 1;
    model.component("comp1").geom("geom1").feature(tag)
        .selection("domain").set(input, ids);
    model.component("comp1").geom("geom1").feature(tag)
        .set("partitionwith", "workplane");
    model.component("comp1").geom("geom1").feature(tag)
        .set("workplane", wp);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.create("Model");
    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    model.component("comp1").geom("geom1").lengthUnit("mm");
    model.component("comp1").geom("geom1").create("sph", "Sphere");
    model.component("comp1").geom("geom1").feature("sph").set("r", "7.8");
    wp(model, "wpxn", "yz", "-1");
    partition(model, "p1", "sph", 1, "wpxn");
    wp(model, "wpxp", "yz", "1");
    partition(model, "p2", "p1", 2, "wpxp");
    generalWp(model, "wptn", "cos(40[deg])", "sin(40[deg])");
    partition(model, "p3", "p2", 3, "wptn");
    generalWp(model, "wptp", "cos(40[deg])", "-sin(40[deg])");
    model.component("comp1").geom("geom1").create("p4", "PartitionDomains");
    model.component("comp1").geom("geom1").feature("p4")
        .selection("domain").all();
    model.component("comp1").geom("geom1").feature("p4")
        .set("partitionwith", "workplane");
    model.component("comp1").geom("geom1").feature("p4")
        .set("workplane", "wptp");
    model.component("comp1").geom("geom1").run();
    for (String tag : new String[] {"wptn", "wptp"}) {
      System.out.println(tag + " planetype=" + model.component("comp1")
          .geom("geom1").feature(tag).getString("planetype"));
      System.out.println(tag + " normal=" + Arrays.toString(model.component("comp1")
          .geom("geom1").feature(tag).getStringArray("normalvector")));
    }
    System.out.println("FINAL=" + model.component("comp1").geom("geom1")
        .feature("fin").getString("buildmessage"));
    model.save("probe_stage510_four_partition_toy.mph");
    ModelUtil.disconnect();
  }
}
