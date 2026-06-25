import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_start_patch_existing_solution {
  private static double value(
      Model model, String tag, String type, String expr) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset574old");
    model.result().numerical(tag).selection().named("sel_patch_candidate574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574e_stage574_local_cornea_patch_geometry.mph");
      try { model.result().dataset().remove("dset574old"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset574old", "Solution");
      model.result().dataset("dset574old").set("solution", "sol93");
      System.out.println("PATCH=" + Arrays.toString(
          model.component("comp1").selection("sel_patch_candidate574")
              .entities(2)));
      String gap = "geomgap_dst_cp_lid_cornea";
      double area = value(model, "a574old", "IntSurface", "1");
      double valid = value(model, "v574old", "IntSurface",
          "if(isdefined(" + gap + "),"
              + "if(abs(" + gap + ")<0.1[mm],1,0),0)");
      double core = value(model, "c574old", "IntSurface", "M_core573");
      double coreValid = value(model, "cv574old", "IntSurface",
          "M_core573*if(isdefined(" + gap + "),"
              + "if(abs(" + gap + ")<0.1[mm],1,0),0)");
      double minimum = value(model, "mi574old", "MinSurface",
          "if(abs(" + gap + ")<0.1[mm]," + gap + ",1[m])");
      double maximum = value(model, "ma574old", "MaxSurface",
          "if(abs(" + gap + ")<0.1[mm]," + gap + ",-1[m])");
      System.out.printf(Locale.US,
          "AREA=%.12g VALID=%.12g CORE_VALID=%.12g MIN=%.12g MAX=%.12g%n",
          area, valid / area, coreValid / core, minimum, maximum);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
