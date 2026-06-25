import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage63_mu_jump_probe {
  private static void values(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_closedloop_film62");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    double[][] v = m.result().numerical(tag).getReal();
    System.out.println("BEGIN " + tag + " expr=" + expr);
    for (int i = 0; i < v[0].length; i++) {
      double t = .01 * i;
      if (t >= .03 && t <= .08) {
        System.out.printf(Locale.US, "t=%.2f value=%.12g %s%n", t, v[0][i], unit);
      }
    }
    System.out.println("END " + tag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    String[] params = {"T_pre", "T_slide", "T_structure_pre", "T_structure_slide", "F_total_target", "h0_tear"};
    for (String p : params) {
      try {
        System.out.println("PARAM " + p + "=" + m.param().get(p));
      } catch (Exception ignored) {}
    }
    String[] vars = {"slide_fraction", "slide_fraction_structure", "phi_lid_structure", "t_film_replay", "lid_speed", "W_film", "F_film_shear"};
    for (String group : m.component("comp1").variable().tags()) {
      for (String v : vars) {
        try {
          String val = m.component("comp1").variable(group).get(v);
          if (val != null) System.out.println("VAR " + group + " " + v + "=" + val);
        } catch (Exception ignored) {}
      }
    }
    values(m, "probe63_Wfilm", "W_film", "N");
    values(m, "probe63_Wcontact", "max(F_total_target-W_film,0)", "N");
    values(m, "probe63_Fshear", "F_film_shear", "N");
    values(m, "probe63_Ffriction", "F_film_shear+0.02*max(F_total_target-W_film,0)", "N");
    values(m, "probe63_mu", "(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target", "1");
  }
}
