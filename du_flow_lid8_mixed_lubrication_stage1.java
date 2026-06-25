import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage1 {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\du_cornea_lid_8mm_force_calibrated_iteration7_local_gain050_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\31_lid8mm_mixed_lubrication_stage1_tff_water_setup.mph";

  private static boolean hasVariable(Model model, String tag) {
    for (String t : model.component("comp1").variable().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("31_lid8mm_mixed_lubrication_stage1_tff_water_setup.mph");

    model.param().set("F_total_target", "0.03[N]", "Total lid normal load shared by fluid film and cornea");
    model.param().set("Rq_cornea", "20[nm]", "Anterior cornea RMS roughness");
    model.param().set("Rq_lid", "40[nm]", "Initial lid-wiper RMS roughness estimate");
    model.param().set("Rq_eq", "sqrt(Rq_cornea^2+Rq_lid^2)", "Combined RMS roughness");
    model.param().set("rho_tear", "998[kg/m^3]", "Water-like tear-film density");
    model.param().set("eta_tear", "1.0e-3[Pa*s]", "Water-like tear-film dynamic viscosity");
    model.param().set("h0_tear", "3[um]", "Initial tear-film thickness");
    model.param().set("h_min_tear", "50[nm]", "Numerical minimum film thickness");
    model.param().set("v_blink_avg", "180[mm/s]", "Average blink sliding speed");
    model.param().set("L_slide", "R_cor*70[deg]", "Sliding arc length from -35 deg to +35 deg");
    model.param().set("T_slide", "L_slide/v_blink_avg", "Sliding time using average blink speed");
    model.param().set("T_pre", "0.01[s]", "Initial hold before sliding");
    model.param().set("T_hold", "0.01[s]", "Final hold after sliding");
    model.param().set("dt_out", "0.002[s]", "Lubrication result output interval");

    if (!hasVariable(model, "var_mixed_lub")) model.component("comp1").variable().create("var_mixed_lub");
    model.component("comp1").variable("var_mixed_lub").label("Mixed lubrication variables");
    model.component("comp1").variable("var_mixed_lub").set("h_film_input",
        "max(h_min_tear,h0_tear+Rq_eq)");
    model.component("comp1").variable("var_mixed_lub").set("omega_lid",
        "theta_slide_total*if(t<T_pre,0,if(t<T_pre+T_slide,0.5*pi/T_slide*sin(pi*(t-T_pre)/T_slide),0))");
    model.component("comp1").variable("var_mixed_lub").set("vwall_x", "0");
    model.component("comp1").variable("var_mixed_lub").set("vwall_y", "-omega_lid*Z");
    model.component("comp1").variable("var_mixed_lub").set("vwall_z", "omega_lid*Y");

    try { model.component("comp1").physics().remove("tff"); } catch (Exception ignored) {}
    model.component("comp1").physics().create("tff", "ThinFilmFlowShell", "geom1");
    model.component("comp1").physics("tff").label("Water-like tear-film mixed lubrication");
    model.component("comp1").physics("tff").selection().named("sel_cornea_anterior_surface");
    model.component("comp1").physics("tff").feature("ffp1").set("hw1", "h_film_input");
    model.component("comp1").physics("tff").feature("ffp1").set("hb1", "0");
    model.component("comp1").physics("tff").feature("ffp1").set("TangentialBaseVelocity", "Off");
    model.component("comp1").physics("tff").feature("ffp1").set("TangentialWallVelocity", "userdef");
    model.component("comp1").physics("tff").feature("ffp1")
        .set("vw", new String[]{"vwall_x", "vwall_y", "vwall_z"});
    model.component("comp1").physics("tff").feature("ffp1").set("ForceModel", "PressureAndShear");
    model.component("comp1").physics("tff").feature("ffp1").set("mure_mat", "userdef");
    model.component("comp1").physics("tff").feature("ffp1").set("mure", "eta_tear");
    model.component("comp1").physics("tff").feature("ffp1").set("rho_mat", "userdef");
    model.component("comp1").physics("tff").feature("ffp1").set("rho", "rho_tear");
    model.component("comp1").physics("tff").feature("bdr1").set("BorderCondition", "ZeroPressure");
    model.component("comp1").physics("tff").feature("init1").set("pfilm", "0[Pa]");

    try { model.component("comp1").physics("solid").feature("dcnt1").feature().remove("fric1"); }
    catch (Exception ignored) {}

    model.save(OUT);
    System.out.println("T_slide=" + model.param().evaluate("T_slide") + "[s]");
    System.out.println("SAVED_STAGE1=" + OUT);
  }
}
