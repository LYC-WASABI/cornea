import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_gap_field {
  static void surfaceEval(
      Model model, String tag, String type, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset540s");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    double[][] value = model.result().numerical(tag).getReal();
    System.out.println(tag + " " + expr + "="
        + Arrays.deepToString(value));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");
      for (String expr : new String[] {
          "solid.gap", "if(isdefined(solid.gap),solid.gap,0)",
          "if(isdefined(solid.gap),1,0)",
          "if(lid_mask>0.5,solid.gap,0)",
          "if(lid_mask>0.5,abs(solid.gap),0)",
          "if(solid.Tn>1[Pa],solid.gap,0)",
          "if(solid.Tn>1[Pa],abs(solid.gap),0)",
          "if(abs(solid.gap)<1[mm],solid.gap,0)",
          "if(abs(solid.gap)<100[um],solid.gap,0)",
          "if(abs(solid.gap)<100[um],1,0)",
          "lid_mask"
      }) {
        try { surfaceEval(model, "min555", "MinSurface", expr); }
        catch (Exception error) {
          System.out.println("MIN ERROR " + expr + " " + error.getMessage());
        }
        try { surfaceEval(model, "max555", "MaxSurface", expr); }
        catch (Exception error) {
          System.out.println("MAX ERROR " + expr + " " + error.getMessage());
        }
        try { surfaceEval(model, "avg555", "AvSurface", expr); }
        catch (Exception error) {
          System.out.println("AVG ERROR " + expr + " " + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
