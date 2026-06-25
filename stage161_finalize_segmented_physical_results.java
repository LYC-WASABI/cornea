import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage161_finalize_segmented_physical_results {
  private static void dataset(Model m, String tag, String sol, String label) {
    try { m.result().dataset().remove(tag); } catch (Exception ignore) {}
    m.result().dataset().create(tag, "Solution");
    m.result().dataset(tag).set("solution", sol);
    m.result().dataset(tag).label(label);
  }
  private static void surface(Model m, String tag, String label, String data,
      String sel, String expr, String unit) {
    try { m.result().remove(tag); } catch (Exception ignore) {}
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label("Stage 161 - " + label);
    m.result(tag).set("data", data);
    m.result(tag).selection().named(sel);
    m.result(tag).feature().create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }
  private static void global(Model m, String tag, String label, String data,
      String[] expr, String[] unit) {
    try { m.result().remove(tag); } catch (Exception ignore) {}
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label("Stage 161 - " + label);
    m.result(tag).set("data", data);
    m.result(tag).feature().create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("unit", unit);
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "stage160_final_minus67_to_minus70_output_Model.mph");
      dataset(m, "dset161a", "sol25", "Physical friction path 0 to -47 deg");
      dataset(m, "dset161b", "sol26", "Bridge -46 to -48 deg");
      dataset(m, "dset161c", "sol27", "Path -48 to -52 deg");
      dataset(m, "dset161d", "sol28", "Bridge -51 to -52.4 deg");
      dataset(m, "dset161e", "sol29", "Path -52.6 to -61.8 deg");
      dataset(m, "dset161f", "sol30", "Path -62 to -66.8 deg");
      dataset(m, "dset161g", "sol31", "Final path -67 to -70 deg");

      String last = "dset161g";
      surface(m, "pg161_contact", "Physical model contact pressure",
          last, "sel_cornea_anterior_surface", "solid.Tn", "Pa");
      surface(m, "pg161_film_tau", "Replayed physical film shear stress",
          last, "sel_cornea_anterior_surface", "tau_film_replay154", "Pa");
      surface(m, "pg161_boundary_tau", "Local boundary shear stress",
          last, "sel_cornea_anterior_surface", "tau_boundary_replay154", "Pa");
      surface(m, "pg161_total_tau", "Predicted mixed shear stress",
          last, "sel_cornea_anterior_surface", "tau_total_physical154", "Pa");
      surface(m, "pg161_hfilm", "Replayed tear-film thickness",
          last, "sel_cornea_anterior_surface", "h_replay154", "um");
      surface(m, "pg161_lambda", "Replayed lambda ratio",
          last, "sel_cornea_anterior_surface", "lambda_replay154", "1");
      surface(m, "pg161_cornea_disp", "Cornea displacement",
          last, "sel_cornea_anterior_surface", "solid.disp", "mm");
      surface(m, "pg161_lid_disp", "Lid wiper displacement",
          last, "sel_lid_contact_source_robust", "solid.disp", "mm");
      surface(m, "pg161_cornea_mises", "Cornea von Mises stress",
          last, "sel_cornea_anterior_surface", "solid.mises", "Pa");
      surface(m, "pg161_lid_mises", "Lid wiper von Mises stress",
          last, "sel_lid_contact_source_robust", "solid.mises", "Pa");
      global(m, "pg161_normal", "Final-segment normal-load control",
          last, new String[]{"Fn_contact119", "Fn_film119", "Fn_total119",
              "F_total_target"},
          new String[]{"N", "N", "N", "N"});
      global(m, "pg161_friction", "Final-segment predicted friction",
          last, new String[]{"F_film_physical154", "F_boundary_physical154",
              "F_friction_physical154", "mu_physical154"},
          new String[]{"N", "N", "N", "1"});
      global(m, "pg161_indent", "Final-segment bounded indentation",
          last, new String[]{"dr_indent119"}, new String[]{"mm"});

      try { m.result().numerical().remove("eval161"); } catch (Exception ignore) {}
      m.result().numerical().create("eval161", "EvalGlobal");
      m.result().numerical("eval161").set("data", last);
      m.result().numerical("eval161").set("expr", new String[]{
          "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
          "Fn_total119", "Fn_error119", "dr_indent119",
          "F_film_physical154", "F_boundary_physical154",
          "F_friction_physical154", "mu_physical154"
      });
      double[][] a = m.result().numerical("eval161").getReal();
      System.out.println("FINAL_ROWS=" + a[0].length);
      for (int j = 0; j < a[0].length; j++)
        System.out.printf(Locale.US,
            "row=%d phi=%.8g Ft=%.8g err=%.8g d=%.8g Ffilm=%.8g Fb=%.8g Ff=%.8g mu=%.8g%n",
            j, a[0][j], a[4][j], a[5][j], a[6][j],
            a[7][j], a[8][j], a[9][j], a[10][j]);

      m.label("290_lid8mm_stage161_bounded_contact_predicted_friction_results.mph");
      m.save("290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
