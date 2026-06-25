import com.comsol.model.*;
import com.comsol.model.util.*;

public class comsol_probe {
  public static Model run() {
    Model model = ModelUtil.create("Model");
    model.label("comsol_probe.mph");
    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    model.component("comp1").geom("geom1").lengthUnit("mm");
    model.component("comp1").geom("geom1").create("sph1", "Sphere");
    model.component("comp1").geom("geom1").feature("sph1").set("r", "1[mm]");
    model.component("comp1").geom("geom1").run();
    return model;
  }

  public static void main(String[] args) {
    run();
  }
}
