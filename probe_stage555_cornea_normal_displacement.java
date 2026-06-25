import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_cornea_normal_displacement {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");
      model.result().numerical().create("evn555", "EvalGlobal");
      model.result().numerical("evn555").set("data", "dset540s");
      model.result().numerical("evn555").set("expr", new String[] {
        "intop_film(lid_mask*(u*nx+v*ny+w*nz))/intop_film(lid_mask)",
        "intop_film(lid_mask*h0_tear)/intop_film(lid_mask)",
        "dr_indent119"
      });
      model.result().numerical("evn555").set(
          "unit", new String[] {"um", "um", "um"});
      System.out.println(Arrays.deepToString(
          model.result().numerical("evn555").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
