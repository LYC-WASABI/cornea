import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage88_light_lambda_film_shear_setup {
  private static boolean hasFunc(Model m, String tag) {
    return Arrays.asList(m.func().tags()).contains(tag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
    m.label("166_lid8mm_stage88_light_lambda_film_shear_setup.mph");
    m.param().set("scale_shear_lambda81", "1",
        "Scale factor for local-lambda film-only shear feedback");

    double[] ff = m.result().numerical("eval71_Ffilm").getReal()[0];
    String[][] rows = new String[ff.length][2];
    for (int i = 0; i < ff.length; i++) {
      rows[i] = new String[] {Double.toString(0.01 * i), Double.toString(ff[i])};
    }
    if (hasFunc(m, "ffilm88")) m.func().remove("ffilm88");
    m.func().create("ffilm88", "Interpolation");
    m.func("ffilm88").label("Stage 88 local-lambda film-only shear-force schedule");
    m.func("ffilm88").set("funcname", "F_film_sched88");
    m.func("ffilm88").set("table", rows);
    m.func("ffilm88").set("argunit", new String[] {"s"});
    m.func("ffilm88").set("fununit", "N");
    m.func("ffilm88").set("interp", "piecewisecubic");
    m.func("ffilm88").set("extrap", "const");

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("lambda_film88", "h_film_input/Rq_eq");
    m.component("comp1").variable(mv).set("f_asp_film88",
        "if(lambda_film88<=1,1,if(lambda_film88>=3,0,0.5*(1+cos(pi*(lambda_film88-1)/2))))");
    m.component("comp1").variable(mv).set("F_asp_lambda88", "0[N]");
    m.component("comp1").variable(mv).set("F_total_lambda88", "F_film_shear");
    m.component("comp1").variable(mv).set("mu_lambda88", "F_total_lambda88/F_total_target");

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("F_film_feedback88",
        "shear_speed_window73*F_film_sched88(t)");
    m.component("comp1").variable(pv).set("F_asp_feedback88", "0[N]");
    m.component("comp1").variable(pv).set("F_total_feedback88", "F_film_feedback88");
    m.component("comp1").variable(pv).set("mu_feedback88", "F_total_feedback88/F_total_target");
    m.component("comp1").variable(pv).set("tau_nominal_film88",
        "scale_shear_lambda81*F_total_feedback88/A_contact_nominal73");
    m.component("comp1").variable(pv).set("h_replay88",
        "withsol('sol21',h_film_input,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("lambda_local88", "h_replay88/Rq_eq");
    m.component("comp1").variable(pv).set("f_asp_local88",
        "if(lambda_local88<=1,1,if(lambda_local88>=3,0,0.5*(1+cos(pi*(lambda_local88-1)/2))))");

    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .label("Stage 88 local-lambda film-only shear feedback on cornea");
    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .set("FperArea", new String[] {
            "0",
            "tau_nominal_film88*pfilm_window_shear73*ty_shear73",
            "tau_nominal_film88*pfilm_window_shear73*tz_shear73"});
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .label("Stage 88 opposite film-only shear feedback on lid");
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .set("FperArea", new String[] {
            "0",
            "-tau_nominal_film88*ty_shear73",
            "-tau_nominal_film88*tz_shear73"});

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\166_lid8mm_stage88_light_lambda_film_shear_setup.mph");
    System.out.println("SAVED_STAGE88_SETUP=166_lid8mm_stage88_light_lambda_film_shear_setup.mph");
  }
}
