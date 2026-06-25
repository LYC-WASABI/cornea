import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage94_raise_break_thresholds_inline_probe {
  private static double trapz(double[] values, double dt, int lo, int hi) {
    double sum = 0;
    for (int i = lo + 1; i <= hi && i < values.length; i++) {
      sum += 0.5 * dt * (values[i - 1] + values[i]);
    }
    return sum;
  }

  private static void printRange(String tag, double[] values, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values) {
      if (Double.isFinite(v)) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
    if (values.length > 51) {
      System.out.printf(Locale.US, "%s sliding_avg=%.12g[%s]%n",
          tag, trapz(values, .01, 1, 51) / .50, unit);
    }
  }

  private static void surface(Model m, String type, String tag, String expr, String unit) {
    if (Arrays.asList(m.result().numerical().tags()).contains(tag)) {
      try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    m.result().numerical().create(tag, type);
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void global(Model m, String tag, String expr, String unit) {
    if (Arrays.asList(m.result().numerical().tags()).contains(tag)) {
      try { m.result().numerical().remove(tag); } catch (Exception ignored) {}
    }
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_rq0p5_film92");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    printRange(tag, m.result().numerical(tag).getReal()[0], unit);
  }

  private static void plot3(Model m, String tag, String label, String expr, String unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_rq0p5_film92");
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void plot1(Model m, String tag, String label, String[] expr, String[] unit) {
    if (Arrays.asList(m.result().tags()).contains(tag)) {
      try { m.result().remove(tag); } catch (Exception ignored) {}
    }
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label(label);
    m.result(tag).set("data", "dset_rq0p5_film92");
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\170_lid8mm_stage92_rq0p5um_film_replay_results.mph");
    m.label("172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph");

    m.param().set("h_break_low", "3.0[um]",
        "Raised effective film rupture lower threshold");
    m.param().set("h_break_high", "4.0[um]",
        "Raised effective film rupture upper threshold");

    String cfilm = "if(h_film_input<=3.0[um],0,"
        + "if(h_film_input>=4.0[um],1,"
        + "0.5-0.5*cos(pi*(h_film_input-3.0[um])/(1.0[um]))))";
    String fbreak = "1-(" + cfilm + ")";
    String pnom = "F_total_target/A_contact_nominal73";
    String fboundary = "intop_film(mu_boundary_break90*" + pnom + "*(" + fbreak + "))";
    String ftotal = "F_film_shear+" + fboundary;

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("C_film_break94", cfilm);
    m.component("comp1").variable(mv).set("f_break94", fbreak);
    m.component("comp1").variable(mv).set("F_boundary_break94", fboundary);
    m.component("comp1").variable(mv).set("F_total_break94", ftotal);
    m.component("comp1").variable(mv).set("mu_break94", "F_total_break94/F_total_target");

    surface(m, "MinSurface", "min94_hfilm", "h_film_input", "um");
    surface(m, "MaxSurface", "max94_fbreak", fbreak, "1");
    surface(m, "MinSurface", "min94_lambda", "h_film_input/Rq_eq", "1");
    global(m, "eval94_Ffilm", "F_film_shear", "N");
    global(m, "eval94_fbreak_area_avg", "intop_film(" + fbreak + ")/A_contact_nominal73", "1");
    global(m, "eval94_Fboundary", fboundary, "N");
    global(m, "eval94_Ftotal", ftotal, "N");
    global(m, "eval94_mu", "(" + ftotal + ")/F_total_target", "1");

    plot3(m, "pg94_fbreak", "Stage 94 inline 3-4 um film rupture weight", fbreak, "1");
    plot3(m, "pg94_hfilm", "Stage 94 h film with inline rupture threshold", "h_film_input", "um");
    plot1(m, "pg94_break_force_mu",
        "Stage 94 film + rupture-boundary shear force and friction coefficient",
        new String[] {"F_film_shear", fboundary, ftotal, "(" + ftotal + ")/F_total_target"},
        new String[] {"N", "N", "N", "1"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph");
    System.out.println("SAVED_STAGE94=172_lid8mm_stage94_rq0p5um_break3to4um_inline_postprocess.mph");
  }
}
