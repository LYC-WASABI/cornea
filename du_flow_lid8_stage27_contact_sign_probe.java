import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage27_contact_sign_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\82_lid8mm_stage27_force_controlled_qs_scan_short_results.mph";

  private static void global(Model model, String tag, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset6");
    model.result().numerical(tag).set("expr", new String[]{expr});
    double[][] v = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double x : v[0]) { min = Math.min(min, x); max = Math.max(max, x); }
    System.out.println(tag + " expr=" + expr + " min=" + min + " max=" + max);
  }

  private static void surface(Model model, String tag, String selection, String expr) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("data", "dset6");
    model.result().numerical(tag).set("expr", new String[]{expr});
    double[][] v = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double x : v[0]) { min = Math.min(min, x); max = Math.max(max, x); }
    System.out.println(tag + " expr=" + expr + " min=" + min + " max=" + max);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    for (String pg : new String[]{"pg_dyn_contact_pressure", "pg_preload_contact_pressure"}) {
      try {
        System.out.println(pg + "_EXPR=" + model.result(pg).feature("surf1").getString("expr"));
      } catch (Exception e) {
        System.out.println(pg + "_EXPR=<unavailable>");
      }
    }
    global(model, "gev_q", "q_force");
    global(model, "gev_resid",
        "(comp1.intop_contact(if(isdefined(solid.Tn),solid.Tn,0))+comp1.W_film_qs27-F_total_target)/F_total_target");
    surface(model, "int_tn_source", "sel_lid_wiper_inner_surface", "solid.Tn");
    surface(model, "int_neg_tn_source", "sel_lid_wiper_inner_surface", "-solid.Tn");
    surface(model, "int_abs_tn_source", "sel_lid_wiper_inner_surface", "abs(solid.Tn)");
    surface(model, "int_pos_tn_source", "sel_lid_wiper_inner_surface", "max(solid.Tn,0)");
    surface(model, "int_negpart_tn_source", "sel_lid_wiper_inner_surface", "max(-solid.Tn,0)");
  }
}
