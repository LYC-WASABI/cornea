import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_gap_side_matrix {
  private static double value(
      Model model, String tag, String type, String selection, String expr) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574side");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model",
          "build_stage574_midpoint_local_patch_structure_output6_Model.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      try { model.result().dataset().remove("dset574side"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574side", "Solution");
      model.result().dataset("dset574side").set("solution", "sol94");

      System.out.println("GAP_TRUE=" + pair.gapName(true));
      System.out.println("GAP_FALSE=" + pair.gapName(false));
      System.out.println("SOURCE=" + Arrays.toString(pair.source().entities()));
      System.out.println("DESTINATION="
          + Arrays.toString(pair.destination().entities()));

      String[] selections = {
        "sel_lid_contact_source574", "sel_local_cornea_patch574"
      };
      String[] gaps = {
        "geomgap_src_cp_lid_cornea", "geomgap_dst_cp_lid_cornea"
      };
      int index = 0;
      for (String selection : selections) {
        double area = value(model, "a" + index, "IntSurface", selection, "1");
        for (String gap : gaps) {
          double defined = value(
              model, "d" + index, "IntSurface", selection,
              "if(isdefined(" + gap + "),1,0)");
          double near = value(
              model, "n" + index, "IntSurface", selection,
              "if(isdefined(" + gap + "),"
                  + "if(abs(" + gap + ")<0.1[mm],1,0),0)");
          double minimum = value(
              model, "mi" + index, "MinSurface", selection,
              "if(isdefined(" + gap + ")," + gap + ",1[m])");
          double maximum = value(
              model, "ma" + index, "MaxSurface", selection,
              "if(isdefined(" + gap + ")," + gap + ",-1[m])");
          System.out.printf(Locale.US,
              "%s|%s|DEFINED=%.12g|NEAR=%.12g|MIN=%.12g|MAX=%.12g%n",
              selection, gap, defined / area, near / area, minimum, maximum);
          index++;
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
