import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage510_parametric_surface {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.create("Model");
    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    String[] types = {
      "ParametricSurface", "Parametric", "InterpolationSurface",
      "BezierSurface", "Surface"
    };
    for (String type : types) {
      String tag = "f" + type.toLowerCase(Locale.ROOT).replace("surface", "");
      try {
        model.component("comp1").geom("geom1").create(tag, type);
        System.out.println("CREATED " + type + " PROPS=" + Arrays.toString(
            model.component("comp1").geom("geom1").feature(tag).properties()));
      } catch (Exception error) {
        System.out.println("FAILED " + type + " " + error.getMessage());
      }
    }
    ModelUtil.disconnect();
  }
}
