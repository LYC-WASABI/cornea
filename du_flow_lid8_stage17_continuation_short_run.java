import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage17_continuation_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\59_lid8mm_stage17_strong_coupling_continuation_controls_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\60_lid8mm_stage17_motion1_wall0_feedback0_short_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("60_lid8mm_stage17_motion1_wall0_feedback0_short_results.mph");
    String[] sols = model.sol().tags();
    String sol = sols[sols.length - 1];
    model.sol(sol).runAll();
    model.save(OUT);
    System.out.println("SAVED_STAGE17_SHORT=" + OUT);
  }
}
