import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage4_coupled_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\34_lid8mm_mixed_lubrication_stage3_moving_footprint_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\36_lid8mm_mixed_lubrication_stage4_bidirectional_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("36_lid8mm_mixed_lubrication_stage4_bidirectional_setup.mph");
    model.param().set("h0_tear", "5[um]", "Initial tear-film thickness for mixed-load first trial");
    model.param().set("tau0_asp", "1[kPa]", "Initial asperity shear-strength intercept for calibration");
    model.param().set("alpha_asp", "0.02", "Initial pressure-dependent asperity shear-strength factor");
    model.param().set("T_film_ramp", "8[ms]", "Smooth tear-film feedback ramp before sliding");

    model.component("comp1").physics("tff").feature("ffp1").set("ub_src", "Off");
    model.component("comp1").physics("tff").feature("ffp1")
        .set("TangentialBaseVelocity", "Off");

    try { model.component("comp1").physics("solid").feature().remove("load_tearfilm_base"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("load_tearfilm_base", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("load_tearfilm_base")
        .label("Bidirectional tear-film load feedback on anterior cornea");
    model.component("comp1").physics("solid").feature("load_tearfilm_base")
        .selection().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("load_tearfilm_base")
        .set("FperArea", new String[]{"film_feedback_ramp*tff.fbasex",
                                     "film_feedback_ramp*tff.fbasey",
                                     "film_feedback_ramp*tff.fbasez"});

    model.component("comp1").variable("var_mixed_lub").set("film_feedback_ramp",
        "if(t<=0,0,if(t<T_film_ramp,0.5-0.5*cos(pi*t/T_film_ramp),1))");
    model.component("comp1").variable("var_mixed_lub").set("asp_shear_density",
        "if(isdefined(solid.Tn),tau0_asp+alpha_asp*max(solid.Tn,0),0)");
    model.component("comp1").variable("var_mixed_lub").set("F_asp_shear",
        "intop_contact(asp_shear_density)");
    model.component("comp1").variable("var_mixed_lub").set("F_friction_mixed",
        "F_film_shear+F_asp_shear");
    model.component("comp1").variable("var_mixed_lub").set("mu_app_mixed",
        "F_friction_mixed/F_total_target");

    try { model.study().remove("std_mixed_coupled"); } catch (Exception ignored) {}
    model.study().create("std_mixed_coupled");
    model.study("std_mixed_coupled").label("Stage 4 bidirectional mixed-lubrication trial");
    model.study("std_mixed_coupled").create("time", "Transient");
    model.study("std_mixed_coupled").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.study("std_mixed_coupled").feature("time").set("geometricNonlinearity", "on");
    model.study("std_mixed_coupled").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "on"});
    model.study("std_mixed_coupled").feature("time").set("useinitsol", "on");
    model.study("std_mixed_coupled").feature("time").set("initmethod", "sol");
    model.study("std_mixed_coupled").feature("time").set("initstudy", "std_preload");
    model.study("std_mixed_coupled").feature("time").set("initstudystep", "stat");
    model.study("std_mixed_coupled").feature("time").set("initsol", "sol1");
    model.study("std_mixed_coupled").feature("time").set("initsoluse", "sol1");
    model.study("std_mixed_coupled").feature("time").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE4_SETUP=" + OUT);
  }
}
