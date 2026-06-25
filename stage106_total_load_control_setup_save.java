import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage106_total_load_control_setup_save {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\178_lid8mm_stage100_total_normal_loadshare_results.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String pContact = "if(isdefined(solid.Tn),solid.Tn,0)";
      String fnContact = "intop_contact(" + pContact + ")";
      String fnTotal = fnContact + "+W_film_replay53";
      m.param().set("q_force_total106_init", "0.02", "Initial dimensionless radial indentation for total normal load closed-loop setup");
      m.component(c).variable(v).set("dr_force_total106", "q_force_total106*1[mm]");
      m.component(c).variable(v).set("Fn_contact106", fnContact);
      m.component(c).variable(v).set("Fn_film106", "W_film_replay53");
      m.component(c).variable(v).set("Fn_total106", fnTotal);
      m.component(c).variable(v).set("Fn_total_error106", "(Fn_total106-F_total_target)/F_total_target");
      m.component(c).variable(v).descr("dr_force_total106", "Closed-loop radial indentation unknown for total normal force control");
      m.component(c).variable(v).descr("Fn_total106", "Contact normal force plus replayed tear-film normal load");

      try { m.component(c).physics().remove("ge_force_total106"); } catch (Exception ignore) {}
      m.component(c).physics().create("ge_force_total106", "GlobalEquations");
      m.component(c).physics("ge_force_total106").label("Total normal load closed-loop indentation setup");
      m.component(c).physics("ge_force_total106").feature("ge1").set("name", 1, 1, "q_force_total106");
      m.component(c).physics("ge_force_total106").feature("ge1").set("equation", 1, 1, "(Fn_total106-F_total_target)/F_total_target");
      m.component(c).physics("ge_force_total106").feature("ge1").set("initialValueU", 1, 1, "q_force_total106_init");
      m.component(c).physics("ge_force_total106").feature("ge1").set("initialValueUt", 1, 1, "0");
      m.component(c).physics("ge_force_total106").feature("ge1").set("description", 1, 1,
          "Adjusts radial indentation so Fn_contact + W_film_replay53 equals 0.03 N");

      m.component(c).physics("solid").feature("disp_lid_time").set("U0", new String[]{
          "0",
          "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_total106*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
          "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_total106*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"
      });
      m.study("std_shear_feedback73").feature("time").set("activate",
          new String[]{"solid", "on", "tff", "off", "ge_force_total106", "on"});
      m.study("std_shear_feedback73").feature("time").set("tlist", "range(0,0.001,0.02)");

      try { m.result().remove("pg106_total_load_setup"); } catch (Exception ignore) {}
      m.result().create("pg106_total_load_setup", "PlotGroup1D");
      m.result("pg106_total_load_setup").label("Stage 106 total normal load control setup variables");
      m.result("pg106_total_load_setup").set("data", "dset_shear_feedback76");
      m.result("pg106_total_load_setup").feature().create("glob1", "Global");
      m.result("pg106_total_load_setup").feature("glob1").set("expr",
          new String[]{"Fn_contact106", "Fn_film106", "Fn_total106", "F_total_target", "Fn_total_error106", "dr_force_total106"});
      m.result("pg106_total_load_setup").feature("glob1").set("unit",
          new String[]{"N", "N", "N", "N", "1", "mm"});

      m.save("184_lid8mm_stage106_total_load_control_setup_Model.mph");
      System.out.println("Saved local setup: 184_lid8mm_stage106_total_load_control_setup_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
