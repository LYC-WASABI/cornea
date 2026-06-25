import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage6_partitioned_solid_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\38_lid8mm_mixed_lubrication_stage5_partitioned_film_h6um_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\39_lid8mm_mixed_lubrication_stage6_partitioned_solid_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("39_lid8mm_mixed_lubrication_stage6_partitioned_solid_setup.mph");
    model.param().set("tau0_asp", "1[kPa]", "Initial asperity shear-strength intercept for calibration");
    model.param().set("alpha_asp", "0.02", "Initial pressure-dependent asperity shear-strength factor");

    try { model.component("comp1").physics("solid").feature().remove("load_tearfilm_replay"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("load_tearfilm_replay", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("load_tearfilm_replay")
        .label("Partitioned tear-film load replay on anterior cornea");
    model.component("comp1").physics("solid").feature("load_tearfilm_replay")
        .selection().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("load_tearfilm_replay")
        .set("FperArea", new String[]{
            "-withsol('sol19',max(pfilm,0),setval(t,t))*nx",
            "-withsol('sol19',max(pfilm,0),setval(t,t))*ny",
            "-withsol('sol19',max(pfilm,0),setval(t,t))*nz"});
    model.component("comp1").physics("solid").feature("load_tearfilm_replay").active(false);

    model.component("comp1").variable("var_mixed_lub").set("W_film_replay",
        "withsol('sol19',W_film,setval(t,t))");
    model.component("comp1").variable("var_mixed_lub").set("F_film_shear_replay",
        "withsol('sol19',F_film_shear,setval(t,t))");
    model.component("comp1").variable("var_mixed_lub").set("W_total_partitioned",
        "W_film_replay+W_contact");
    model.component("comp1").variable("var_mixed_lub").set("asp_shear_density",
        "if(isdefined(solid.Tn),tau0_asp+alpha_asp*max(solid.Tn,0),0)");
    model.component("comp1").variable("var_mixed_lub").set("F_asp_shear",
        "intop_contact(asp_shear_density)");
    model.component("comp1").variable("var_mixed_lub").set("F_friction_partitioned",
        "F_film_shear_replay+F_asp_shear");
    model.component("comp1").variable("var_mixed_lub").set("mu_app_partitioned",
        "F_friction_partitioned/F_total_target");

    try { model.study().remove("std_partitioned_solid"); } catch (Exception ignored) {}
    model.study().create("std_partitioned_solid");
    model.study("std_partitioned_solid").label("Stage 6 partitioned mixed-lubrication solid replay");
    model.study("std_partitioned_solid").create("time", "Transient");
    model.study("std_partitioned_solid").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.study("std_partitioned_solid").feature("time").set("geometricNonlinearity", "on");
    model.study("std_partitioned_solid").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "off"});
    model.study("std_partitioned_solid").feature("time").set("useinitsol", "on");
    model.study("std_partitioned_solid").feature("time").set("initmethod", "sol");
    model.study("std_partitioned_solid").feature("time").set("initstudy", "std_preload");
    model.study("std_partitioned_solid").feature("time").set("initstudystep", "stat");
    model.study("std_partitioned_solid").feature("time").set("initsol", "sol1");
    model.study("std_partitioned_solid").feature("time").set("initsoluse", "sol1");
    model.study("std_partitioned_solid").feature("time").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE6_SETUP=" + OUT);
  }
}
