import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage73_inventory {
  private static void printTags(String title, String[] tags) {
    System.out.println("## " + title);
    for (String tag : tags) System.out.println(tag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
    printTags("component tags", m.component().tags());
    printTags("comp1 selection tags", m.component("comp1").selection().tags());
    for (String s : m.component("comp1").selection().tags()) {
      try {
        System.out.println("SEL " + s + " label=" + m.component("comp1").selection(s).label());
      } catch (Exception e) {
        System.out.println("SEL " + s + " label=<err>");
      }
    }
    printTags("comp1 variable tags", m.component("comp1").variable().tags());
    for (String v : m.component("comp1").variable().tags()) {
      try {
        System.out.println("VAR " + v + " label=" + m.component("comp1").variable(v).label());
      } catch (Exception e) {
        System.out.println("VAR " + v + " label=<err>");
      }
    }
    printTags("physics tags", m.component("comp1").physics().tags());
    for (String p : m.component("comp1").physics().tags()) {
      System.out.println("PHYS " + p + " label=" + m.component("comp1").physics(p).label());
      printTags("features of " + p, m.component("comp1").physics(p).feature().tags());
      for (String f : m.component("comp1").physics(p).feature().tags()) {
        try {
          System.out.println("FEAT " + p + "/" + f + " label=" + m.component("comp1").physics(p).feature(f).label());
        } catch (Exception e) {
          System.out.println("FEAT " + p + "/" + f + " label=<err>");
        }
      }
    }
    printTags("studies", m.study().tags());
    for (String st : m.study().tags()) {
      System.out.println("STUDY " + st + " label=" + m.study(st).label());
      printTags("features of study " + st, m.study(st).feature().tags());
    }
    printTags("functions", m.func().tags());
    printTags("datasets", m.result().dataset().tags());
    printTags("numericals", m.result().numerical().tags());
  }
}
