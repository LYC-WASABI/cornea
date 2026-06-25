import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_contact_mask_alignment {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }
  private static double[] evaluate(Model model, String solution, double fraction) {
    removeDataset(model, "dset576p2Align");
    model.result().dataset().create("dset576p2Align", "Solution");
    model.result().dataset("dset576p2Align").set("solution", solution);
    removeNumerical(model, "int576p2Align");
    model.result().numerical().create("int576p2Align", "IntSurface");
    model.result().numerical("int576p2Align").set("data", "dset576p2Align");
    model.result().numerical("int576p2Align").selection().named("sel_film_swept571");
    model.result().numerical("int576p2Align").set("expr", new String[] {
      "M_core573", "M_core573*Y", "M_core573*Z",
      "abs(solid.Tn)", "abs(solid.Tn)*Y", "abs(solid.Tn)*Z",
      "if(isdefined(geomgap_dst_cp_lid_cornea),1,0)",
      "if(isdefined(geomgap_dst_cp_lid_cornea),Y,0[m])",
      "if(isdefined(geomgap_dst_cp_lid_cornea),Z,0[m])"
    });
    double[][] rows = model.result().numerical("int576p2Align").getReal();
    double[] v = new double[rows.length];
    for (int i=0;i<rows.length;i++) v[i]=rows[i][rows[i].length-1];
    System.out.printf(Locale.US,
        "ALIGN fraction=%.3f coreY=%.12g coreZ=%.12g contactY=%.12g contactZ=%.12g gapDefinedArea=%.12g gapY=%.12g gapZ=%.12g%n",
        fraction, v[1]/v[0], v[2]/v[0], v[4]/v[3], v[5]/v[3],
        v[6], v[7]/v[6], v[8]/v[6]);
    return v;
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model=ModelUtil.load("Model",
          "576p2_stage576_moving_structure_sparse_jfo_checkpoint.mph");
      model.param().set("t_position576p2","T_pre572");
      evaluate(model,"sol143",0.0);
      model.param().set("t_position576p2","T_pre572+0.25*T_slide572");
      evaluate(model,"sol149",0.25);
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch(Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch(Exception ignored) {}
      System.exit(1);
    }
  }
}
