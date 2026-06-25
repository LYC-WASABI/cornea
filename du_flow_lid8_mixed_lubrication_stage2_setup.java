import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage2_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\31_lid8mm_mixed_lubrication_stage1_tff_water_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\32_lid8mm_mixed_lubrication_stage2_oneway_setup.mph";

  private static boolean hasCpl(Model model, String tag) {
    for (String t : model.component("comp1").cpl().tags()) if (t.equals(tag)) return true;
    return false;
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("32_lid8mm_mixed_lubrication_stage2_oneway_setup.mph");

    if (!hasCpl(model, "intop_film")) model.component("comp1").cpl().create("intop_film", "Integration");
    model.component("comp1").cpl("intop_film").selection().named("sel_cornea_anterior_surface");
    if (!hasCpl(model, "intop_contact")) model.component("comp1").cpl().create("intop_contact", "Integration");
    model.component("comp1").cpl("intop_contact").selection().named("sel_cornea_anterior_surface");

    model.component("comp1").variable("var_mixed_lub").set("film_force_n_density",
        "max(pfilm,0)");
    model.component("comp1").variable("var_mixed_lub").set("W_film",
        "intop_film(film_force_n_density)");
    model.component("comp1").variable("var_mixed_lub").set("W_contact",
        "intop_contact(if(isdefined(solid.Tn),solid.Tn,0))");
    model.component("comp1").variable("var_mixed_lub").set("W_total_mixed",
        "W_film+W_contact");
    model.component("comp1").variable("var_mixed_lub").set("film_load_share",
        "W_film/F_total_target");
    model.component("comp1").variable("var_mixed_lub").set("fwall_dot_n",
        "tff.fwallx*nx+tff.fwally*ny+tff.fwallz*nz");
    model.component("comp1").variable("var_mixed_lub").set("tau_film_wall",
        "sqrt((tff.fwallx-fwall_dot_n*nx)^2+(tff.fwally-fwall_dot_n*ny)^2"
            + "+(tff.fwallz-fwall_dot_n*nz)^2)");
    model.component("comp1").variable("var_mixed_lub").set("F_film_shear",
        "intop_film(tau_film_wall)");
    model.component("comp1").variable("var_mixed_lub").set("mu_app_film_only",
        "F_film_shear/F_total_target");

    try { model.study().remove("std_tff_oneway"); } catch (Exception ignored) {}
    model.study().create("std_tff_oneway");
    model.study("std_tff_oneway").label("Stage 2 one-way tear-film flow driven by calibrated lid motion");
    model.study("std_tff_oneway").create("time", "Transient");
    model.study("std_tff_oneway").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.study("std_tff_oneway").feature("time").set("activate",
        new String[]{"solid", "off", "tff", "on"});
    model.save(OUT);
    System.out.println("SAVED_STAGE2_SETUP=" + OUT);
  }
}
