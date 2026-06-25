import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage82_local_lambda_shear_setup {
  private static boolean hasStudy(Model m, String tag) {
    return Arrays.asList(m.study().tags()).contains(tag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\160_lid8mm_stage78_windowed_shear_feedback_structure_results.mph");
    m.label("161_lid8mm_stage82_local_lambda_mixed_lubrication_setup.mph");

    m.param().set("lambda_boundary81", "1", "Boundary lubrication threshold h/Rq_eq");
    m.param().set("lambda_fullfilm81", "3", "Full-film lubrication threshold h/Rq_eq");
    m.param().set("scale_shear_lambda81", "1", "Scale factor for local-lambda mixed shear feedback");
    m.param().set("p_asp_floor81", "0[Pa]", "Optional lower bound for asperity pressure");

    String fAsp = "if(lambda_local81<=lambda_boundary81,1,"
        + "if(lambda_local81>=lambda_fullfilm81,0,"
        + "0.5*(1+cos(pi*(lambda_local81-lambda_boundary81)/(lambda_fullfilm81-lambda_boundary81)))))";
    String fAspFilm = "if(lambda_film81<=lambda_boundary81,1,"
        + "if(lambda_film81>=lambda_fullfilm81,0,"
        + "0.5*(1+cos(pi*(lambda_film81-lambda_boundary81)/(lambda_fullfilm81-lambda_boundary81)))))";

    String mv = "var_mixed_lub";
    m.component("comp1").variable(mv).set("lambda_film81", "h_film_input/Rq_eq");
    m.component("comp1").variable(mv).set("f_asp_film81", fAspFilm);
    m.component("comp1").variable(mv).set("f_asp_area_avg81",
        "min(1,max(0,intop_film(f_asp_film81)/A_contact_nominal73))");
    m.component("comp1").variable(mv).set("F_asp_lambda_budget81",
        "mu_asp_cal72*max(F_total_target-W_film,0)*f_asp_area_avg81");
    m.component("comp1").variable(mv).set("F_total_lambda_shear81",
        "F_film_shear+F_asp_lambda_budget81");
    m.component("comp1").variable(mv).set("mu_lambda81",
        "F_total_lambda_shear81/F_total_target");

    String pv = "var_partitioned_local_pfilm";
    m.component("comp1").variable(pv).set("h_replay81",
        "withsol('sol21',h_film_input,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("lambda_local81", "h_replay81/Rq_eq");
    m.component("comp1").variable(pv).set("f_asp_lambda81", fAsp);
    m.component("comp1").variable(pv).set("tau_film_replay81",
        "withsol('sol21',tau_film_wall,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("W_film_replay81",
        "withsol('sol21',W_film,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("F_film_shear_replay81",
        "withsol('sol21',F_film_shear,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("F_asp_lambda_replay81",
        "withsol('sol21',F_asp_lambda_budget81,setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(pv).set("F_total_lambda_replay81",
        "shear_speed_window73*(F_film_shear_replay81+F_asp_lambda_replay81)");
    m.component("comp1").variable(pv).set("mu_lambda_replay81",
        "F_total_lambda_replay81/F_total_target");
    m.component("comp1").variable(pv).set("p_asp_nominal81",
        "max(p_asp_floor81,max(F_total_target-W_film_replay81,0)/A_contact_nominal73)");
    m.component("comp1").variable(pv).set("tau_asp_lambda81",
        "mu_asp_cal72*p_asp_nominal81*f_asp_lambda81");
    m.component("comp1").variable(pv).set("tau_total_lambda81",
        "scale_shear_lambda81*shear_speed_window73*(tau_film_replay81+tau_asp_lambda81)");
    m.component("comp1").variable(pv).set("tau_lid_nominal_lambda81",
        "scale_shear_lambda81*F_total_lambda_replay81/A_contact_nominal73");

    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .label("Stage 82 local-lambda mixed shear feedback on cornea");
    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .set("FperArea", new String[] {
            "0",
            "tau_total_lambda81*ty_shear73",
            "tau_total_lambda81*tz_shear73"});

    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .label("Stage 82 opposite local-lambda mixed shear feedback on lid");
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .set("FperArea", new String[] {
            "0",
            "-tau_lid_nominal_lambda81*ty_shear73",
            "-tau_lid_nominal_lambda81*tz_shear73"});

    if (hasStudy(m, "std_local_lambda_shear82")) m.study().remove("std_local_lambda_shear82");
    m.study().create("std_local_lambda_shear82");
    m.study("std_local_lambda_shear82").label("Stage 82 structural transient with local lambda mixed shear");
    m.study("std_local_lambda_shear82").create("time", "Transient");
    m.study("std_local_lambda_shear82").feature("time").set("tlist",
        "range(0,dt_structure_out,T_structure_pre+T_structure_slide+T_structure_hold)");
    m.study("std_local_lambda_shear82").feature("time").set("geometricNonlinearity", "on");
    m.study("std_local_lambda_shear82").feature("time").set("activate",
        new String[] {"solid", "on", "tff", "off"});
    m.study("std_local_lambda_shear82").feature("time").set("useinitsol", "on");
    m.study("std_local_lambda_shear82").feature("time").set("initmethod", "sol");
    m.study("std_local_lambda_shear82").feature("time").set("initstudy", "std_preload");
    m.study("std_local_lambda_shear82").feature("time").set("initstudystep", "stat");
    m.study("std_local_lambda_shear82").feature("time").set("initsol", "sol1");
    m.study("std_local_lambda_shear82").feature("time").set("initsoluse", "sol1");
    m.study("std_local_lambda_shear82").feature("time").set("initsolusesolnum", 15);

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\161_lid8mm_stage82_local_lambda_mixed_lubrication_setup.mph");
    System.out.println("SAVED_STAGE82_SETUP=161_lid8mm_stage82_local_lambda_mixed_lubrication_setup.mph");
  }
}
