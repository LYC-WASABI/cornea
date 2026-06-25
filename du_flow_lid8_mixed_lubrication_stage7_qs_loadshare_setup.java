import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage7_qs_loadshare_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\38_lid8mm_mixed_lubrication_stage5_partitioned_film_h6um_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\41_lid8mm_mixed_lubrication_stage7_qs_loadshare_setup.mph";
  private static final String THETA_LIST = "range(0,1,70)";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("41_lid8mm_mixed_lubrication_stage7_qs_loadshare_setup.mph");
    model.param().set("theta_qs", "0[deg]", "Whole-lid quasi-static rotation from scratch start");
    model.param().set("K_contact_qs", "0.8051323301[N/mm]",
        "Local contact stiffness estimate used for first mixed-load correction");
    model.param().set("mixed_correction_gain", "0.35",
        "First-pass load-share correction gain");
    model.param().set("tau0_asp", "1[kPa]", "Initial asperity shear-strength intercept for calibration");
    model.param().set("alpha_asp", "0.02", "Initial pressure-dependent asperity shear-strength factor");

    model.component("comp1").variable().create("var_qs_loadshare");
    model.component("comp1").variable("var_qs_loadshare").label("Quasi-static mixed-load sharing");
    model.component("comp1").variable("var_qs_loadshare").set("slide_fraction_qs",
        "theta_qs/abs(theta_slide_total)");
    model.component("comp1").variable("var_qs_loadshare").set("phi_lid_qs", "-theta_qs");
    model.component("comp1").variable("var_qs_loadshare").set("t_film_qs",
        "T_pre+(T_slide/pi)*acos(max(-1,min(1,1-2*slide_fraction_qs)))");
    model.component("comp1").variable("var_qs_loadshare").set("W_film_qs",
        "withsol('sol19',W_film,setval(t,t_film_qs))");
    model.component("comp1").variable("var_qs_loadshare").set("F_film_shear_qs",
        "withsol('sol19',F_film_shear,setval(t,t_film_qs))");
    model.component("comp1").variable("var_qs_loadshare").set("dr_mixed_qs",
        "dr_force_sched(slide_fraction_qs)-mixed_correction_gain*W_film_qs/K_contact_qs");
    model.component("comp1").variable("var_qs_loadshare").set("W_contact_qs",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_qs_loadshare").set("W_total_qs",
        "W_film_qs+W_contact_qs");
    model.component("comp1").variable("var_qs_loadshare").set("asp_shear_density_qs",
        "if(isdefined(solid.Tn),tau0_asp+alpha_asp*max(solid.Tn,0),0)");
    model.component("comp1").variable("var_qs_loadshare").set("F_asp_shear_qs",
        "intop_contact(asp_shear_density_qs)");
    model.component("comp1").variable("var_qs_loadshare").set("F_friction_qs",
        "F_film_shear_qs+F_asp_shear_qs");
    model.component("comp1").variable("var_qs_loadshare").set("mu_app_qs",
        "F_friction_qs/F_total_target");

    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_qs)-1)-Z*sin(phi_lid_qs)"
              + "-dr_mixed_qs*(Y*cos(phi_lid_qs)-Z*sin(phi_lid_qs))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_qs)+Z*(cos(phi_lid_qs)-1)"
              + "-dr_mixed_qs*(Y*sin(phi_lid_qs)+Z*cos(phi_lid_qs))/sqrt(Y^2+Z^2)"
        });

    try { model.study().remove("std_qs_loadshare"); } catch (Exception ignored) {}
    model.study().create("std_qs_loadshare");
    model.study("std_qs_loadshare").label("Stage 7 quasi-static positions with 0.03 N mixed-load sharing");
    model.study("std_qs_loadshare").create("param", "Parametric");
    model.study("std_qs_loadshare").feature("param").set("pname", new String[]{"theta_qs"});
    model.study("std_qs_loadshare").feature("param").set("plistarr", new String[]{THETA_LIST});
    model.study("std_qs_loadshare").feature("param").set("punit", new String[]{"deg"});
    model.study("std_qs_loadshare").create("stat", "Stationary");
    model.study("std_qs_loadshare").feature("stat").set("geometricNonlinearity", true);
    model.study("std_qs_loadshare").feature("stat").set("activate",
        new String[]{"solid", "on", "tff", "off"});

    model.save(OUT);
    System.out.println("SAVED_STAGE7_QS_SETUP=" + OUT);
  }
}
