import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage19_qs_local_pfilm_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\61_lid8mm_stage18_partitioned_local_pfilm_feedback_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\66_lid8mm_stage19_qs_local_pfilm_continuation_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("66_lid8mm_stage19_qs_local_pfilm_continuation_setup.mph");
    model.param().set("s_qs", "0", "Quasi-static whole-lid sliding fraction from -35 deg to +35 deg");
    model.param().set("scale_partitioned_pfilm", "0.10", "Continuation scale for local pfilm replay");

    model.component("comp1").variable().create("var_stage19_qs_local_pfilm");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").label("Stage 19 quasi-static local pfilm replay");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("phi_lid_qs19",
        "theta_slide_total*s_qs");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("t_film_qs19",
        "T_pre+(T_slide/pi)*acos(max(-1,min(1,1-2*s_qs)))");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("pfilm_qs19",
        "withsol('sol19',max(pfilm,0),setval(t,t_film_qs19))");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("W_film_qs19",
        "withsol('sol19',W_film,setval(t,t_film_qs19))");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("F_film_shear_qs19",
        "withsol('sol19',F_film_shear,setval(t,t_film_qs19))");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("W_contact_qs19",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("W_total_qs19",
        "W_contact_qs19+W_film_qs19");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("F_friction_qs19",
        "F_film_shear_qs19+0.02*max(W_contact_qs19,0)");
    model.component("comp1").variable("var_stage19_qs_local_pfilm").set("mu_app_qs19",
        "F_friction_qs19/F_total_target");

    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_qs19)-1)-Z*sin(phi_lid_qs19)"
              + "-dr_force_sched(s_qs)*(Y*cos(phi_lid_qs19)-Z*sin(phi_lid_qs19))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_qs19)+Z*(cos(phi_lid_qs19)-1)"
              + "-dr_force_sched(s_qs)*(Y*sin(phi_lid_qs19)+Z*cos(phi_lid_qs19))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
        .set("FperArea", new String[]{
            "-scale_partitioned_pfilm*pfilm_qs19*nx",
            "-scale_partitioned_pfilm*pfilm_qs19*ny",
            "-scale_partitioned_pfilm*pfilm_qs19*nz"});

    try {
      model.component("comp1").physics("solid").feature("dcnt1").feature().remove("fric_partitioned_stabilizer");
    } catch (Exception ignored) {}
    model.component("comp1").physics("solid").feature("dcnt1").feature()
        .create("fric_partitioned_stabilizer", "Friction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .feature("fric_partitioned_stabilizer")
        .label("Numerical tangential stabilizer only - excluded from reported friction");
    model.component("comp1").physics("solid").feature("dcnt1")
        .feature("fric_partitioned_stabilizer").set("mu_fric", "0.1");

    try { model.study().remove("std_qs_local_pfilm19"); } catch (Exception ignored) {}
    model.study().create("std_qs_local_pfilm19");
    model.study("std_qs_local_pfilm19").label("Stage 19 quasi-static local pfilm position continuation");
    model.study("std_qs_local_pfilm19").create("param", "Parametric");
    model.study("std_qs_local_pfilm19").feature("param").set("pname", new String[]{"s_qs"});
    model.study("std_qs_local_pfilm19").feature("param").set("plistarr", new String[]{"range(0,0.01,1)"});
    model.study("std_qs_local_pfilm19").feature("param").set("punit", new String[]{"1"});
    model.study("std_qs_local_pfilm19").create("stat", "Stationary");
    model.study("std_qs_local_pfilm19").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_qs_local_pfilm19").feature("stat").set("activate",
        new String[]{"solid", "on", "tff", "off"});
    model.study("std_qs_local_pfilm19").feature("stat").set("useinitsol", "on");
    model.study("std_qs_local_pfilm19").feature("stat").set("initmethod", "sol");
    model.study("std_qs_local_pfilm19").feature("stat").set("initstudy", "std_preload");
    model.study("std_qs_local_pfilm19").feature("stat").set("initstudystep", "stat");
    model.study("std_qs_local_pfilm19").feature("stat").set("initsol", "sol1");
    model.study("std_qs_local_pfilm19").feature("stat").set("initsoluse", "sol1");
    model.study("std_qs_local_pfilm19").feature("stat").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE19_QS_SETUP=" + OUT);
  }
}
