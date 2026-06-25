import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage27_force_controlled_qs_scan_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\80_lid8mm_stage25_force_controlled_static_preload_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\82_lid8mm_stage27_force_controlled_qs_scan_short_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("82_lid8mm_stage27_force_controlled_qs_scan_short_results.mph");
    model.param().set("s_qs27", "0", "Force-controlled quasi-static sliding fraction");
    model.component("comp1").variable().create("var_stage27_qs");
    model.component("comp1").variable("var_stage27_qs").label("Stage 27 force-controlled quasi-static replay");
    model.component("comp1").variable("var_stage27_qs").set("phi_qs27", "theta_slide_total*s_qs27");
    model.component("comp1").variable("var_stage27_qs").set("t_film_qs27",
        "T_pre+(T_slide/pi)*acos(max(-1,min(1,1-2*s_qs27)))");
    model.component("comp1").variable("var_stage27_qs").set("pfilm_qs27",
        "withsol('sol19',max(pfilm,0),setval(t,t_film_qs27))");
    model.component("comp1").variable("var_stage27_qs").set("W_film_qs27",
        "withsol('sol19',W_film,setval(t,t_film_qs27))");
    model.component("comp1").variable("var_stage27_qs").set("W_contact_qs27",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_stage27_qs").set("W_total_qs27",
        "W_contact_qs27+W_film_qs27");

    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_qs27)-1)-Z*sin(phi_qs27)"
              + "-q_force*1[mm]*(Y*cos(phi_qs27)-Z*sin(phi_qs27))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_qs27)+Z*(cos(phi_qs27)-1)"
              + "-q_force*1[mm]*(Y*sin(phi_qs27)+Z*cos(phi_qs27))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm").set("FperArea",
        new String[]{"-scale_partitioned_pfilm*pfilm_qs27*nx",
            "-scale_partitioned_pfilm*pfilm_qs27*ny",
            "-scale_partitioned_pfilm*pfilm_qs27*nz"});
    model.component("comp1").physics("ge_force_mixed").feature("ge1").set("equation", 1, 1,
        "(intop_contact(if(isdefined(solid.Tn),solid.Tn,0))+W_film_qs27-F_total_target)/F_total_target");

    try { model.study().remove("std_force_qs27"); } catch (Exception ignored) {}
    model.study().create("std_force_qs27");
    model.study("std_force_qs27").label("Stage 27 force-controlled quasi-static position scan");
    model.study("std_force_qs27").create("param", "Parametric");
    model.study("std_force_qs27").feature("param").set("pname", new String[]{"s_qs27"});
    model.study("std_force_qs27").feature("param").set("plistarr", new String[]{"range(0,0.002,0.15)"});
    model.study("std_force_qs27").feature("param").set("punit", new String[]{"1"});
    model.study("std_force_qs27").create("stat", "Stationary");
    model.study("std_force_qs27").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_force_qs27").feature("stat").set("activate",
        new String[]{"solid", "on", "tff", "off", "ge_force_mixed", "on"});
    model.study("std_force_qs27").feature("stat").set("useinitsol", "on");
    model.study("std_force_qs27").feature("stat").set("initmethod", "sol");
    model.study("std_force_qs27").feature("stat").set("initstudy", "std_force_preload25");
    model.study("std_force_qs27").feature("stat").set("initstudystep", "stat");
    model.study("std_force_qs27").feature("stat").set("initsol", "sol20");
    model.study("std_force_qs27").feature("stat").set("initsoluse", "sol20");
    model.study("std_force_qs27").createAutoSequences("sol");
    String solver = lastSolution(model);
    try { model.sol(solver).feature("s1").feature().remove("se1"); } catch (Exception ignored) {}
    try { model.sol(solver).feature("s1").feature().remove("fc1"); } catch (Exception ignored) {}
    model.sol(solver).feature("s1").create("fc1", "FullyCoupled");
    model.sol(solver).feature("s1").feature("fc1").set("linsolver", "d1");
    model.sol(solver).feature("s1").feature("fc1").set("maxiter", 100);
    System.out.println("RUN_STAGE27_FORCE_CONTROLLED_QS_SHORT");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("STAGE27_QS_SOLVER=" + solver);
    System.out.println("SAVED_STAGE27_QS_SHORT=" + OUT);
  }
}
