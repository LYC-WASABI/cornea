import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage98_gap_limited_hfilm {
  private static void setVar(Model model, String name, String expr, String descr) {
    model.component("comp1").variable("var_mixed_lub").set(name, expr);
    if (descr != null && descr.length() > 0) {
      model.component("comp1").variable("var_mixed_lub").descr(name, descr);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\175_lid8mm_stage97_rq0p5um_break0p5to1_results.mph";
      String localOut = "176_lid8mm_stage98_gap_limited_hfilm_results_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      model.param().set("Rq_lid", "0.5[um]", "Effective lid wiper RMS roughness");
      model.param().set("h_break_low", "0.5[um]", "Local tear-film rupture lower threshold");
      model.param().set("h_break_high", "1.0[um]", "Local tear-film rupture upper threshold");
      model.param().set("h_outside_track", "3[um]", "Tear-film thickness outside lid footprint/track");

      setVar(model, "h_available", "h0_tear+Rq_eq", "Available wet film thickness including roughness offset");
      setVar(model, "h_geom_limit", "gap_smooth_replay_tear+Rq_eq", "Local geometric clearance limit including roughness offset");
      setVar(model, "h_inside_lid", "max(h_min_tear,min(h_available,h_geom_limit))", "Gap-limited effective film thickness under lid");
      setVar(model, "h_film_input", "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)", "Film thickness with gap-limited lid footprint and 3 um outside track");

      setVar(model, "C_film_break95", "if(h_film_input<=h_break_low,0,if(h_film_input>=h_break_high,1,0.5-0.5*cos(pi*(h_film_input-h_break_low)/(h_break_high-h_break_low))))", "Local intact film weight");
      setVar(model, "f_break95", "1-C_film_break95", "Local film rupture/boundary weight");
      setVar(model, "p_boundary_nominal95", "F_total_target/A_contact_nominal73", "Nominal normal pressure for boundary shear");
      setVar(model, "tau_boundary_break95", "mu_boundary_break90*p_boundary_nominal95*f_break95", "Boundary shear stress active in ruptured film zones");
      setVar(model, "F_boundary_break95", "intop_film(tau_boundary_break95)", "Boundary shear force");
      setVar(model, "F_total_break95", "F_film_shear+F_boundary_break95", "Total shear force");
      setVar(model, "mu_break95", "F_total_break95/F_total_target", "Apparent mixed friction coefficient");
      setVar(model, "tau_total_break95", "tau_film_wall+tau_boundary_break95", "Total shear stress");

      model.study("std_tff_gap_qs45").run();
      model.save(localOut);
      System.out.println("Saved local: " + localOut);
      System.out.println("h_inside_lid=max(h_min_tear,min(h_available,h_geom_limit))");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
