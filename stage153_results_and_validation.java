import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage153_results_and_validation {
  private static void removeResult(Model m, String tag) {
    try { m.result().remove(tag); } catch (Exception ignore) {}
  }

  private static void surface(Model m, String tag, String label, String expr,
      String unit, String selection) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup3D");
    m.result(tag).label("Stage 153 - " + label);
    m.result(tag).set("data", "dset152");
    m.result(tag).selection().named(selection);
    m.result(tag).create("surf1", "Surface");
    m.result(tag).feature("surf1").set("expr", expr);
    m.result(tag).feature("surf1").set("unit", unit);
  }

  private static void globalPlot(Model m, String tag, String label,
      String[] expr, String[] descr, String[] unit) {
    removeResult(m, tag);
    m.result().create(tag, "PlotGroup1D");
    m.result(tag).label("Stage 153 - " + label);
    m.result(tag).set("data", "dset152");
    m.result(tag).create("glob1", "Global");
    m.result(tag).feature("glob1").set("expr", expr);
    m.result(tag).feature("glob1").set("descr", descr);
    m.result(tag).feature("glob1").set("unit", unit);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "272_lid8mm_stage152_fullpath_shear_feedback_results_Model.mph");

      m.component("comp1").variable("var_partitioned_local_pfilm").set(
          "F_shear_calibrated152", "F_shear_feedback73");
      m.component("comp1").variable("var_partitioned_local_pfilm").descr(
          "F_shear_calibrated152",
          "Calibrated tangential feedback force required for target friction");
      m.component("comp1").variable("var_partitioned_local_pfilm").set(
          "tau_shear_calibrated152", "tau_pfilm_shear73");
      m.component("comp1").variable("var_partitioned_local_pfilm").set(
          "F_boundary_remainder152",
          "max(F_shear_feedback73-if(isdefined(F_film_shear),F_film_shear,0),0)");

      surface(m, "pg153_cornea_disp", "Cornea anterior displacement",
          "solid.disp", "mm", "sel_cornea_anterior_surface");
      surface(m, "pg153_lid_disp", "Lid wiper displacement",
          "solid.disp", "mm", "sel_lid_contact_source_robust");
      surface(m, "pg153_cornea_mises", "Cornea anterior von Mises stress",
          "solid.mises", "Pa", "sel_cornea_anterior_surface");
      surface(m, "pg153_lid_mises", "Lid wiper von Mises stress",
          "solid.mises", "Pa", "sel_lid_contact_source_robust");
      surface(m, "pg153_contact_p", "Corneal contact pressure",
          "solid.Tn", "Pa", "sel_cornea_anterior_surface");
      surface(m, "pg153_lid_p", "Lid contact-side pressure",
          "solid.Tn", "Pa", "sel_lid_contact_source_robust");
      surface(m, "pg153_film_p", "Moving tear-film pressure footprint",
          "pfilm_replay53", "Pa", "sel_cornea_anterior_surface");
      surface(m, "pg153_shear", "Calibrated moving shear traction",
          "tau_shear_calibrated152", "Pa", "sel_cornea_anterior_surface");
      surface(m, "pg153_hfilm", "Effective tear-film thickness",
          "h_film_input", "um", "sel_cornea_anterior_surface");
      surface(m, "pg153_break", "Local intact-film weight",
          "C_film_break95", "1", "sel_cornea_anterior_surface");

      globalPlot(m, "pg153_normal_force", "Normal-load components",
          new String[]{"Fn_contact119", "Fn_film119", "Fn_total119",
              "F_total_target"},
          new String[]{"Solid contact", "Tear film", "Total", "Target"},
          new String[]{"N", "N", "N", "N"});
      globalPlot(m, "pg153_friction", "Friction force and coefficient",
          new String[]{"F_shear_feedback73", "mu_shear_feedback73"},
          new String[]{"Calibrated friction force", "Apparent coefficient"},
          new String[]{"N", "1"});
      globalPlot(m, "pg153_indent", "Solved lid indentation",
          new String[]{"dr_indent119"},
          new String[]{"Radial indentation"}, new String[]{"mm"});

      try { m.result().table().remove("tbl153"); } catch (Exception ignore) {}
      m.result().table().create("tbl153", "Table");
      m.result().table("tbl153").label("Stage 153 key validation values");
      try { m.result().numerical().remove("eval153"); } catch (Exception ignore) {}
      m.result().numerical().create("eval153", "EvalGlobal");
      m.result().numerical("eval153").set("data", "dset152");
      m.result().numerical("eval153").set("table", "tbl153");
      m.result().numerical("eval153").set("expr", new String[]{
          "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
          "Fn_total119", "Fn_error119", "dr_indent119",
          "F_shear_feedback73", "mu_shear_feedback73",
          "if(isdefined(F_film_shear),F_film_shear,0)",
          "max(F_shear_feedback73-if(isdefined(F_film_shear),F_film_shear,0),0)"
      });
      m.result().numerical("eval153").set("descr", new String[]{
          "Rotation", "Reconstructed time", "Solid contact force",
          "Film normal force", "Total normal force", "Relative force error",
          "Solved indentation", "Calibrated shear force",
          "Apparent friction coefficient", "Physical film shear force",
          "Boundary/calibration remainder"
      });
      m.result().numerical("eval153").setResult();

      m.label("273_lid8mm_stage153_complete_qs_mixed_shear_results.mph");
      m.save("273_lid8mm_stage153_complete_qs_mixed_shear_results_Model.mph");
      System.out.println("SAVED_STAGE153");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
