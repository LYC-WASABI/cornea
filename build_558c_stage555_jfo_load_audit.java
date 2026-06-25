import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_558c_stage555_jfo_load_audit {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558b_stage555_JFO_only_alpha0p964_results.mph");
      String comp = "comp1";
      String output = "558c_stage555_JFO_load_audit.mph";

      // Step 1.2: the film integration operator is restricted to the
      // imprinted cornea-side local curved film track.
      model.component(comp).cpl("intop_film")
          .selection().named("sel_film_track");

      // Step 1.3: use one pressure scaling definition for both the
      // integrated film load and the structural pressure feedback.
      model.component(comp).variable("var_load_coupled555").set(
          "Wfilm555",
          "scale_pfilm555*intop_film(max(tff.p,0))");
      model.component(comp).variable("var_load_coupled555").set(
          "Ftotal555", "Wfilm555+Fn_contact119");
      model.component(comp).physics("solid")
          .feature("load_partitioned_pfilm").set(
              "FperArea", new String[] {
                "-scale_pfilm555*max(tff.p,0)*nx",
                "-scale_pfilm555*max(tff.p,0)*ny",
                "-scale_pfilm555*max(tff.p,0)*nz"
              });

      // Step 1.4: create a dedicated, saved audit table on the JFO-only
      // alpha=0.964 result dataset. This evaluates existing results only.
      String table = "tbl558c_load_audit";
      String eval = "eval558c_load_audit";
      try { model.result().table().remove(table); } catch (Exception ignored) {}
      model.result().table().create(table, "Table");
      model.result().table(table).label(
          "Stage 555 JFO load audit at alpha 0.964");

      try { model.result().numerical().remove(eval); } catch (Exception ignored) {}
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).label(
          "Stage 555 JFO raw and scaled film-load audit");
      model.result().numerical(eval).set("data", "dset562");
      model.result().numerical(eval).set("expr", new String[] {
        "intop_film(max(tff.p,0))",
        "Wfilm555",
        "scale_pfilm555",
        "F_total_target"
      });
      model.result().numerical(eval).set("unit", new String[] {
        "N", "N", "1", "N"
      });
      model.result().numerical(eval).set("descr", new String[] {
        "Raw local-film pressure integral",
        "Scaled film load used by structure",
        "Film-pressure feedback scale",
        "Target total normal load"
      });
      model.result().numerical(eval).set("table", table);
      model.result().numerical(eval).setResult();

      double[][] values = model.result().numerical(eval).getReal();
      System.out.println("AUDIT=" + Arrays.deepToString(values));
      System.out.println("INTOP_SELECTION=" + Arrays.toString(
          model.component(comp).cpl("intop_film")
              .selection().entities()));
      System.out.println("FILM_TRACK_SELECTION=" + Arrays.toString(
          model.component(comp).selection("sel_film_track").entities(2)));
      System.out.println("Wfilm555="
          + model.component(comp).variable("var_load_coupled555")
              .get("Wfilm555"));
      System.out.println("FPERAREA=" + Arrays.toString(
          model.component(comp).physics("solid")
              .feature("load_partitioned_pfilm")
              .getStringArray("FperArea")));

      model.label("Stage 555 JFO load audit at alpha 0.964");
      model.save(output);
      System.out.println("OUTPUT=" + output);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
