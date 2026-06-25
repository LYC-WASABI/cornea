import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage510_local_film_track {
  static int[] get(Model model, String tag, int dim) {
    return model.component("comp1").selection(tag).entities(dim);
  }

  static void expectAtLeast(Model model, String tag, int dim, int count) {
    int[] ids = get(model, tag, dim);
    System.out.println(tag + "=" + Arrays.toString(ids));
    if (ids.length < count) {
      throw new IllegalStateException(tag + " count=" + ids.length);
    }
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "513_stage510_local_film_track_checked.mph");
    if (Math.abs(model.param().evaluate("stage500_revision") - 500) > 0.1) {
      throw new IllegalStateException("Stage 500 parent metadata missing");
    }
    if (!Arrays.asList(model.result().numerical().tags())
        .contains("eval500_audit")) {
      throw new IllegalStateException("Stage 500 audit node missing");
    }
    expectAtLeast(model, "sel_film_track", 2, 1);
    expectAtLeast(model, "sel_film_surface_tool", 2, 1);
    expectAtLeast(model, "sel_film_edges_all", 1, 4);
    expectAtLeast(model, "sel_film_inlet", 1, 1);
    expectAtLeast(model, "sel_film_outlet", 1, 1);
    expectAtLeast(model, "sel_film_side_left", 1, 1);
    expectAtLeast(model, "sel_film_side_right", 1, 1);
    expectAtLeast(model, "sel_cornea_contact_target", 2, 4);
    if (!Arrays.asList(model.component("comp1").pair().tags())
        .contains("cp_lid_cornea")) {
      throw new IllegalStateException("cp_lid_cornea missing");
    }
    int[] domains =
        model.component("comp1").physics("solid").selection().entities();
    System.out.println("solid_domains=" + Arrays.toString(domains));
    if (domains.length != 2) {
      throw new IllegalStateException("solid domain count changed");
    }
    System.out.println("geom_message="
        + model.component("comp1").geom("geom1").feature("fin")
            .getString("buildmessage"));
    System.out.println("mesh_features=" + Arrays.toString(
        model.component("comp1").mesh("mesh1").feature().tags()));
    System.out.println("theta_max_deg="
        + model.param().evaluate("film_track_theta_max") * 180 / Math.PI);
    double halfWidth =
        model.param().evaluate("film_track_half_width") * 1000;
    double totalWidth = 2*halfWidth;
    System.out.println("film_track_half_width_mm=" + halfWidth);
    System.out.println("film_track_total_width_mm=" + totalWidth);
    if (Math.abs(halfWidth - 4.5) > 1e-6) {
      throw new IllegalStateException(
          "Corrected film-track half width is not 4.5 mm");
    }
    System.out.println("parent_stage="
        + model.param().evaluate("stage500_revision"));
    System.out.println("STAGE510_RELOAD_VALIDATION=PASS");
    ModelUtil.disconnect();
  }
}
