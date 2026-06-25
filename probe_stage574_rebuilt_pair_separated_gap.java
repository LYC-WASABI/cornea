import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_rebuilt_pair_separated_gap {
  private static double value(
      Model model, String tag, String type, String expr, String selection) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574sep");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "scan_stage574_rebuilt_pair_indentation_output5_Model.mph");
      try { model.result().dataset().remove("dset574sep"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574sep", "Solution");
      model.result().dataset("dset574sep").set("solution", "sol95");
      String gap = "geomgap_dst_cp_lid_cornea";
      double area = value(model, "a574sep", "IntSurface", "1",
          "sel_local_cornea_patch574");
      double finite = value(model, "f574sep", "IntSurface",
          "if(isdefined(" + gap + "),if(abs(" + gap
              + ")<1[mm],1,0),0)",
          "sel_local_cornea_patch574");
      double negative = value(model, "n574sep", "IntSurface",
          "if(isdefined(" + gap + "),if(" + gap
              + "<0[m]&&abs(" + gap + ")<1[mm],1,0),0)",
          "sel_local_cornea_patch574");
      double minimum = value(model, "min574sep", "MinSurface",
          "if(abs(" + gap + ")<1[mm]," + gap + ",1[m])",
          "sel_local_cornea_patch574");
      double maximum = value(model, "max574sep", "MaxSurface",
          "if(abs(" + gap + ")<1[mm]," + gap + ",-1[m])",
          "sel_local_cornea_patch574");
      System.out.printf(Locale.US,
          "AREA=%.12g FINITE=%.12g NEGATIVE=%.12g"
              + " MIN=%.12g MAX=%.12g%n",
          area, finite / area, negative / area, minimum, maximum);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
