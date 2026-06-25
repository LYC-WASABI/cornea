import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_contact_gap_locations {
  static void test(
      Model model, String selection, String expr, String type, int index) {
    String tag = "p555_" + index;
    try {
      model.result().numerical().create(tag, type);
      model.result().numerical(tag).set("data", "dset540s");
      model.result().numerical(tag).selection().named(selection);
      model.result().numerical(tag).set("expr", expr);
      System.out.println(selection + " " + type + " " + expr + "="
          + Arrays.deepToString(
              model.result().numerical(tag).getReal()));
    } catch (Exception error) {
      System.out.println(selection + " " + type + " " + expr
          + " ERROR=" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");
      String[] selections = {
        "sel_lid_contact_source_robust", "sel_cornea_contact_target",
        "sel_film_track"
      };
      String[] expressions = {
        "solid.gap", "solid.gap_src", "solid.gap_dst",
        "solid.dcnt1.gap", "solid.dcnt1.gap_src",
        "solid.dcnt1.gap_dst", "solid.Tn", "solid.Tn_src",
        "solid.Tn_dst"
      };
      int i = 0;
      for (String selection : selections) {
        for (String expr : expressions) {
          test(model, selection, expr, "MinSurface", ++i);
          test(model, selection, expr, "MaxSurface", ++i);
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
