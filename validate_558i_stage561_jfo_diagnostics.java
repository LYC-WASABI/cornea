import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_558i_stage561_jfo_diagnostics {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      for (String tag : new String[] {
          "pg561_hgeom_diag", "pg561_pfilm_diag",
          "pg561_theta_diag", "pg561_cavitation_diag"
      }) {
        System.out.println(tag + "=" + model.result(tag).label());
      }
      System.out.println("SUMMARY=" + Arrays.deepToString(
          model.result().numerical("eval561_diag_summary").getReal()));
      System.out.println("HMIN=" + Arrays.deepToString(
          model.result().numerical("min561_hgeom_pos").getReal()));
      System.out.println("PMAX=" + Arrays.deepToString(
          model.result().numerical("max561_pfilm_pos").getReal()));
      System.out.println("THETAMIN=" + Arrays.deepToString(
          model.result().numerical("min561_theta_pos").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
