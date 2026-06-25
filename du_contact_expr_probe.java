import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_contact_expr_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_center_directed_lid_load_scan_results.mph");
    System.out.println("SOLUTIONS");
    for (String sol : model.sol().tags()) {
      System.out.println(sol + " : " + model.sol(sol).label());
      for (String f : model.sol(sol).feature().tags()) System.out.println("  " + f + " : " + model.sol(sol).feature(f).label());
    }
    System.out.println("DATASETS");
    for (String ds : model.result().dataset().tags()) {
      System.out.println(ds + " : " + model.result().dataset(ds).label());
    }
    String[] pressure = {
      "solid.Tn", "solid.pn", "solid.dcnt1.Tn", "solid.dcnt1.pn",
      "solid.contactPressure", "solid.cntpress", "solid.cpressure",
      "solid.Tn_dst", "solid.Tn_src", "solid.dcnt1.Tn_dst", "solid.dcnt1.Tn_src"
    };
    String[] gap = {
      "solid.gap", "solid.gap_dst", "solid.gap_src",
      "solid.dcnt1.gap", "solid.dcnt1.gap_dst", "solid.dcnt1.gap_src",
      "solid.contactGap", "solid.cgap"
    };
    test(model, "pressure", pressure);
    test(model, "gap", gap);
  }

  private static void test(Model model, String label, String[] exprs) {
    for (String expr : exprs) {
      try {
        String tag = "tmp_" + label;
        try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
        model.result().numerical().create(tag, "IntSurface");
        model.result().numerical(tag).set("data", "dset2");
        model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
        model.result().numerical(tag).set("expr", expr);
        double[][] val = model.result().numerical(tag).getReal();
        System.out.println("OK " + label + " " + expr + " -> " + Arrays.deepToString(val));
      } catch (Exception e) {
        System.out.println("NO " + label + " " + expr + " : " + e.getMessage());
      }
    }
  }
}
