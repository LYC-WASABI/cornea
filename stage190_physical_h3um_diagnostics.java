import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage190_physical_h3um_diagnostics {
  static void removeResult(Model model, String tag) {
    try {
      model.result().remove(tag);
    } catch (Exception ignored) {
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "355_lid8mm_stage189_physical_h3um_reference_checked_Model.mph");

      try {
        model.result().dataset().remove("dset190");
      } catch (Exception ignored) {
      }
      model.result().dataset().create("dset190", "Solution");
      model.result().dataset("dset190").set("solution", "sol43");
      model.result().dataset("dset190").label(
          "Physical h0=3 um reference film solution");

      removeResult(model, "pg190_hfilm");
      model.result().create("pg190_hfilm", "PlotGroup3D");
      model.result("pg190_hfilm").label(
          "Stage 190 physical film thickness without offset");
      model.result("pg190_hfilm").set("data", "dset190");
      model.result("pg190_hfilm").feature().create("surf1", "Surface");
      model.result("pg190_hfilm").feature("surf1").set("expr", "h_iter189");
      model.result("pg190_hfilm").feature("surf1").set("unit", "um");

      removeResult(model, "pg190_pfilm");
      model.result().create("pg190_pfilm", "PlotGroup3D");
      model.result("pg190_pfilm").label(
          "Stage 190 signed thin-film pressure");
      model.result("pg190_pfilm").set("data", "dset190");
      model.result("pg190_pfilm").feature().create("surf1", "Surface");
      model.result("pg190_pfilm").feature("surf1").set("expr", "pfilm");
      model.result("pg190_pfilm").feature("surf1").set("unit", "Pa");

      removeResult(model, "pg190_tau");
      model.result().create("pg190_tau", "PlotGroup3D");
      model.result("pg190_tau").label(
          "Stage 190 physical film shear stress");
      model.result("pg190_tau").set("data", "dset190");
      model.result("pg190_tau").feature().create("surf1", "Surface");
      model.result("pg190_tau").feature("surf1").set(
          "expr", "tau_film_wall");
      model.result("pg190_tau").feature("surf1").set("unit", "Pa");

      removeResult(model, "pg190_load");
      model.result().create("pg190_load", "PlotGroup1D");
      model.result("pg190_load").label(
          "Stage 190 pressure integral diagnostic");
      model.result("pg190_load").set("data", "dset190");
      model.result("pg190_load").feature().create("glob1", "Global");
      model.result("pg190_load").feature("glob1").set(
          "expr",
          new String[] {
            "intop_film(max(pfilm,0))",
            "-intop_film(min(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)"
          });
      model.result("pg190_load").feature("glob1").set(
          "unit", new String[] {"N", "N", "N", "N"});

      try {
        model.result().numerical().remove("eval190");
      } catch (Exception ignored) {
      }
      model.result().numerical().create("eval190", "EvalGlobal");
      model.result().numerical("eval190").set("data", "dset190");
      model.result().numerical("eval190").set(
          "expr",
          new String[] {
            "intop_film(h_iter189)/intop_film(1)",
            "intop_film(max(pfilm,0))",
            "-intop_film(min(pfilm,0))",
            "intop_film(pfilm)",
            "intop_film(tau_film_wall)",
            "intop_film(tau_film_wall)/F_total_target"
          });
      model.result().numerical("eval190").set(
          "unit", new String[] {"um", "N", "N", "N", "N", "1"});
      double[][] x = model.result().numerical("eval190").getReal();
      System.out.printf(
          Locale.US,
          "STAGE190 h=%.12g Wpos=%.12g WnegMagnitude=%.12g"
              + " Wnet=%.12g Fshear=%.12g muFilm=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0], x[4][0], x[5][0]);
      model.save("356_lid8mm_stage190_physical_h3um_diagnostics_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
