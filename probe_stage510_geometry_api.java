import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_geometry_api {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");
    System.out.println("PARAMETERS");
    for (String name : model.param().varnames()) {
      if (name.toLowerCase(Locale.ROOT).contains("cor")
          || name.toLowerCase(Locale.ROOT).contains("lid")
          || name.toLowerCase(Locale.ROOT).contains("radius")
          || name.toLowerCase(Locale.ROOT).contains("theta")) {
        System.out.println("  " + name + "=" + model.param().get(name));
      }
    }

    String[] inspect = {
      "sph_cor_outer", "sph_cor_inner", "dif_cor_shell",
      "cyl_cor_cap", "int_cornea", "fin"
    };
    for (String tag : inspect) {
      try {
        System.out.println("FEATURE " + tag + " PROPS="
            + Arrays.toString(model.component("comp1").geom("geom1")
                .feature(tag).properties()));
        for (String prop : model.component("comp1").geom("geom1")
            .feature(tag).properties()) {
          try {
            System.out.println("  " + prop + "="
                + model.component("comp1").geom("geom1").feature(tag)
                    .getString(prop));
          } catch (Exception ignored) {}
        }
      } catch (Exception error) {
        System.out.println("FEATURE " + tag + " ERROR=" + error.getMessage());
      }
    }

    try {
      model.component("comp1").geom("geom1")
          .create("wp510probe", "WorkPlane");
      System.out.println("WORKPLANE PROPS=" + Arrays.toString(
          model.component("comp1").geom("geom1").feature("wp510probe")
              .properties()));
      System.out.println("WORKPLANE GEOM FEATURES="
          + Arrays.toString(model.component("comp1").geom("geom1")
              .feature("wp510probe").geom().feature().tags()));
    } catch (Exception error) {
      System.out.println("WORKPLANE ERROR=" + error);
    }

    try {
      model.component("comp1").geom("geom1")
          .create("pd510probe", "PartitionDomains");
      System.out.println("PARTITION PROPS=" + Arrays.toString(
          model.component("comp1").geom("geom1").feature("pd510probe")
              .properties()));
      System.out.println("PARTITION INPUT=" + model.component("comp1")
          .geom("geom1").feature("pd510probe").selection("input"));
    } catch (Exception error) {
      System.out.println("PARTITION ERROR=" + error);
    }

    try {
      model.component("comp1").geom("geom1")
          .create("ps510probe", "PartitionSurfaces");
      System.out.println("PARTITION_SURFACES PROPS=" + Arrays.toString(
          model.component("comp1").geom("geom1").feature("ps510probe")
              .properties()));
    } catch (Exception error) {
      System.out.println("PARTITION_SURFACES ERROR=" + error);
    }
    ModelUtil.disconnect();
  }
}
