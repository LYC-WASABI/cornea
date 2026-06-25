import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class audit_566c_dynamic_cleanup {
  private static void line(String kind, String tag, String label) {
    System.out.println(kind + "|" + tag + "|" + label);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "566c_stage569_true_pair_gap_checked.mph");
      ModelNode comp = model.component("comp1");

      System.out.println("MODEL|" + model.label());
      for (String tag : model.study().tags()) {
        line("STUDY", tag, model.study(tag).label());
      }
      for (String tag : model.sol().tags()) {
        String study = "";
        try { study = model.sol(tag).study(); } catch (Exception ignored) {}
        line("SOL", tag, study);
      }
      for (String tag : model.result().dataset().tags()) {
        String sol = "";
        try { sol = model.result().dataset(tag).getString("solution"); }
        catch (Exception ignored) {}
        line("DATASET", tag,
            model.result().dataset(tag).label() + "|solution=" + sol);
      }
      for (String tag : model.result().tags()) {
        line("PLOT", tag, model.result(tag).label());
      }
      for (String tag : model.result().numerical().tags()) {
        line("NUM", tag, model.result().numerical(tag).label());
      }
      for (String tag : model.result().table().tags()) {
        line("TABLE", tag, model.result().table(tag).label());
      }
      for (String tag : comp.variable().tags()) {
        line("VARIABLE", tag, comp.variable(tag).label());
        for (String name : comp.variable(tag).varnames()) {
          String low = name.toLowerCase(Locale.ROOT);
          if (low.contains("555") || low.contains("566")
              || low.contains("567") || low.contains("568")
              || low.contains("569") || low.contains("feedback")) {
            System.out.println("VAR|" + tag + "|" + name + "|"
                + comp.variable(tag).get(name));
          }
        }
      }
      for (String tag : comp.physics().tags()) {
        line("PHYSICS", tag, comp.physics(tag).label());
        for (String feature : comp.physics(tag).feature().tags()) {
          line("FEATURE", tag + "/" + feature,
              comp.physics(tag).feature(feature).label());
        }
      }
      for (String tag : comp.cpl().tags()) {
        line("CPL", tag, comp.cpl(tag).label());
      }
      for (String tag : comp.selection().tags()) {
        String label = "";
        try { label = comp.selection(tag).label(); }
        catch (Exception ignored) {}
        if (tag.contains("film") || tag.contains("cornea")
            || tag.contains("lid")) {
          line("SELECTION", tag, label);
        }
      }
      System.out.println("PAIR_TAGS="
          + Arrays.toString(comp.pair().tags()));
      System.out.println("TFF_HW1="
          + comp.physics("tff").feature("ffp1").getString("hw1"));
      System.out.println("FILM_LOAD="
          + Arrays.toString(comp.physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
