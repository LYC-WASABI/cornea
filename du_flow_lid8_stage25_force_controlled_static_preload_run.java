import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage25_force_controlled_static_preload_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\78_lid8mm_stage24_force_controlled_reconnect_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\80_lid8mm_stage25_force_controlled_static_preload_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("80_lid8mm_stage25_force_controlled_static_preload_results.mph");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "0");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{"0", "-q_force*1[mm]*Y/sqrt(Y^2+Z^2)", "-q_force*1[mm]*Z/sqrt(Y^2+Z^2)"});
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm").set("FperArea",
        new String[]{"-scale_partitioned_pfilm*withsol('sol19',max(pfilm,0),setval(t,T_pre))*nx",
            "-scale_partitioned_pfilm*withsol('sol19',max(pfilm,0),setval(t,T_pre))*ny",
            "-scale_partitioned_pfilm*withsol('sol19',max(pfilm,0),setval(t,T_pre))*nz"});
    model.component("comp1").physics("ge_force_mixed").feature("ge1").set("equation", 1, 1,
        "(intop_contact(if(isdefined(solid.Tn),solid.Tn,0))"
            + "+withsol('sol19',W_film,setval(t,T_pre))-F_total_target)/F_total_target");

    try { model.study().remove("std_force_preload25"); } catch (Exception ignored) {}
    model.study().create("std_force_preload25");
    model.study("std_force_preload25").label("Stage 25 force-controlled static preload");
    model.study("std_force_preload25").create("stat", "Stationary");
    model.study("std_force_preload25").feature("stat").set("geometricNonlinearity", "on");
    model.study("std_force_preload25").feature("stat").set("activate",
        new String[]{"solid", "on", "tff", "off", "ge_force_mixed", "on"});
    model.study("std_force_preload25").feature("stat").set("useinitsol", "on");
    model.study("std_force_preload25").feature("stat").set("initmethod", "sol");
    model.study("std_force_preload25").feature("stat").set("initstudy", "std_preload");
    model.study("std_force_preload25").feature("stat").set("initstudystep", "stat");
    model.study("std_force_preload25").feature("stat").set("initsol", "sol1");
    model.study("std_force_preload25").feature("stat").set("initsoluse", "sol1");
    model.study("std_force_preload25").feature("stat").set("initsolusesolnum", 15);
    model.study("std_force_preload25").createAutoSequences("sol");
    String solver = lastSolution(model);
    try { model.sol(solver).feature("s1").feature().remove("se1"); } catch (Exception ignored) {}
    try { model.sol(solver).feature("s1").feature().remove("fc1"); } catch (Exception ignored) {}
    model.sol(solver).feature("s1").create("fc1", "FullyCoupled");
    model.sol(solver).feature("s1").feature("fc1").set("linsolver", "d1");
    System.out.println("RUN_STAGE25_FORCE_CONTROLLED_STATIC_PRELOAD");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("STAGE25_PRELOAD_SOLVER=" + solver);
    System.out.println("SAVED_STAGE25_PRELOAD_RESULTS=" + OUT);
  }
}
