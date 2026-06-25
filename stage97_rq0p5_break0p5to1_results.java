import com.comsol.model.*;
import com.comsol.model.util.*;

public class stage97_rq0p5_break0p5to1_results {
  private static void clearResults(Model model) {
    for (String tag : model.result().numerical().tags()) {
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
    }
    for (String tag : model.result().tags()) {
      try { model.result().remove(tag); } catch (Exception ignore) {}
    }
    for (String tag : model.result().table().tags()) {
      try { model.result().table().remove(tag); } catch (Exception ignore) {}
    }
  }

  private static void addSurface(Model model, String pgTag, String label, String dataset,
                                 String selection, String expr, String unit) {
    model.result().create(pgTag, "PlotGroup3D");
    model.result(pgTag).label(label);
    model.result(pgTag).set("data", dataset);
    model.result(pgTag).selection().named(selection);
    model.result(pgTag).feature().create("surf1", "Surface");
    model.result(pgTag).feature("surf1").set("expr", expr);
    model.result(pgTag).feature("surf1").set("unit", unit);
  }

  private static void addGlobalPlot(Model model, String pgTag, String label, String dataset,
                                    String[] expr, String[] unit) {
    model.result().create(pgTag, "PlotGroup1D");
    model.result(pgTag).label(label);
    model.result(pgTag).set("data", dataset);
    model.result(pgTag).feature().create("glob1", "Global");
    model.result(pgTag).feature("glob1").set("expr", expr);
    model.result(pgTag).feature("glob1").set("unit", unit);
  }

  private static void addEval(Model model, String tag, String label, String dataset,
                              String[] expr, String[] unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
  }

  private static void addSurfaceIntegral(Model model, String tag, String label, String dataset,
                                         String selection, String expr, String unit) {
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).label(label);
    model.result().numerical(tag).set("data", dataset);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      String inPath = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\flow\\\\174_lid8mm_stage96_houtside3um_no_hfeedback_recomputed.mph";
      String localOut = "175_lid8mm_stage97_rq0p5um_break0p5to1_results_Model.mph";
      Model model = ModelUtil.load("Model", inPath);

      model.param().set("Rq_lid", "0.5[um]", "Effective lid wiper RMS roughness");
      model.param().set("h_break_low", "0.5[um]", "Local tear-film rupture lower threshold");
      model.param().set("h_break_high", "1.0[um]", "Local tear-film rupture upper threshold");
      model.param().set("h_outside_track", "3[um]", "Tear-film thickness outside lid footprint/track");

      model.component("comp1").variable("var_mixed_lub").set(
        "h_inside_lid",
        "max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq)"
      );
      model.component("comp1").variable("var_mixed_lub").set(
        "h_film_input",
        "h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)"
      );
      model.component("comp1").variable("var_mixed_lub").set(
        "C_film_break95",
        "if(h_film_input<=h_break_low,0,if(h_film_input>=h_break_high,1,0.5-0.5*cos(pi*(h_film_input-h_break_low)/(h_break_high-h_break_low))))"
      );
      model.component("comp1").variable("var_mixed_lub").set("f_break95", "1-C_film_break95");
      model.component("comp1").variable("var_mixed_lub").set("p_boundary_nominal95", "F_total_target/A_contact_nominal73");
      model.component("comp1").variable("var_mixed_lub").set("tau_boundary_break95", "mu_boundary_break90*p_boundary_nominal95*f_break95");
      model.component("comp1").variable("var_mixed_lub").set("F_boundary_break95", "intop_film(tau_boundary_break95)");
      model.component("comp1").variable("var_mixed_lub").set("F_total_break95", "F_film_shear+F_boundary_break95");
      model.component("comp1").variable("var_mixed_lub").set("mu_break95", "F_total_break95/F_total_target");
      model.component("comp1").variable("var_mixed_lub").set("tau_total_break95", "tau_film_wall+tau_boundary_break95");

      clearResults(model);

      model.study("std_tff_gap_qs45").run();

      String film = "dset_rq0p5_film92";
      String solid = "dset_shear_feedback76";
      String cornea = "sel_cornea_anterior_surface";
      String lid = "sel_lid_contact_source_robust";

