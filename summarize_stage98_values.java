import com.comsol.model.*;
import com.comsol.model.util.*;

public class summarize_stage98_values {
  private static void evalGlobal(Model model, String label, String dataset, String[] expr, String[] unit) {
    try {
      String tag = "tmp_" + label.replaceAll("[^A-Za-z0-9]", "_");
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, "EvalGlobal");
      model.result().numerical(tag).set("data", dataset);
      model.result().numerical(tag).set("expr", expr);
      model.result().numerical(tag).set("unit", unit);
      double[][] vals = model.result().numerical(tag).getReal();
      System.out.println("GLOBAL " + label);
      for (int i = 0; i < expr.length; i++) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        int imax = -1, imin = -1, n = 0;
        for (int j = 0; j < vals[i].length; j++) {
          double v = vals[i][j];
          if (!Double.isNaN(v)) {
            n++;
            if (v < min) { min = v; imin = j; }
            if (v > max) { max = v; imax = j; }
          }
        }
        System.out.println(expr[i] + " [" + unit[i] + "] n=" + n + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
      }
    } catch (Exception e) {
      System.out.println("GLOBAL " + label + " ERROR " + e.getMessage());
    }
  }

  private static void evalSurface(Model model, String type, String label, String dataset, String selection, String expr, String unit) {
    try {
      String tag = "tmp_" + type + "_" + label.replaceAll("[^A-Za-z0-9]", "_");
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, type);
      model.result().numerical(tag).set("data", dataset);
      model.result().numerical(tag).selection().named(selection);
      model.result().numerical(tag).set("expr", expr);
      model.result().numerical(tag).set("unit", unit);
      double[][] vals = model.result().numerical(tag).getReal();
      double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
      int imax = -1, imin = -1, n = 0;
      for (int r = 0; r < vals.length; r++) {
        for (int c = 0; c < vals[r].length; c++) {
          double v = vals[r][c];
          if (!Double.isNaN(v)) {
            n++;
            if (v < min) { min = v; imin = c; }
            if (v > max) { max = v; imax = c; }
          }
        }
      }
      System.out.println(type + " " + label + " expr=" + expr + " [" + unit + "] n=" + n + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
    } catch (Exception e) {
      System.out.println(type + " " + label + " ERROR " + e.getMessage());
    }
  }

  private static void evalIntegral(Model model, String label, String dataset, String selection, String expr, String unit) {
    try {
      String tag = "tmp_int_" + label.replaceAll("[^A-Za-z0-9]", "_");
      try { model.result().numerical().remove(tag); } catch (Exception ignore) {}
      model.result().numerical().create(tag, "IntSurface");
      model.result().numerical(tag).set("data", dataset);
      model.result().numerical(tag).selection().named(selection);
      model.result().numerical(tag).set("expr", expr);
      model.result().numerical(tag).set("unit", unit);
      double[][] vals = model.result().numerical(tag).getReal();
      double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
      int imax = -1, imin = -1, n = 0;
      for (int r = 0; r < vals.length; r++) {
        for (int c = 0; c < vals[r].length; c++) {
          double v = vals[r][c];
          if (!Double.isNaN(v)) {
            n++;
            if (v < min) { min = v; imin = c; }
            if (v > max) { max = v; imax = c; }
          }
        }
      }
      System.out.println("INT " + label + " expr=" + expr + " [" + unit + "] n=" + n + " min=" + min + " @idx=" + imin + " max=" + max + " @idx=" + imax);
    } catch (Exception e) {
      System.out.println("INT " + label + " ERROR " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "176_lid8mm_stage98_gap_limited_hfilm_results_Model.mph");
      String film = "dset_rq0p5_film92";
      String solid = "dset_shear_feedback76";
      String cornea = "sel_cornea_anterior_surface";
      String lid = "sel_lid_contact_source_robust";
      System.out.println("PARAM Rq_lid=" + model.param().get("Rq_lid"));
      System.out.println("PARAM Rq_eq=" + model.param().evaluate("Rq_eq", "um"));
      System.out.println("PARAM h0_tear=" + model.param().get("h0_tear"));
      System.out.println("PARAM h_break_low=" + model.param().get("h_break_low"));
      System.out.println("PARAM h_break_high=" + model.param().get("h_break_high"));
      System.out.println("VAR h_available=" + model.component("comp1").variable("var_mixed_lub").get("h_available"));
      System.out.println("VAR h_geom_limit=" + model.component("comp1").variable("var_mixed_lub").get("h_geom_limit"));
      System.out.println("VAR h_inside_lid=" + model.component("comp1").variable("var_mixed_lub").get("h_inside_lid"));
      System.out.println("VAR h_film_input=" + model.component("comp1").variable("var_mixed_lub").get("h_film_input"));

      evalGlobal(model, "forces_mu", film,
        new String[]{"W_film","F_film_shear","F_boundary_break95","F_total_break95","mu_break95","mu_app_film_only","W_film/intop_film(1)"},
        new String[]{"N","N","N","N","1","1","Pa"});
      evalGlobal(model, "hfilm_lambda_break", film,
        new String[]{"intop_film(h_film_input)/intop_film(1)","intop_film(f_break95)/intop_film(1)","intop_film(h_film_input/Rq_eq)/intop_film(1)"},
        new String[]{"um","1","1"});
      evalGlobal(model, "tau_avg", film,
        new String[]{"intop_film(tau_film_wall)/intop_film(1)","intop_film(tau_boundary_break95)/intop_film(1)","intop_film(tau_total_break95)/intop_film(1)"},
        new String[]{"Pa","Pa","Pa"});

      evalSurface(model, "MinSurface", "hfilm", film, cornea, "h_film_input", "um");
      evalSurface(model, "MaxSurface", "hfilm", film, cornea, "h_film_input", "um");
      evalSurface(model, "MaxSurface", "break_weight", film, cornea, "f_break95", "1");
      evalSurface(model, "MaxSurface", "film_pressure", film, cornea, "max(pfilm,0)", "Pa");
      evalSurface(model, "MaxSurface", "film_shear", film, cornea, "tau_film_wall", "Pa");
      evalSurface(model, "MaxSurface", "boundary_shear", film, cornea, "tau_boundary_break95", "Pa");
      evalSurface(model, "MaxSurface", "total_shear", film, cornea, "tau_total_break95", "Pa");
      evalSurface(model, "MaxSurface", "cornea_contact_pressure", solid, cornea, "if(isdefined(solid.Tn),solid.Tn,0)", "Pa");
      evalSurface(model, "MaxSurface", "cornea_disp", solid, cornea, "solid.disp", "mm");
      evalSurface(model, "MaxSurface", "lid_disp", solid, lid, "solid.disp", "mm");
      evalSurface(model, "MaxSurface", "cornea_mises", solid, cornea, "solid.mises", "Pa");
      evalSurface(model, "MaxSurface", "lid_mises", solid, lid, "solid.mises", "Pa");

      evalIntegral(model, "film_pressure_integral", film, cornea, "max(pfilm,0)", "N");
      evalIntegral(model, "film_shear_integral", film, cornea, "tau_film_wall", "N");
      evalIntegral(model, "boundary_shear_integral", film, cornea, "tau_boundary_break95", "N");
      evalIntegral(model, "total_shear_integral", film, cornea, "tau_total_break95", "N");
      evalIntegral(model, "cornea_contact_pressure_integral", solid, cornea, "if(isdefined(solid.Tn),solid.Tn,0)", "N");

      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
