import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_select_inset_patch {
  private static void removeSelection(ModelNode component, String tag) {
    try { component.selection().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "probe_stage574_inset_lid_faces.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");

      removeSelection(comp, "sel_lid_source_full574");
      comp.selection().create("sel_lid_source_full574", "Explicit");
      comp.selection("sel_lid_source_full574").geom("geom1", 2);
      comp.selection("sel_lid_source_full574").set(
          pair.source().entities());

      removeSelection(comp, "sel_lid_film_inset574");
      comp.selection().create("sel_lid_film_inset574", "Box");
      comp.selection("sel_lid_film_inset574").label(
          "Stage 574 inset lid-attached film patch");
      comp.selection("sel_lid_film_inset574").set("entitydim", "2");
      comp.selection("sel_lid_film_inset574").set(
          "inputent", "selections");
      comp.selection("sel_lid_film_inset574").set(
          "input", new String[] {"sel_lid_source_full574"});
      comp.selection("sel_lid_film_inset574").set(
          "xmin", "-film_half_x574-0.005[mm]");
      comp.selection("sel_lid_film_inset574").set(
          "xmax", "film_half_x574+0.005[mm]");
      comp.selection("sel_lid_film_inset574").set(
          "ymin", "-10[mm]");
      comp.selection("sel_lid_film_inset574").set(
          "ymax", "10[mm]");
      comp.selection("sel_lid_film_inset574").set("zmin", "-10[mm]");
      comp.selection("sel_lid_film_inset574").set("zmax", "10[mm]");
      comp.selection("sel_lid_film_inset574").set("condition", "inside");

      System.out.println("FULL_SOURCE=" + Arrays.toString(
          comp.selection("sel_lid_source_full574").entities(2)));
      System.out.println("INSET_FILM=" + Arrays.toString(
          comp.selection("sel_lid_film_inset574").entities(2)));
      comp.mesh("mesh1").run();
      System.out.println("MESH=PASS");
      model.save("probe_stage574_select_inset_patch.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