      addSurface(model, "pg_film_pressure", "Film pressure on cornea surface", film, cornea, "max(pfilm,0)", "Pa");
      addSurface(model, "pg_cornea_contact_pressure", "Cornea surface contact pressure", solid, cornea, "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      addSurface(model, "pg_lid_contact_pressure", "Lid wiper inner-surface contact pressure", solid, lid, "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      addSurface(model, "pg_film_shear_stress", "Film wall shear stress plus boundary shear", film, cornea, "tau_total_break95", "Pa");
      addSurface(model, "pg_film_thickness", "Tear-film thickness", film, cornea, "h_film_input", "um");
      addSurface(model, "pg_film_break_weight", "Local film rupture weight", film, cornea, "f_break95", "1");
      addSurface(model, "pg_lambda", "Local lambda h/Rq", film, cornea, "h_film_input/Rq_eq", "1");
      addSurface(model, "pg_cornea_mises", "Cornea anterior von Mises stress", solid, cornea, "solid.mises", "Pa");
      addSurface(model, "pg_lid_mises", "Lid wiper von Mises stress", solid, lid, "solid.mises", "Pa");
      addSurface(model, "pg_cornea_displacement", "Cornea anterior displacement", solid, cornea, "solid.disp", "mm");
      addSurface(model, "pg_lid_displacement", "Lid wiper displacement", solid, lid, "solid.disp", "mm");

      addGlobalPlot(model, "pg_force_mu_time", "Friction force and friction coefficient over time", film,
        new String[]{"F_film_shear", "F_boundary_break95", "F_total_break95", "mu_break95", "mu_app_film_only"},
        new String[]{"N", "N", "N", "1", "1"});
      addGlobalPlot(model, "pg_normal_pressure_time", "Film normal load and mean pressure over time", film,
        new String[]{"W_film", "W_film/A_contact_nominal73", "maxop1(pfilm)"},
        new String[]{"N", "Pa", "Pa"});
      addGlobalPlot(model, "pg_hfilm_time", "Film thickness and rupture metrics over time", film,
        new String[]{"intop_film(h_film_input)/A_contact_nominal73", "intop_film(f_break95)/A_contact_nominal73"},
        new String[]{"um", "1"});
      addGlobalPlot(model, "pg_tau_time", "Shear stress metrics over time", film,
        new String[]{"intop_film(tau_film_wall)/A_contact_nominal73", "intop_film(tau_boundary_break95)/A_contact_nominal73", "intop_film(tau_total_break95)/A_contact_nominal73"},
        new String[]{"Pa", "Pa", "Pa"});

      addEval(model, "eval_key_force_mu", "Key force and friction coefficient values", film,
        new String[]{"W_film", "F_film_shear", "F_boundary_break95", "F_total_break95", "mu_break95", "mu_app_film_only"},
        new String[]{"N", "N", "N", "N", "1", "1"});
      addEval(model, "eval_key_hfilm", "Key film thickness values", film,
        new String[]{"intop_film(h_film_input)/A_contact_nominal73", "intop_film(f_break95)/A_contact_nominal73"},
        new String[]{"um", "1"});
      model.result().numerical().create("min_hfilm", "MinSurface");
      model.result().numerical("min_hfilm").label("Minimum film thickness");
      model.result().numerical("min_hfilm").set("data", film);
      model.result().numerical("min_hfilm").selection().named(cornea);
      model.result().numerical("min_hfilm").set("expr", "h_film_input");
      model.result().numerical("min_hfilm").set("unit", "um");
      model.result().numerical().create("max_hfilm", "MaxSurface");
      model.result().numerical("max_hfilm").label("Maximum film thickness");
      model.result().numerical("max_hfilm").set("data", film);
      model.result().numerical("max_hfilm").selection().named(cornea);
      model.result().numerical("max_hfilm").set("expr", "h_film_input");
      model.result().numerical("max_hfilm").set("unit", "um");
      addSurfaceIntegral(model, "int_film_shear", "Film shear force integral", film, cornea, "tau_film_wall", "N");
      addSurfaceIntegral(model, "int_boundary_shear", "Boundary rupture shear force integral", film, cornea, "tau_boundary_break95", "N");
      addSurfaceIntegral(model, "int_cornea_contact_pressure", "Cornea contact pressure integral", solid, cornea, "if(isdefined(solid.Tn),solid.Tn,0)", "N");

      model.save(localOut);
      System.out.println("Saved local: " + localOut);
      System.out.println("Rq_lid=" + model.param().get("Rq_lid"));
      System.out.println("h_break_low=" + model.param().get("h_break_low"));
      System.out.println("h_break_high=" + model.param().get("h_break_high"));
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
