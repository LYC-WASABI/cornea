import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_tff_edge_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576p2r_stage576_moving_structure_sparse_jfo_results.mph");
      ModelNode comp = model.component("comp1");
      for (String tag : comp.physics("tff").feature().tags()) {
        PhysicsFeature feature = comp.physics("tff").feature(tag);
        System.out.println("TFF_FEATURE tag=" + tag + " type="
            + feature.getType() + " active=" + feature.isActive()
            + " label=" + feature.label());
        try { System.out.println("  named=" + feature.selection().named()); }
        catch (Exception ignored) {}
        try { System.out.println("  entities="
            + Arrays.toString(feature.selection().entities())); }
        catch (Exception ignored) {}
        if (tag.startsWith("bdr_")) {
          for (String property : feature.properties()) {
            try {
              String[] value = feature.getStringArray(property);
              if (value.length > 0) System.out.println("  prop=" + property
                  + " value=" + Arrays.toString(value));
            } catch (Exception ignored) {}
          }
        }
      }
      for (String tag : new String[] {
          "sel_film_inlet571", "sel_film_outlet571",
          "sel_film_side_left571", "sel_film_side_right571"
      }) {
        System.out.println("EDGE_SELECTION tag=" + tag + " entities="
            + Arrays.toString(comp.selection(tag).entities(1)));
      }
      PhysicsFeature continuity = comp.physics("tff").feature("dcont1");
      for (String property : continuity.properties()) {
        try { System.out.println("DCONT_PROP " + property + "="
            + Arrays.toString(continuity.getStringArray(property))); }
        catch (Exception ignored) {}
      }
      for (String tag : comp.pair().tags()) {
        Pair pair = comp.pair(tag);
        System.out.println("PAIR tag=" + tag + " type=" + pair.type()
            + " source=" + Arrays.toString(pair.source().entities())
            + " destination=" + Arrays.toString(pair.destination().entities()));
      }
      try { comp.selection().remove("sel576p2ExteriorEdges"); }
      catch (Exception ignored) {}
      comp.selection().create("sel576p2ExteriorEdges", "Adjacent");
      comp.selection("sel576p2ExteriorEdges").set("entitydim", "2");
      comp.selection("sel576p2ExteriorEdges").set("outputdim", "1");
      comp.selection("sel576p2ExteriorEdges").set(
          "input", new String[] {"sel_film_swept571"});
      comp.selection("sel576p2ExteriorEdges").set("exterior", "on");
      System.out.println("ADJ_EXTERIOR=" + Arrays.toString(
          comp.selection("sel576p2ExteriorEdges").entities(1)));
      comp.selection("sel576p2ExteriorEdges").set("exterior", "off");
      System.out.println("ADJ_ALL=" + Arrays.toString(
          comp.selection("sel576p2ExteriorEdges").entities(1)));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
