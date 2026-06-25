import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage12_weakcoupled_results_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\49_lid8mm_mixed_lubrication_stage11_weakcoupled_film_h12um_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\50_lid8mm_mixed_lubrication_stage12_weakcoupled_loadshare_results.mph";

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_tff_oneway");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void printRange(Model model, String tag, String unit) {
    double[][] values = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) { min = Math.min(min, v); max = Math.max(max, v); }
    System.out.printf("%s min=%.12g[%s] max=%.12g[%s] final=%.12g[%s]%n",
        tag, min, unit, max, unit, values[0][values[0].length - 1], unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("50_lid8mm_mixed_lubrication_stage12_weakcoupled_loadshare_results.mph");
    model.param().set("T_pre_dry_replay", "0.01[s]", "Validated dry structural replay hold");
    model.param().set("T_slide_dry_replay", "0.50[s]", "Validated dry structural replay slide duration");
    model.param().set("alpha_asp_budget", "0.02", "Initial roughness-contact shear fraction for calibration");

    model.component("comp1").variable().create("var_weakcoupled_results");
    model.component("comp1").variable("var_weakcoupled_results").label("Stable weak-coupled mixed-lubrication results");
    model.component("comp1").variable("var_weakcoupled_results").set("slide_fraction_weak",
        "if(t<T_pre,0,if(t<T_pre+T_slide,0.5-0.5*cos(pi*(t-T_pre)/T_slide),1))");
    model.component("comp1").variable("var_weakcoupled_results").set("t_dry_replay",
        "T_pre_dry_replay+(T_slide_dry_replay/pi)*acos(max(-1,min(1,1-2*slide_fraction_weak)))");
    model.component("comp1").variable("var_weakcoupled_results").set("W_contact_dry_replay",
        "withsol('sol18',intop_contact(if(isdefined(solid.Tn),solid.Tn,0)),setval(t,t_dry_replay))");
    model.component("comp1").variable("var_weakcoupled_results").set("W_contact_budget",
        "max(F_total_target-W_film,0)");
    model.component("comp1").variable("var_weakcoupled_results").set("W_total_budget",
        "W_contact_budget+W_film");
    model.component("comp1").variable("var_weakcoupled_results").set("contact_budget_scale",
        "W_contact_budget/max(W_contact_dry_replay,1e-9[N])");
    model.component("comp1").variable("var_weakcoupled_results").set("F_asp_budget",
        "alpha_asp_budget*W_contact_budget");
    model.component("comp1").variable("var_weakcoupled_results").set("F_friction_weak",
        "F_film_shear+F_asp_budget");
    model.component("comp1").variable("var_weakcoupled_results").set("mu_app_weak",
        "F_friction_weak/F_total_target");
    model.component("comp1").variable("var_weakcoupled_results").set("contact_pressure_budget",
        "contact_budget_scale*withsol('sol18',if(isdefined(solid.Tn),solid.Tn,0),setval(t,t_dry_replay))");

    global(model, "eval_weak_W_film", "W_film", "N");
    global(model, "eval_weak_W_contact_budget", "max(F_total_target-W_film,0)", "N");
    global(model, "eval_weak_W_total_budget", "max(F_total_target-W_film,0)+W_film", "N");
    global(model, "eval_weak_F_film_shear", "F_film_shear", "N");
    global(model, "eval_weak_F_asp_budget", "0.02*max(F_total_target-W_film,0)", "N");
    global(model, "eval_weak_F_friction", "F_film_shear+0.02*max(F_total_target-W_film,0)", "N");
    global(model, "eval_weak_mu_app",
        "(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target", "1");
    printRange(model, "eval_weak_W_film", "N");
    printRange(model, "eval_weak_W_contact_budget", "N");
    printRange(model, "eval_weak_W_total_budget", "N");
    printRange(model, "eval_weak_F_friction", "N");
    printRange(model, "eval_weak_mu_app", "1");

    model.result().create("pg_weak_loadshare", "PlotGroup1D");
    model.result("pg_weak_loadshare").label("Weak-coupled load sharing: total 0.03 N");
    model.result("pg_weak_loadshare").set("data", "dset_tff_oneway");
    model.result("pg_weak_loadshare").create("glob1", "Global");
    model.result("pg_weak_loadshare").feature("glob1").set("expr",
        new String[]{"W_film", "max(F_total_target-W_film,0)", "max(F_total_target-W_film,0)+W_film"});
    model.result("pg_weak_loadshare").feature("glob1").set("unit", new String[]{"N", "N", "N"});
    model.result("pg_weak_loadshare").feature("glob1").set("descr",
        new String[]{"Tear-film load", "Cornea contact-load budget", "Total shared load"});

    model.result().create("pg_weak_mu", "PlotGroup1D");
    model.result("pg_weak_mu").label("Apparent friction coefficient from tear film and roughness contact");
    model.result("pg_weak_mu").set("data", "dset_tff_oneway");
    model.result("pg_weak_mu").create("glob1", "Global");
    model.result("pg_weak_mu").feature("glob1").set("expr",
        new String[]{"(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target"});
    model.result("pg_weak_mu").feature("glob1").set("unit", new String[]{"1"});

    model.save(OUT);
    System.out.println("SAVED_STAGE12_WEAKCOUPLED_RESULT=" + OUT);
  }
}
