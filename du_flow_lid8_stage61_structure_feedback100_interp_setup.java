import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage61_structure_feedback100_interp_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\145_lid8mm_stage60_closedloop50_gap_film_replay_results.mph");
    m.label("146_lid8mm_stage61_structure_feedback100_interp_setup.mph");
    m.param().set("scale_partitioned_pfilm", "1.0", "Full interpolated local tear-film pressure feedback");
    double[][] wf = m.result().numerical("eval60_Wfilm").getReal();
    double[][] fs = m.result().numerical("eval60_Ffilm_shear").getReal();
    String[][] wfRows = new String[wf[0].length][2];
    String[][] fsRows = new String[fs[0].length][2];
    for (int i = 0; i < wf[0].length; i++) {
      wfRows[i] = new String[] {Double.toString(.01 * i), Double.toString(wf[0][i])};
      fsRows[i] = new String[] {Double.toString(.01 * i), Double.toString(fs[0][i])};
    }
    m.func("wf54").set("table", wfRows);
    m.func("fs54").set("table", fsRows);
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\146_lid8mm_stage61_structure_feedback100_interp_setup.mph");
    System.out.println("SAVED_STAGE61_SETUP=146_lid8mm_stage61_structure_feedback100_interp_setup.mph");
  }
}
