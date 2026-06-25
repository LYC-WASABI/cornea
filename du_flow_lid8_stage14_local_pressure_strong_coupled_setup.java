import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage14_local_pressure_strong_coupled_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\52_lid8mm_stage14_local_pressure_strong_coupled_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("52_lid8mm_stage14_local_pressure_strong_coupled_setup.mph");
    model.param().set("T_feedback_delay", "5[ms]", "Delay before local tear-film pressure feedback");
    model.param().set("T_feedback_ramp", "20[ms]", "Smooth local tear-film pressure feedback ramp");
    model.param().set("T_motion_smooth", "1[ms]", "Smooth lid-motion onset regularization");
    model.param().set("mu_contact_stabilization", "0.1",
        "Numerical tangential contact stabilization; excluded from reported friction coefficient");

    model.component("comp1").variable("var_mixed_lub").set("film_feedback_ramp",
        "if(t<=T_feedback_delay,0,if(t<T_feedback_delay+T_feedback_ramp,"
            + "0.5-0.5*cos(pi*(t-T_feedback_delay)/T_feedback_ramp),1))");
    model.component("comp1").variable("var_mixed_lub").set("tau_motion_strong",
        "min(t,T_slide)");
    model.component("comp1").variable("var_mixed_lub").set("dtau_motion_strong",
        "if(t<T_slide,1,0)");
    model.component("comp1").variable("var_dynamic_lid_motion").set("slide_fraction",
        "0.5-0.5*cos(pi*min(1,tau_motion_strong/T_slide))");
    model.component("comp1").variable("var_dynamic_lid_motion").set("phi_lid_dyn",
        "theta_slide_total*slide_fraction");
    model.component("comp1").variable("var_mixed_lub").set("omega_lid",
        "theta_slide_total*0.5*pi/T_slide*sin(pi*min(1,tau_motion_strong/T_slide))*dtau_motion_strong");

    model.component("comp1").physics("tff").feature("ffp1").set("hw1", "h_film_input");
    model.component("comp1").physics("tff").feature("ffp1").set("hb1", "0");
    model.component("comp1").physics("tff").feature("ffp1").set("ub_src", "Off");
    model.component("comp1").physics("tff").feature("ffp1").set("TangentialBaseVelocity", "Off");

    try { model.component("comp1").physics("solid").feature().remove("load_local_pfilm_feedback"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("load_local_pfilm_feedback", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback")
        .label("Strong coupling: local tear-film pressure feedback");
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback")
        .selection().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("load_local_pfilm_feedback")
        .set("FperArea", new String[]{
            "-film_feedback_ramp*max(pfilm,0)*nx",
            "-film_feedback_ramp*max(pfilm,0)*ny",
            "-film_feedback_ramp*max(pfilm,0)*nz"});

    boolean hasFriction = false;
    for (String child : model.component("comp1").physics("solid").feature("dcnt1").feature().tags()) {
      if (child.equals("fric1")) hasFriction = true;
    }
    if (!hasFriction) {
      model.component("comp1").physics("solid").feature("dcnt1").feature().create("fric1", "Friction");
    }
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1")
        .label("Numerical tangential stabilization only");
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1")
        .set("mu_fric", "mu_contact_stabilization");

    try { model.study().remove("std_local_pressure_strong"); } catch (Exception ignored) {}
    model.study().create("std_local_pressure_strong");
    model.study("std_local_pressure_strong").label("Stage 14 local pressure feedback strong-coupling trial");
    model.study("std_local_pressure_strong").create("time", "Transient");
    model.study("std_local_pressure_strong").feature("time").set("tlist",
        "range(0,0.0005[s],0.02[s])");
    model.study("std_local_pressure_strong").feature("time").set("geometricNonlinearity", "on");
    model.study("std_local_pressure_strong").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "on"});
    model.study("std_local_pressure_strong").feature("time").set("useinitsol", "on");
    model.study("std_local_pressure_strong").feature("time").set("initmethod", "sol");
    model.study("std_local_pressure_strong").feature("time").set("initstudy", "std_preload");
    model.study("std_local_pressure_strong").feature("time").set("initstudystep", "stat");
    model.study("std_local_pressure_strong").feature("time").set("initsol", "sol1");
    model.study("std_local_pressure_strong").feature("time").set("initsoluse", "sol1");
    model.study("std_local_pressure_strong").feature("time").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE14_STRONG_SETUP=" + OUT);
  }
}
