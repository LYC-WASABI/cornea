import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_source_to_local_gap {
  private static double value(
      Model model, String tag, String type, String expr, String selection) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574s2d");
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
      try { model.result().dataset().remove("dset574s2d"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574s2d", "Solution");
      model.result().dataset("dset574s2d").set("solution", "sol94");
      String src = "geomgap_src_cp_lid_cornea";
      String mappedValid = "src2dst_cp_lid_cornea("
          + "if(isdefined(" + src + "),"
          + "if(abs(" + src + ")<0.1[mm],1,0),0))";
      String mappedGap = "src2dst_cp_lid_cornea("
          + "if(isdefined(" + src + "),"
          + "if(abs(" + src + ")<0.1[mm]," + src + ",0[m]),0[m]))";
      double sourceArea = value(model, "as574s2d", "IntSurface", "1",
          "sel_lid_contact_source574");
      double sourceValid = value(model, "vs574s2d", "IntSurface",
          "if(isdefined(" + src + "),"
              + "if(abs(" + src + ")<0.1[mm],1,0),0)",
          "sel_lid_contact_source574");
      double patchArea = value(model, "ap574s2d", "IntSurface", "1",
          "sel_local_cornea_patch574");
      double mappedArea = value(model, "mp574s2d", "IntSurface",
          mappedValid, "sel_local_cornea_patch574");
      double mappedIntegral = value(model, "mg574s2d", "IntSurface",
          mappedGap, "sel_local_cornea_patch574");
      double minimum = value(model, "min574s2d", "MinSurface",
          "if(" + mappedValid + ">0.5," + mappedGap + ",1[m])",
          "sel_local_cornea_patch574");
      double maximum = value(model, "max574s2d", "MaxSurface",
          "if(" + mappedValid + ">0.5," + mappedGap + ",-1[m])",
          "sel_local_cornea_patch574");
      System.out.printf(Locale.US,
          "SOURCE_VALID=%.12g MAPPED_FRACTION=%.12g"
              + " AVG=%.12g MIN=%.12g MAX=%.12g%n",
          sourceValid / sourceArea, mappedArea / patchArea,
          mappedIntegral / mappedArea, minimum, maximum);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
