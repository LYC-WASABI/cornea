import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage50_film_load_feedback_iteration3_setup {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model result = ModelUtil.load("Result",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\128_lid8mm_stage49_h3um_film_load_feedback_iteration2_results.mph");
    double[][] film = result.result().numerical("eval49_Wfilm").getReal();
    double[][] oldSep = result.result().numerical("eval49_hsep").getReal();
    List<String[]> rows = new ArrayList<>();
    for (int i = 0; i < film[0].length; i++) {
      double ratio = Math.max(1.0, film[0][i] / 0.03);
      double sep = oldSep[0][i] + 9.0 * (Math.sqrt(ratio) - 1.0);
      sep = Math.max(0.0, Math.min(20.0, sep));
      rows.add(new String[]{String.format(Locale.US, "%.12g", 0.01 * i),
          String.format(Locale.US, "%.12g", sep)});
    }
    ModelUtil.remove("Result");
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    model.label("129_lid8mm_stage50_h3um_film_load_feedback_iteration3_setup.mph");
    model.func().create("hsep50", "Interpolation");
    model.func("hsep50").set("funcname", "h_sep_feedback50");
    model.func("hsep50").set("table", rows.toArray(new String[0][0]));
    model.func("hsep50").set("argunit", new String[]{"s"});
    model.func("hsep50").set("fununit", "um");
    model.func("hsep50").set("interp", "piecewisecubic");
    model.func("hsep50").set("extrap", "const");
    model.component("comp1").variable("var_mixed_lub").set("h_feedback_sep50", "h_sep_feedback50(t_replay)");
    model.component("comp1").variable("var_mixed_lub").set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq+h_feedback_sep50)");
    model.component("comp1").variable("var_mixed_lub").set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    model.save("D:\\COMSOL_Outputs\\models\\du\\flow\\129_lid8mm_stage50_h3um_film_load_feedback_iteration3_setup.mph");
  }
}
