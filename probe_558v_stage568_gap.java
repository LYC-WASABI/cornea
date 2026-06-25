import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558v_stage568_gap {
  private static void evalSurface(
      Model model, String tag, String type, String expr, String unit) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset_probe568");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("unit", unit);
    System.out.println(tag + "=" + Arrays.deepToString(
        model.result().numerical(tag).getReal()));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("MODEL=" + model.label());
      for (String tag : comp.variable().tags()) {
        for (String name : new String[] {
            "h_film566", "h_geom555", "h_gap_direct555", "h_raw555",
            "dgap_n555", "p_feedback567", "Wfilm567", "Ftotal567",
            "Ferr567", "dr_indent119"
        }) {
          try {
            String value = comp.variable(tag).get(name);
            if (value != null && !value.isEmpty()) {
              System.out.println("VAR|" + tag + "|" + name + "|" + value);
            }
          } catch (Exception ignored) {}
        }
      }
      try { model.result().dataset().remove("dset_probe568"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_probe568", "Solution");
      model.result().dataset("dset_probe568").set("solution", "sol91");

      evalSurface(model, "min_hgeom568", "MinSurface", "h_geom555", "um");
      evalSurface(model, "max_hgeom568", "MaxSurface", "h_geom555", "um");
      evalSurface(model, "avg_hgeom568", "IntSurface", "h_geom555", "m^3");
      evalSurface(model, "area568", "IntSurface", "1", "m^2");
      evalSurface(model, "min_hdirect568", "MinSurface",
          "h_gap_direct555", "um");
      evalSurface(model, "max_hdirect568", "MaxSurface",
          "h_gap_direct555", "um");

      try { model.result().numerical().remove("eval_global568"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval_global568", "EvalGlobal");
      model.result().numerical("eval_global568").set("data", "dset_probe568");
      model.result().numerical("eval_global568").set("expr", new String[] {
          "Wfilm567", "Fn_contact119", "Ftotal567", "Ferr567",
          "dr_indent119", "q_force_total111"
      });
      System.out.println("GLOBAL=" + Arrays.deepToString(
          model.result().numerical("eval_global568").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
