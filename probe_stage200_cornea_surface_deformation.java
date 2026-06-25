import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage200_cornea_surface_deformation {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph");

      try { model.result().dataset().remove("dset_probe200s"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_probe200s", "Solution");
      model.result().dataset("dset_probe200s").set("solution", "sol49");

      try { model.result().numerical().remove("eval_probe200_global"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval_probe200_global", "EvalGlobal");
      model.result().numerical("eval_probe200_global").set("data", "dset_probe200s");
      model.result().numerical("eval_probe200_global").set("expr", new String[] {
        "dr_indent119",
        "intop_film(lid_mask*(u*nx+v*ny+w*nz))/intop_film(lid_mask)",
        "intop_film(lid_mask*solid.disp)/intop_film(lid_mask)",
        "intop_film(lid_mask*h_jfo197)/intop_film(lid_mask)",
        "Fn_contact119",
        "Wfilm199",
        "Ftotal199"
      });
      model.result().numerical("eval_probe200_global").set(
          "unit", new String[] {"um", "um", "um", "um", "N", "N", "N"});
      double[][] g = model.result().numerical("eval_probe200_global").getReal();
      System.out.printf(Locale.US,
          "GLOBAL dr_indent_um=%.12g avg_normal_disp_um=%.12g avg_disp_mag_um=%.12g avg_hfilm_um=%.12g Fn_contact_N=%.12g Wfilm_N=%.12g Ftotal_N=%.12g%n",
          g[0][0], g[1][0], g[2][0], g[3][0], g[4][0], g[5][0], g[6][0]);

      String[][] probes = new String[][] {
        {"max_cornea_disp_mag", "MaxSurface", "solid.disp", "um"},
        {"max_cornea_normal_disp", "MaxSurface", "u*nx+v*ny+w*nz", "um"},
        {"min_cornea_normal_disp", "MinSurface", "u*nx+v*ny+w*nz", "um"}
      };
      for (String[] p : probes) {
        try { model.result().numerical().remove(p[0]); }
        catch (Exception ignored) {}
        model.result().numerical().create(p[0], p[1]);
        model.result().numerical(p[0]).set("data", "dset_probe200s");
        model.result().numerical(p[0]).selection().named("sel_cornea_anterior_surface");
        model.result().numerical(p[0]).set("expr", p[2]);
        model.result().numerical(p[0]).set("unit", p[3]);
        double[][] v = model.result().numerical(p[0]).getReal();
        System.out.printf(Locale.US, "%s=%.12g %s%n", p[0], v[0][0], p[3]);
      }

      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
