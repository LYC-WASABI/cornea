import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage72_constant_speed_calibrated_mixed_shear_results {
  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi; i++) sum += 0.5 * dt * (values[i - 1] + values[i]);
    return sum;
  }

  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_constant_speed_film71");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical(tag).getReal()[0]) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\153_lid8mm_stage71_h3um_constant_speed_film_replay_results.mph");
    m.label("154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
    double[] wf = m.result().numerical("eval71_Wfilm").getReal()[0];
    double[] ff = m.result().numerical("eval71_Ffilm").getReal()[0];
    double[] wc = new double[wf.length];
    for (int i = 0; i < wf.length; i++) wc[i] = Math.max(.03 - wf[i], 0);
    double avgFilm = trapz(ff, .01, 1, 51) / .50;
    double avgContact = trapz(wc, .01, 1, 51) / .50;
    double targetShear = .1 * .03;
    double muAsp = Math.max(0, (targetShear - avgFilm) / avgContact);
    System.out.printf(Locale.US,
        "CAL72 avgFilm=%.12g[N] avgContactBudget=%.12g[N] targetShear=%.12g[N] muAsp=%.12g%n",
        avgFilm, avgContact, targetShear, muAsp);

    m.param().set("mu_asp_cal72", Double.toString(muAsp),
        "Calibrated wet roughness-asperity boundary friction coefficient for constant-speed replay");
    String mu = Double.toString(muAsp);
    String vars = "var_mixed_lub";
    m.component("comp1").variable(vars).set("W_asp_budget72", "max(F_total_target-W_film,0)");
    m.component("comp1").variable(vars).set("F_asp_shear72", "mu_asp_cal72*W_asp_budget72");
    m.component("comp1").variable(vars).set("F_total_shear72", "F_film_shear+F_asp_shear72");
    m.component("comp1").variable(vars).set("mu_app72", "F_total_shear72/F_total_target");
    m.component("comp1").variable(vars).set("tau_nominal_total72", "F_total_shear72/(8[mm^2])");

    global(m, "eval72_Wfilm", "W_film", "N");
    global(m, "eval72_Wasp", "max(F_total_target-W_film,0)", "N");
    global(m, "eval72_Ffilm", "F_film_shear", "N");
    global(m, "eval72_Fasp", mu + "*max(F_total_target-W_film,0)", "N");
    global(m, "eval72_Ftotal", "F_film_shear+" + mu + "*max(F_total_target-W_film,0)", "N");
    global(m, "eval72_mu", "(F_film_shear+" + mu + "*max(F_total_target-W_film,0))/F_total_target", "1");
    global(m, "eval72_tau_nominal",
        "(F_film_shear+" + mu + "*max(F_total_target-W_film,0))/(8[mm^2])", "Pa");

    double[] ft = m.result().numerical("eval72_Ftotal").getReal()[0];
    double avgTotal = trapz(ft, .01, 1, 51) / .50;
    System.out.printf(Locale.US,
        "AVG72_Ftotal_slide=%.12g[N] AVG72_tau_total_slide=%.12g[Pa] AVG72_mu_slide=%.12g%n",
        avgTotal, avgTotal / 8e-6, avgTotal / .03);

    m.result().create("pg72_loadshare", "PlotGroup1D");
    m.result("pg72_loadshare").label("Stage 72 constant-speed normal load sharing");
    m.result("pg72_loadshare").set("data", "dset_constant_speed_film71");
    m.result("pg72_loadshare").create("glob1", "Global");
    m.result("pg72_loadshare").feature("glob1").set("expr",
        new String[] {"W_film", "max(F_total_target-W_film,0)", "W_film+max(F_total_target-W_film,0)"});

    m.result().create("pg72_shearshare", "PlotGroup1D");
    m.result("pg72_shearshare").label("Stage 72 constant-speed calibrated mixed shear-force sharing");
    m.result("pg72_shearshare").set("data", "dset_constant_speed_film71");
    m.result("pg72_shearshare").create("glob1", "Global");
    m.result("pg72_shearshare").feature("glob1").set("expr",
        new String[] {"F_film_shear", mu + "*max(F_total_target-W_film,0)",
            "F_film_shear+" + mu + "*max(F_total_target-W_film,0)"});

    m.result().create("pg72_mu", "PlotGroup1D");
    m.result("pg72_mu").label("Stage 72 constant-speed calibrated apparent friction coefficient");
    m.result("pg72_mu").set("data", "dset_constant_speed_film71");
    m.result("pg72_mu").create("glob1", "Global");
    m.result("pg72_mu").feature("glob1").set("expr",
        new String[] {"(F_film_shear+" + mu + "*max(F_total_target-W_film,0))/F_total_target",
            "(F_film_shear+" + mu + "*max(F_total_target-W_film,0))/(8[mm^2])"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
    System.out.println("SAVED_STAGE72=154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
  }
}
