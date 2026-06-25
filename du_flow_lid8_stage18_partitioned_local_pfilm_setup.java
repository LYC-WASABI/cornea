import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage18_partitioned_local_pfilm_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\51_lid8mm_mixed_lubrication_stage13_complete_postprocessing_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\61_lid8mm_stage18_partitioned_local_pfilm_feedback_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("61_lid8mm_stage18_partitioned_local_pfilm_feedback_setup.mph");
    model.param().set("scale_partitioned_pfilm", "0.25", "Continuation scale for replayed local pfilm");
    model.param().set("T_structure_pre", "0.01[s]", "Validated structural preload hold");
    model.param().set("T_structure_slide", "0.50[s]", "Validated structural continuation duration");
    model.param().set("T_structure_hold", "0.02[s]", "Validated structural final hold");
    model.param().set("dt_structure_out", "0.01[s]", "Validated structural output interval");

    model.component("comp1").variable().create("var_partitioned_local_pfilm");
    model.component("comp1").variable("var_partitioned_local_pfilm").label("Partitioned local pfilm replay variables");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("slide_fraction_structure",
        "if(t<T_structure_pre,0,if(t<T_structure_pre+T_structure_slide,"
            + "0.5-0.5*cos(pi*(t-T_structure_pre)/T_structure_slide),1))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("phi_lid_structure",
        "theta_slide_total*slide_fraction_structure");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("t_film_replay",
        "T_pre+(T_slide/pi)*acos(max(-1,min(1,1-2*slide_fraction_structure)))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("pfilm_replay",
        "withsol('sol19',max(pfilm,0),setval(t,t_film_replay))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_film_replay",
        "withsol('sol19',W_film,setval(t,t_film_replay))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("F_film_shear_replay",
        "withsol('sol19',F_film_shear,setval(t,t_film_replay))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_contact_partitioned",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("W_total_partitioned_local",
        "W_contact_partitioned+W_film_replay");
    model.component("comp1").variable("var_partitioned_local_pfilm").set("mu_app_partitioned_local",
        "(F_film_shear_replay+0.02*max(W_contact_partitioned,0))/F_total_target");

    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-dr_force_sched(slide_fraction_structure)"
              + "*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-dr_force_sched(slide_fraction_structure)"
              + "*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    try { model.component("comp1").physics("solid").feature().remove("load_partitioned_pfilm"); }
    catch (Exception ignored) {}
    model.component("comp1").physics("solid").create("load_partitioned_pfilm", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
        .label("Partitioned replay of local tear-film pressure");
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
        .selection().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm")
        .set("FperArea", new String[]{
            "-scale_partitioned_pfilm*pfilm_replay*nx",
            "-scale_partitioned_pfilm*pfilm_replay*ny",
            "-scale_partitioned_pfilm*pfilm_replay*nz"});

    try { model.study().remove("std_partitioned_local_pfilm"); } catch (Exception ignored) {}
    model.study().create("std_partitioned_local_pfilm");
    model.study("std_partitioned_local_pfilm").label("Stage 18 partitioned local pfilm feedback");
    model.study("std_partitioned_local_pfilm").create("time", "Transient");
    model.study("std_partitioned_local_pfilm").feature("time").set("tlist",
        "range(0,dt_structure_out,T_structure_pre+T_structure_slide+T_structure_hold)");
    model.study("std_partitioned_local_pfilm").feature("time").set("geometricNonlinearity", "on");
    model.study("std_partitioned_local_pfilm").feature("time").set("activate",
        new String[]{"solid", "on", "tff", "off"});
    model.study("std_partitioned_local_pfilm").feature("time").set("useinitsol", "on");
    model.study("std_partitioned_local_pfilm").feature("time").set("initmethod", "sol");
    model.study("std_partitioned_local_pfilm").feature("time").set("initstudy", "std_preload");
    model.study("std_partitioned_local_pfilm").feature("time").set("initstudystep", "stat");
    model.study("std_partitioned_local_pfilm").feature("time").set("initsol", "sol1");
    model.study("std_partitioned_local_pfilm").feature("time").set("initsoluse", "sol1");
    model.study("std_partitioned_local_pfilm").feature("time").set("initsolusesolnum", 15);

    model.save(OUT);
    System.out.println("SAVED_STAGE18_PARTITIONED_SETUP=" + OUT);
  }
}
