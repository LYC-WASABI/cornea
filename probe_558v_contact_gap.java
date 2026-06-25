import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558v_contact_gap {
  private static void surface(
      Model model, String tag, String type, String expr) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset_gap_probe");
    model.result().numerical(tag)
        .selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    System.out.println(tag + "|" + expr + "|"
        + Arrays.deepToString(
            model.result().numerical(tag).getReal()));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("PAIRS=" + Arrays.toString(comp.pair().tags()));
      for (String tag : comp.pair().tags()) {
        Pair pair = comp.pair(tag);
        System.out.println("PAIR|" + tag + "|" + pair.label()
            + "|type=" + pair.type());
        try {
          System.out.println("SOURCE|"
              + Arrays.toString(pair.source().entities()));
          System.out.println("DESTINATION|"
              + Arrays.toString(pair.destination().entities()));
        } catch (Exception error) {
          System.out.println("ENTITY_ERROR|" + error.getMessage());
        }
        try { System.out.println("PAIR_NAME|" + pair.pairName()); }
        catch (Exception ignored) {}
        try { System.out.println("MAPPING|" + pair.mapping()); }
        catch (Exception ignored) {}
        try { System.out.println("GAP_DST|" + pair.gapName(true)); }
        catch (Exception error) {
          System.out.println("GAP_DST_ERROR|" + error.getMessage());
        }
        try { System.out.println("GAP_SRC|" + pair.gapName(false)); }
        catch (Exception error) {
          System.out.println("GAP_SRC_ERROR|" + error.getMessage());
        }
        try {
          System.out.println("IN_CONTACT|" + pair.inContactName());
        } catch (Exception error) {
          System.out.println("IN_CONTACT_ERROR|" + error.getMessage());
        }
      }
      System.out.println("DCNT_PROPS=" + Arrays.toString(
          comp.physics("solid").feature("dcnt1").properties()));
      for (String prop :
          comp.physics("solid").feature("dcnt1").properties()) {
        try {
          String[] values = comp.physics("solid").feature("dcnt1")
              .getStringArray(prop);
          if (values.length > 0) {
            System.out.println("DCNT_PROP|" + prop + "|"
                + Arrays.toString(values));
          }
        } catch (Exception ignored) {}
      }

      try { model.result().dataset().remove("dset_gap_probe"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_gap_probe", "Solution");
      model.result().dataset("dset_gap_probe").set("solution", "sol91");

      List<String> expressions = new ArrayList<>();
      for (String pairTag : comp.pair().tags()) {
        Pair pair = comp.pair(pairTag);
        try { expressions.add(pair.gapName(true)); }
        catch (Exception ignored) {}
        try { expressions.add(pair.gapName(false)); }
        catch (Exception ignored) {}
        try { expressions.add(pair.inContactName()); }
        catch (Exception ignored) {}
      }
      expressions.add("solid.Tn");
      expressions.add("h_geom555");
      int index = 0;
      for (String expr : expressions) {
        try { surface(model, "min_gap_p" + (++index), "MinSurface", expr); }
        catch (Exception error) {
          System.out.println("MIN_ERROR|" + expr + "|"
              + error.getMessage());
        }
        try { surface(model, "max_gap_p" + (++index), "MaxSurface", expr); }
        catch (Exception error) {
          System.out.println("MAX_ERROR|" + expr + "|"
              + error.getMessage());
        }
        try { surface(model, "avg_gap_p" + (++index), "AvSurface", expr); }
        catch (Exception error) {
          System.out.println("AVG_ERROR|" + expr + "|"
              + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
