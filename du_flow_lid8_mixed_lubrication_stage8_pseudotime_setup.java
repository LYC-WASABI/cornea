import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage8_pseudotime_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\38_lid8mm_mixed_lubrication_stage5_partitioned_film_h6um_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\43_lid8mm_mixed_lubrication_stage8_pseudotime_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("43_lid8mm_mixed_lubrication_stage8_pseudotime_setup.mph");
    model.param().set("T_pre_structure", "0.01[s]", "Validated dry-model hold before structural continuation");
    model.param().set("T_slide_structure", "0.50[s]", "Validated dry-model quasi-static structural continuation duration");
    model.param().set("T_hold_structure", "0.02[s]", "Validated dry-model hold after structural continuation");
    model.param().set("dt_structure", "0.01[s]", "Validated dry-model structural output interval");
    model.param().set("K_contact_structure", "0.8051323301[N/mm]", "Local contact stiffness estimate");
    model.param().set("mixed_correction_gain_structure", "0", "Dry-trajectory verification before mixed-load correction");
    model.param().set("mu_contact_regularization", "0.1",
        "Temporary diagnostic tangential-contact floor; excluded from friction output");
    model.param().set("tau0_asp", "1[kPa]", "Initial asperity shear-strength intercept for calibration");
    model.param().set("alpha_asp", "0.02", "Initial pressure-dependent asperity shear-strength factor");

    model.component("comp1").variable().create("var_structure_pseudotime");
    model.component("comp1").variable("var_structure_pseudotime").label("Pseudo-time mixed-load structural continuation");
    model.component("comp1").variable("var_structure_pseudotime").set("slide_fraction_structure",
        "if(t<T_pre_structure,0,if(t<T_pre_structure+T_slide_structure,"
            + "0.5-0.5*cos(pi*(t-T_pre_structure)/T_slide_structure),1))");
    model.component("comp1").variable("var_structure_pseudotime").set("phi_lid_structure",
        "theta_slide_total*slide_fraction_structure");
    model.component("comp1").variable("var_structure_pseudotime").set("t_film_structure",
        "T_pre+(T_slide/pi)*acos(max(-1,min(1,1-2*slide_fraction_structure)))");
    model.component("comp1").variable("var_structure_pseudotime").set("W_film_structure",
        "withsol('sol19',W_film,setval(t,t_film_structure))");
    model.component("comp1").variable("var_structure_pseudotime").set("F_film_shear_structure",
        "withsol('sol19',F_film_shear,setval(t,t_film_structure))");
    model.component("comp1").variable("var_structure_pseudotime").set("mu_contact_lubricated",
        "max(mu_contact_regularization,min(0.5,F_film_shear_structure/F_total_target))");
    model.component("comp1").variable("var_structure_pseudotime").set("dr_mixed_structure",
        "dr_force_sched(slide_fraction_structure)");
    model.component("comp1").variable("var_structure_pseudotime").set("W_contact_structure",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_structure_pseudotime").set("W_total_structure",
        "W_film_structure+W_contact_structure");
    model.component("comp1").variable("var_structure_pseudotime").set("asp_shear_density_structure",
        "if(isdefined(solid.Tn),tau0_asp+alpha_asp*max(solid.Tn,0),0)");
    model.component("comp1").variable("var_structure_pseudotime").set("F_asp_shear_structure",
        "intop_contact(asp_shear_density_structure)");
    model.component("comp1").variable("var_structure_pseudotime").set("F_friction_structure",
        "F_film_shear_structure+F_asp_shear_structure");
    model.component("comp1").variable("var_structure_pseudotime").set("mu_app_structure",
        "F_friction_structure/F_total_target");

    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_mixed_structure*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_mixed_structure*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    boolean hasFriction = false;
    for (String child : model.component("comp1").physics("solid").feature("dcnt1").feature().tags()) {
      if (child.equals("fric1")) hasFriction = true;
    }
    if (!hasFriction) {
      model.component("comp1").physics("solid").feature("dcnt1").feature().create("fric1", "Friction");
    }
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1")
        .label("Temporary exact dry-baseline A/B diagnostic, mu = 0.1");
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1")
        .set("mu_fric", "0.1");

    try { model.study().remove("std_structure_pseudotime"); } catch (Exception ignored) {}
    model.study().create("std_structure_pseudotime");
    model.study("std_structure_pseudotime").label("Stage 8 pseudo-time structural continuation with mixed-load budget");
    model.study("std_structure_pseudotime").create("time", "Transient");
    model.study("std_structure_pseudotime").feature("time").set("tlist",
        "range(0,dt_structure,T_pre_structure+T_slide_structure+T_hold_structure)");
    model.study("std_structure_pseudotime").feature("time").set("geometricNonlinearity", "on");
    model.study("std_structure_pseudotime").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "off"});
    model.study("std_structure_pseudotime").feature("time").set("useinitsol", "on");
    model.study("std_structure_pseudotime").feature("time").set("initmethod", "sol");
    model.study("std_structure_pseudotime").feature("time").set("initstudy", "std_preload");
    model.study("std_structure_pseudotime").feature("time").set("initstudystep", "stat");
    model.study("std_structure_pseudotime").feature("time").set("initsol", "sol1");
    model.study("std_structure_pseudotime").feature("time").set("initsoluse", "sol1");
    model.study("std_structure_pseudotime").feature("time").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE8_SETUP=" + OUT);
  }
}
