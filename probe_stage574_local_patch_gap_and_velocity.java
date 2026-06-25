import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_local_patch_gap_and_velocity {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double[] surface(
      Model model, String solution, String tag, String[] expr) {
    String dataset = "dset_" + tag;
    removeDataset(model, dataset);
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).set("solution", solution);
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag)
        .selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] result = new double[raw.length];
    for (int i = 0; i < result.length; i++) result[i] = raw[i][0];
    return result;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "build_stage574_midpoint_local_patch_jfo_output2_Model.mph");
      ModelNode comp = model.component("comp1");
      String gap = "geomgap_dst_cp_lid_cornea";
      String replay = "withsol('sol94'," + gap + ")";
      try { comp.variable().remove("var_gap_replay_probe574"); }
      catch (Exception ignored) {}
      comp.variable().create("var_gap_replay_probe574");
      comp.variable("var_gap_replay_probe574")
          .selection().named("sel_local_cornea_patch574");
      comp.variable("var_gap_replay_probe574")
          .set("g_pair_replay_probe574", replay);
      double[] gapValues = surface(
          model, "sol94", "int574lp_gap_probe", new String[] {
            "1",
            "if(isdefined(" + gap + "),1,0)",
            "if(isdefined(g_pair_replay_probe574),1,0)",
            "if(isdefined(" + gap + "),"
                + "if(abs(" + gap + ")<0.1[mm],1,0),0)",
            "if(isdefined(g_pair_replay_probe574),"
                + "if(abs(g_pair_replay_probe574)<0.1[mm],1,0),0)",
            "if(isdefined(" + gap + "),"
                + "if(abs(" + gap + ")<0.1[mm]," + gap + ",0[m]),0[m])",
            "if(isdefined(g_pair_replay_probe574),"
                + "if(abs(g_pair_replay_probe574)<0.1[mm],"
                + "g_pair_replay_probe574"
                + ",0[m]),0[m])"
          });
      double[] velocity = surface(
          model, "sol101", "int574lp_velocity_probe", new String[] {
            "tff.p-p_amb573",
            "max(tff.p-p_amb573,0[Pa])",
            "tff.theta",
            "M_core573",
            "Bfilm573*M_core573",
            "p_load573"
          });
      System.out.println("GAP=" + Arrays.toString(gapValues));
      System.out.println("VELOCITY_0P1=" + Arrays.toString(velocity));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
