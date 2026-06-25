import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage573_mapped_destination_gap {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double value(
      Model model, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset573map");
    model.result().numerical(tag).selection().named("sel573_src_map");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "572_stage572_dynamic_motion_mask_checked.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      String dst = pair.gapName(true);
      String op = "dst2src_cp_lid_cornea";
      String mask = op + "(if(" + dst + "<0.1[mm],1,0))";
      String mapped = op
          + "(if(" + dst + "<0.1[mm]," + dst + ",0[m]))";
      String contact = op + "(if(" + dst + "<0.1[mm],"
          + "incontact_cp_lid_cornea,0))";

      removeSelection(comp, "sel573_src_map");
      comp.selection().create("sel573_src_map", "Explicit");
      comp.selection("sel573_src_map").geom("geom1", 2);
      comp.selection("sel573_src_map").set(pair.source().entities());
      removeDataset(model, "dset573map");
      model.result().dataset().create("dset573map", "Solution");
      model.result().dataset("dset573map").set("solution", "sol93");

      double area = value(model, "int573m_area", "IntSurface", "1");
      double mappedArea =
          value(model, "int573m_mask", "IntSurface", mask);
      double gapIntegral =
          value(model, "int573m_gap", "IntSurface", mapped);
      double negativeArea = value(model, "int573m_neg", "IntSurface",
          "if(" + mask + ">0.5&&" + mapped + "<0[m],1,0)");
      double contactArea =
          value(model, "int573m_contact", "IntSurface", contact);
      double minimum = value(model, "min573m_gap", "MinSurface",
          "if(" + mask + ">0.5," + mapped + ",1[m])");
      double maximum = value(model, "max573m_gap", "MaxSurface",
          "if(" + mask + ">0.5," + mapped + ",-1[m])");

      System.out.println("MASK_EXPR=" + mask);
      System.out.println("GAP_EXPR=" + mapped);
      System.out.printf(Locale.US,
          "AREA=%.12g%nMAPPED_AREA=%.12g%nMAPPED_FRACTION=%.12g%n"
              + "NEGATIVE_AREA=%.12g%nCONTACT_AREA=%.12g%n"
              + "AVG_GAP=%.12g%nMIN_GAP=%.12g%nMAX_GAP=%.12g%n",
          area, mappedArea, mappedArea / area, negativeArea,
          contactArea, gapIntegral / mappedArea, minimum, maximum);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
