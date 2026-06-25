import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage73_shear_feedback_setup {
  private static boolean hasFunc(Model m, String tag) {
    return Arrays.asList(m.func().tags()).contains(tag);
  }

  private static boolean hasFeature(Model m, String phys, String tag) {
    return Arrays.asList(m.component("comp1").physics(phys).feature().tags()).contains(tag);
  }

  private static boolean hasStudy(Model m, String tag) {
    return Arrays.asList(m.study().tags()).contains(tag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\154_lid8mm_stage72_h3um_constant_speed_calibrated_mixed_shear_results.mph");
    m.label("155_lid8mm_stage73_shear_feedback_setup.mph");

    double[] ft = m.result().numerical("eval72_Ftotal").getReal()[0];
    String[][] rows = new String[ft.length][2];
    for (int i = 0; i < ft.length; i++) {
      rows[i] = new String[] {Double.toString(0.01 * i), Double.toString(ft[i])};
    }
    if (hasFunc(m, "fshear73")) m.func().remove("fshear73");
    m.func().create("fshear73", "Interpolation");
    m.func("fshear73").label("Stage 73 total mixed-lubrication shear-force schedule");
    m.func("fshear73").set("funcname", "F_shear_sched73");
    m.func("fshear73").set("table", rows);
    m.func("fshear73").set("argunit", new String[] {"s"});
    m.func("fshear73").set("fununit", "N");
    m.func("fshear73").set("interp", "piecewisecubic");
    m.func("fshear73").set("extrap", "const");

    m.param().set("scale_shear_feedback73", "1",
        "Scale factor for tangential mixed-lubrication shear feedback");
    m.param().set("A_contact_nominal73", "8[mm^2]",
        "Nominal rectangular curved contact area for lid 8 mm by 1 mm");
    m.param().set("W_eps_shear73", "1e-9[N]",
        "Regularization load for pfilm-weighted tangential traction");
    m.param().set("r_eps_shear73", "1e-9[m]",
        "Regularization radius for tangential unit vector");

    String v = "var_partitioned_local_pfilm";
    m.component("comp1").variable(v).set("F_shear_feedback73", "F_shear_sched73(t)");
    m.component("comp1").variable(v).set("tau_nominal_shear73",
        "scale_shear_feedback73*F_shear_feedback73/A_contact_nominal73");
    m.component("comp1").variable(v).set("pfilm_weight_shear73",
        "max(pfilm_replay53,0)/max(W_film_replay53,W_eps_shear73)");
    m.component("comp1").variable(v).set("tau_pfilm_shear73",
        "scale_shear_feedback73*F_shear_feedback73*pfilm_weight_shear73");
    m.component("comp1").variable(v).set("slide_sign73", "sign(theta_slide_total/1[deg])");
    m.component("comp1").variable(v).set("ty_shear73",
        "slide_sign73*(-(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/"
            + "max(sqrt(Y^2+Z^2),r_eps_shear73))");
    m.component("comp1").variable(v).set("tz_shear73",
        "slide_sign73*((Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/"
            + "max(sqrt(Y^2+Z^2),r_eps_shear73))");
    m.component("comp1").variable(v).set("F_shear_feedback_check73",
        "F_shear_feedback73");
    m.component("comp1").variable(v).set("mu_shear_feedback73",
        "F_shear_feedback73/F_total_target");

    if (hasFeature(m, "solid", "load_shear_cornea73")) {
      m.component("comp1").physics("solid").feature().remove("load_shear_cornea73");
    }
    m.component("comp1").physics("solid").create("load_shear_cornea73", "BoundaryLoad", 2);
    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .label("Stage 73 mixed-lubrication shear feedback on cornea");
    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .selection().named("sel_cornea_anterior_surface");
    m.component("comp1").physics("solid").feature("load_shear_cornea73")
        .set("FperArea", new String[] {
            "0",
            "tau_pfilm_shear73*ty_shear73",
            "tau_pfilm_shear73*tz_shear73"});

    if (hasFeature(m, "solid", "load_shear_lid73")) {
      m.component("comp1").physics("solid").feature().remove("load_shear_lid73");
    }
    m.component("comp1").physics("solid").create("load_shear_lid73", "BoundaryLoad", 2);
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .label("Stage 73 opposite shear feedback on lid wiper inner surface");
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .selection().named("sel_lid_contact_source_robust");
    m.component("comp1").physics("solid").feature("load_shear_lid73")
        .set("FperArea", new String[] {
            "0",
            "-tau_nominal_shear73*ty_shear73",
            "-tau_nominal_shear73*tz_shear73"});

    if (hasStudy(m, "std_shear_feedback73")) m.study().remove("std_shear_feedback73");
    m.study().create("std_shear_feedback73");
    m.study("std_shear_feedback73").label("Stage 73 structural transient with shear feedback");
    m.study("std_shear_feedback73").create("time", "Transient");
    m.study("std_shear_feedback73").feature("time").set("tlist",
        "range(0,dt_structure_out,T_structure_pre+T_structure_slide+T_structure_hold)");
    m.study("std_shear_feedback73").feature("time").set("geometricNonlinearity", "on");
    m.study("std_shear_feedback73").feature("time").set("activate",
        new String[] {"solid", "on", "tff", "off"});
    m.study("std_shear_feedback73").feature("time").set("useinitsol", "on");
    m.study("std_shear_feedback73").feature("time").set("initmethod", "sol");
    m.study("std_shear_feedback73").feature("time").set("initstudy", "std_preload");
    m.study("std_shear_feedback73").feature("time").set("initstudystep", "stat");
    m.study("std_shear_feedback73").feature("time").set("initsol", "sol1");
    m.study("std_shear_feedback73").feature("time").set("initsoluse", "sol1");
    m.study("std_shear_feedback73").feature("time").set("initsolusesolnum", 15);

    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\155_lid8mm_stage73_shear_feedback_setup.mph");
    System.out.println("SAVED_STAGE73_SETUP=155_lid8mm_stage73_shear_feedback_setup.mph");
  }
}
