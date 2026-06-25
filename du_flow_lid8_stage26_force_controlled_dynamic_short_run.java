import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage26_force_controlled_dynamic_short_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\80_lid8mm_stage25_force_controlled_static_preload_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\81_lid8mm_stage26_force_controlled_dynamic_short_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("81_lid8mm_stage26_force_controlled_dynamic_short_results.mph");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairDisconnect", "1");
    model.component("comp1").physics("solid").feature("disp_lid_time").set("U0",
        new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)"
              + "-q_force*1[mm]*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)"
              + "-q_force*1[mm]*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
        });
    model.component("comp1").physics("solid").feature("load_partitioned_pfilm").set("FperArea",
        new String[]{"-scale_partitioned_pfilm*pfilm_replay*nx",
            "-scale_partitioned_pfilm*pfilm_replay*ny",
            "-scale_partitioned_pfilm*pfilm_replay*nz"});
    model.component("comp1").physics("ge_force_mixed").feature("ge1").set("equation", 1, 1,
        "(intop_contact(if(isdefined(solid.Tn),solid.Tn,0))+W_film_replay-F_total_target)/F_total_target");
    model.study("std_partitioned_local_pfilm").feature("time").set("tlist", "range(0,0.002,0.08)");
    model.study("std_partitioned_local_pfilm").feature("time").set("useinitsol", "on");
    model.study("std_partitioned_local_pfilm").feature("time").set("initmethod", "sol");
    model.study("std_partitioned_local_pfilm").feature("time").set("initstudy", "std_force_preload25");
    model.study("std_partitioned_local_pfilm").feature("time").set("initstudystep", "stat");
    model.study("std_partitioned_local_pfilm").feature("time").set("initsol", "sol20");
    model.study("std_partitioned_local_pfilm").feature("time").set("initsoluse", "sol20");
    model.study("std_partitioned_local_pfilm").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).feature("t1").set("consistent", "off");
    try { model.sol(solver).feature("t1").feature().remove("se1"); } catch (Exception ignored) {}
    try { model.sol(solver).feature("t1").feature().remove("fc1"); } catch (Exception ignored) {}
    model.sol(solver).feature("t1").create("fc1", "FullyCoupled");
    model.sol(solver).feature("t1").feature("fc1").set("linsolver", "d1");
    System.out.println("RUN_STAGE26_FORCE_CONTROLLED_DYNAMIC_SHORT");
    model.sol(solver).runAll();
    model.save(OUT);
    System.out.println("STAGE26_DYNAMIC_SOLVER=" + solver);
    System.out.println("SAVED_STAGE26_DYNAMIC_SHORT=" + OUT);
  }
}
