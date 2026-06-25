import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_unanchored_boundary_matrix {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final int[] FULL = new int[] {6, 7, 10, 15, 16, 18};

  private static void run(String name, int[] surfaces) {
    String tag = "Model_" + name;
    try {
      Model model = ModelUtil.load(tag, INPUT);
      ModelNode comp = model.component("comp1");
      model.param().set("t_position576p2", "T_pre572+0.25*T_slide572");
      int[] allEdges = comp.physics("tff").feature("bdr1")
          .selection().entities();
      comp.physics("tff").selection().set(surfaces);
      comp.physics("tff").feature("ms_vent573").selection().set(surfaces);
      comp.physics("tff").feature("wc_open_anchor573").active(false);
      comp.physics("tff").feature("bdr_inlet520").active(true);
      comp.physics("tff").feature("bdr_inlet520").selection().set(allEdges);
      comp.physics("tff").feature("bdr_outlet520").active(false);
      comp.physics("tff").feature("bdr_left520").active(false);
      comp.physics("tff").feature("bdr_right520").active(false);
      for (String feature : comp.physics("tff").feature().tags()) {
        try { comp.physics("tff").feature(feature)
            .set("StudyStep", "std576p2_jfo_5/stat"); }
        catch (Exception ignored) {}
      }
      model.sol("sol150").clearSolutionData();
      System.out.println("UNANCHORED_CASE_START=" + name + " surfaces="
          + Arrays.toString(surfaces));
      model.sol("sol150").runAll();
      System.out.println("UNANCHORED_CASE_PASS=" + name);
    } catch (Exception error) {
      System.out.println("UNANCHORED_CASE_FAIL=" + name);
      System.out.println("UNANCHORED_ERROR=" + error.getMessage());
    } finally {
      try { ModelUtil.remove(tag); } catch (Exception ignored) {}
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      run("full", FULL);
      for (int surface : FULL) {
        run("surface_" + surface, new int[] {surface});
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
