import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage41_h3um_dynamic_gap_film_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\112_lid8mm_stage41_h3um_dynamic_gap_film_setup.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("112_lid8mm_stage41_h3um_dynamic_gap_film_setup.mph");
    model.param().set("h0_tear", "3[um]", "Requested baseline tear-film thickness");
    model.param().set("gap_cap_tear", "30[um]", "Finite cap for pair gap outside mapped contact neighborhood");

    model.component("comp1").variable("var_mixed_lub").set("gap_replay_tear",
        "min(max(withsol('sol20',geomgap_dst_cp_lid_cornea,setval(t,t)),0),gap_cap_tear)");
    model.component("comp1").variable("var_mixed_lub").set("h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)");
    model.component("comp1").variable("var_mixed_lub").set("h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    model.component("comp1").physics("tff").feature("ffp1").set("hw1", "h_film_input");
    model.component("comp1").physics("tff").feature("bdr1").set("BorderCondition", "ZeroPressure");
    model.component("comp1").physics("tff").feature("init1").set("pfilm", "0[Pa]");

    try { model.study().remove("std_tff_dynamic_gap41"); } catch (Exception ignored) {}
    model.study().create("std_tff_dynamic_gap41");
    model.study("std_tff_dynamic_gap41").label("Stage 41 h0=3 um thin-film flow with replayed dynamic contact gap");
    model.study("std_tff_dynamic_gap41").create("time", "Transient");
    model.study("std_tff_dynamic_gap41").feature("time").set("tlist",
        "range(0,dt_out,T_pre+T_slide+T_hold)");
    model.study("std_tff_dynamic_gap41").feature("time").set("activate",
        new String[]{"solid", "off", "tff", "on"});

    model.save(OUT);
    System.out.println("H0_TEAR=" + model.param().get("h0_tear"));
    System.out.println("H_FILM_INPUT="
        + model.component("comp1").variable("var_mixed_lub").get("h_film_input"));
    System.out.println("SAVED_STAGE41_SETUP=" + OUT);
  }
}
