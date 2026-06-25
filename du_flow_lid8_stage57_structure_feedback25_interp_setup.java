import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage57_structure_feedback25_interp_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\137_lid8mm_stage55_closedloop_gap_film_replay_results.mph");
    m.label("140_lid8mm_stage57_structure_feedback25_interp_setup.mph");
    m.param().set("scale_partitioned_pfilm", "0.25", "Continuation scale for interpolated local tear-film pressure feedback");

    double[][] wf = m.result().numerical("eval55_Wfilm").getReal();
    double[][] fs = m.result().numerical("eval55_Ffilm_shear").getReal();
    String[][] wfRows = new String[wf[0].length][2];
    String[][] fsRows = new String[fs[0].length][2];
    for (int i = 0; i < wf[0].length; i++) {
      wfRows[i] = new String[] {Double.toString(.01 * i), Double.toString(wf[0][i])};
      fsRows[i] = new String[] {Double.toString(.01 * i), Double.toString(fs[0][i])};
    }
    m.func("wf54").set("table", wfRows);
    m.func("fs54").set("table", fsRows);

    String v = "var_partitioned_local_pfilm";
    m.component("comp1").variable(v).set("t_film_replay_lo", "min(0.53[s],max(0[s],0.01[s]*floor(t_film_replay/0.01[s])))");
    m.component("comp1").variable(v).set("t_film_replay_hi", "min(0.53[s],max(0[s],0.01[s]*ceil(t_film_replay/0.01[s])))");
    m.component("comp1").variable(v).set("alpha_film_replay", "(t_film_replay-t_film_replay_lo)/max(0.01[s],t_film_replay_hi-t_film_replay_lo)");
    m.component("comp1").variable(v).set("pfilm_replay53", "(1-alpha_film_replay)*withsol('sol21',max(pfilm,0),setval(t_replay,t_film_replay_lo))+alpha_film_replay*withsol('sol21',max(pfilm,0),setval(t_replay,t_film_replay_hi))");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\140_lid8mm_stage57_structure_feedback25_interp_setup.mph");
    System.out.println("SAVED_STAGE57_SETUP=140_lid8mm_stage57_structure_feedback25_interp_setup.mph");
  }
}
