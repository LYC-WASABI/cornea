import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage48_film_load_feedback_setup {
  private static final String BASE_SETUP =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph";
  private static final String BASE_RESULT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\124_lid8mm_stage47_h3um_dynamic_gap_quasisteady_final_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\125_lid8mm_stage48_h3um_film_load_feedback_setup.mph";
  private static final double TARGET_N = 0.03;
  private static final double H0_UM = 3.0;

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model result = ModelUtil.load("Result", BASE_RESULT);
    double[][] film = result.result().numerical("eval47_Wfilm").getReal();
    List<String[]> rows = new ArrayList<>();
    for (int i = 0; i < film[0].length; i++) {
      double t = 0.01 * i;
      double ratio = Math.max(1.0, film[0][i] / TARGET_N);
      double separationUm = H0_UM * (Math.sqrt(ratio) - 1.0);
      separationUm = Math.max(0.0, Math.min(10.0, separationUm));
      rows.add(new String[]{
          String.format(Locale.US, "%.12g", t),
          String.format(Locale.US, "%.12g", separationUm)
      });
    }
    ModelUtil.remove("Result");

    Model model = ModelUtil.load("Model", BASE_SETUP);
    model.label("125_lid8mm_stage48_h3um_film_load_feedback_setup.mph");
    try { model.func().remove("hsep48"); } catch (Exception ignored) {}
    model.func().create("hsep48", "Interpolation");
    model.func("hsep48").label("Stage 48 local separation feedback from Stage 47 film-load excess");
    model.func("hsep48").set("funcname", "h_sep_feedback48");
    model.func("hsep48").set("table", rows.toArray(new String[0][0]));
    model.func("hsep48").set("argunit", new String[]{"s"});
    model.func("hsep48").set("fununit", "um");
    model.func("hsep48").set("interp", "piecewisecubic");
    model.func("hsep48").set("extrap", "const");

    model.component("comp1").variable("var_mixed_lub").set("h_feedback_sep48",
        "h_sep_feedback48(t_replay)");
    model.component("comp1").variable("var_mixed_lub").set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq+h_feedback_sep48)");
    model.component("comp1").variable("var_mixed_lub").set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    model.save(OUT);
    System.out.println("FEEDBACK_ROWS=" + rows.size());
    System.out.println("SAVED_STAGE48_SETUP=" + OUT);
  }
}
